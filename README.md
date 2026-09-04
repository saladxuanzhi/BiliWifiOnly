# BiliWifiOnly

一个 [LSPosed](https://github.com/LSPosed/LSPosed) 模块，让哔哩哔哩只能识别到 **Wi-Fi 状态**。以便去除烦人的免流量推广。另外移除部分直播间官方添加的**马赛克遮罩**。

对目标 App 谎报网络：
- `NetworkInfo.getType()` → 总是返回 `TYPE_WIFI`
- `NetworkCapabilities.hasTransport()` → 蜂窝网络返回 `false`，Wi-Fi 返回 `true`

## 移除直播马赛克遮罩

移植自 [BiliRoamingX](https://github.com/BiliRoamingX/BiliRoamingX) 的「移除马赛克遮罩」（`remove_live_mask`）功能。

部分直播间的画面会被官方叠加马赛克遮罩，这是直播间信息接口返回的 `area_mask_info` 字段触发的。本模块 hook 目标 App 内置的 fastjson `JSON.parseObject`，在反序列化之后把 `BiliLiveRoomInfo.areaMaskInfo` 置空，客户端不再渲染遮罩。

- 该功能默认开启，无需配置；仅对存在相关类的目标 App 生效，不影响其他版本。
- 与 BiliRoamingX 的行为一致：只置空 `area_mask_info`，不修改其他直播间数据。

## 支持的目标 App

| 包名 | 说明 |
|---|---|
| `tv.danmaku.bili` | 哔哩哔哩 主版本 |
| `com.bilibili.app.blue` | 哔哩哔哩 概念版 |
| `com.bilibili.app.in` | 哔哩哔哩 国际版 |
| `tv.danmaku.bilibilihd` | 哔哩哔哩 HD |

## 使用方法

1. 安装 APK：在 Actions 中下载最新构建
2. 打开 LSPosed（管理器），进入「模块」，勾选 **BiliWifiOnly**
3. 重启bilibili app

> 注意：当前 APK 用的是**临时 debug 签名**（无需任何配置即可构建）。后续更新 APK 时如果签名不同，系统会拒绝覆盖安装，需要先卸载旧版再装新版（LSPosed 里需重新勾选启用）。

## 本地构建

需要 JDK 17 + Android SDK（platform-35 / build-tools 35.0.0），SDK 路径写在 `local.properties`：

```bash
./gradlew assembleRelease
# 产物：app/build/outputs/apk/release/app-release.apk
```

`app/build.gradle.kts` 里已保证 release 一定有签名：
- 提供了 `signingStoreFile/signingStorePassword/signingKeyAlias/signingKeyPassword` 这四个 gradle property 时，用正式 keystore 签名；
- 否则自动回退到 debug keystore（临时签名）。

## AI声明

本项目由AI参与完成。
