# XposedNavigationBar

![app_icon](https://github.com/EggUncle/Demo/blob/master/markdownimg/ic_launcher.png?raw=true)

基于Xposed框架实现的导航栏功能拓展模块
在导航栏中实现一个左划菜单，实现多种快捷功能
代码基于GPL3.0协议开源


## 下载地址
http://www.coolapk.com/apk/com.egguncle.xposednavigationbar
## 历史版本下载地址
http://repo.xposed.info/module/com.egguncle.xposednavigationbar
## 更新日志
https://github.com/EggUncle/XposedNavigationBar/blob/master/ChangeLog.md


# -开发中-
## 扩展的实现
实现的方法还是很简单的，只是在导航栏对应的view中使用addview加入一些布局，但是获取到view实例的方法有两种，一种通过布局文件，一种通过view这个类的onFinishInflate方法，起初使用的是前一种方法，现在用的是后一种，因为目前发现在lineage OS上，对应布局文件的hook无法生效，但是目前通过第二种方法以及可以hook成功了，rr上也有类似的问题，使用第二种方法也无法解决，原因不详，因为xposed的handleInitPackageResources方法会在布局文件加载的时候生效，可能是不同的rom在这个地方的行为有一些不同，rr上很多对导航栏进行修改的模块都没有生效。

还有就是远程进程间通信的思路，这里统一用的广播，因为这个模块的设置需要即时在导航栏上生效，而导航栏是在systemuiapplication中的，所以只能使用广播等进程间通信的方法，而且在7.0以后，因为sharedpreference权限的限制，只能在系统启动后打开app（这里其实是打开了一个属于这个模块的透明的activity），发送广播初始化各种设置;还有一些方法权限很高，即使是systemuiapplication也调用不了的，也只能使用广播进行通信，先在需要的地方hook进去设置广播接收器，然后再向它发送广播，这里具体的体现是在清理内存功能的实现上，起初使用的killprocess方法，这个是自带的api，效果不是很理想，后来在在ActivityManager中找到一个叫forceStopPackage的方法，但是使用它的条件比较苛刻，所以这里就hook了ActivityManager并设置一个广播监听器，然后其他地方发送广播来调用这个方法。

## 关于激活的判定
https://github.com/rovo89/XposedBridge/issues/64

在XposedBridge的项目下有一个关于这个功能的issues，最后一个朋友提到，其实可以自己hook自己的app，给自己的app一个判断是否激活的方法，然后hook这个方法，修改返回值，这样就能在调用的时候判断是不是激活了，因为如果激活成功，就会hook到app里面的这个判断激活的方法。

## 导航栏变色
这里只是做了变色，实现的方法也很简单，hook了style以及activity等类，然后取到主题颜色，给导航栏设置颜色就行了:
```java
activity.getWindow().setNavigationBarColor(colorPrimaryDark);
```
还有就是关于沉浸式状态栏导航栏的实现，这个没有看太多，但是在完美状态栏这个模块上面会有一个很有意思的现象，沉浸以后图标也会变成白色或者黑色，好像是和一个叫ColorMatrixColorFilter(色彩矩阵颜色过滤器)这类东西有关，回头再看看。

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

(2017-09-15)这个东西的实现现在改成了在phonewindowmanager中hook并注册广播接收器然后在其他地方向其发送广播，使它执行自己的takescreenshot方法，但是这个方法在Nougat上(Nexus5 lineage os 14.1)出现了导致机器重启的情况，所以现在做了版本判断，如果是nougat，则还是使用原来的方案来进行截图。

(2017-09-19)系统自带takescreenshot方法在7.0上和原来的有些不一样，现在做了修复，但是仍然有机器存在软重启的情况，（LG G4 官方自带rom 5.1）可能和受限广播有关系，回头再尝试处理这个问题。

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
关于这个功能的实现
https://egguncle.github.io/2017/07/08/Android%E6%B8%85%E9%99%A4%E6%89%80%E6%9C%89noticafition%E7%9A%84%E4%B8%80%E4%BA%9B%E6%8E%A2%E7%A9%B6/
https://egguncle.github.io/2017/08/27/Android%E6%B8%85%E9%99%A4%E6%89%80%E6%9C%89noticafition%E7%9A%84%E4%B8%80%E4%BA%9B%E6%8E%A2%E7%A9%B6%EF%BC%88%E4%BA%8C%EF%BC%89/#more

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
~~待改进，清理效果不是很理想。~~
阅读部分阻止运行源码以后发现有一个forceStopPackage方法，它的注释是这样：
```java
2443    /**
2444     * Have the system perform a force stop of everything associated with
2445     * the given application package.  All processes that share its uid
2446     * will be killed, all services it has running stopped, all activities
2447     * removed, etc.  In addition, a {@link Intent#ACTION_PACKAGE_RESTARTED}
2448     * broadcast will be sent, so that any of its registered alarms can
2449     * be stopped, notifications removed, etc.
2450     *
2451     * <p>You must hold the permission
2452     * {@link android.Manifest.permission#FORCE_STOP_PACKAGES} to be able to
2453     * call this method.
2454     *
2455     * @param packageName The name of the package to be stopped.
2456     * @param userId The user for which the running package is to be stopped.
2457     *
2458     * @hide This is not available to third party applications due to
2459     * it allowing them to break other applications by stopping their
2460     * services, removing their alarms, etc.
2461     */
```
该方法需要有系统签名才可以使用，最近会改成这个方法来清理后台。实测后发现systemui并没有使用它的权限，在阻止运行源码中发现他hook的位置是SystemServer,回头hook到这里面去尝试调用这个方法。
(2017-8-27)hook进了ActivityManager中调用了这个方法，效果拔群，不过因为效果太拔群了，在level为50(最低)的情况下，会误伤很多"com.android.*"中的包，所以这里做了过滤，不杀死这些报名下的进程。

## 手电筒

## 三个本来的按钮 ✓
back和home键使用按键模拟，都很简单的实现了，但是recent键没有对应的模拟code，所以使用了XposedHelpers.callMethod直接调用toggleRecentApps来实现的。
```java
  XposedHelpers.callMethod(PhoneSatatusBarHook.getPhoneStatusBar(), "toggleRecentApps");
```


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
关于这个功能，起初有很多想法，一开始是想hook对应的音乐软件，再hook其使用的mediaplayer类，获取到播放时长，以跳转到最后一秒的方式实现下一曲的功能。然而在hook网易云后，发现并没有获得mediaplayer类，查阅了相关资料得知有了另外一个叫mediasession的类，可以更方便的控制音乐播放，hook网易云后成功获取这个类的实例，但是在调用方法的时候就成空对象了。(2017.9.7 我突然想起来为什么会是空对象了，以前也碰上了这类比较诡异的情况，但是转念一想。。我调用的地方在systemuiapplication，但是获取到对象的位置在其他带有这个对象的进程里面。。也就是说，其实这是两个进程，所以肯定会出现这种情况的2333333。)

就这个问题发送邮件请教了其他xposed模块的作者，得出的解决方案也很简单，效果拔群：
模拟按键来控制上一曲下一曲
```java
 //下一曲功能实现，上一曲暂停播放类似，这个方法不能在主线程里运行
 Instrumentation mInst = new Instrumentation();
 mInst.sendKeyDownUpSync(KeyEvent.KEYCODE_MEDIA_NEXT);
```

## 剪贴板 ✓
实现一个获取剪贴板记录的功能，这个功能的实现比较简单，获取ClipboardManager再对其使用变化监听即可。

## 导航栏隐藏/呼出 ✓
这个功能参考了这篇博客http://blog.csdn.net/dliyuedong/article/details/49360807 实际使用xposed以后的情况大体相同，只是呼出导航栏直接调用调用了addNavigationBar()方法。
这个功能的实现遇到了一些问题，在监听手势的时候获取到的addnavigationbar的method和phonebar都是null，本来想获取SystemGesturesPointerEventListener中的mContext来发送广播，但是在genymotion虚拟机上反射获取关于这个类的信息后发现并没有mContext,这个属性在5.1的源码中也没有发现，但是在一加一的cm13中发现了，而且addnavigationbar这个方法，在具有实体按键的rom中似乎没有（测试机器一加一，cm13,使用设置中调用出的虚拟导航栏，可能使用了别的方法并不是而且addnavigationbar这个方法）。总之调用这两个方法来隐藏和呼出的通用性不高，暂时不使用这个方案，在其他开发者实现的隐藏呼出导航栏的模块中，看到了类似直接调整导航栏高度的思路，以后可能会使用这种思路来实现这个功能。

(2017-09-20)现在这个功能的实现还是按照了之前使用windowmanager remove navbarview的思路，添加导航栏的上划动作参考了这个实现https://github.com/ztc1997/HideableNavBar

部分机器上没有addnavigationbar的无参方法，所以直接按照方法里的内容实现了addnavigationbar的功能

(2017-09-21)现在这个功能参考了https://github.com/ztc1997/HideableNavBar 这个模块的整体的实现思路，通过修改导航栏高度来实现想要的效果，因为原先使用windowmanager remove  addview造成了一些问题。


## 踩到的一些小坑
获取上下文对象可以通过在hook资源时拿到某个view，再getContext

~~资源文件要放在asset文件夹下，使用时取出来转换为byte[]再去转换为需要的类型~~
这并不全对，资源文件还是可以放在他们本来该放的文件夹里面。
```java
    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        if (resparam.packageName.equals(SYSTEM_UI)) {
            try {
                XModuleResources xRes = XModuleResources.createInstance(MODULE_PATH,resparam.res);
                TEST_ICON_ID=resparam.res.addResource(xRes, R.drawable.ic_save);
            } catch (Exception e) {
                XpLog.e(e.getMessage());
            }
        }
    }
```
这种方法也可以获取到app本身的一些资源。但是在Android 7.0上似乎有一些限制（Lineage OS 14.1 Nexus5，这个rom当初在尝试hook导航栏布局的时候就出现了问题所以后来转为hook类里面的东西来给navbarview添加扩展，但是魔趣没有出现这样的情况，考虑到7.0刚刚适配，而且rovo89没有发布正式的Xposed for N），所以仍然使用原来的方案，（哇要不开个7.0的专版吧N和以前的系统各种不一样233333333）

当应用被放在冰箱里时，无法正常打开app

起初的版本在设置完以后需要重启才能让设置好的快捷方式生效，修改后使用广播进行进程间通信解决了这个问题。

Toast不能直接在子线程中使用，因为在其内部实现中使用了Handler，需要 Looper.prepare();和Looper.loop();才能正常使用

最近在做Android N上的适配，由于以前的快捷按钮设置的数据是用sharedpreference存储的，但是 N 禁止了sp的MODE_WORLD_READABLE模式，所以在尝试使用其他的方法，检索源码后发现这个模式检测是在contextimpl中的checkmode做的，目前将这个方法替换为空，即什么都不执行，这样虽然没报错，但是仍然没有获取到内容,检索源码以后没有在这个地方发现类似uid类的限制，可能在更深层次做了限制导致无法访问。可能要替换其他的方案做设置的保存，然后再等等7.0完整的框架发布后看看。

关于sp在7.0上的问题做了一些妥协，7.0上的模块无法在开机后自动完成设置，需要手动设置一次（这个手动可以在系统启动的时候启动模块对应的一个activity或者是别的来进行一次初始化操作来避免手动设置，但是无法直接在systemuiapplication去通过读取sp来初始化了），但是这样有一个好处就是因为这个原因现在所有设置都是动态设置的了，不需要重启再生效。（这个方案现在进行了一次修改，在扩展数据为空的时候点击导航栏的小点会启动一个透明的activity，给systemuiapplication发送包含扩展数据广播来扩展数据，不需要用户进入app再次点按钮了。(2017-09-09 这个方案现在有了变动，现在是将targetSdkVersion调整至23来避免这个7.0新特性的影响)

# Licence
```
Navigation bar function expansion module
Copyright (C) 2017 egguncle cicadashadow@gmail.com
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
```
