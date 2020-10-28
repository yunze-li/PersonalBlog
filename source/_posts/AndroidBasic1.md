---
title: Android开发笔记（一）- 四大组件
date: 2020-06-10
thumbnail: /thumbnails/AndroidBasic.jpg
toc: true
categories:
  - 技术博客
  - Android
tags:
  - Chinese
---

因为最近开始系统地学习Android开发，所以在这里记录一下所学到的Android内容做一个整理。既算是对已学到知识的总结和梳理，也可以检查自己理解中出现的问题，和大家共同讨论一下。因为是第一次写东西，所以有什么不详细，不清楚，不准确的地方，还希望可以指正，我们共同讨论，共同进步。

<!-- more -->

本篇文章大部分的内容都是基于自己通过Google官方说明文档，StackoverFlow, CSDN等网站的阅读获得的，希望通过我个人的理解，让这些知识更容易被读者所理解和接受。

## Application Structure

进入Andorid的第一个需要理解的知识，也是我个人认为最为重要的基础知识之一，就是了解Android application的架构。构成一个完整安卓APP的，是各种各样不同的**Component**（组件），这些Component分别承担着不同的工作和职责，比如负责与用户进行交互的**Activity**，负责后台相关工作的**Service**，负责监听传递信息的**BoardcastReceiver**， 以及存储读取数据的**ContentProvider**等等。

我们用一个简单的例子来解释一下：如果说现在需要做一个负责计算汇率转换的Android应用，那么其对应的组件功能分别为：

- 首先要实现汇率转换的功能，我们就需要一个让用户输入金额大小，选择转换货币类别的界面，这就是**CurrencyConvertActivity**，在其中定义了一切UI与用户进行交互的逻辑；
- 接着我们想在后台服务器实时获取当前的汇率数据信息，那么就需要一个**BoardcastReceiver**来随时向服务器发送请求获取当前最新的汇率信息并传送给CurrencyConvertActivity，用来计算金额；
- 如果我们想在App启动时根据系统中用户设定的当前国家来显示对应的国家货币，并且在用户切换国家/时区时在APP内进行实时更新，就需要建立一个**CountryChangeReceiver**来监听TIMEZONE_CHANGED系统事件并在监听到切换国家/时区时进行识别；
- 如果我们想将实时获得的汇率进行缓存，以便在网络环境恶劣的情况下获得最近一次实时汇率表，那么就可以使用**ExchangeRateContentProvider**将汇率数据进行保存和处理

现在整个Application的架构大致就是这样:

```
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.yunze.myapplication">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
        <activity android:name="com.yunze.myapplication.CurrencyActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        
        <service android:name=".BackendService">
            <intent-filter />
        </service>
        
        <receiver android:name=".CountryChangeReceiver">
            <intent-filter>
                <action android:name="android.intent.action.TIMEZONE_CHANGED" />
            </intent-filter>
        </receiver>
        
        <provider android:name=".ExchangeRateContentProvider"
        		android:authorities=".ExchangeRateDB">
        </provider>
    </application>
</manifest>
```

上面显示的这个`AndroidManifest.xml`，可以理解为一个Android Application的“骨架”。正如我们之前所说，Activity，Service，BroadcastReceiver和ContentProvider可以算是Android Application的四大“组件”。而AndroidManifest.xml文件则正是**申明这个App所使用的所有组件**的地方，所以当我们开始上手一个新的安卓项目时，应该要从AndroidManifest.xml开始看起，才能比较详尽全面地了解这个App。

## Android Components

下面具体来比较一下不同的组件之间的关系，让我们有更进一步的了解。

**Activity**：整个Android开发中最为重要的一个概念。Acitivity中定义了App与用户的交互逻辑，例如当用户点击当前这个按钮，应该发生什么事，是显示一个文本框，更改图片的颜色，或者关闭当前界面回到上一个界面等等。可以说：所有关于用户交互的逻辑全部都只能定义和存在与Activity之中。

**Service**：如果我们的APP中需要一个长时间运行在后台来处理业务的功能，比如频繁向服务器发送请求，或者需要等待服务器随时返回请求，或者要长时间从服务器加载大型文件（比如在线浏览视频），此时就需要用到我们的Service组件。Service组件最重要的特征就是：可以长时间的运行在后台进行服务，直到完成任务后将自己结束，并且其中不能定义任何与用户UI有关的内容。

**BroadcastReceiver**: 这可能是四个组件中比较不好理解的一个，其实可以把它想象成APP的一根“天线”，它的功能就是主动接受其他APP传来的特定“信号”。比如我们之前说的TIMEZONE_CHANGED这个event，当安卓系统检测到用户手机切换了时区时，就会向整个系统内**广播**这个TIMEZONE_CHANGED event，此时如果我们的APP中含有一个注册了TIMEZONE_CHANGED的BroadcastReceiver，那么这个receiver就会被触发并完成一系列我们定义好的逻辑。

