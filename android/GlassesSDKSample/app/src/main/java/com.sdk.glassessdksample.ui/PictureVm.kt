package com.sdk.glassessdksample.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.io.File

class PictureVm(
    private val p2p: P2PController,
    private val ble: BleIpBridge,
    private val dl: AlbumDownloader
) : ViewModel() { // Extend ViewModel for lifecycle awareness

    // Use viewModelScope provided by androidx.lifecycle.ViewModel for automatic cancellation
    // private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main) // Not needed if using viewModelScope

    // For emitting events like "all files downloaded" or errors
    sealed class DownloadEvent {
        data class FileDownloaded(val file: File, val remaining: Int) : DownloadEvent()
        object AllFilesDownloaded : DownloadEvent()
        data class Error(val message: String) : DownloadEvent()
        data class IpReceived(val ip: String) : DownloadEvent()
        data class P2PGroupCreated(val success: Boolean) : DownloadEvent()
    }

    private val _downloadEvents = MutableSharedFlow<DownloadEvent>()
    val downloadEvents = _downloadEvents.asSharedFlow()

    fun startPhotoPullProcess() {
        viewModelScope.launch {
            // 1) Start P2P (phone as GO)
            Log.i("VM", "Attempting to create P2P group...")
            p2p.createGroup { ok ->
                Log.i("VM", "createGroup result: $ok")
                viewModelScope.launch { _downloadEvents.emit(DownloadEvent.P2PGroupCreated(ok)) }
                if (!ok) {
                    viewModelScope.launch { _downloadEvents.emit(DownloadEvent.Error("Failed to create P2P group")) }
                }
                // Proceed only if group creation was successful
            }
        }

        // 2) When BLE tells us the device IP, download media list & files
        viewModelScope.launch {
            ble.ip.filterNotNull().take(1).collect { ip ->
                Log.i("VM", "Device IP received via BLE: $ip")
                _downloadEvents.emit(DownloadEvent.IpReceived(ip))
                try {
                    val items = dl.fetchConfig(ip)
                    Log.i("VM", "Total files from config: ${items.size}")
                    if (items.isEmpty()) {
                        Log.i("VM", "No items found in media.config or failed to parse.")
                        _downloadEvents.emit(DownloadEvent.AllFilesDownloaded) // Or an error/empty state
                        return@collect
                    }

                    var left = items.size
                    for (it in items) {
                        val f = dl.fetchOne(ip, it.fileName)
                        if (f != null) {
                            Log.i("VM", "Saved -> ${f.absolutePath}")
                            left--
                            _downloadEvents.emit(DownloadEvent.FileDownloaded(f, left))
                            Log.i("VM", "Files left: $left")
                        } else {
                            Log.e("VM", "Failed to download ${it.fileName}")
                            _downloadEvents.emit(DownloadEvent.Error("Failed to download ${it.fileName}"))
                            // Decide if you want to stop or continue on error
                        }
                    }
                    Log.i("VM", "All files processed.")
                    _downloadEvents.emit(DownloadEvent.AllFilesDownloaded)
                } catch (e: Exception) {
                    Log.e("VM", "Error during download process for IP $ip", e)
                    _downloadEvents.emit(DownloadEvent.Error("Error during download: ${e.message}"))
                }
            }
        }
    }

    fun cleanupP2P() {
        Log.i("VM", "Cleaning up P2P connection.")
        p2p.removeGroup { removed ->
            Log.i("VM", "P2P group removal status: $removed")
        }
        // p2p.cancelConnect() // cancelConnect is for stopping an ongoing connection attempt, removeGroup is for an established group.
    }

    // ViewModel's onCleared is called when the ViewModel is no longer used and will be destroyed.
    // This is a good place for cleanup, though viewModelScope handles coroutine cancellation automatically.
    override fun onCleared() {
        super.onCleared()
        Log.d("VM", "PictureVm onCleared")
        // scope.cancel() // Not needed if using viewModelScope
        // Consider if P2P cleanup should always happen here, or more explicitly managed by the UI lifecycle.
        // If PictureVm is tied to a Fragment/Activity, this cleanup might be desired.
        // cleanupP2P() // Uncomment if you want P2P to stop when ViewModel is cleared.
    }
}
