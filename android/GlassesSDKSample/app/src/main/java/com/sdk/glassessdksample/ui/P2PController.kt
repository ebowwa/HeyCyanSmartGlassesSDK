package com.sdk.glassessdksample.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class P2PController(
    private val ctx: Context,
    private val scope: CoroutineScope
) : WifiP2pManager.ConnectionInfoListener {

    private val mgr = ctx.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    private val channel = mgr.initialize(ctx, Looper.getMainLooper(), null)

    private val _connection = MutableStateFlow<WifiP2pInfo?>(null)
    val connection = _connection.asStateFlow()

    fun registerReceiver(): BroadcastReceiver {
        val f = IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        }
        val r = object : BroadcastReceiver() {
            override fun onReceive(c: Context, i: Intent) {
                when (i.action) {
                    WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                        Log.d("P2PController", "Connection changed action")
                        mgr.requestConnectionInfo(channel, this@P2PController)
                    }
                    WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                        Log.d("P2PController", "Peers changed action")
                        mgr.requestPeers(channel) { peers ->
                            Log.d("P2PController", "Peers available: ${peers?.deviceList?.joinToString { it.deviceName }}")
                            // Optional: list peers
                        }
                    }
                    WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                        val state = i.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                        Log.d("P2PController", "P2P State changed: ${if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) "Enabled" else "Disabled"}")
                    }
                    WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                        val deviceInfo = i.getParcelableExtra<android.net.wifi.p2p.WifiP2pDevice>(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)
                        Log.d("P2PController", "This device changed: ${deviceInfo?.deviceName} - ${deviceInfo?.status}")
                    }
                }
            }
        }
        ctx.registerReceiver(r, f, Context.RECEIVER_EXPORTED) // Use RECEIVER_EXPORTED for Android 12+ if targeting SDK 33+ and receiver is not for system only
        return r
    }

    /** Phone acts as GO (group owner) like in your log */
    fun createGroup(onReady: (Boolean) -> Unit) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            mgr.createGroup(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Log.i("P2PController", "createGroup onSuccess")
                    onReady(true)
                }
                override fun onFailure(reason: Int) {
                    Log.e("P2PController", "createGroup onFailure: $reason")
                    onReady(false)
                }
            })
        } else {
             mgr.createGroup(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Log.i("P2PController", "createGroup onSuccess (legacy)")
                    onReady(true)
                }
                override fun onFailure(reason: Int) {
                    Log.e("P2PController", "createGroup onFailure (legacy): $reason")
                    onReady(false)
                }
            })
        }
    }

    override fun onConnectionInfoAvailable(info: WifiP2pInfo) {
        Log.i("P2PController", "onConnectionInfoAvailable: groupFormed=${info.groupFormed}, isGroupOwner=${info.isGroupOwner}, goIp=${info.groupOwnerAddress?.hostAddress}")
        _connection.value = info
        if (info.groupFormed && info.isGroupOwner) {
            mgr.requestPeers(channel) { peers ->
                 Log.d("P2PController", "Peers available after connection: ${peers?.deviceList?.joinToString { it.deviceName }}")
                // Optional: list peers
            }
        }
    }

    fun removeGroup(onResult: (Boolean) -> Unit) {
        mgr.removeGroup(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.i("P2PController", "removeGroup onSuccess")
                _connection.value = null // Clear connection state
                onResult(true)
            }
            override fun onFailure(reason: Int) {
                Log.e("P2PController", "removeGroup onFailure: $reason")
                onResult(false)
            }
        })
    }


    fun cancelConnect() {
        mgr.cancelConnect(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.i("P2PController", "cancelConnect onSuccess")
            }
            override fun onFailure(reason: Int) {
                Log.e("P2PController", "cancelConnect onFailure: $reason")
            }
        })
    }
}