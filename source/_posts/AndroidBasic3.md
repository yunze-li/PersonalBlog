---
title: Android开发笔记（三）- Multi-thread
date: 2020-06-22
thumbnail: /thumbnails/AndroidBasic.jpg
toc: true
categories:
  - 技术博客
  - Android
tags:
  - Multi-thread
  - Chinese
---

今天的内容是这周工作中出现的一个相关内容的学习：多线程（**Multi-thread**）。多线程可以极大地减少程序在并发执行时所付出的时空开销，提高操作系统的并发性能。在Android的开发中，多线程是非常重要的一环，而相信很多人也在面试中被问到过不止一次进程（Process）与线程（Thread）的区别。那么我们就来看看和Android相关的多线程组件**Process**，**Thread**，**Looper**，**Handler**各自的概念和实例。

<!-- more -->

##  Process vs Thread

进程（Process）是多线程的第一个概念。简单来说，进程是指计算机中**已经运行的程序**，而对于安卓系统，一般**一个Android应用程序就是一个进程**，其中包含**多个线程协同工作**。

线程（Thread）是一种轻量级的**子进程**，是一个基本的CPU执行单元 & 程序执行流的**最小单元**，也是独立运行和独立调度的**基本单位**。在Android的环境里，一个线程可以理解为**一群任务的有序集合**，所有线程共享进程所拥有的系统资源和存储资源，各线程之间通信快速高效。当这个线程的任务全部执行结束时，**线程就会被终止**。

## Android Multi-thread

Android系统中的线程分为两类：**主线程**（Main Thread）和其他**子线程**。