**ContentProvider**：这个组件一般为存储和获取数据提供统一的接口，可以在不同的应用程序之间共享数据。通过这个接口，其他应用看不到数据是如何存储的, 但通过**ContentResolver**可以对其进行包括：添加(insert), 删除(delete), 获取(query), 修改(update) & 返回MIME类型(getType)等一系列的操作。在这个组件中，所有文件都是通过URI来识别，结构为: content://授权信息/表名/记录。

这里要稍微说一下BroadcastReceiver与Service的区别，它们都是运行在后台，不带有任何UI的组件。但是Service作为消息的接收方是只能被单独触发的，而不同的BoardcastReceiver却是可以被一起触发的，前提就是它们都注册了同一个event。下面是在看他人的总结介绍时看到的一张很有意思的图：

![](https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/AndroidComponents.png?token=AOJCUF6JTVXEDNHQWFOX7T265W7AC)

从这张图上来看，有几点值得我们特别注意的：

1. **只有Activity才可以启动一个新的Activity**（理论上讲其实是有其他办法可以从Service和BoardcastReceiver中启动的，但是这里说的不是“能不能”，而是“是否应该”），当我们创建一个新的Android项目时，都会自动生成一个MainActivity，所有关于APP的行为和逻辑都应该在这个或者几个Activity内来完成，项目也会有一个Application类，这个类一般是作为各种Activity的容器来使用，不要轻易将代码逻辑写在Application里；而启动新的Activity的方式就是在**StartActivity()**方法中传递一个**Intent**类，这个下面会讲到。
2. **只有Activity才可以Layout Inflation**。这个也比较好理解，因为前面说过，只有Activity才能处理和用户的交互，显示所有的UI和Layout（其实所有的UI都只能run在UI Thread，也就是Main Thread上，而只有Activity可以access到Main Thread，所以当然只有它可以处理UI的部分）。当然也是有Tricky way来做的，比如call runOnUiThread()之类的，之后等到写multi-thread的时候会涉及这部分。
3. **Broadcast Receiver所监听的Event一般来说是向全系统广播的一些事件**，比如手机连接好了WIFI，手机外接了电源等等。当然也可以监听自定义的event，但是根据我们前面说的用途，自定义的情况比较少，毕竟没有必要只向自己的一个service或者activity进行广播。

## Communication between Components

下面我们来了解一下各组件间的通信方式，所谓“通信”，其实就是指：1. 从一个组件向另一个组件传递参数；2. 从一个组件生成另一个新组件。传递参数的方式有很多，但是生成组件的方式却很有限：

### Activity

启动一个新的Activity的方式主要就是通过 **startActivity()** 或**startActivityForResult()** 方法并传递一个Intent实例。这两个方法的主要区别在于是否需要从新的activity返回一个结果，比如通过新的activity选择一张本地存储的图片，或者通过新的activity确定一个文件是否已经发送完毕等等。Intent中包含了新的Activity的类名以及一些初始化需要的数据(Extra)。 通过putExtra()方法可以将这些数据传入Intent，从而传递给新的Activity。

### Service

启动一个Service的方法大致有三种：

1. `直接开启`：通过**startService()**方法并传递一个Intent实例，这个方法和Activity基本一致；
2. `绑定开启`：通过**bindService()**方法并传递一个Intent实例，绑定开启与直接开启的一个最大的区别就是：**绑定开启的service会在开启它的组件生命周期结束时随之结束，而直接开启则不会**。实际上，直接开启的service会一直运行，直到**stopService()**被执行，或者App被卸载。
3. `JobScheduler调度`：在Android 5.0+中，可以通过[JobScheduler](https://developer.android.com/reference/android/app/job/JobScheduler)中的**schedule()**方法来启动一个service，这个方式目前我还没有用到过，以后如果涉及到再单独写一篇吧。

### BroadcastReceiver

启动BroadcastReceiver的方式和Activity基本一致，区别在于可使用的方法更多，其中包括：**sendBroadCast()**，**sendOrderedBroadcast()**以及**sendStickyBroadcast()**，也都需要传递Intent实例到各个方法中。Broadcast有一个不同点在于**静态注册/动态注册**，之后会专门写一篇讲它。

### ContentProvider

这个组件和其他组件不同，因为它只是一个接口，所以其不需要进行启动或者初始化。而使用ContentProvider的一般步骤包括：

1. 创建一个实现ContentProvider接口的类，并实现接口中的各个方法（insert()， delete()， query()等）;
2. 在AndroidManifest.xml中进行注册；
3. 通过`getContentResolver().query(URI)`方法就可以直接获得定义的ContentProvider中的数据。

## 参考链接

[Application Fundamentals](https://developer.android.com/guide/components/fundamentals) - Google Android 官方说明文档























