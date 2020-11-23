---
title: RxJava基础概念梳理
date: 2020-08-06
thumbnail: /thumbnails/RxJava.png
toc: true
categories:
  - Technical
  - Android
tags:
  - Chinese
  - RxJava
---

Reactive programming这个概念我在最近的安卓开发中极其频繁的接触到，但是对于从没有接触过的我来说，很多概念还是有点抽象和难以理解。所以这篇博客就来梳理一下在进行了一些了解和学习后，关于所谓“响应式编程”的我自己的理解。这里的例子主要都是应用于RxJava中，但实际上在稍微接触了RxSwift以及RxCocoa之后，我认为它们的基本概念都是相同的，所以了解RxJava之后，RxSwift和RxCocoa就完全不在话下。

<!-- more -->

## The 3 O's

如果你熟悉设计模式中的[观察者模式](https://www.runoob.com/design-pattern/observer-pattern.html)， 那么简单来说，Rxactive programming就是一种更为复杂和多样的**观察者模式的应用**。首先要介绍的，就是在RxJava中出于最核心部分的三个概念： [Observable](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html)， [Observer](http://reactivex.io/RxJava/javadoc/io/reactivex/Observer.html)和[Operator](http://reactivex.io/documentation/operators.html)，简称 **the 3 O's**。

### Observable

Observable直译就是”可被观察的“，我认为可以把它理解成一种”**数据源**“，就是源源不断的产生数据的”工厂“。通常当我们需要任何数据时，我们都可以创建一个Observable实例并**在其中声明产生数据的方法**，比如网络请求，进行复杂运算，或等待用户输入等等。当需要获取对应数据时，它会依次发送出需要的数据，形成**数据流**，并被Observer依次获取。

创建一个Observable的方法有很多，最基础的方法就是使用`Observable.create()`方法并传入一个`Observable.OnSubscribe`实例，例如：

```java
Observable<Integer> observable = Observable.create(new Observable.OnSubscribe<Integer>() {
   @Override public void call(Subscriber<? super Integer> subscriber) {
       subscriber.onNext(1);
       subscriber.onNext(2);
       subscriber.onNext(3);
       subscriber.onCompleted();
   }
});
```

在这个例子中，observable会依次发送1，2，3三个值，然后声明自己完成发送并结束。这里需要注意的是，如果不执行onCompleted()方法，那么Observable就会被认定**依旧运行**，这个对象也**不会被回收**直至一个onCompleted()或者onError()方法被执行或者observable本身被销毁（会存在**内存泄漏**的问题）。并且Observable在没有任何observer订阅时，**不会发送任何数据**。

为了更加方便的创建observable，RxJava又为我们提供了一些快速创建的方法，比如创建一个和上边一样的observable，也可以使用如下代码：

```java
Observable.just(1,2,3);
```

通过这种方式，我们可以更加快速直观的创建一个Observable并且看到需要发送的数据，但是这仅适用于发送简单数据的情况。

### Observer

说完Observable，下面就是对应的“观察者”**Observer**了。顾名思义，observer就是数据的“**接收方**”，也就是对于**接收数据进行响应**的对象。相比于Observable，observer的使用方法更为简单，我们只需要创建一个继承Observer接口：`onNext()`, `onComplete()`和`onError()`的实例，然后执行`observable.subscribe()`**订阅方法**并将observer传入即可。例如：

```java
// 创建Observer
Observer<Object> observer = new Observer<Object>() {

    @Override
    public void onCompleted() {
      Log.d("Test", "In onCompleted()");
    }

    @Override
    public void onError(Throwable e) {
      Log.d("Test", "In onError()");
    }

    @Override
    public void onNext(Object s) {
      Log.d("Test", "In onNext():" + integer);
    }
 };
// Observable订阅Observer
observable.subscribe(observer);
```

通过`subscribe()`的方法进行订阅后，observer就可以直接接收到所有从observable发出的数据，不仅十分方便，代码也十分直观。

### Operator

相比于Observable和Observer，Operator更像一个辅助性的“**中间商**”。它负责处理observable传递过来的原始数据，将其**转化（transform & polish）成**observer需要的数据类型，再传给订阅的observer。通过这种方式，可以**让数据类型的转换更为方便快捷**，整体的代码也更为清晰明了。常用的一些operator方法包括：

- `map()`：对于每个数据都执行一个对应的function，执行结果与原数据**一一对应**并输出；
- `flatMap()`：对于每个数据都执行一个对应的function，执行结果**全部存入**一个Observable并输出（`flat`意思就在于此），相当于**一对多**的对应关系；
- `groupBy()`：对于每个数据源按grouping rule进行mapping，结果**按不同group**存入不同observable，最终输出observable set；
- `filter`：顾名思义，按照某种条件对其**进行筛选**并将符合筛选条件的数据输出。

所有build-in operator可以在[这里](http://reactivex.io/documentation/operators.html#alphabetical)找到，里面也有十分详尽的对于每个operator作用的介绍，关于Operator更为详细的介绍，以后会专门在写一篇文章，这里不再赘述。

综合上面对于三个基本概念的介绍，我们可以知道，Reactive Programming主体逻辑的流程图大致就是：

<img src="https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/RxJava-the3Os.png" style="zoom:120%;" />

## Multi-threading

首先要说明的是，RxJava本身在不声明任何scheduler时是**默认单线程运行**的，但实际使用时很多时候我们都需要在不同线程上去进行数据的产生和传递，这是就需要用到它的**多线程(Multi-threading)**特性。RxJava的多线程控制主要由两个方法来决定：**subscribeOn()** 和**observeOn()**。

### subscribeOn()

subscribeOn()这个方法用于指定**Emitter**（Observable，Single，Flowable等等）在哪一个thread上**运行并产生数据**，它的具体声明位置对于代码的执行**没有任何影响**，无论声明的先后顺序。举个例子：

```kotlin
Observable.just("1", "2", "3", "4", "5")
		.subscribeOn(Schedulers.computation())
    .flatMap { value -> Observable.just(value.toInt()) }
    .subscribe {
     		print(it)
    }
```

```kotlin
Observable.just("1", "2", "3", "4", "5")
    .flatMap { value -> Observable.just(value.toInt()) }
		.subscribeOn(Schedulers.computation())
    .subscribe {
     		print(it)
    }
```

以上两段代码，它们在运行逻辑上没有任何区别，Observable都是在computation thread上运行。

### obverseOn()

observeOn()这个方法用于指定**Observer**在哪一个thread上去**处理Observable传递出来的数据**，它只对**当前声明位置下方**的代码产生影响，换个方式来说，当代码执行到obverseOn()时，**会切换线程并继续执行**后面的逻辑。例如：

```kotlin
Observable.just("1", "2", "3", "4", "5")
		.observeOn(Schedulers.computation())										// 切换到 computation thread
		.flatMap { value -> Observable.just(value.toInt()) }		// computation thread上执行
		.observeOn(Schedulers.io())															// 切换到 io thread
 		.map { value -> value * value }													// io thread上执行
   	.subscribe {
    		print(it)																						// io thread上执行
    }
```

### Scheduler

从上面的例子可以看出，**subscribeOn()**和**observeOn()**这两个方法都是通过**传入Scheduler的类型**作为参数来**保证数据的产生，处理和接收都发生在对应的thread**里的。那么Scheduler的类型都有哪些呢？这里就列举了一些Android平台上最为常用的Scheduler类型以及对应的适用情况：

- `Scheduler.io()` 最常使用的一种scheduler，主要用于处理IO操作例如网络请求，文件读写操作；
- `Scheduler.computation()` 处理与计算相关的操作，数量上限等同于处理器内核的数量；
- `Scheduler.newThread()` 创建一个新的scheduler，要尽量避免创建过多scheduler，尽量多使用已经存在的线程；
- `Scheduler.single()` 在一个独立的单线程处理操作，类似于一个后台独立的main thread；
- `Scheduler.trampoline()  ` 用于即时处理操作，它会立刻开始并严格按顺序处理所有当前线程上的Observable；
- `AndroidSchedulers.mainThread() ` 安卓的主UI thread，必须注意不要让它被费时间，有延时的操作block住，否则会**ANR**。

## Emitter

除了Observable这个“数据源工厂”，RxJava还有许多其他的类型，它们被统称为Emitter，也就是“发射器“。它们都是产生数据或者获得数据的来源，但是不同的类型又有一些不同的特性，在这里列出几种除了Observable以外常用的Emitter类型：

### Flowable

Flowable和Observable的用法几乎一摸一样，唯一的区别在于Flowable**可以处理Backpressure的情况**。所谓Backpressure，就是当Emitter的**数据产生的速度过快**，而Observer接收端**处理数据的速度过慢**时，数据会堆积在Emitter的memory里等待发送，累积到一定程度时就会抛出**OutOfMemoryException**异常。

Flowable通过使用`BackpressureStrategy.DROP`的策略，也就是在memory即将存满时丢弃一些数据的方法，来处理backpressure的问题。Observable也可以通过`observable.toFlowable(BackpressureStrategy.DROP)`的方法转换为Flowable来进行使用。

### Single

Single是一个非常简单的发射器，顾名思义，它**仅在获取并发射一个单独的数据实例**之后便结束发送。如果数据获取并发送成功，运行`onSuccess()`；发送失败的话则运行`onError()`。这种类型在我们**返回单个数据**时十分实用。Single的示例代码：

```kotlin
Single.just("This is a single")
		.subscribe(
				{ v -> print("value is $v") },
				{ e -> print("error is $e") }
		)
```

### Maybe

Maybe和Single类似，其区别在于：Maybe有可能**不发送任何数据**就结束发送。这种类型主要用于**获取一些optional的数据**时使用，因为它可能存在，也可以不存在。如果数据获取并发送成功，运行`onSuccess()`；发送失败的话，运行`onError()`；而**没有获得数据的话则运行`onComplete()`**。这种类型在我们**返回单个optional数据**时十分实用。Maybe的示例代码：

```kotlin
Maybe.just("This is a single")
		.subscribe(
				{ v -> print("value is $v") },
				{ e -> print("error is $e") },
				{ print("Completed") }
		)
```

### Completable

相比于其他所有的Emitter的最大区别就是：它**不发射数据**，而是仅关心**操作是否成功完成**。如果成功完成，运行`onComplete()`，如果失败，则运行`onError()`。Completable就像是RxJava版的`Runnable`，可以通过`addThen()`方法进行连接并执行。同时，上面列举出的所有发射器类型，都可以通过例如`Completable.fromSingle(Single.just("this is single"))`的方式转换为Completable并**串联其他Completable**进行运行。Completable示例代码：

```kotlin
Completable.create { emitter ->
		emitter.onComplete()
		emitter.onError(Exception())
}

Completable
		.fromSingle(Single.just("This is a single"))
		.subscribe(
				{ print("complete!") },
				{ print("error!") }
		)
```

## Subject

前面说到，Operator主要用于对传送数据进行**处理和转化**。但是对于一些实际使用场景来说，operator还是不够灵活。于是RxJava又提供了**Subject**这个类型来让我们更近灵活地进行数据的传输。

> A Subject is a sort of bridge or proxy that is available in some implementations of ReactiveX that acts both as an observer and as an Observable. 

上面是官方文档关于Subject的解释，简单来说Subject就像是一个**连接不同Observable和Observer的”桥梁“**。它既可以作为Observable来发送数据，也可以作为Observer来接收数据。通过Subject，我们可以将任意Observable的数据进行接收，处理，并再次发送给其他Observer。关于Subject，其实RxJava里还有许多其他的类型。这里只列举最为常用的四种类型，其余类型会在以后碰到时补充更新在这里：

### PublishSubject

比较简单的一种Subject类型，**会在subscription之后将所有数据依次发送**。值得注意的是：PublishSubject会**在初始化后**立刻开始发送数据，而不是有Observer subscribe之后发送，所以会存在**observer接收数据不完整**的情况。对于这种情况，ReactiveX给出了两种方式解决：1. 使用`Create()`方法并在初始化前**确认observer已经完成订阅**；2.使用下面要介绍到的**ReplaySubject**。

### BehaviourSubject

相比于PublishSubject，BehaviourSubject最大的特点就是：它会**发送Subscription之前的最后一个数据（last emitted data）以及Subscription之后将所有数据**。除此之外，它和PublishSubject的特点基本一致。

### ReplaySubject

ReplaySubject会**发送Observable产生的全部数据，无论是subscription之前还是之后**。其内部就是用一个`List`动态存储所有接收的数据，并在subscription时发送给Observer。这也就是上面说到的为什么可以使用`ReplaySubject`来确保Observer**接收数据的完整性**。

### AsyncSubject

AsyncSubject**仅会发送Observable产生的最后一个数据，无论是subscription之前还是之后**。

## 总结

这是关于RxJava的第一篇总结文章，主要介绍了一些比较基础的概念。之后会继续写一些没有涉及到的概念比如Subject，更多Operator的具体用法，以及比较RxJava和RxSwift的一些异同等等。Reactive programming是非常强大的工具，可以让我们更轻松的管理数据流并实现多线程的同步，希望这篇文章中的概念可以对于响应式编程有一个更清晰的理解，happy coding!。

### 参考文章

[ReactiveX 官方文档](http://reactivex.io/intro.html)

[Meet RxJava: The Missing Reactive Programming Library for Android](https://www.toptal.com/android/functional-reactive-android-rxjava)

[Fundamentals of RxJava with Kotlin for absolute beginners](https://medium.com/@gabrieldemattosleon/fundamentals-of-rxjava-with-kotlin-for-absolute-beginners-3d811350b701)

[Reactive X: RxJava Data Flows: Observable, Flowable, Single, Maybe and Completable](https://bugfender.com/blog/data-flows-in-rxjava2-observable-flowable-single-maybe-completable/)

[RxJava/RxAndroid使用实践实例](https://www.jianshu.com/p/031745744bfa)

