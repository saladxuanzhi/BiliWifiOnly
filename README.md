# BiliWifiOnly

一个 [LSPosed](https://github.com/LSPosed/LSPosed) 模块，让哔哩哔哩只能识别到 **Wi-Fi 状态**，并移除部分直播间官方添加的**马赛克遮罩**。

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

1. 安装 APK：`dist/BiliWifiOnly-1.0.apk`
2. 打开 LSPosed（管理器），进入「模块」，勾选 **BiliWifiOnly**
3. 重启系统（或重启目标 App 的进程）
4. 在「作用域」里确认目标 App 已被选中并生效

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

## GitHub Actions 自动构建

`push` 到 `main` / `master` 分支（或手动 `workflow_dispatch`）时，`.github/workflows/build.yml` 会自动：

1. 在 `ubuntu-latest` 上安装 JDK 17 + Android SDK
2. 构建 release APK（自动 debug 签名兜底）
3. 用 `apksigner` 校验签名
4. 把 APK 上传到 workflow 的 **artifact**（Actions 页面 → 构建记录 → Artifacts → 下载）

推送和提交由你自己执行，例如：

```bash
git init
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin <你的仓库地址>
git push -u origin main
```

推送后在 GitHub 仓库的 **Actions** 标签页查看构建，构建完成后在对应运行记录底部下载 `app-release-apk`。
