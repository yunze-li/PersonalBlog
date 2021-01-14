---
title: Android开发笔记之基础篇（六）- BroadcastReceiver组件
date: 2021-01-13
thumbnail: /thumbnails/AndroidBasic.jpg
toc: true
categories:
  - Technical
  - Android
tags:
  - Kotlin
  - Java
  - Chinese
---

在介绍了[Activity组件](https://yunze-li.github.io/2020/06/16/AndroidBasic2/)和[Service组件](https://yunze-li.github.io/2021/01/11/AndroidBasic5/)之后，这篇文章将会介绍Android开发中的第三个重要组件：[BroadcastRecevier](https://developer.android.com/reference/android/content/BroadcastReceiver)。简单来说，`BroadcastReceiver`主要用于**监听、接收来自Android系统或者其他应用程序的广播信息**，是用于不同APP之间，以及APP和Android系统之间通信的重要工具。APP可以根据接收到的信息，采取不同的操作来进行响应。

<!-- more -->

## BroadcastReceiver

首先还是先上Google的官方说明文档：

> Android apps can send or receive broadcast messages from the Android system and other Android apps, similar to the [publish-subscribe](https://en.wikipedia.org/wiki/Publish–subscribe_pattern) design pattern. These broadcasts are sent when an event of interest occurs.

这里值得注意的是：`BroadcastReceiver`使用了**观察者模式**（[Observer Pattern](https://www.tutorialspoint.com/design_pattern/observer_pattern.htm)），从而**解耦了广播的发送者和接收者**。一个APP既可以是广播的发送者，也可以是接收者。发送广播需要通过**手动调用`sendBroadCast(Intent) `方法**来完成，而接收广播则需要通过**注册一个自定义的[BroadcastReceiver](https://developer.android.com/reference/android/content/BroadcastReceiver)子类**来完成。

### 实现原理

`BroadcastReceiver`的工作流程如下图所示：

<img src="https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/broadcast_receiver.png" style="zoom:90%;" />

如上图所示，其中**实现BroadcastReceiver子类以及注册子类**需要通过代码手动完成，其他部分均是自动完成的。那么我们就来看看如何创建并注册BroadcastReceiver子类，示例代码如下：

```kotlin
class SampleBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // define the behaviour when receive broadcast
        // 在这里定义接收到广播之后的操作
        TODO("Not yet implemented")
    }
}
```

要注意的是：一般情况下`onReceive()`会**运行在主线程（main thread）**，所以为了**避免ANR**，尽量**不要在这里执行长时间的复杂操作**，必要的话可以从这里开启一个Service来执行具体的操作。

### 静态注册 VS 动态注册 （重要！！！）

实现了BroadcastReceiver子类之后，就是要**在消息中心进行注册(register)**，这里可以采用两种注册方式：**静态注册**和**动态注册**：

#### 静态注册（静态广播）

在`Androidmanifest.xml`中对实现的BroadcastReceiver子类进行声明，示例代码如下：

```xml
<receiver
    android:name="broadcast.SimpleBroadcastReceiver"
    android:enabled="true"
    android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.BOOT_COMPLETED" />
        <action android:name="android.net.conn.CONNECTIVITY_CHANGE" />
    </intent-filter>
</receiver>
```

其中`receiver`标签内可以定义注册时的很多属性，比如`exported`，`enabled`，`permission`，`precess`等，而`intent-filter`标签里则**定义了允许接收的广播类型**，比如`BOOT_COMPLETED`就是当Android设备Boot结束时系统会发送的广播类型，而`INPUT_METHOD_CHANGED`则是当用户切换了输入法时系统会发送的广播类型。**通过在`intent-filter`里声明各种不同的广播类型，可以让APP对于不同的系统事件做出反应**（`onReceive()`被调用）。

#### 动态注册（动态广播）

在代码中**手动调用`Context.registerReceiver()`方法**，传入`BroadcastReceiver`子类实例，并在使用结束后**手动调用`unregisterReceiver()`方法来销毁**。在[Activity](https://developer.android.com/reference/android/app/Activity)中使用动态注册的实例代码如下：

```kotlin
class BroadcastDemoActivity : AppCompatActivity() {

    // 实例化实现的BroadcastReceiver子类
    private val sampleBroadcastReceiver = SampleBroadcastReceiver()
    
    override fun onResume() {
        super.onResume()

        // 创建IntentFilter实例并添加需要监听的广播类型(Action)
        val intentFilter = IntentFilter()
        intentFilter.addAction(android.content.Intent.ACTION_BOOT_COMPLETED)
        intentFilter.addAction(android.content.Intent.ACTION_INPUT_METHOD_CHANGED)
        
        // 通过registerReceiver()方法动态注册
        this.registerReceiver(sampleBroadcastReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        
        // 通过unregisterReceiver()方法销毁广播接收者
        this.unregisterReceiver(sampleBroadcastReceiver)
    }
}
```

#### 两者区别

- 使用**静态注册**时，当app退出后，BroadcastReceiver**依旧可以接收广播并处理**，而使用**动态注册**时，**BroadcastReceiver的生命周期跟随Context的生命周期**。例如，如果使用`Activity.registerReceiver()`，则当activity销毁时，receiver也会失效；而使用`ApplicationContext.registerReceiver()`时，当application退出时，receiver才会失效；
- 使用**静态注册**时，**不需要手动声明销毁**；而使用**静态注册**时，需要通过手动调用`unregisterReceiver()`来销毁接收器对象，否则会出现**内存泄漏**的情况，一般来说要在activity的`onResume()`和`onPause()`中**成对出现**，因为其他的生命周期都**无法保证被成对调用**，会出现重复注册或无法销毁的情况，Activity的生命周期相关内容可以看[这里](https://yunze-li.github.io/2020/06/16/AndroidBasic2/)；
- 静态注册会更消耗内存和设备的电量，常用于**需要时刻监听广播**的情况，而动态注册更加灵活，常用于**特定情况需要监听广播**的情况，一般来说**动态注册要优于静态注册**。

### Broadcast广播类型

#### 普通广播（normal broadcast）

通过发送、接收**包含自定义Action的Intent**来实现通信的广播，这个Intent可以是开发者自行定义并创建的，普通广播是最常用的广播类型。如果发送的广播**包含权限**，那么接收器**也需要相对应的权限才能接收**。示例代码如下：

```kotlin
    // 创建一个Intent示例并设置自定义的Action
    val intent = Intent()
    intent.action = "my_custom_action_name"
        
    // 调用sendBroadcast()方法来发送广播
    this.sendBroadcast(intent)
```

此时，只需要广播接收者在注册时**添加这个`my_custom_action_name`作为监听的广播类型（Action）**即可接收这个自定义的广播。要注意的是，**普通广播不能将处理结果传递给下一个接收者，并且无法终止广播Intent的传播**。

#### 系统广播（system broadcast）

Android系统内置的广播，**Android操作系统是发送者**，主要用于监听手机的操作，状态变化等信息。**不需要手动发送广播，只需要注册特定Action类型的接收器**就可以等待接收。常用的系统广播包括：`ACTION_AIRPLANE_MODE_CHANGED()`，`ACTION_BATTERY_LOW`，`ACTION_BOOT_COMPLETED`等，系统广播的所有Action类型可以在[这里](https://chromium.googlesource.com/android_tools/+/refs/heads/master/sdk/platforms/android-28/data/broadcast_actions.txt)找到。

#### 本地广播（local broadcast）

普通广播意味着其他APP也可以发送、接收当前APP的广播，但如果想获得**效率更高，安全性更强**的广播，可以使用本地广播。可以把它理解成一种**APP内部的局部广播**，它可以通过不同的方式来**阻止外部广播的发送和接收**。具体的方式有两种：

1. 对普通广播加以限制，使其只能在APP内部通信：
   - 对于**静态注册**的BroadcastReceiver，可以通过在`manifest.xml`中加入`exported = "false"`，使得非App内部发出的此广播不被接收。并且增设相应权限 `permission`，用于权限验证；
   - 对于**动态注册**的BroadcastReceiver，可以通过调用`intent.setPackage(packageName)`来**指定目标广播接收器的包名**，可以使**除此包名外的所有接收器无法接受广播**（排他性）；
2. 使用`LocalBroadcastManager`类来进行**动态注册**，此时发送、接受的广播全部属于本地广播，但是**LocalBroadcastManager已经被弃用**了，所以这里就不多做解释了。

#### 有序广播（ordered broadcast）

不论是普通广播，还是系统广播，都是**按照随机顺序发送给所有符合条件的接收者的**。但是**有序广播会按照`priority`由大到小的顺序**，依次发给所有符合条件的接收者。priority在`manifest.xml`或者通过手动调用`intentFilter.setPrority(1000)`来设置，prority是一个Int类型，示例代码如下：

```kotlin
// 静态注册设置prority
<receiver
    android:name="broadcast.SampleBroadcastReceiver"
    android:enabled="true"
    android:exported="true">
    <intent-filter android:priority="1000">
        <action android:name="android.intent.action.BOOT_COMPLETED" />
        <action android:name="android.net.conn.CONNECTIVITY_CHANGE" />
    </intent-filter>
</receiver>

// 动态注册设置prority
val intentFilter = IntentFilter()
intentFilter.addAction(android.content.Intent.ACTION_BOOT_COMPLETED)
intentFilter.addAction(android.content.Intent.ACTION_INPUT_METHOD_CHANGED)
intentFilter.priority = 1000

// 发送有序广播, finalResultRecevier是终结接收者
this.sendOrderedBroadcast(intent, null, finalResultRecevier)
```

这里要注意有序广播区别于其他广播的几个特点：

1. 有序广播**可以被截止，高优先级（priority）的广播接收者有权利决定**比它低的接收者们是否可以接收到对应的广播，截断广播通过手动调用`abortBroadcast()`来实现；
2. 有序广播**可以被修改，高优先级（priority）的广播接收者可以修改**后续比它低的接收者们所接收到的广播；
3. 有序广播**可以设置终结接收者**，也就是**无论终结接收者的优先级，最终它都会接收到广播并在此终结**。

#### 滞留广播（sticky broadcast）

滞留广播在**发送后会一直等待**，在有符合条件的接收器被注册后，会**立刻发送至该接收器，并继续等待**。发送滞留广播**需要获得`BROADCAST_STICKY`权限**，而停止滞留广播需要**手动调用`removeStickyBroadcast()`方法**。因为滞留广播也已经在API 21中弃用，这里就不再多讲了。

### 注意事项

对于不同注册方式的广播接收器回调OnReceive（Context context，Intent intent）中的context返回类型是不一样的：

- 对于静态注册（全局+应用内广播），回调onReceive(context, intent)中的context返回类型是**ReceiverRestrictedContext**；
- 对于全局广播的动态注册，回调onReceive(context, intent)中的context返回类型是Activity Context**；
- 对于应用内广播的动态注册（非LocalBroadcastManager方式），回调onReceive(context, intent)中的context返回类型是**Activity Context**；
- 对于应用内广播的动态注册（LocalBroadcastManager方式），回调onReceive(context, intent)中的context返回类型是**Application Context**。

### 参考文章

[Google Document: BroadcastReceiver overview](https://developer.android.com/guide/components/broadcasts)

[Android四大组件：BroadcastReceiver史上最全面解析](https://www.jianshu.com/p/ca3d87a4cdf3)

[Android四大组件——BroadcastReceiver普通广播、有序广播、拦截广播、本地广播、Sticky广播、系统广播](https://blog.csdn.net/qq_30379689/article/details/53341313)

