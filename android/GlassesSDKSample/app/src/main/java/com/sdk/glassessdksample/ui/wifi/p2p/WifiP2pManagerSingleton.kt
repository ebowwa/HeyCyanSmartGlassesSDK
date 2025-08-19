package com.sdk.glassessdksample.ui.wifi.p2p

import android.content.Context
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.util.concurrent.CopyOnWriteArrayList

class WifiP2pManagerSingleton private constructor(private val context: Context) {
    
    companion object {
        @Volatile
        private var instance: WifiP2pManagerSingleton? = null
        
        fun getInstance(context: Context): WifiP2pManagerSingleton {
            return instance ?: synchronized(this) {
                instance ?: WifiP2pManagerSingleton(context).also { instance = it }
            }
        }
    }
    
    private val wifiP2pManager: WifiP2pManager = context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    private var wifiP2pChannel: WifiP2pManager.Channel? = null
    private var wifiP2pDevice: WifiP2pDevice? = null
    private val handler = Handler(Looper.getMainLooper())
    private val callbacks = CopyOnWriteArrayList<WifiP2pCallback>()
    
    private var connected = false
    private var connecting = false
    private var connectRetry = 0
    private var discoveryRetry = 0
    
    private val intentFilter = IntentFilter().apply {
        addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_CONNECTION_STATE_CHANGE_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
    }
    
    init {
        Log.d(TAG, "WifiP2pManagerSingleton initialized")
        initP2P()
    }
    
    private fun initP2P() {
        Log.d(TAG, "Initializing P2P...")
        wifiP2pChannel?.close()
        wifiP2pChannel = wifiP2pManager.initialize(context, Looper.getMainLooper(), object : WifiP2pManager.ChannelListener {
            override fun onChannelDisconnected() {
                Log.d(TAG, "wifiP2pChannel disconnect")
            }
        })
        Log.d(TAG, "P2P initialized, channel: ${wifiP2pChannel != null}")
    }
    
    fun addCallback(callback: WifiP2pCallback) {
        if (!callbacks.contains(callback)) {
            callbacks.add(callback)
        }
    }
    
    fun removeCallback(callback: WifiP2pCallback) {
        callbacks.remove(callback)
    }
    
    fun registerReceiver(): BroadcastReceiver {
        val receiver = WifiP2pBroadcastReceiver(this)
        context.registerReceiver(receiver, intentFilter, Context.RECEIVER_EXPORTED)
        return receiver
    }
    
    fun unregisterReceiver(receiver: BroadcastReceiver) {
        try {
            context.unregisterReceiver(receiver)
        } catch (e: Exception) {
            Log.e(TAG, "Error unregistering receiver", e)
        }
    }
    