**主线程**也叫UI线程，在Android APP启动时会自动生成，主要负责处理Android四大组件（可以看[这篇](https://yunze-li.github.io/2018/09/22/AndroidBasic1/)中关于四大组件的介绍）与用户进行交互的事件响应与逻辑，所以也叫主线程也叫**UI线程**。要注意的是，因为用户**随时会与界面发生交互**，因此主线程任何时候都必须保持很高的响应速度，所以主线程**不允许进行耗时操作**，否则会出现**ANR**（ApplicationNotResponding）异常。

**子线程**就是APP的**工作线程**，子线程都是APP运行过程中**手动创建**的线程，可以在其中处理一些**耗时的操作**例如网络请求、I/O操作等。可以通过使用[RxJava](https://github.com/ReactiveX/RxJava)等函数库来创建用于不同操作的特定子线程。

在Android官方声明中，对于多线程编程时有两大原则：

1. 不要阻塞**UI线程**（即**主线程**）：主线程被阻塞超过5s则会出现ANR错误；
2. 不要在UI线程之外更新UI组件。

那么现在我们来看看如何在代码中创建一个子线程吧。代码如下：

```java
package com.example.yunzeli.testapplication

public class MyWorkingThread extends Thread {
  private boolean isRunning;
  @Override
  public void run() {
    // 开始线程的工作
  }
}
```

可以看到，我们真正需要做的，就是继承[Thread](https://developer.android.com/reference/java/lang/Thread)这个类，并实现run()方法。将我们需要做的工作在run()里完成即可，这个方法简单方便，但是却有一个问题：上面我们说过，当这个线程的任务全部执行结束时，**线程就会被终止**，那么如果我们需要在工作线程完成当前工作后**不被中止**，而是继续**等待新的任务**到来呢？那么就可以这样：

```java
package com.example.yunzeli.testapplication

public class MyWorkingThread extends Thread {
  private boolean isRunning;
  @Override
  public void run() {
    isRunning = ture;
    while (isRunning) {
      // 开始线程的工作
      
      // 线程的工作完成后
      isRunning = false;
    }
  }
}
```

上面的代码中，我们通过设置一个isRunning的flag，可以**用循环的方式来block住这个线程**，然后等待新的任务到来。很好，现在我们有了一个独立运行的，随时可以使用的工作线程。但事实上，Java代码库里给我们提供了一个更加简单清晰，并且可靠的类，这就是[**Looper**](https://developer.android.com/reference/kotlin/android/os/Looper)。

## Looper
从上面的内容我们知道，要想将一个线程block住，我们需要用looping将其不断**循环并等待新的任务**。在Android的开发包里，就有一个设计好的Looper类供我们使用。这个Looper一直在不停的循环并监听新来的task，每个Looper都有一个[**messageQueue**](https://developer.android.com/reference/android/os/MessageQueue)用来存放分配的task，也叫做[**Message**](https://developer.android.com/reference/android/os/Message)。Looper的使用有两个基本原则：

1. **每一个Thread有且只能有一个Looper；**
2. **当想让某一个Thread执行某个任务时，就将message传送给对应Looper的messageQueue。**

Looper在thread的具体使用方法如下：

```java
package com.example.yunzeli.testapplication
import android.os.Looper

public class MyLooperThread extends Thread {
  
  @Override
  public void run() {
    // prepare the loop
    Looper.prepare();
    
    // start looping
    Looper.loop();
    
    // do your staff here...
  }
}
```

- Looper.prepare()：检查当前这个thread是否已经分配好looper，如果没有，则**新建一个looper**来监听这个thread；
- Looper.loop()：looper开始监听当前线程，开始处理收到的message；

简而言之，Looper的作用就是一直**looping and keep thread alive**。值得注意的是，Main Thread也就是**UI线程**，是已经初始化了MainLooper的。所以不需要对于Main Thread进行任何的操作，Android系统已经帮我们都处理好了。

## MessageQueue & Handler
说完了Looper，现在我们来说说Looper里面的一个重要概念：[MessageQueue](https://developer.android.com/reference/android/os/MessageQueue)。当我们想向一个thread里派送一个新的task时，Java规定了我们只能派送如下两种类型：**Message和Runnable**。那么，我们如何将这两种类型的实例派送到目标线程的messageQueue里呢？这里就用到了另一个重要概念：[**Handler**](https://developer.android.com/reference/android/os/Handler)。Handler的功能相当于一个**装配器**，它可以将**任意继承了Message或者Runnable类的实例放入其监听的Looper的messageQueue里**，例如：

```java
public class MyLooperThread extends Thread {
  private static final int MSG_1 = 1;
  private static final int MSG_2 = 2;
  
  @Override
  public void run() {
    // prepare the loop
    Looper.prepare();
    
    // start looping
    Looper.loop();
    
    // send message here
    Handler handler = new MyHandler();
    handler.obtainMessage(MSG_1).sendToTarget();
    handler.post(new Runnable() {
      @Override
      public void run() {
        // do something here...
      }
    });
  }
  
  class MyHandler extends Handler {
    @Override
    public void handleMessage(Message message) {
      
      // switch your message type here
      switch(message.what) {
        case MSG_1:
          // do something here...
          break;
        case MSG_2：
          // do something here...
          break;
        default:  
      }
    }
  }
}
```

由上面例子可见，Handler通过**obtainMessage(Message msg)**的方法来传送message实例，用**post(Runnable runable)**的方法来传送Runnable实例。那么，Handler如何知道所要传送的目标thread呢？Android官方文档里写的很清楚，Handler有两个默认的构造器（constructor），如果使用**new Handler()**的话，则当前线程就会成为创建的Handler的目标线程；而如果使用**new Handler(Looper looper)**的话，则传入的looper对应的线程就会作为创建的Handler的目标线程。总结一下，Hanlder的作用主要就是两点：

1. **从任意thread中将message或者runnable传入其对应的looper的message queue中；**
2. **获取其对应looper的message queue中的下一个task（message或runnable）并执行对应的操作。**

需要强调的一点是：虽然**一个thread只能有一个对应的looper，但是一个looper却可以同时被多个Handler所引用**（允许将同一个Looper传入多个Handler的构造器）以便指定给同一thread的不同handler分别处理不同的message或者runnable。这样的设计也方便了我们对于多个task的处理。

## Message & Runnable
最后关于[Message](https://developer.android.com/reference/android/os/Message)和[Runnable](https://developer.android.com/reference/java/lang/Runnable)再简单介绍一下：

当我们希望在一个thread里执行一段代码（task）时，Android OS 规定了这两种类型作为传入的参数。Message的使用方式在于：我们**预先在Handler里定义好了如何处理这个MESSAGE_TYPE的逻辑，然后将对应的MESSAGE_TYPE传入**。个人理解比较像是pre-define了一个处理逻辑，然后只需要传入对应message就可以trigger这个task；而Runnable是一个Interface，当我们传入时，则**需要重写它的run()方法并将我们想执行的代码逻辑写在里面**。

关于这两种方式的区别，个人认为主要取决于这段逻辑**是否需要传入动态变化的参数**：比如，如果是执行例如`显示一个文本框并高亮`这种固定的，静态的逻辑，那么message的方法是比较好的选择。但如果执行`根据用户输入值来显示字体大小`这种需要参数的逻辑，则runnable的方法或更好些。

## 总结
这一篇只是对于Android多线程一个非常简单的介绍。希望通过我的简单介绍能让读者对于多线程有一个简单直观的认识。Android多线程还有着许多许多复杂的内容，以后会在碰到并解决之后，一一在这里介绍给大家的。让我们共同学习，共同进步，谢谢！

###  参考文章：
[Understanding Android Core: Looper, Handler, and HandlerThread](https://blog.mindorks.com/android-core-looper-handler-and-handlerthread-bd54d69fe91a)

[A journey on the Android Main Thread — PSVM](https://medium.com/square-corner-blog/a-journey-on-the-android-main-thread-psvm-55b2238ace2b)

[Android Handler Internals](https://medium.com/@jagsaund/android-handler-internals-b5d49eba6977)

[Android多线程](https://www.jianshu.com/p/7a8cb20cfd80)

[Multithreading Interview Question](https://www.javatpoint.com/java-multithreading-interview-questions)