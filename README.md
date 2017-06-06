# XposedNavigationBar
基于Xposed框架实现的导航栏功能拓展模块
在导航栏中实现一个左划菜单，实现多种快捷功能

--开发中--

### 截图

### 音量调整

### 亮度调整

### 通知栏下拉 ✓
通过反射调用了collapsePanels（收起),expandSettingsPanel(完全展开),expandNotificationsPanel(展开一部分),

但是这个方法的下拉在android 6.0上会有通知栏展开十分缓慢的问题，Nova launcher在6.0上说通过root变通的解决了这个问题，在酷安开发者群有朋友提出获取root权限后，模拟手势滑动等来展开状态栏，测试后发现确实可行。

调用如下指令模拟手势实现下拉：

adb shell input swipe 100 1 100 500 300   模拟滑动事件 在x 100 y 1的位置滑动到 x 100 y 500的位置 历时300毫秒

此时通知栏并不会完全展开，解决方法也非常简单，再模拟一次点击事件，点击刚刚没有完全展开的状态栏即可：

adb shell input tap 100 100  模拟点击事件，点击了一下x 100 y 100的位置

这样通知栏就完全展开了

### 通知栏消息清空 ✓
关于这个功能的实现我写了一篇博客

http://www.jianshu.com/p/d17ce2880753

### 快速备忘 ✓ 
弹出对话框以后输入备忘内容，并在通知栏上显示，但是对话框的弹出需要依附一个activity，在Xp框架hook导航栏后，获取的context是systemuiapplication，无法启动对话框，这里用了一个变通的方式解决这个问题：

启动一个全透明背景的activity，并在这上面去显示对话框，这样看起来就有一种在桌面上凭空打开对话框的方法。

### 息屏 ✓ 
这个功能的实现比较简单，阅读源码后发现gotosleep这个方法被hide了，解决方法也很简单，直接通过反射去调用即可。

后台清理

手电筒

三个本来的按钮

### 快捷启动应用 ✓ 
输入一个包名启动应用
```java
  public void launchActivity(Context context, String pkgName) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(pkgName);
        context.startActivity(intent);
    }
```
