package com.sdk.glassessdksample.ui

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.oudmon.ble.base.bluetooth.BleAction
import com.oudmon.ble.base.bluetooth.BleBaseControl
import com.oudmon.ble.base.bluetooth.BleOperateManager
import com.oudmon.ble.base.communication.LargeDataHandler
import java.io.File
import kotlin.properties.Delegates

/**
 * @Author: Hzy
 * @CreateDate: 2021/6/25 11:50
 *
 * "程序应该是写给其他人读的,
 * 让机器来运行它只是一个附带功能"
 */
class MyApplication : Application(){

    var hardwareVersion: String = ""
    var firmwareVersion:String =""

    override fun onCreate() {
        super.onCreate()
        CONTEXT = applicationContext
        initBle()
    }
    private fun initBle() {
        initReceiver()
        val intentFilter = BleAction.getIntentFilter()
        val myBleReceiver = MyBluetoothReceiver()
        LocalBroadcastManager.getInstance(CONTEXT)
            .registerReceiver(myBleReceiver, intentFilter)
        BleBaseControl.getInstance(CONTEXT).setmContext(this)
    }

    private fun initReceiver() {
        LargeDataHandler.getInstance()
        BleOperateManager.getInstance(this)
        BleOperateManager.getInstance().setApplication(this)
        BleOperateManager.getInstance().init()
        val deviceFilter: IntentFilter = BleAction.getDeviceIntentFilter()
        val deviceReceiver = BluetoothReceiver()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(deviceReceiver, deviceFilter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(deviceReceiver, deviceFilter)
        }

    }

    fun getDeviceIntentFilter(): IntentFilter? {
        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        return intentFilter
    }

    fun getAppRootFile(context: Context): File {
        // /storage/emulated/0/Android/data/pack_name/files
        return if(context.getExternalFilesDir("")!=null){
            context.getExternalFilesDir("")!!
        }else{
            val externalSaveDir = context.externalCacheDir
            externalSaveDir ?: context.cacheDir
        }

    }


    companion object {
        private var application: Application? = null
        var CONTEXT: Context by Delegates.notNull()
        private lateinit var instance: MyApplication

        fun getApplication(): Application? {
            if (application == null) {
                throw RuntimeException("Not support calling this, before create app or after terminate app.")
            }
            return application
        }

        fun getInstance(): MyApplication {
            return instance
        }

        val getInstance: MyApplication by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            MyApplication()
        }
    }
}