package com.sdk.glassessdksample.ui

/**
 * @author hzy ,
 * @date  2021/1/3
 * <p>
 * "程序应该是写给其他人读的,
 * 让机器来运行它只是一个附带功能"
 **/
data class SmartWatch (
    val deviceName:String,
    val deviceAddress:String?,
    val rssi:Int,
        ){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SmartWatch

        if (deviceName != other.deviceName) return false
        if (deviceAddress != other.deviceAddress) return false

        return true
    }

    override fun hashCode(): Int {
        var result = deviceName.hashCode()
        result = 31 * result + (deviceAddress?.hashCode() ?: 0)
        return result
    }
}
