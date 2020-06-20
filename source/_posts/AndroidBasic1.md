---
title: Android开发笔记之基础篇（一）
date: 2018-09-22 17:35:06
thumbnail: /thumbnails/AndroidBasic.jpg
toc: true
tags:
    - Android
---

因为最近开始系统地学习Android开发，所以在这里记录一下所学到的Android内容做一个整理。既算是对已学到知识的总结和梳理，也可以检查自己理解中出现的问题，和大家共同讨论一下。因为是第一次写东西，所以有什么不详细，不清楚，不准确的地方，还希望可以指正，我们共同讨论，共同进步。

<!-- more -->

本篇文章大部分的内容都是基于自己通过Google官方说明文档，StackoverFlow, CSDN等网站的阅读获得的，希望通过我个人的理解，让这些知识更容易被读者所理解和接受。

## Application Components

进入Andorid的第一个需要理解的知识，也是我个人认为最为重要的基础知识之一，就是了解Android app的架构。构成一个完整安卓APP的，是各种各样不同的Component（组件），这些Component分别承担着不同的工作和职责，比如负责与用户进行交互的Activity，负责后台相关工作的Service，以及负责监听传递信息的BoardcastReceiver等等。我们用一个简单的例子来解释一下：如果说现在需要做一个负责计算汇率转换的Android应用，首先Application指的就是这个App本身；要实现汇率转换的功能，我们就需要一个让用户输入金额大小，选择转换货币类别的界面，这就是CurrencyConvertActivity，在其中定义了一切关于UI的逻辑；如果我们想在后台服务器存储当前的汇率数据信息，那么就需要一个NetworkService来随时向服务器发送请求获取当前最新的汇率信息并交给CurrencyConvertActivity来计算并显示；如果我们想加入一个自动识别当前国家并显示该国家货币的功能，就需要建立一个CountryChangeReceiver来监听一个叫做TIMEZONE_CHANGED的event（系统事件）并在监听到切换国家/时区时进行识别。现在整个Application的架构大致就是这样：

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
    </application>
</manifest>
```

上面显示的这个`AndroidManifest.xml`，可以理解为一个Android Application的“骨架”。正如我们之前所说，Activity，Service以及Broadcast Receiver可以算是Android Application的三个“组件”，而Manifest.xml文件则正是申明这个App所使用的所有组件的地方，所以当我们开始深入的看一个安卓项目时，应该要从AndroidManifest.xml开始看起，才能比较详尽全面地了解这个App。

## Activity vs BoardcastReceiver vs Service

下面具体来比较一下不同的Component之间的关系，让我们有更进一步的了解。

Activity：整个Android开发中最为重要的一个概念。Acitivity中定义了App与用户的交互逻辑，例如当用户点击当前这个按钮，应该发生什么事，是显示一个文本框，更改图片的颜色，或者关闭当前界面回到上一个界面等等。可以说：所有关于用户交互的逻辑全部都只能定义和存在与Activity之中。

Service：如果我们的APP中需要一个长时间运行在后台来处理业务的功能，比如频繁向服务器发送请求，或者需要等待服务器随时返回请求，或者要长时间从服务器加载大型文件（比如在线浏览视频），此时就需要用到我们的Service组件。Service组件最重要的特征就是：可以长时间的运行在后台进行服务，直到完成任务后将自己结束，并且其中不能定义任何与用户UI有关的内容。

Broadcast Receiver: 这可能是三个组件中比较不常用的一个，也是不太好理解的一个。为了方便理解，可以把它想象成：APP的一根“天线”，它的功能就是主动接受其他APP传来的特定“信号”。比如我们之前说的TIMEZONE_CHANGED这个event，当安卓系统检测到用户手机切换了时区时，就会向全系统内广播这个TIMEZONE_CHANGED event，此时如果我们的APP中含有一个注册了TIMEZONE_CHANGED的BroadcastReceiver，那么这个receiver就会被触发并完成一系列我们定义好的逻辑。

这里要稍微说一下BroadcastReceiver与Service的区别，它们都是运行在后台，不带有任何UI的组件。但是Service作为消息的接收方是只能被单独触发的，而不同的BoardcastReceiver却是可以被一起触发的，前提就是它们都注册了同一个event。下面是在看他人的总结介绍时看到的一张很有意思的图：

![](https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/AndroidComponents.png?token=AOJCUF6JTVXEDNHQWFOX7T265W7AC)

从这张图上来看，有几点值得我们特别注意的：

1. 只有Activity才可以启动一个新的Activity（理论上讲其实是有其他办法可以从Service和BoardcastReceiver中启动的，但是这里说的不是“能不能”，而是“是否应该”），当我们创建一个新的Android项目时，都会自动生成一个MainActivity，所有关于APP的行为和逻辑都应该在这个或者几个Activity内来完成，项目也会有一个Application类，这个类一般是作为各种Activity的容器来使用，不要轻易将代码逻辑写在Application里；

2. 只有Activity才可以Layout Inflation。这个也比较好理解，因为前面说过，只有Activity才能处理和用户的交互，显示所有的UI和Layout（其实所有的UI都只能run在UI Thread，也就是Main Thread上，而只有Activity可以access到Main Thread，所以当然只有它可以处理UI的部分）。当然也是有Tricky way来做的，比如call runOnUiThread()之类的，这些也是留给之后再讲吧。

3. Broadcast Receiver所监听的Event一般来说是向全系统广播的一些事件，比如手机连接好了WIFI，手机外接了电源等等。当然也可以监听自定义的event，但是根据我们前面说的用途，自定义的情况比较少，毕竟没有必要只向自己的一个service或者activity进行广播。

这其中还提到了另一个组件：ContentProvider，这个还是之后讲，个人认为相比起其他三个，这个要稍微用得少一些，使用方法也相对简单一些。
