---
title: Android开发笔记之基础篇（五）- 易混淆概念
date: 2020-10-24
thumbnail: /thumbnails/AndroidBasic.jpg
toc: true
categories:
  - 技术博客
  - Android
tags:
  - Kotlin
  - Java
  - Chinese
---

这篇文章中将会列出在Android开发中经常遇到并且容易混淆的概念，他们基本都是成对出现并且需要进行比较异同的。这其中不光设计安卓的知识，也有关于Java和Kotlin语言特性的一些基本概念。在面试时很多成对的概念会同时出现并被问到异同，这篇文章也会持续更新碰到的各种易混淆概念。

<!-- more -->

## Kotlin语言特性

###lateinit VS lazy

首先来看一下使用`lateinit`的代码示例：

```kotlin
class LateinitTest {

    private lateinit var manager: Manager

    fun setupManager(manager: Manager) {
        this.manager = manager
    }

    fun run() {
        manager.run()
    }
}
```

然后是`lazy`的代码示例：

```kotlin
class LazyTest {

    private val manager = lazy { ManagerProvider.provideManager() }

    fun run() {
        manager.value.run()
    }
}
```

可以看出这两种初始化方式有一个非常明显的区别：

> lateinit用于**可变量**`var`， 而lazy用于**不可变量**`val`。

如果从他们的具体作用来看，这其实很容易理解：`lateinit`用于class initialization时**延期加载**一个property属性，它可以**随时在这个class的其他方法中**（除了primary constructor）**通过赋值的方式进行初始化加载**，所以这个property必须是可变的variable。`lateinit`的主要使用场景包括：dependency injection以及non-null property的推迟加载（懒加载）。

`lazy`的具体作用则是：通过接收一个**lambda function**给property属性进行加载，后续再次调用property的`get()`函数时则会使用lambda返回的对象**而不会再次初始化**，也就是说lambda**只会执行一次**但获得的结果**会持续返回**，所以需要使用不可变量来保证lambda结果的唯一。`lazy`的主要使用场景包括：单例模式（if-null-then-init-else-return）。

总体来说，`lateinit`比`lazy`的适用范围更大，可以用`lazy`的地方都可以用`lateinit`但是反之却不行。但是对于单例模式来说使用lazy可以大量节省初始化资源，所以当可以使用的时候还是应该优先考虑`lazy`。



## Android SDK相关

###Serializable vs Parcelable

`Serializable`是一个**Java Interface**，通过实现这个接口，可以轻松的将一个POJO（plain old java object）**在Activity中进行传递**。实现`Serializable`接口**不需要Override任何方法**，但是因为它使用了**Java Reflection API**所以在编译过程中会**生成很多额外的文件**，有时会触发垃圾回收（garbage collection），浪费系统资源以及设备电量。

`Parcelable`是**Android SDK**中的一个**Android Interface**，它不需要使用Java Reflection API 所以**不存在冗余文件的问题**，可以节省资源。但是实现`Parcelable`接口**需要额外实现包括`writeToParcel`，`describeContents`以及`Parcelable.Creator`在内的几个方法**，同时也会使得POJO中的代码难以理解和阅读。

总体来说，`Serializable`实现简单，代码易读；而`Parcelable`高效迅速，降低功耗。两者各有优劣，现在还有**第三种方式**，就是使用`Serializable`并实现**writeObject()**以及**readObject()**这两个方法来**避免Reflection自动生成的多余文件**，这相当于一种折中和妥协。只要根据具体使用场景来判断哪一种更好就可以了，具体问题，具体分析。



## Java语言特性相关

###Overriding vs Overloading

**TL,DR:**

>`Overloading` occurs when two or more methods in one class have the **same method name but different parameters**.
>
>`Overriding` means having two methods with the **same method name and parameters** (i.e., *method signature*). One of the methods is in the parent class and the other is in the child class.

这是一个经常出现在面试中的经典问题，简单来说`Overriding`是指在**同一个class内声明方法名相同但参数不同的多个方法**（two or more methods **in one class** have the **same method name but different parameters**），而`Overloading`则是指**在子类中声明一个和父类名称，参数均相同的方法**，但却有不同的具体实现（two methods with the **same method name and parameters**， one is in the **parent class** and the other is in the **child class**）。代码实例如下：

