package com.sdk.glassessdksample.ui
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.oudmon.ble.base.bluetooth.BleOperateManager
import com.oudmon.ble.base.bluetooth.DeviceManager
import org.greenrobot.eventbus.EventBus

/**
 * @author hzy ,
 * @date 2020/8/3,
 *
 *
 * "程序应该是写给其他人读的,
 * 让机器来运行它只是一个附带功能"
 */
class BluetoothReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            BluetoothAdapter.ACTION_STATE_CHANGED -> {
                val connectState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
                if (connectState == BluetoothAdapter.STATE_OFF) {
                    Log.i("qc" ,"蓝牙关闭了 --> ")
                    BleOperateManager.getInstance().setBluetoothTurnOff(false)
                    BleOperateManager.getInstance().disconnect()
                    EventBus.getDefault().post(BluetoothEvent(false))
                } else if (connectState == BluetoothAdapter.STATE_ON) {
                    Log.i("qc" ,"蓝牙开启了 --> ")
                    BleOperateManager.getInstance().setBluetoothTurnOff(true)
                    BleOperateManager.getInstance().reConnectMac=DeviceManager.getInstance().deviceAddress
                    BleOperateManager.getInstance().connectDirectly(DeviceManager.getInstance().deviceAddress)

                }
            }
            BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {

            }
            BluetoothDevice.ACTION_ACL_CONNECTED -> {

            }
            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {

            }

            BluetoothDevice.ACTION_FOUND -> {
                val device =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                if (device != null) {
                  //发现设备，当蓝牙地址和当前 BLE地址相等调用配对
                    BleOperateManager.getInstance().createBondBluetoothJieLi(device)
                }
            }
        }
    }

}