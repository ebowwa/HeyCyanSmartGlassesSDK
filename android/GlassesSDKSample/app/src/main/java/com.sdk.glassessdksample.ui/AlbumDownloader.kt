package com.sdk.glassessdksample.ui

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException

data class MediaItem(val fileName: String, val type: Int) // Type 1 for JPG, 2 for MP4 as per your example
data class MediaConfig(val files: List<MediaItem>) // This might need to be adjusted based on actual config format

class AlbumDownloader(private val ctx: Context) {
    // It's good practice to reuse the OkHttpClient instance
    private val ok = OkHttpClient()

    suspend fun fetchConfig(baseIp: String): List<MediaItem> = withContext(Dispatchers.IO) {
        val url = "http://$baseIp/files/media.config"
        Log.i("DL", "Fetching config from: $url")
        try {
            val request = Request.Builder().url(url).build()
            val body = ok.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                response.body?.string() ?: ""
            }
            // The real app’s format is unknown; adapt this parser to what you see.
            // For demo, accept one-filename-per-line:
            val items = body
                .lineSequence()
                .filter { it.isNotBlank() && (it.endsWith(".jpg", ignoreCase = true) || it.endsWith(".mp4", ignoreCase = true)) }
                .map {
                    val trimmedName = it.trim()
                    MediaItem(trimmedName, if (trimmedName.endsWith(".jpg", ignoreCase = true)) 1 else 2)
                }
                .toList()
            Log.i("DL", "Parsed config: $items items")
            items
        } catch (e: IOException) {
            Log.e("DL", "Failed to fetch or parse config from $url", e)
            emptyList() // Return an empty list or throw an exception as per your error handling strategy
        }
    }

    suspend fun fetchOne(baseIp: String, fname: String): File? = withContext(Dispatchers.IO) {
        val url = "http://$baseIp/files/$fname"
        Log.i("DL", "Fetching file from: $url")
        try {
            val request = Request.Builder().url(url).build()
            val dir = File(ctx.getExternalFilesDir(null), "DCIM_1").apply { mkdirs() }
            val out = File(dir, fname)

            ok.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response for $url")
                response.body!!.byteStream().use { input ->
                    out.outputStream().use { fileOut ->
                        input.copyTo(fileOut)
                    }
                }
            }
            Log.i("DL", "Successfully downloaded $fname to ${out.absolutePath}")
            out
        } catch (e: Exception) { // Catching generic Exception to log more details, consider specific exceptions
            Log.e("DL", "Failed to fetch file $fname from $url", e)
            null // Return null or throw an exception
        }
    }
}