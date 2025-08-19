package com.sdk.glassessdksample.ui.wifi.p2p

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log

class WifiP2pBroadcastReceiver(
    private val wifiP2pManagerSingleton: WifiP2pManagerSingleton
) : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    Log.d(TAG, "WiFi P2P is enabled")
                    wifiP2pManagerSingleton.onWifiP2pEnabled()
                } else {
                    Log.d(TAG, "WiFi P2P is disabled")
                    wifiP2pManagerSingleton.onWifiP2pDisabled()
                }
            }
            
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                Log.d(TAG, "Peers changed")
                // Request updated list of peers
                wifiP2pManagerSingleton.requestPeers()
            }
            
            WifiP2pManager.WIFI_P2P_CONNECTION_STATE_CHANGE_ACTION -> {
                val networkInfo: NetworkInfo? = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO)
                Log.d(TAG, "Connection state changed: ${networkInfo?.isConnected}")
                
                if (networkInfo?.isConnected == true) {
                    Log.d(TAG, "Connected to P2P device")
                    // Request connection info
                    wifiP2pManagerSingleton.requestConnectionInfo()
                } else {
                    Log.d(TAG, "Disconnected from P2P device")
                    wifiP2pManagerSingleton.onDisconnected()
                }
            }
            
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                val device: WifiP2pDevice? = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)
                device?.let {
                    Log.d(TAG, "This device changed: ${it.deviceName} - ${it.status}")
                    wifiP2pManagerSingleton.onThisDeviceChanged(it)
                }
            }
        }
    }
    
    companion object {
        private const val TAG = "WifiP2pBroadcastReceiver"
    }
} 