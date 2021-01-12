---
title: Android开发笔记之基础篇（五）- Service组件
date: 2021-01-11
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

在[Android开发笔记之基础篇（二）](https://yunze-li.github.io/2020/06/16/AndroidBasic2/)中，我们简单总结了Activity组件的特性和使用方法。在其之后的第二个重要组件就是**Service**（服务）。简单来说，如果说Activity是负责处理与用户交互的UI部分的内容，那么Service则是负责**在后台处理一些长时间，大型的操作或计算的内容**。今天我们就继续来看看Service组件的特性以及使用方法。

<!-- more -->

## Service

首先，在Google doc官方说明文档中对Service的解释是：

> A `Service` is an [application component](https://developer.android.com/guide/components/fundamentals#Components) that can perform **long-running operations in the background**. It does not provide a user interface.

所以从这里我们可以很清晰的看到Service的两个特性：**后台运行**和**长时间运行**。适合使用Service的场景实例有很多，比如播放音乐或者进行网络文件下载，与ContentProvider进行交互等等。在某些情况下，**即使App被关闭后，其开启的Service依旧能够在后台运行**。

### Service分类

按照不同的分类方式，Service可以从**运行地点，运行类型以及功能**进行分类：

#### 运行地点

- `本地服务(local service)`：运行在**主线程（main thread）**，受线程的控制，主线程终止时，其也会被终止；
- `远程服务(remote service)`：运行在自己的**独立线程**，常驻后台，不被其他activity影响，**但会消耗更多资源**。

#### 运行类型

- `前台服务(Forground)`：所谓“前台”，是指其**会在通知栏中显示出来**，需要让用户看到，并且**终止时通知也会消失**；
- `后台服务(Background)`：**不会显示在通知栏**，用户无法知晓是否仍在运行。

#### 功能

- `不可通信`：使用`startService()`直接开启，调用者退出后，**该service可继续存在**，不可**与调用者（例如activity）进行通信**；
- `可通信`：使用`bindService()`绑定开启，调用者退出后，**该service也会退出并销毁**，可以**与调用者（例如activity）进行通信**。

### 生命周期 Service lifecycle

启动一个Service主要有两种方式：**`startService()` 直接开启** 或 **`bindService()`绑定开启**，两种方式产生的Service的生命周期不完全相同。先放上Google doc官方文档中给出的说明图：

<img src="https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/Service_Lifecycle.png" style="zoom:50%;" />

这里我们首先需要知道，在`Service`中有四个**手动调用**的方法：`startService()`, `stopService()`, `bindService()`和`unbindService()`，通过手动调用这四个方法，可以使其**自动调用另外五个方法**：`onCreate()`, `onStartCommand()`, `onBind()`, `onDestory()`和`onUnbind()`。所以下面我们就对四个需手动调用的方法来一一详细分析其在Service生命周期中的使用方法和规律：

#### startService()

<img src="https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/start_service.png" style="zoom:80%;" />

- `startService()`会直接启动Service服务，即使多次手动调用`startService()` **只会调用一次onCreate()， 但会多次调用onStartCommand()**，`onStartCommand()`的调用次数和`startService()`保持一致，也**只有onStartCommand()可以被多次自动调用**。
- 当`onStartCommand()`被自动调用时，会返回一个整数flag，其用于表示**当该Service被系统销毁时要如何处理**，这个flag有三种可能的状态：1. `START_NOT_STICKY`：除非还存在未发送的intent，**否则该Service不会被重建**；2. `START_STICKY`: **重建服务，但不会再次发送最近一次已发送的intent**，适用于例如媒体播放等**需要持续待命，但不用立刻运行**的场景；3. `START_REDELIVER_STICKY`： **重建服务并发送最近一次已发送的intent**，适用于例如文件下载等**需要立刻恢复运行**的场景；
- 当返回的flag是`START_STICKY`或`START_REDELIVER_STICKY`时， `onStartCommand()`也会被**重新调用**。

#### stopService()

<img src="https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/stop_service.png" style="zoom:80%;" />

- `stopService()`会直接关闭Service服务，但需要注意的是：**在已经绑定服务（调用过`bindService()`）之后如果没有解绑，`stopService()`是不会关闭服务的**。

#### bindService()

<img src="https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/bind_service.png" style="zoom:80%;" />

- 即使多次手动调用`bindService()`，实际上**`onCreate()`也只会被调用一次**，即只存在一个Service实例；
- 当一个服务已经通过`startService()`直接启动之后，**依旧可以通过`bindService()`来绑定服务并使用**。

#### unbindService()

<img src="https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/unbind_service.png" style="zoom:80%;" />

- 如果使用`bindService()`绑定启动服务，那么
- 当多个客户端（client）绑定到同一个Service服务时，系统会在**所有的客户端都解绑之后**自动销毁服务，不需要手动调用`unbindService()`。

### 题目练习

不妨用以下的test case来测试一下上面的流程图是否已经掌握，通过手动调用方法的顺序来判断Service自动调用方法的顺序：

<img src="https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/service_test_cases.png" style="zoom:80%;" />



## Service VS IntentService

`Service`与`IntentService`的区别是一个非常常见的面试题目，其主要区别包括：

- `Service`**运行在主线程**(main thread)，无法处理耗时任务，否则主线程阻塞会出现ANR。`IntentService`可以在**独立的子线程**上运行；
- `Service`通过**手动调用**`stopService()`来停止并销毁服务。但`IntentService`会**在所有intent被处理完之后自动停止**；
- `IntentService`会**自动调用`onBind()`方法**并返回一个null值，并为`onStartCommand()`提供了默认实现，将请求的intent添加进队列中。

### 参考文章

[Google Document: Services overview](https://developer.android.com/guide/components/services)

[JavaTpoint: Android Service Tutorial](https://www.javatpoint.com/android-service-tutorial#:~:text=Android%20service%20is%20a%20component,even%20if%20application%20is%20destroyed.)

[Android：Service生命周期 完全解析](https://www.jianshu.com/p/8d0cde35eb10)

[Android的Service生命周期](https://www.jianshu.com/p/ee224f18a4bd)

