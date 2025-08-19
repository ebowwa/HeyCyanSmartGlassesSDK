package com.sdk.glassessdksample

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.oudmon.ble.base.bluetooth.BleOperateManager
import com.oudmon.ble.base.bluetooth.DeviceManager
import com.oudmon.ble.base.communication.LargeDataHandler
import com.oudmon.ble.base.communication.bigData.resp.GlassesDeviceNotifyListener
import com.oudmon.ble.base.communication.bigData.resp.GlassesDeviceNotifyRsp
import com.sdk.glassessdksample.databinding.AcitivytMainBinding
import com.sdk.glassessdksample.ui.BluetoothUtils
import com.sdk.glassessdksample.ui.DeviceBindActivity
import com.sdk.glassessdksample.ui.hasBluetooth
import com.sdk.glassessdksample.ui.requestAllPermission
import com.sdk.glassessdksample.ui.requestBluetoothPermission
import com.sdk.glassessdksample.ui.requestLocationPermission
import com.sdk.glassessdksample.ui.requestNearbyWifiDevicesPermission
import com.sdk.glassessdksample.ui.setOnClickListener
import com.sdk.glassessdksample.ui.startKtxActivity
import com.sdk.glassessdksample.ui.P2PController
import com.sdk.glassessdksample.ui.wifi.p2p.WifiP2pManagerSingleton
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import org.greenrobot.eventbus.EventBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {
    private lateinit var binding: AcitivytMainBinding
    private val deviceNotifyListener by lazy { MyDeviceNotifyListener() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AcitivytMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }
    inner class PermissionCallback : OnPermissionCallback {
        override fun onGranted(permissions: MutableList<String>, all: Boolean) {
            if (!all) {

            }else{
                startKtxActivity<DeviceBindActivity>()
            }
        }

        override fun onDenied(permissions: MutableList<String>, never: Boolean) {
            super.onDenied(permissions, never)
            if(never){
                XXPermissions.startPermissionActivity(this@MainActivity, permissions);
            }
        }

    }


    override fun onResume() {
        super.onResume()
        try {
            if (!BluetoothUtils.isEnabledBluetooth(this)) {
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                }
                startActivityForResult(intent, 300)
            }
        } catch (e: Exception) {
        }
        if (!hasBluetooth(this)) {
            requestBluetoothPermission(this, BluetoothPermissionCallback())
        }

        requestAllPermission(this, OnPermissionCallback { permissions, all ->  })
    }

    inner class BluetoothPermissionCallback : OnPermissionCallback {
        override fun onGranted(permissions: MutableList<String>, all: Boolean) {
            if (!all) {

            }
        }

        override fun onDenied(permissions: MutableList<String>, never: Boolean) {
            super.onDenied(permissions, never)
            if (never) {
                XXPermissions.startPermissionActivity(this@MainActivity, permissions)
            }
        }

    }

    private fun initView() {
        setOnClickListener(
            binding.btnScan,
            binding.btnConnect,
            binding.btnDisconnect,
            binding.btnAddListener,
            binding.btnSetTime,
            binding.btnVersion,
            binding.btnCamera,
            binding.btnVideo,
            binding.btnRecord,
            binding.btnThumbnail,
            binding.btnBt,
            binding.btnBattery,
            binding.btnVolume,
            binding.btnMediaCount,
            binding.btnDataDownload
        ) {
            when (this) {
                binding.btnScan -> {
                    requestLocationPermission(this@MainActivity, PermissionCallback())
                }

                binding.btnConnect -> {
                    BleOperateManager.getInstance()
                        .connectDirectly(DeviceManager.getInstance().deviceAddress)
                }

                binding.btnDisconnect -> {
                    BleOperateManager.getInstance().unBindDevice()
                }

                binding.btnAddListener -> {
                    LargeDataHandler.getInstance().addOutDeviceListener(100, deviceNotifyListener)
                }

                binding.btnSetTime -> {
                    Log.i("setTime", "setTime"+BleOperateManager.getInstance().isConnected)
                    LargeDataHandler.getInstance().syncTime { _, _ -> }
                }

                binding.btnVersion -> {
                    LargeDataHandler.getInstance().syncDeviceInfo { _, response ->
                        if (response != null) {
                            //wifi 固件版本
                             response.wifiFirmwareVersion
                            //wifi 产品版本
                            response.wifiHardwareVersion
                            //蓝牙产品版本
                             response.hardwareVersion
                            //蓝牙固件版本
                             response.firmwareVersion
                        }
                    }
                }

                binding.btnCamera -> {
                    LargeDataHandler.getInstance().glassesControl(
                        byteArrayOf(0x02, 0x01, 0x01)
                    ) { _, it ->
                        if (it.dataType == 1 && it.errorCode == 0) {
                            when (it.workTypeIng) {
                                2 -> {
                                    //眼镜正在录像
                                }
                                4 -> {
                                    //眼镜正在传输模式
                                }
                                5 -> {
                                    //眼镜正在OTA模式
                                }
                                1, 6 ->{
                                    //眼镜正在拍照模式
                                }
                                7 -> {
                                    //眼镜正在AI对话
                                }
                                8 ->{
                                    //眼镜正在录音模式
                                }
                            }
                        } else {
                            //执行开始和结束
                        }
                    }
                }

                binding.btnVideo -> {
                    //videoStart  true 开始录制   false 停止录制
                    val videoStart=true
                    val value = if (videoStart) 0x02 else 0x03
                    LargeDataHandler.getInstance().glassesControl(
                        byteArrayOf(0x02, 0x01, value.toByte())
                    ) { _, it ->
                        if (it.dataType == 1) {
                            if (it.errorCode == 0) {
                                when (it.workTypeIng) {
                                    2 -> {
                                        //眼镜正在录像
                                    }
                                    4 -> {
                                        //眼镜正在传输模式
                                    }
                                    5 -> {
                                        //眼镜正在OTA模式
                                    }
                                    1, 6 ->{
                                        //眼镜正在拍照模式
                                    }
                                    7 -> {
                                        //眼镜正在AI对话
                                    }
                                    8 ->{
                                        //眼镜正在录音模式
                                    }
                                }
                            } else {
                                //执行开始和结束
                            }
                        }
                    }
                }

                binding.btnRecord -> {
                    //recordStart  true 开始录制   false 停止录制
                    val recordStart=true
                    val value = if (recordStart) 0x08 else 0x0c
                    LargeDataHandler.getInstance().glassesControl(
                        byteArrayOf(0x02, 0x01, value.toByte())
                    ) { _, it ->
                        if (it.dataType == 1) {
                            if (it.errorCode == 0) {
                                when (it.workTypeIng) {
                                    2 -> {
                                        //眼镜正在录像
                                    }
                                    4 -> {
                                        //眼镜正在传输模式
                                    }
                                    5 -> {
                                        //眼镜正在OTA模式
                                    }
                                    1, 6 ->{
                                        //眼镜正在拍照模式
                                    }
                                    7 -> {
                                        //眼镜正在AI对话
                                    }
                                    8 ->{
                                        //眼镜正在录音模式
                                    }
                                }
                            } else {
                                //执行开始和结束
                            }
                        }
                    }
                }

                binding.btnThumbnail -> {
                    //thumbnailSize  0..6
                    val thumbnailSize=0x02
                    LargeDataHandler.getInstance().glassesControl(
                        byteArrayOf(
                            0x02,
                            0x01,
                            0x06,
                            thumbnailSize.toByte(),
                            thumbnailSize.toByte(),
                            0x02
                        )
                    ) { _, it ->
                        if (it.dataType == 1) {
                            if (it.errorCode == 0) {
                                when (it.workTypeIng) {
                                    2 -> {
                                        //眼镜正在录像
                                    }
                                    4 -> {
                                        //眼镜正在传输模式
                                    }
                                    5 -> {
                                        //眼镜正在OTA模式
                                    }
                                    1, 6 ->{
                                        //眼镜正在拍照模式
                                    }
                                    7 -> {
                                        //眼镜正在AI对话
                                    }
                                    8 ->{
                                        //眼镜正在录音模式
                                    }
                                }
                            } else {
                                //触发AI拍照，上报缩略图会收到上报指令
                            }
                        }
                    }
                }

                binding.btnBt -> {
                    //BT扫描
                    BleOperateManager.getInstance().classicBluetoothStartScan()

                }
                binding.btnBattery -> {
                    //添加电量监听
                    LargeDataHandler.getInstance().addBatteryCallBack("init") { _, response ->

                    }
                    //电量
                    LargeDataHandler.getInstance().syncBattery()
                }
                binding.btnVolume ->{
                    //读取音量控制
                    LargeDataHandler.getInstance().getVolumeControl { _, response ->
                        if (response != null) {
                            //眼镜音量 音乐最小值 最大值 当前值
                            response.minVolumeMusic
                            response.maxVolumeMusic
                            response.currVolumeMusic
                            //眼镜电话 电话最小值 最大值 当前值
                            response.minVolumeCall
                            response.maxVolumeCall
                            response.currVolumeCall
                            //眼镜系统 系统最小值 最大值 当前值
                            response.minVolumeSystem
                            response.maxVolumeSystem
                            response.currVolumeSystem
                            //眼镜当前的模式
                            response.currVolumeType
                        }
                    }
                }
                binding.btnMediaCount ->{
                    LargeDataHandler.getInstance().glassesControl(byteArrayOf(0x02, 0x04)) { _, it ->
                        if (it.dataType == 4) {
                            val mediaCount = it.imageCount + it.videoCount + it.recordCount
                            if (mediaCount > 0) {
                                //眼镜有多少个媒体没有上传
                            } else {
                                //无
                            }
                        }
                    }
                }
                binding.btnDataDownload -> {
                    // 检查并请求必要的权限
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        // Android 13+ 需要 NEARBY_WIFI_DEVICES 权限
                        requestNearbyWifiDevicesPermission(this@MainActivity, object : OnPermissionCallback {
                            override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                                if (all) {
                                    // 启动BLE+WiFi P2P数据下载
                                    startDataDownload()
                                }
                            }

                            override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                                super.onDenied(permissions, never)
                                if (never) {
                                    XXPermissions.startPermissionActivity(this@MainActivity, permissions)
                                }
                            }
                        })
                    } else {
                        // Android 12 及以下版本直接启动下载
                        startDataDownload()
                    }
                }
            }
        }
    }

    private fun startDataDownload() {
        Log.i("DataDownload", "Starting BLE+WiFi P2P data download...")
        
        // 检查蓝牙连接状态
        if (!BleOperateManager.getInstance().isConnected) {
            Log.e("DataDownload", "Bluetooth not connected. Please connect to glasses first.")
            return
        }
        
        // 检查必要的权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!XXPermissions.isGranted(this, "android.permission.NEARBY_WIFI_DEVICES")) {
                Log.e("DataDownload", "NEARBY_WIFI_DEVICES permission not granted")
                return
            }
        }
        
        // 启动P2P连接和数据下载
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 1. 通过BLE获取眼镜的IP地址
                val deviceIp = getDeviceIpFromBLE()
                if (deviceIp.isNullOrEmpty()) {
                    Log.e("DataDownload", "Failed to get device IP from BLE")
                    return@launch
                }
                
                Log.i("DataDownload", "Device IP from BLE: $deviceIp")
                
                // 2. 建立WiFi P2P连接 - 使用新的WifiP2pManagerSingleton
                val wifiP2pManager = WifiP2pManagerSingleton.getInstance(this@MainActivity)
                val receiver = wifiP2pManager.registerReceiver()
                
                try {
                    // 添加回调监听器
                    wifiP2pManager.addCallback(object : WifiP2pManagerSingleton.WifiP2pCallback {
                        override fun onWifiP2pEnabled() {
                            Log.i("DataDownload", "WiFi P2P enabled, creating P2P group...")
                            // 创建P2P组（手机作为GO）
                            wifiP2pManager.createGroup { success ->
                                if (success) {
                                    Log.i("DataDownload", "P2P group created successfully")
                                    // 等待P2P连接完全建立
                                    CoroutineScope(Dispatchers.IO).launch {
                                        delay(2000) // 等待2秒让连接稳定
                                        
                                        // 测试连接是否可用
                                        if (testConnection(deviceIp)) {
                                            Log.i("DataDownload", "Connection test successful, starting downloads...")
                                            
                                            // 3. 下载媒体文件列表
                                            downloadMediaList(deviceIp)
                                        } else {
                                            Log.e("DataDownload", "Connection test failed, cannot reach device")
                                            withContext(Dispatchers.Main) {
                                                showDownloadError("Cannot connect to glasses device. Please check P2P connection.")
                                            }
                                        }
                                    }
                                } else {
                                    Log.e("DataDownload", "Failed to create P2P group")
                                    withContext(Dispatchers.Main) {
                                        showDownloadError("Failed to create P2P group")
                                    }
                                }
                            }
                        }
                        
                        override fun onWifiP2pDisabled() {
                            Log.e("DataDownload", "WiFi P2P disabled")
                        }
                        
                        override fun onPeersChanged(peers: Collection<WifiP2pDevice>) {
                            Log.i("DataDownload", "Found ${peers.size} P2P devices")
                        }
                        
                        override fun onThisDeviceChanged(device: WifiP2pDevice) {
                            Log.i("DataDownload", "This device changed: ${device.deviceName} - ${device.status}")
                        }
                        
                        override fun onConnected(info: WifiP2pInfo) {
                            Log.i("DataDownload", "P2P connected: groupFormed=${info.groupFormed}, isGroupOwner=${info.isGroupOwner}")
                        }
                        
                        override fun onDisconnected() {
                            Log.i("DataDownload", "P2P disconnected")
                        }
                        
                        override fun onPeerDiscoveryStarted() {
                            Log.i("DataDownload", "Peer discovery started")
                        }
                        
                        override fun onPeerDiscoveryFailed(reason: Int) {
                            Log.e("DataDownload", "Peer discovery failed: $reason")
                        }
                        
                        override fun onConnectRequestSent() {
                            Log.i("DataDownload", "Connect request sent")
                        }
                        
                        override fun onConnectRequestFailed(reason: Int) {
                            Log.e("DataDownload", "Connect request failed: $reason")
                        }
                        
                        override fun connecting() {
                            Log.i("DataDownload", "Connecting to P2P device...")
                        }
                        
                        override fun cancelConnect() {
                            Log.i("DataDownload", "P2P connection cancelled")
                        }
                        
                        override fun cancelConnectFail(reason: Int) {
                            Log.e("DataDownload", "Cancel connect failed: $reason")
                        }
                        
                        override fun retryAlsoFailed() {
                            Log.e("DataDownload", "P2P connection retry failed")
                        }
                    })
                    
                } finally {
                    // 清理P2P连接
                    wifiP2pManager.removeGroup { success ->
                        Log.i("DataDownload", "P2P group removed: $success")
                    }
                    wifiP2pManager.unregisterReceiver(receiver)
                }
                
            } catch (e: Exception) {
                Log.e("DataDownload", "Error during data download: ${e.message}", e)
            }
        }
    }
    
    private fun getDeviceIpFromBLE(): String? {
        // 这里应该通过BLE特征值读取获取眼镜的IP地址
        // 根据你的日志，眼镜会通过BLE上报IP地址
        // 暂时返回一个示例IP，实际应该从BLE数据中解析
        return "192.168.49.79"
    }
    
    private fun downloadMediaList(deviceIp: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = "http://$deviceIp/files/media.config"
                Log.i("DataDownload", "Downloading media list from: $url")
                
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 30000
                
                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val content = inputStream.bufferedReader().use { it.readText() }
                    
                    // 显示下载的内容
                    Log.i("DataDownload", "=== MEDIA CONFIG CONTENT ===")
                    Log.i("DataDownload", content)
                    Log.i("DataDownload", "=== END MEDIA CONFIG ===")
                    
                    // 解析媒体文件列表
                    parseMediaList(content)
                    
                    withContext(Dispatchers.Main) {
                        showDownloadSuccess("Media list downloaded successfully")
                    }
                } else {
                    Log.e("DataDownload", "Failed to download media list. Response code: ${connection.responseCode}")
                    withContext(Dispatchers.Main) {
                        showDownloadError("Failed to download media list. Response code: ${connection.responseCode}")
                    }
                }
                
                connection.disconnect()
            } catch (e: Exception) {
                Log.e("DataDownload", "Error downloading media list: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    when (e) {
                        is java.io.IOException -> {
                            if (e.message?.contains("Cleartext HTTP traffic") == true) {
                                showDownloadError("Network security blocked HTTP connection. Please check app settings.")
                            } else if (e.message?.contains("Failed to connect") == true) {
                                showDownloadError("Cannot connect to glasses device. Please ensure P2P connection is established.")
                            } else {
                                showDownloadError("Network error: ${e.message}")
                            }
                        }
                        else -> showDownloadError("Download failed: ${e.message}")
                    }
                }
            }
        }
    }
    
    private fun parseMediaList(content: String) {
        // 解析媒体配置文件内容 - 这是一个包含JPG文件名的文本文件
        Log.i("DataDownload", "Parsing media list content...")
        
        try {
            // 按行分割，每行应该是一个文件名
            val lines = content.trim().split("\n")
            val jpgFiles = mutableListOf<String>()
            
            lines.forEach { line ->
                val trimmedLine = line.trim()
                if (trimmedLine.isNotEmpty()) {
                    // 检查是否是JPG文件
                    if (trimmedLine.endsWith(".jpg", ignoreCase = true) || 
                        trimmedLine.endsWith(".jpeg", ignoreCase = true)) {
                        jpgFiles.add(trimmedLine)
                        Log.i("DataDownload", "Found JPG file: $trimmedLine")
                    } else {
                        Log.i("DataDownload", "Found non-JPG file: $trimmedLine")
                    }
                }
            }
            
            Log.i("DataDownload", "Total JPG files found: ${jpgFiles.size}")
            
            if (jpgFiles.isNotEmpty()) {
                // 开始下载所有JPG文件
                downloadAllJpgFiles(jpgFiles)
            } else {
                Log.w("DataDownload", "No JPG files found in media.config")
                withContext(Dispatchers.Main) {
                    showDownloadError("No JPG files found in media.config")
                }
            }
            
        } catch (e: Exception) {
            Log.e("DataDownload", "Error parsing media list: ${e.message}", e)
            withContext(Dispatchers.Main) {
                showDownloadError("Failed to parse media list: ${e.message}")
            }
        }
    }
    
    private fun downloadAllJpgFiles(jpgFiles: List<String>) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.i("DataDownload", "Starting download of ${jpgFiles.size} JPG files...")
            
            var successCount = 0
            var failCount = 0
            
            for ((index, fileName) in jpgFiles.withIndex()) {
                try {
                    Log.i("DataDownload", "Downloading file ${index + 1}/${jpgFiles.size}: $fileName")
                    
                    val success = downloadSingleJpgFile(fileName)
                    if (success) {
                        successCount++
                        Log.i("DataDownload", "✓ Successfully downloaded: $fileName")
                    } else {
                        failCount++
                        Log.e("DataDownload", "✗ Failed to download: $fileName")
                    }
                    
                    // 添加小延迟避免过快请求
                    delay(500)
                    
                } catch (e: Exception) {
                    failCount++
                    Log.e("DataDownload", "Error downloading $fileName: ${e.message}", e)
                }
            }
            
            // 显示最终结果
            val message = "Download completed: $successCount successful, $failCount failed"
            Log.i("DataDownload", message)
            
            withContext(Dispatchers.Main) {
                if (failCount == 0) {
                    showDownloadSuccess("All $successCount files downloaded successfully!")
                } else {
                    showDownloadError("Download completed with errors: $successCount successful, $failCount failed")
                }
            }
        }
    }
    
    private suspend fun downloadSingleJpgFile(fileName: String): Boolean {
        return try {
            val deviceIp = getDeviceIpFromBLE() ?: return false
            val url = "http://$deviceIp/files/$fileName"
            Log.i("DataDownload", "Downloading: $url")
            
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 30000
            
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream
                val file = File(getExternalFilesDir("DCIM"), fileName)
                val outputStream = FileOutputStream(file)
                
                val buffer = ByteArray(8192)
                var bytesRead: Int
                var totalBytes = 0L
                
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                    totalBytes += bytesRead
                }
                
                outputStream.close()
                inputStream.close()
                
                Log.i("DataDownload", "File downloaded: $fileName (${totalBytes} bytes)")
                
                // 保存到相册
                saveToAlbum(file, fileName)
                
                true
            } else {
                Log.e("DataDownload", "Failed to download $fileName. Response code: ${connection.responseCode}")
                false
            }
            
        } catch (e: Exception) {
            Log.e("DataDownload", "Error downloading $fileName: ${e.message}", e)
            false
        }
    }
    
    private fun saveToAlbum(file: File, fileName: String) {
        try {
            // 保存文件信息到相册数据库
            val albumInfo = mapOf(
                "fileName" to fileName,
                "filePath" to file.absolutePath,
                "fileDate" to "2025-08-18",
                "fileType" to 1,
                "timestamp" to System.currentTimeMillis(),
                "mac" to "71:33:1D:2C:CF:A0"
            )
            
            Log.i("DataDownload", "Album info: $albumInfo")
            // TODO: 实现保存到相册数据库的逻辑
            
        } catch (e: Exception) {
            Log.e("DataDownload", "Error saving to album: ${e.message}", e)
        }
    }
    
    private fun showDownloadSuccess(message: String) {
        // Show success message to user
        Log.i("DataDownload", "SUCCESS: $message")
        // You can implement a Toast or Snackbar here
        // Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    
    private fun showDownloadError(message: String) {
        // Show error message to user
        Log.e("DataDownload", "ERROR: $message")
        // You can implement a Toast or Snackbar here
        // Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun testConnection(deviceIp: String): Boolean {
        Log.i("DataDownload", "Testing connection to $deviceIp...")
        try {
            // 尝试连接到实际的媒体配置文件
            val url = URL("http://$deviceIp/files/media.config")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000 // 连接超时
            connection.readTimeout = 5000 // 读取超时
            
            val responseCode = connection.responseCode
            Log.i("DataDownload", "Connection test response code: $responseCode")
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 尝试读取一小部分内容来确认连接可用
                val inputStream = connection.inputStream
                val buffer = ByteArray(1024)
                val bytesRead = inputStream.read(buffer)
                inputStream.close()
                
                Log.i("DataDownload", "Connection test successful - read $bytesRead bytes")
                return true
            }
            
            return false
        } catch (e: Exception) {
            Log.e("DataDownload", "Connection test failed: ${e.message}", e)
            return false
        }
    }

    inner class MyDeviceNotifyListener : GlassesDeviceNotifyListener() {

        @RequiresApi(Build.VERSION_CODES.O)
        override fun parseData(cmdType: Int, response: GlassesDeviceNotifyRsp) {
            when (response.loadData[6].toInt()) {
                //眼镜电量上报
                0x05 -> {
                    //当前电量
                    val battery = response.loadData[7].toInt()
                    //是否在充电
                    val changing = response.loadData[8].toInt()
                }
                //眼镜通过快捷识别
                0x02 -> {
                    if (response.loadData.size > 9 && response.loadData[9].toInt() == 0x02) {
                        //要设置识别意图：eg 请帮我看看眼前是什么，图片中的内容
                    }
                    //获取图片缩略图
                    LargeDataHandler.getInstance().getPictureThumbnails { cmdType, success, data ->
                        //请将data存入路径,jpg的图片
                    }
                }

                0x03 -> {
                    if (response.loadData[7].toInt() == 1) {
                        //眼镜启动麦克风开始说话
                    }
                }
                //ota 升级
                0x04 -> {
                    try {
                        val download = response.loadData[7].toInt()
                        val soc = response.loadData[8].toInt()
                        val nor = response.loadData[9].toInt()
                        //download 固件下载进度 soc 下载进度 nor 升级进度
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                0x0c -> {
                    //眼镜触发暂停事件，语音播报
                    if (response.loadData[7].toInt() == 1) {
                        //to do
                    }
                }

                0x0d -> {
                    //解除APP绑定事件
                    if (response.loadData[7].toInt() == 1) {
                        //to do
                    }
                }
                //眼镜内存不足事件
                0x0e -> {

                }
                //翻译暂停事件
                0x10 -> {

                }
                //眼镜音量变化事件
                0x12 -> {
                    //音乐音量
                    //最小音量
                    response.loadData[8].toInt()
                    //最大音量
                    response.loadData[9].toInt()
                    //当前音量
                    response.loadData[10].toInt()

                    //来电音量
                    //最小音量
                    response.loadData[12].toInt()
                    //最大音量
                    response.loadData[13].toInt()
                    //当前音量
                    response.loadData[14].toInt()

                    //眼镜系统音量
                    //最小音量
                    response.loadData[16].toInt()
                    //最大音量
                    response.loadData[17].toInt()
                    //当前音量
                    response.loadData[18].toInt()

                    //当前的音量模式
                    response.loadData[19].toInt()

                }
            }
        }
    }
}