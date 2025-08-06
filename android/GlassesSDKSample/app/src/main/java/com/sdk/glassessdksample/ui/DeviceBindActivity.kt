package com.sdk.glassessdksample.ui
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import android.content.Intent
import android.os.*
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.oudmon.ble.base.bluetooth.BleOperateManager
import com.oudmon.ble.base.communication.CommandHandle
import com.oudmon.ble.base.communication.req.SimpleKeyReq
import com.oudmon.ble.base.scan.BleScannerHelper
import com.oudmon.ble.base.scan.ScanRecord
import com.oudmon.ble.base.scan.ScanWrapperCallback
import com.sdk.glassessdksample.R
import com.sdk.glassessdksample.databinding.ActivityDeviceBindBinding
import com.xiasuhuei321.loadingdialog.view.LoadingDialog
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class DeviceBindActivity : BaseActivity() {
    private lateinit var binding: ActivityDeviceBindBinding
    private lateinit var  adapter: DeviceListAdapter
    private var scanSize:Int=0
    private val runnable=MyRunnable()

    private lateinit var loadingDialog: LoadingDialog
    private val myHandler : Handler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
        }
    }

    val deviceList = mutableListOf<SmartWatch>()
    val bleScanCallback: BleCallback = BleCallback()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDeviceBindBinding.inflate(layoutInflater)
        EventBus.getDefault().register(this)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        requestLocationPermission(this, PermissionCallback())
        binding.startScan.performClick()
    }

    private fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = getSystemService(POWER_SERVICE) as PowerManager
            return powerManager.isIgnoringBatteryOptimizations(this.packageName)
        }
        return false
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(messageEvent: BluetoothEvent) {
        Log.i(TAG, "onMessageEvent: "+messageEvent.connect)
        if(messageEvent.connect){
            loadingDialog.close()
            finish()
        }
    }

    override fun setupViews() {
        super.setupViews()
        adapter = DeviceListAdapter(this, deviceList)
        binding.run {
            deviceRcv.layoutManager = LinearLayoutManager(this@DeviceBindActivity)
            deviceRcv.adapter = adapter
            titleBar.tvTitle.text="扫描"
            titleBar.ivNavigateBefore.setOnClickListener {
                finish()
            }
        }

        adapter.notifyDataSetChanged()

        adapter.run {
            setOnItemClickListener{ _, _, position->
                myHandler.removeCallbacks(runnable)
                val smartWatch:SmartWatch= deviceList[position]
                BleOperateManager.getInstance().connectDirectly(smartWatch.deviceAddress)

                loadingDialog =LoadingDialog(this@DeviceBindActivity)
                loadingDialog.setLoadingText(getString(R.string.text_22))
                    .show()
            }
        }

        setOnClickListener(binding.startScan) {
            deviceList.clear()
            adapter.notifyDataSetChanged()
            BleScannerHelper.getInstance().reSetCallback()
            if(!BluetoothUtils.isEnabledBluetooth(this@DeviceBindActivity)){
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                activity!!.startActivityForResult(intent, 300)
            }else{
                scanSize = 0
                BleScannerHelper.getInstance()
                    .scanDevice(this@DeviceBindActivity, null, bleScanCallback)
                myHandler.removeCallbacks(runnable)
                myHandler.postDelayed(runnable, 15 * 1000)
            }
        }
    }

    inner class MyRunnable:Runnable{
        override fun run() {
            BleScannerHelper.getInstance().stopScan(this@DeviceBindActivity)
        }

    }



    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }



    inner class PermissionCallback : OnPermissionCallback {
        override fun onGranted(permissions: MutableList<String>, all: Boolean) {
            if (!all) {

            }
        }

        override fun onDenied(permissions: MutableList<String>, never: Boolean) {
            super.onDenied(permissions, never)
            if(never){
                XXPermissions.startPermissionActivity(this@DeviceBindActivity, permissions);
            }
        }

    }


    inner class BleCallback : ScanWrapperCallback {
        override fun onStart() {
        }

        override fun onStop() {

        }

        override fun onLeScan(device: BluetoothDevice?, rssi: Int, scanRecord: ByteArray?) {
            if (device != null && (!device.name.isNullOrEmpty())) {
//                if (device.name.startsWith("O_")||device.name.startsWith("Q_")) {
//
//                }

                val smartWatch = SmartWatch(device.name, device.address, rssi)
                Log.i("1111",device.name+"---"+ device.address)

                if (!deviceList.contains(smartWatch)) {
                    scanSize++
                    deviceList.add(0, smartWatch)
                    deviceList.sortByDescending { it -> it.rssi }
                    adapter.notifyDataSetChanged()
                    if (scanSize > 30) {
                        BleScannerHelper.getInstance().stopScan(this@DeviceBindActivity)
                    }
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {

        }

        override fun onParsedData(device: BluetoothDevice?, scanRecord: ScanRecord?) {

        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {

        }

    }
}