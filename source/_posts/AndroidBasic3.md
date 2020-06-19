---
title: Android开发笔记之基础篇（三）
date: 2018-10-13 09:58:01
thumbnail: /thumbnails/AndroidBasic.jpg
toc: true
tags:
    - Android
---

今天的内容是这周工作中出现的一个相关内容的学习：都说在Android的开发中，多线程（Multi-thread）是非常重要的一环，那么我们就来看看和Android OS相关的Thread, Looper, Handler这几个组件各自的概念和实例。

<!-- more -->

## Thread
Thread(线程)本身是java.lang.Thread这个包里的非常基本的一个Class，它定义了一个java的线程。在Android的环境里，一个线程可以理解为一群任务的有序集合。当这个线程的任务全部执行结束时，线程就会被终止。那么，对于多线程来说，如何可以让一个线程不被中止，而是持续等待新的任务呢？我们可以在线程内这样：

```java
package com.example.yunzeli.testapplication

public class MyThread extends Thread {
  private boolean isRunning;
  @Override
  public void run() {
    isRunning = ture;
    while (isRunning) {
      // do some tasks here...
      
      // after finish the job
      isRunning = false;
    }
  }
}
```

上面的代码很清晰，我们通过设置一个flag，可以用loop的方式来block住这个thread，然后等待新的任务到来。但是，Java代码库里给我们提供了一个更加简单清晰，并且可靠的工具，这就是Looper。

## Looper
从上面的内容我们知道，要想将一个线程block住，我们需要一个执行一个loop并不停的等待新的任务。在Android的开发包里，就有一个设计好的Looper类供我们使用。这个Looper一直在不停的循环并监听新来的task，每个Looper都有一个messageQueue用来存放分配的task，也叫做Message。关于MessageQueue的内容我们下面会讲到。Looper的使用有两个基本原则：

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

Looper.prepare()方法主要是检查当前这个thread是否已经分配好looper，如果没有，则新建一个looper来监听这个thread，而Looper.loop()则是当前的looper开始监听线程，之后就可以处理相应的message了。简而言之，Looper的作用就是一直looping并且keep thread alive。值得注意的是，MainThread，也就是UIThread，是已经初始化了MainLooper的。所以不需要对于MainThread进行任何的操作。

## MessageQueue & Handler
说完了Looper，现在我们来说说Looper里面的一个重要组成部分：MessageQueue。当我们想向一个thread里派送一个新的task时，Java规定了我们只能派送如下两种类型：Message和Runnable。那么，我们如何将这两种类型的实例“派送”到thread的messageQueue里呢？这时就用到了Handler。Handler的功能相当于一个“配装器”，它可以将任意的Message或者Runnable放入其监听的Looper的messageQueue里，例如：

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

由上面例子可见，Hanlder通过obtainMessage(Message msg)的方法来传送message类型，用post(Runnable runable)的方法来传Runnable类型。那么，Handler如何知道所要传的Looper呢？其实Android官方文档里写的很清楚，Handler有两个default的constructor，如果使用new Handler()的话，则当前call这个constructor的thread的looper，就会成为这个handler的looper；如果使用new Handler(Looper looper)的话，则可以使用传入的looper作为handler的target。总结一下，Hanlder的作用主要就是两点：

1. **从任意thread中将message或者runnable传入其对应的looper的message queue中；**
2. **获取其对应looper的message queue中的下一个task（message或runnable）并执行对应的操作。**

需要强调的一点是：虽然一个thread只能有一个对应的looper，但是一个looper却可以同时被多个Handler所引用（允许将同一个Looper传入多个Handler的构造器）以便指定给同一thread的不同handler分别处理不同的message或者runnable。这样的设计也方便了我们对于多个task的处理。

## Message & Runnable
最后关于Message和Runnable再简单介绍一下，当我们希望在一个thread里执行一段代码（task）时，Android OS 规定了这两种类型作为传入的参数。Message的使用方式在于：我们预先在Handler里定义好了如何处理这个MESSAGE_TYPE的逻辑，然后将对应的MESSAGE_TYPE传入。个人理解比较像是pre-define了一个处理逻辑，然后只需要传入对应message就可以trigger这个task；而Runnable是一个Interface，当我们传入时，则需要重写它的run()方法并将我们想执行的代码逻辑写在里面。所以关于这两种方式的区别，个人认为主要取决于这段逻辑是否需要重复执行并且有不用的参数：比如，如果是执行“显示一个文本框并高亮”，那么可以用message的方法，因为这段逻辑是重复执行的；但如果执行“根据用户选择不同来显示不同布局”，需要传入用户选择数据，则runnable的方法或更好些。

## 总结
这一篇只是对于Android OS一个非常简单的介绍。希望通过我的简单介绍能让读者对于Android OS的组件有一个简单直观的认识。AndroidOS的世界还有许多许多复杂的内容，都是基于我们今天介绍的这几个概念产生的。以后我会在慢慢碰到并解决之后，一一在这里介绍给大家的。让我们共同学习，共同进步，谢谢！

###  参考文章：
[Understanding Android Core: Looper, Handler, and HandlerThread](https://blog.mindorks.com/android-core-looper-handler-and-handlerthread-bd54d69fe91a)

[A journey on the Android Main Thread — PSVM](https://medium.com/square-corner-blog/a-journey-on-the-android-main-thread-psvm-55b2238ace2b)

[Android Handler Internals](https://medium.com/@jagsaund/android-handler-internals-b5d49eba6977)

[Understanding Activity.runOnUiThread()](https://medium.com/@yossisegev/understanding-activity-runonuithread-e102d388fe93)