# 🚀 DuoTimeFaker 官方模块商店发布指南

本指南将协助你将 **DuoTimeFaker** 发布到 GitHub 并申请收录进 **LSPosed 官方模块仓库 (Xposed-Modules-Repo)**，让全世界的多邻国用户都能在 LSPosed / LSPatch 管理器中直接搜索并一键下载。

---

## 步骤一：创建 GitHub 仓库并上传代码

1. 登录你的 GitHub 账号，点击右上角 **New repository**；
2. 填写仓库信息：
   - **Repository name**：`DuoTimeFaker`
   - **Description**：`Force Asia/Tokyo timezone & relax watchdog for Duolingo video call (Lily fix)`
   - **Public**：必须勾选 **Public（公开）**
3. 在本地将本目录的内容推送到 GitHub：
   ```bash
   cd DuoTimeFaker_Repo
   git init
   git add .
   git commit -m "feat: initial release v2.0 with timezone spoof and watchdog relaxer"
   git branch -M main
   git remote add origin https://github.com/<你的GitHub用户名>/DuoTimeFaker.git
   git push -u origin main
   ```

---

## 步骤二：创建 GitHub Release（发布 APK）

1. 打开你刚创建的 GitHub 仓库页面；
2. 在右侧边栏找到 **Releases**，点击 **Create a new release**；
3. 填写发布信息：
   - **Choose a tag**：输入 `v2.0`（点击 Create new tag）；
   - **Release title**：`v2.0: Duolingo Video Call Fix (Timezone + Watchdog)`；
   - **Description**：直接粘贴 `README.md` 的内容；
   - **Attach binaries**：把 `release/DuoTimeFaker-v2.0.apk` 拖拽上传到附件区；
4. 点击 **Publish release**。

---

## 步骤三：向 LSPosed 官方模块仓库提交收录申请

LSPosed 官方提供了一个自动化的模块收录系统，只需提交一个 Issue，机器人会自动审核并在模块商店上架：

1. 打开 LSPosed 官方收录仓库：
   👉 **https://github.com/Xposed-Modules-Repo/submission/issues/new/choose**
2. 点击 **Module Submission** 后面的 **Get started** 按钮；
3. 按照模板填写你的模块信息：

```markdown
### Name
DuoTimeFaker

### Package Name
com.duo.timezone

### Author
<你的名字或GitHub用户名>

### Repository
https://github.com/<你的GitHub用户名>/DuoTimeFaker

### Description
A dedicated Xposed/LSPatch module for Duolingo to bypass regional timezone restrictions (force Asia/Tokyo) and relax the connection quality watchdog for seamless Lily video calls. (多邻国通话时区伪装与连接看门狗放宽补丁)

### Discussion
https://github.com/<你的GitHub用户名>/DuoTimeFaker/issues
```

4. 点击 **Submit new issue**。
5. 官方机器人（LSPosed Bot）会在 1~5 分钟内对你的仓库进行静态检查，确认包名与结构合规后，会自动批准并将其收录入 `Xposed-Modules-Repo`。
6. 审核通过后，所有使用 LSPosed / LSPatch 的用户在“模块仓库”里直接搜索 **`DuoTimeFaker`** 即可一键下载安装！
