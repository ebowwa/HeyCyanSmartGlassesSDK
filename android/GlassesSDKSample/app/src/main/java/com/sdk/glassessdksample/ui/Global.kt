package com.sdk.glassessdksample.ui
import android.view.View

/**
 * @Author: Hzy
 * @CreateDate: 2021/6/25 14:14
 * <p>
 * "程序应该是写给其他人读的,
 * 让机器来运行它只是一个附带功能"
 *
 */
/**
 * 批量设置控件点击事件。
 *
 * @param v 点击的控件
 * @param block 处理点击事件回调代码块
 */
fun setOnClickListener(vararg v: View?, block: View.() -> Unit) {
    val listener = View.OnClickListener { it.block() }
    v.forEach { it?.setOnClickListener(listener) }
}


