# BiliWifiOnly

一个 [LSPosed](https://github.com/LSPosed/LSPosed) 模块，让哔哩哔哩只能识别到 **Wi-Fi 状态**。以便去除烦人的免流量推广。

对目标 App 谎报网络：
- `NetworkInfo.getType()` → 总是返回 `TYPE_WIFI`
- `NetworkCapabilities.hasTransport()` → 蜂窝网络返回 `false`，Wi-Fi 返回 `true`

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

本项目由Gemini 3.7 Flash设计，Deepseek-v4-flash完成。
