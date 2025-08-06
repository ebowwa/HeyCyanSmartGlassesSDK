package com.sdk.glassessdksample.ui
import android.bluetooth.BluetoothDevice
import android.nfc.Tag
import android.os.UserManager
import android.util.Log
import com.oudmon.ble.base.bluetooth.BleOperateManager
import com.oudmon.ble.base.bluetooth.DeviceManager
import com.oudmon.ble.base.bluetooth.QCBluetoothCallbackCloneReceiver
import com.oudmon.ble.base.communication.Constants
import com.oudmon.ble.base.communication.LargeDataHandler
import org.greenrobot.eventbus.EventBus

/**
 * @author hzy ,
 * @date  2021/1/15
 * <p>
 * "程序应该是写给其他人读的,
 * 让机器来运行它只是一个附带功能"
 **/
class MyBluetoothReceiver : QCBluetoothCallbackCloneReceiver() {
    override fun connectStatue(device: BluetoothDevice?, connected: Boolean) {
        Log.e("connectStatue","---connectStatue")
        if(device !=null && connected){
            if(device.name!=null){
                DeviceManager.getInstance().deviceName=device.name
            }
        }else{
            EventBus.getDefault().post(BluetoothEvent(false))
        }
    }

    override fun onServiceDiscovered() {
        //do init
        LargeDataHandler.getInstance().initEnable()
        // 必须收到回调才可以下发其它指令
        // eg. 设置时间.同步设置项等等
        EventBus.getDefault().post(BluetoothEvent(true))
        Log.e("onServiceDiscovered","---onServiceDiscovered")
        BleOperateManager.getInstance().isReady=true
    }

    override fun onCharacteristicChange(address: String?, uuid: String?, data: ByteArray?) {
    }

    override fun onCharacteristicRead(uuid: String?, data: ByteArray?) {
        if (uuid != null && data != null) {
            val version = String(data, Charsets.UTF_8)
            when(uuid){
                Constants.CHAR_FIRMWARE_REVISION.toString() -> {
                    Log.e("rom----",version)
                    //rom  version
                    MyApplication.getInstance.firmwareVersion=version
                }
                Constants.CHAR_HW_REVISION.toString() -> {
                    //hardware  version
                    Log.e("hardware----",version)
                    MyApplication.getInstance.hardwareVersion=version
                }
            }
        }
    }


}