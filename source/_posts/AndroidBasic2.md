---
title: Android开发笔记之基础篇（二）
date: 2020-06-16
thumbnail: /thumbnails/AndroidBasic.jpg
toc: true
categories:
  - Android
tags:
  - Chinese
---

之前的[Android开发笔记之基础篇(一)](https://yunze-li.github.io/2018/09/22/AndroidBasic1/)中主要讲到了一些关于Android Components的基本概念以及它们的异同。今天就来讲解一下关于Activity中涉及UI部分的三个核心组件：**Activity**，**Fragment**和**Dialog**。它们基本承担起了APP中所有的图形界面和交互逻辑，也是面向用户最直观的展现。

<!-- more -->

## Activity
前面我们说到过，在Activity中定义了与用户交互的所有逻辑（**Presentation Layer Logic**）。举个例子，我们想做一个电话本App，那么就需要设计当用户点击某个按钮时，其背后发生的一切逻辑，而这些定义都包含在Activity中。那么，一个Application中可以包含多个Activity么？答案当然是可以的，而且是几乎必须的！想象一下在这个Application中可能会出现的界面包括：

- 基本的浏览电话本页面（DashboardActivity），显示一个联系人的列表；
- 当选中一个电话时，要显示一个详细联系人的页面（ContactActivity）；
- 点击电话号码时，弹出一个拨打电话的界面(DialingActivity)；
- Dashboard里要有一个“编辑”按钮，点击后弹出一个新建联系人界面（NewContactActivity）

可以看出，这每一个单独的界面，都是一个专门的Activity。也正是因为一个APP中有多个Activity的存在，才使整个安卓App的架构变得更加清晰，也更有利于我们阅读，重构以及debug。

对于一个Activity来说，可以采用多种不同的设计模式（Design Pattern）比如**MVC**，**MVVM**以及**MVP**。这其中我个人非常喜欢MVP（Model-View-Presenter）来设计代码。对于不同的Activity，出于解耦（De-coupling）的考虑，通常将它们放入不同的package中，方便区分以及日后对它们分别进行重构。像如上我们举例的电话App，它的结构大致就是这样：

![](https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/MyPhoneApplication.png?token=AOJCUFZM2R5H3WDFIJR7SQC65W7KK)

在上一次我们说到，AndroidManifest这个文件里定义了所有App所需要的Activity，Service，BoardcastReceiver, ContentProvider等等，所以不要忘记在创建完Activity之后，一定要在Manifest里面把它声明好。

### Activity Lifecycle

关于生命周期（Lifecycle），我们只需要知道它定义了一个组件什么时候被初始化，什么时候对应什么状态，以及应该在何时进行何种操作即可。Activity的生命周期如下图所示：

<img src="https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/Activity_Lifecycle.png" alt="Activity Lifecycle" style="zoom: 80%;" />

让我们来一步步说明一下各生命周期方法的作用：

1. **onCreate()**：必须实现，主要用于**初始化**activity以及设定各种参数；
2. **onRestart()**：onStop()后如果没有被kill，会从这里唤醒，早于onStart()；
3. **onStart()**：系统正在启动，**UI已显示，但无法交互**；
4. **onResume()**：UI已显示，**用户可以进行交互**，而如果在Acitivity内部初始化其他Dialog或Fragment时，不论activity是否全屏，它都会保持在此状态；
5. **onPause()**：最好在此释放CPU，保存数据；
6. **onStop()**：activity完全**从前台结束**，看不见时进入此状态；
7. **onDestory()**：activity被**完全销毁**，可以做回收和最终释放。

另外值得注意的一点就是：当设备进行横竖屏切换时：acitivity会销毁并重新初始化。如不销毁，需在manifest对应的activity内部声明`configChange=“orientation | screenSize”`, 此时**onConfigurationChanged()**会被call。

## Fragment

在说完Activity之后，下面我们要说一下Fragment这个重要的component。在各种Android的档案定义里，都会反复出现对于Fragment的定义：`A Fragment represents a behavior or a portion of user interface in a FragmentActivity. You can combine multiple fragments in a single activity to build a multi-pane UI and reuse a fragment in multiple activities`. 但是我一直就觉得这个解释非常不明确，为什么我们需要用Fragment呢？我个人的理解是：**当在一个Activity内有多个单独显示的页面，并且每个页面与用户交互的逻辑都各不相同时，Fragment可以帮助我们减少很多麻烦，将整个Activity的架构变得十分清晰**。这是我认为Fragment最为有用的一点。

还是以前面的电话本App为例，在拨打电话的界面DialingActivity中，可能会分为三个阶段：

1. 接收到来电（InComingCall）
2. 电话被接通（Calling）
3. 通话结束（CallEnded）

根据需求不同，这三个阶段的界面可能会有完全不同的UI布局，每个按钮也可能会有不同的功能，但是它们同属于一个打电话的功能之中（DialingActivity）。此时如果我们使用三个不同的Activity，当然是可以的，这不仅意味着我们需要在各个activity之间传递各种各样的参数，而且处理不同Activity之间的沟通会变得十分麻烦，也不利于我们之后对于代码的维护。

此时就轮到Fragment出场了，我们可以在DialingActivity里定义三个Fragment：InComingCallFragment，CallingFragment以及CallEndedFragment，每个Fragment都可以有自己的model和presenter（MVP pattern）而在DialingActivity里，我们可以定义三个Fragment的共同行为逻辑。通过这种方式，可以使整个Activity结构清晰，分工明确，它的结构大概是这样：

![](https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/MyPhoneFragment.png?token=AOJCUFYRFSRJYNGGNMYPWFK65W7M2)

Activity和Fragment都是关于UI部分的重要组件。一般来说对于一个比较成熟的App来说，其包含的Activity和Fragment可能是成百上千的，所以熟练掌握它们的使用方法是极为重要的。

### Fragment Lifecycle

<img style="float: right;" src="https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/fragment_lifecycle.png" style="zoom:60%;" />Fragment的生命周期相比于Activity要简单不少，下面分别简单介绍一下各生命周期方法下的作用：

1. **onAttach**(Context context)**：与调用的Activity发生联系**，可以通过**getActivity()**获取调用它的Activity实例；
2. **onCreate**(Bundle savedInstanceState)：从bundle中获取从Acitivity传来的数据，可用于**初始化**；
3. **onCreateView**(LayoutInflater, ViewGroup, Bundle)：**创建视图**，inflater用于装载局部文件，viewGroup是父标签对象；
4. **onViewCreated**(View, Bundle)：**此时View创建完成，但还没有显示**， View就是之前创建的对象；
5. **onActivityCreated**(Bundle)：窗口初始化已完成，可以通过**findViewById()**来找到Activity中的view；
6. **onStart**()：此时**UI已经显示**在屏幕上，但无法交互；
7. **onResume**()：可以与用户**开始进行交互**了；
8. **onPause**()：可以保存一些**临时性的暂定工作**，比如播放器的保存音乐播放进度，以便在OnResume里恢复；
9. **onStop**()：fragment从**屏幕上消失**；
10. **onDestroyView**()：**移除所有视图**，所有在onCreateView中的视图都将被移除；
11. **onDestroy**()：此时Activity还是和Fragment保持联系的，可以获得Fragment对象，**无法进行任何操作**；
12. **onDetach**()：最后一步,执行过后Activity与Fragment**不再有任何关联**。

此外，关于Fragment生命周期还有几点值得说明：

- Fragment的引入，主要是为了在大屏幕上显示更加灵活的界面设计，比如在平板电脑上，可以同时并排显示两个Fragment；
- Fragment必须始终托管在 Activity 中，其生命周期直接受宿主 Activity 生命周期的影响，当Activity暂停时，其所有Fragmenty也会暂停，Activity被销毁时，其所有Fragment也会被销毁。

## Dialog

相比于Activity和Fragment，Dialog就简单一些。Dialog主要应用于一些临时的对话框，比如向用户询问是否允许开启一些权限[AlertDialog](https://developer.android.com/reference/android/app/AlertDialog)，让用户选择一个时间[TimePickerDialog](https://developer.android.com/reference/android/app/TimePickerDialog)，或者自定义界面进行选择[DialogFragment](https://developer.android.com/reference/androidx/fragment/app/DialogFragment)。初始化一个简单的Dialog的语法是：

```kotlin
// 使用Builder class来定义AlertDialog的属性
val builder = AlertDialog.Builder(this)
builder.setMessage(R.string.your_dialog_message)
    .setPositiveButton(
        R.string.ok,
        DialogInterface.OnClickListener { dialog, id ->
            // 定义用户按下OK按钮后的行为
        })
    .setNegativeButton(
        R.string.cancel,
        DialogInterface.OnClickListener { dialog, id ->
            // 定义用户按下CANCEL按钮后的行为
        })
// 创建一个AlertDIalog实例
builder.create()             
```

上面的代码会创建出一个带有两个按钮的对话框，并且根据用户的选择来运行相对应的逻辑。

### Dialog Lifecycle

Dialog由于其特殊性，并不存在复杂的生命周期，它在初始化之后显示在用户界面上，随着用户的交互获得结果后被销毁。唯一的例外就是DialogFragment，因为它是Fragment的子类，所以其生命周期遵从于其父类Fragment，这里也就不再赘述了。

## 参考链接

[Activity](https://developer.android.com/reference/android/app/Activity)  - Google Android 官方说明文档

[Fragment](https://developer.android.com/guide/components/fragments)  - Google Android 官方说明文档

[Dialog](https://developer.android.com/guide/topics/ui/dialogs)  - Google Android 官方说明文档