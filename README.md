# XposedNavigationBar
基于Xposed框架实现的导航栏功能拓展模块
在导航栏中实现一个左划菜单，实现多种快捷功能
代码基于GPL3.0协议开源

下载地址
http://www.coolapk.com/apk/com.egguncle.xposednavigationbar

--开发中--
## 支付宝&微信扫一扫 ✓
支付宝扫一扫：
```java
            Uri uri = Uri.parse("alipayqr://platformapi/startapp?saId=10000007");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
```
微信原来的扫一扫打开是这样的，不过目前的版本已经失效了
```java
        Uri uri = Uri.parse("weixin://dl/scan");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
```
所以只能使用am命令来控制二维码界面的activity打开

使用开发者工具调试后发现微信二维码的activity是这个com.tencent.mm.plugin.scanner.ui.BaseScanUI
```java
        String cmd="am start -n com.tencent.mm/com.tencent.mm.plugin.scanner.ui.BaseScanUI";
        try {
            Process p = Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
```
## 截图 ✓
这个实现比较粗暴，使用的shell指令。

## 音量调整 ✓

## 亮度调整 ✓

## 通知栏下拉 ✓
通过反射调用了collapsePanels（收起),expandSettingsPanel(完全展开),expandNotificationsPanel(展开一部分),

但是这个方法的下拉在android 6.0上会有通知栏展开十分缓慢的问题，Nova launcher在6.0上说通过root变通的解决了这个问题，在酷安开发者群有朋友提出获取root权限后，模拟手势滑动等来展开状态栏，测试后发现确实可行。

调用如下指令模拟手势实现下拉：

adb shell input swipe 100 1 100 500 300   模拟滑动事件 在x 100 y 1的位置滑动到 x 100 y 500的位置 历时300毫秒

此时通知栏并不会完全展开，解决方法也非常简单，再模拟一次点击事件，点击刚刚没有完全展开的状态栏即可：

~~adb shell input tap 100 100  模拟点击事件，点击了一下x 100 y 100的位置~~
模拟点击会出现奇奇怪怪的问题，可能在其他rom上会点击到其他的位置，所以再做一次下滑会好一些

这样通知栏就完全展开了

## 通知栏消息清空 ✓
关于这个功能的实现我写了一篇博客

http://www.jianshu.com/p/d17ce2880753

## 快速备忘 ✓ 
弹出对话框以后输入备忘内容，并在通知栏上显示，但是对话框的弹出需要依附一个activity，在Xp框架hook导航栏后，获取的context是systemuiapplication，无法启动对话框，这里用了一个变通的方式解决这个问题：

启动一个全透明背景的activity，并在这上面去显示对话框，这样看起来就有一种在桌面上凭空打开对话框的方法。

## 息屏 ✓ 
这个功能的实现比较简单，阅读源码后发现gotosleep这个方法被hide了，解决方法也很简单，直接通过反射去调用即可。
有朋友说需要长按电源键呼出关机等功能，先记下来。

长按呼出关机菜单功能的实现也比较简单，直接模拟的电源按键的长按事件
```java
 Instrumentation mInst = new Instrumentation();
 KeyEvent keyEvent=new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_POWER);
 mInst.sendKeySync(keyEvent);
```

## 后台清理 ✓
待改进，清理效果不是很理想。

## 手电筒

## 三个本来的按钮

## 快捷启动应用 ✓ 
输入一个包名启动应用
```java
  public void launchActivity(Context context, String pkgName) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(pkgName);
        context.startActivity(intent);
    }
```

## 快捷启动一些快捷方式 ✓ 
如绿色守护休眠并关屏等等，目前发现一个问题，如果被设置为快捷启动的app被冰箱冻结了，就无法启动并且会报错，是由于app被停用了，解决方法是root后通过pm enable packageName命令即可解冻（这个解决方案来源于一个酷安小伙伴的提示，原理应该和冰箱是一样的）

## 显示时间

## 显示电量

## 任务切换

## 音乐播放控制 ✓
关于这个功能，起初有很多想法，一开始是想hook对应的音乐软件，再hook其使用的mediaplayer类，获取到播放时长，以跳转到最后一秒的方式实现下一曲的功能。然而在hook网易云后，发现并没有获得mediaplayer类，查阅了相关资料得知有了另外一个叫mediasession的类，可以更方便的控制音乐播放，hook网易云后成功获取这个类的实例，但是在调用方法的时候就成空对象了。

就这个问题发送邮件请教了其他xposed模块的作者，得出的解决方案也很简单，效果拔群：
模拟按键来控制上一曲下一曲
```java
 //下一曲功能实现，上一曲暂停播放类似，这个方法不能在主线程里运行
 Instrumentation mInst = new Instrumentation();
 mInst.sendKeyDownUpSync(KeyEvent.KEYCODE_MEDIA_NEXT);
```

## 踩到的一些小坑
获取上下文对象可以通过在hook资源时拿到某个view，再getContext

资源文件要放在asset文件夹下，使用时取出来转换为byte[]再去转换为需要的类型

当应用被放在冰箱里时，无法正常打开app

起初的版本在设置完以后需要重启才能让设置好的快捷方式生效，修改后使用广播进行进程间通信解决了这个问题。
