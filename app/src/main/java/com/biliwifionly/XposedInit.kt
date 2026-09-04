package com.biliwifionly

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.util.Log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

/**
 * 让哔哩哔哩只能识别到 Wi-Fi 状态，并移除部分直播间官方添加的马赛克遮罩。
 *
 * 对目标 App 谎报网络：
 *  - NetworkInfo.getType()               -> 总是返回 TYPE_WIFI
 *  - NetworkCapabilities.hasTransport()  -> 蜂窝网络返回 false，Wi-Fi 返回 true
 *
 * 另外移植 BiliRoamingX 的 remove_live_mask：
 *  - 反序列化后置空 BiliLiveRoomInfo.areaMaskInfo，见 [LiveMaskPatch]
 */
class XposedInit : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        if (lpparam.packageName !in TARGET_PACKAGES) return
        // 只处理主进程，避免多余 hook 影响子进程
        if (lpparam.processName != lpparam.packageName) return

        Log.i(TAG, "hook 生效于 ${lpparam.packageName}")

        // 1. 老版本/兼容 API：NetworkInfo.getType() -> TYPE_WIFI
        XposedHelpers.findAndHookMethod(
            NetworkInfo::class.java,
            "getType",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    param.result = ConnectivityManager.TYPE_WIFI
                }
            },
        )

        // 2. 新版本 API：NetworkCapabilities.hasTransport(int) -> 只认 Wi-Fi
        XposedHelpers.findAndHookMethod(
            NetworkCapabilities::class.java,
            "hasTransport",
            Int::class.javaPrimitiveType,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val transport = param.args[0] as Int
                    when (transport) {
                        NetworkCapabilities.TRANSPORT_CELLULAR -> param.result = false
                        NetworkCapabilities.TRANSPORT_WIFI -> param.result = true
                        // 其他传输类型（蓝牙/以太网/VPN 等）保持原样，不干预
                    }
                }
            },
        )

        // 3. 移除部分直播间官方添加的马赛克遮罩（移植自 BiliRoamingX）
        LiveMaskPatch.hook(lpparam.classLoader)
    }

    private companion object {
        const val TAG = "BiliWifiOnly"

        val TARGET_PACKAGES = setOf(
            "tv.danmaku.bili",
            "com.bilibili.app.blue",
            "com.bilibili.app.in",
            "tv.danmaku.bilibilihd",
        )
    }
}