```kotlin
open class iPhone {

    // Overloading examples
    open fun start(password: String) {
        print(password)
    }

    open fun start(fingerPrint: FingerPrint) {
        print(fingerPrint)
    }

    open fun start(face: Face) {
        print(face)
    }
}

class iPhone8 : iPhone() {

    // Overriding examples
    override fun start(password: String) {
        print("iPhone8 $password")
    }

    override fun start(fingerPrint: FingerPrint) {
        print("iPhone8 $fingerPrint")
    }
}

class iPhone11 : iPhone() {

    // Overriding examples
    override fun start(password: String) {
        print("iPhone11 $password")
    }

    override fun start(face: Face) {
        print("iPhone11 $face")
    }
}
```

代码中`iPhone`的三个不同参数的**start()**方法是`Overloading`，而`iPhone8`和`iPhone11`中的start()方法则是`Overriding`，且因为在各自内部方法名一样，参数不一样，所以也是`Overloading`。

除此以外，`Overloading` 和`Overriding`的区别还有以下几点要注意：

1. `Overloading`发生在编译时（**complie time**），`Overriding`发生在运行时（**runtime**），所以从performance来说`Overloading`更高效；
2. 静态方法（**static method**），**final method** 和 **private method** 均可以`Overloading`，但都不可以`Overriding`，因为子类不可以对父类的以上三种方法进行修改；
3. `Overloading`属于静态绑定（**static binding**）而`Overriding`属于动态绑定（**dynamic binding**）；
4. `Overloading`时返回值类型**可以不同**。



### Volatile vs Synchronized

**TL, DR:**

>Use `Volatile` when you variables are going to get read by **multiple** threads, but written to by only **one** thread.
>
>Use `Synchronized` when your variables will get read and written to by **multiple** threads

这两个关键字的用法与Java虚拟机本身的操作特性有关。首先要知道，在Java中当多个CPU线程同时工作，**每个CPU线程都可以从内存空间（main memory）获取需要的共享变量**，存入当前线程的CPU缓存（CPU cache）中并使用。大致工作流程如下图：

<img src="https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/Java%20Multithread%20Model.png" style="zoom:120%;" />

所以这里就要先介绍一下关于工作流程中共享变量的两个特性：**“可见性”**和**“原子性”**:

**可见性**（memory visibility）：指当一个线程**对共享变量进行修改**的时候，会强制其**在内存空间里进行更新**以便其他线程使用更新后的值；

**原子性**（memory atomic）：指在**线程修改共享变量到内存空间更新之间**，其他线程**不可以**从内存空间内读取旧的，未经过更新的共享变量值（保证每个人拿到的都是**最新值**）；

在定义了这两个特性之后，我们可以来介绍一下`Volatile`和`Synchronized`这两个关键字的区别：

`Volatile`可以保证每个线程中共享变量的**“可见性”**，但无法保证**“原子性”**。也就是说，使用`Volatile`的方法有可能会获取到**旧的，未更新过的**共享变量值。因为**当前线程对于共享空间的更新操作和其他线程的读取操作会有Race Condition出现**。所以一般`Volatile`适用于**只需要读取，不需要写入内存空间**的场景；

`Synchronized`可以同时保证每个线程中共享变量的**“可见性”**与**“原子性”**，相比于`Volatile`， `Synchronized`**更可靠**，但同时**开销也更高**。它可以保证在同一时间片段内**有且只有一个线程可以对内存空间的共享变量进行操作**，包括读取，计算和更新。所以一般`Synchronized`更适用于需要**同时进行读取和写入内存空间**的场景。



###  参考文章：

[Learning Kotlin - lateinit vs lazy](https://blog.mindorks.com/learn-kotlin-lateinit-vs-lazy#:~:text=lazy%20can%20only%20be%20used,the%20object%20is%20seen%20from.)

[Overloading and overriding in java](https://beginnersbook.com/2014/01/difference-between-method-overloading-and-overriding-in-java/)

[Parcelable vs Serializable](https://android.jlelse.eu/parcelable-vs-serializable-6a2556d51538)

[Volatile vs Synchronized](https://jorosjavajams.wordpress.com/volatile-vs-synchronized/)