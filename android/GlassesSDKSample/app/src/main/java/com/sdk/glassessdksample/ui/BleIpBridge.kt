package com.sdk.glassessdksample.ui

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.nio.charset.StandardCharsets

class BleIpBridge {
    private val _ip = MutableStateFlow<String?>(null)
    val ip = _ip.asStateFlow()

    //This is a placeholder. You'll need to integrate this with your actual BLE characteristic change notifications.
    fun onCharacteristicChanged(value: ByteArray) {
        // Example: payload like "ip:192.168.49.79" or raw bytes; adjust parsing as needed
        val msg = value.toString(StandardCharsets.UTF_8)
        Log.d("BleIpBridge", "Received BLE message: $msg")

        val logMessage = """
        ╔═══════════════════════════════════════════════════════════════════════════════════════════════════
        ║Thread: ${Thread.currentThread().name}
        ╟───────────────────────────────────────────────────────────────────────────────────────────────────
        ║	─ com.sdk.glassessdksample.ui.BleIpBridge.onCharacteristicChanged(BleIpBridge.kt:XX) <XX will be the line number>
        ╟───────────────────────────────────────────────────────────────────────────────────────────────────
        ║Received BLE message: $msg
        ╚═══════════════════════════════════════════════════════════════════════════════════════════════════
        """.trimIndent()
        Log.i("Glass", logMessage)

        // Regex to find an IPv4 address. 
        // This regex is a common one but might need adjustment based on the exact format of your BLE message.
        Regex("""(\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\b)""").find(msg)?.value?.let { foundIp ->
            Log.i("BLE", "Got device IP via BLE: $foundIp")
            _ip.value = foundIp
        }
    }
}