    fun startPeerDiscovery() {
        handler.postDelayed(discoveryTimeOut, 16000L)
        wifiP2pManager.discoverPeers(wifiP2pChannel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d(TAG, "Peer discovery started successfully")
                callbacks.forEach { it.onPeerDiscoveryStarted() }
            }
            
            override fun onFailure(reason: Int) {
                Log.e(TAG, "Peer discovery failed: $reason")
                callbacks.forEach { it.onPeerDiscoveryFailed(reason) }
            }
        })
    }
    
    fun connectToDevice(device: WifiP2pDevice) {
        if (connecting) {
            Log.d(TAG, "P2P正在连接,不调用连接返回")
            callbacks.forEach { it.connecting() }
            return
        }
        
        if (connected) {
            Log.d(TAG, "P2P已经连接上了，直接返回")
            return
        }
        
        wifiP2pDevice = device
        val config = WifiP2pConfig().apply {
            deviceAddress = device.deviceAddress
            groupOwnerIntent = 0
        }
        
        connecting = true
        Log.d(TAG, "已经在连接设备: ${device.deviceName}")
        
        wifiP2pManager.connect(wifiP2pChannel, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d(TAG, "Connect request sent successfully")
                callbacks.forEach { it.onConnectRequestSent() }
            }
            
            override fun onFailure(reason: Int) {
                Log.e(TAG, "Connect request failed: $reason")
                connecting = false
                callbacks.forEach { it.onConnectRequestFailed(reason) }
            }
        })
    }
    
    fun cancelP2pConnection() {
        try {
            initP2P()
            wifiP2pManager.cancelConnect(wifiP2pChannel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Log.d(TAG, "Cancel connect successful")
                    callbacks.forEach { it.cancelConnect() }
                }
                
                override fun onFailure(reason: Int) {
                    Log.e(TAG, "Cancel connect failed: $reason")
                    callbacks.forEach { it.cancelConnectFail(reason) }
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Error canceling P2P connection", e)
        }
    }
    
    fun resetDeviceP2p() {
        // Simplified for sample app - just log the action
        Log.d(TAG, "resetDeviceP2p called")
    }
    
    fun resetFailCount() {
        connectRetry = 0
        discoveryRetry = 0
    }
    
    fun resetPeerDiscovery() {
        discoveryRetry = 0
    }
    
    fun setConnect(connected: Boolean) {
        this.connected = connected
    }
    
    fun requestPeers() {
        wifiP2pChannel?.let { channel ->
            wifiP2pManager.requestPeers(channel, object : WifiP2pManager.PeerListListener {
                override fun onPeersAvailable(peers: WifiP2pDeviceList) {
                    val deviceList = peers.deviceList
                    Log.d(TAG, "Peers available: ${deviceList.size} devices")
                    callbacks.forEach { it.onPeersChanged(deviceList) }
                }
            })
        }
    }
    
    fun requestConnectionInfo() {
        wifiP2pChannel?.let { channel ->
            wifiP2pManager.requestConnectionInfo(channel, object : WifiP2pManager.ConnectionInfoListener {
                override fun onConnectionInfoAvailable(info: WifiP2pInfo) {
                    Log.d(TAG, "Connection info available: groupFormed=${info.groupFormed}, isGroupOwner=${info.isGroupOwner}")
                    onConnectionInfoAvailable(info)
                }
            })
        }
    }
    
    fun createGroup(onResult: (Boolean) -> Unit) {
        wifiP2pChannel?.let { channel ->
            wifiP2pManager.createGroup(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Log.d(TAG, "P2P group created successfully")
                    onResult(true)
                }
                
                override fun onFailure(reason: Int) {
                    Log.e(TAG, "Failed to create P2P group: $reason")
                    onResult(false)
                }
            })
        } ?: run {
            Log.e(TAG, "P2P channel not initialized")
            onResult(false)
        }
    }
    
    fun removeGroup(onResult: (Boolean) -> Unit) {
        wifiP2pChannel?.let { channel ->
            wifiP2pManager.removeGroup(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Log.d(TAG, "P2P group removed successfully")
                    onResult(true)
                }
                
                override fun onFailure(reason: Int) {
                    Log.e(TAG, "Failed to remove P2P group: $reason")
                    onResult(false)
                }
            })
        } ?: run {
            Log.e(TAG, "P2P channel not initialized")
            onResult(false)
        }
    }
    
    // Internal methods for handling P2P events
    internal fun onWifiP2pEnabled() {
        callbacks.forEach { it.onWifiP2pEnabled() }
    }
    
    internal fun onWifiP2pDisabled() {
        callbacks.forEach { it.onWifiP2pDisabled() }
    }
    
    internal fun onPeersChanged(peers: Collection<WifiP2pDevice>) {
        callbacks.forEach { it.onPeersChanged(peers) }
    }
    
    internal fun onThisDeviceChanged(device: WifiP2pDevice) {
        callbacks.forEach { it.onThisDeviceChanged(device) }
    }
    
    internal fun onConnectionInfoAvailable(info: WifiP2pInfo) {
        connecting = false
        connected = info.groupFormed
        callbacks.forEach { it.onConnected(info) }
    }
    
    internal fun onDisconnected() {
        connecting = false
        connected = false
        callbacks.forEach { it.onDisconnected() }
    }
    
    // Timeout handlers
    private val discoveryTimeOut = object : Runnable {
        override fun run() {
            Log.d(TAG, "内部扫描重试连接: $discoveryRetry")
            if (discoveryRetry < 1) {
                Log.d(TAG, "内部扫描重试连接一次")
                resetDeviceP2p()
                initP2P()
                startPeerDiscovery()
                discoveryRetry++
            }
        }
    }
    
    private val connectTimeOut = object : Runnable {
        override fun run() {
            connecting = false
            if (connectRetry < 1) {
                wifiP2pDevice?.let { device ->
                    Log.d(TAG, "内部连接重试连接一次")
                    connectToDevice(device)
                }
                connectRetry++
            } else {
                Log.d(TAG, "不重连，等外部超时")
                callbacks.forEach { it.retryAlsoFailed() }
            }
        }
    }
    
    interface WifiP2pCallback {
        fun onWifiP2pEnabled()
        fun onWifiP2pDisabled()
        fun onPeersChanged(peers: Collection<WifiP2pDevice>)
        fun onThisDeviceChanged(device: WifiP2pDevice)
        fun onConnected(info: WifiP2pInfo)
        fun onDisconnected()
        fun onPeerDiscoveryStarted()
        fun onPeerDiscoveryFailed(reason: Int)
        fun onConnectRequestSent()
        fun onConnectRequestFailed(reason: Int)
        fun connecting()
        fun cancelConnect()
        fun cancelConnectFail(reason: Int)
        fun retryAlsoFailed()
    }
    
    companion object {
        private const val TAG = "WifiP2pManagerSingleton"
    }
} 