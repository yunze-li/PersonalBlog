---
title: Android开发笔记之基础篇（二）
date: 2018-10-13 09:44:54
thumbnail: /thumbnails/AndroidBasic.jpg
toc: true
tags:
    - Android
---

在之前的[基础篇(一)](https://yunze-li.github.io/2018/10/13/AndroidBasic1/)中主要讲到了一些关于Activity，Service，BoardcastRecevier的基本概念以及它们的异同。今天就来讲解一下关于Activity中UI部分的一些架构：主要就是Activity，Fragment，Dialog这三个不同的Component。

<!-- more -->

## Activity
在前面我们说到过，在Activity中定义了与用户交互的所有逻辑（Presentation Layer Logic）。举个例子，我们想做一个电话本管理的App，那么就需要设计当用户点击某个按钮时会发生什么，这些定义都包含在Activity中。那么，一个Application中可以包含多个Activity么？答案是可以的，而且是几乎必须的！想象一下在这个电话本管理的App中，我们可能会有基本的浏览电话本页面（Dashboard），当选中一个电话时，要显示一个详细联系人的页面；点击电话号码时，要弹出一个拨打电话的界面；在dashboard里要有一个“编辑”按钮，点击后弹出一个新建联系人界面等等。。。为了让我们的APP结构清晰易懂，我们一般会设计多个Activity来执行不同的功能，比如负责显示dashboard的DashboardAcitivity，显示详细联系人的ContactActivity，拨打电话界面的CallingAcitivitiy，已经添加联系人的AddContactActivity。正是因为多个Activity的存在，才使整个安卓App的架构变得十分清晰，也更有利于我们阅读，重构以及debug。

一般来说，在一个Activity中，我们会采用MVP（Model-View-Presenter）来设计代码（MVP是一个我个人非常喜欢的design pattern，在Android Development中也十分常见，会在以后讲到它的具体概念和实现）。对于不同的Activity，出于解耦（De-coupling）的考虑，通常将它们放入不同的package中，方便对它们分别进行重构或调用。像如上我们举例的电话App，它的结构大致就是这样：

{% asset_img MyPhoneApplication.png %}

在上一次我们说到，AndroidManifest这个文件里定义了所有App所需要的Activity，Service，BoardcastReceiver等等，所以不要忘记在创建完Activity之后，一定要在Manifest里面把它声明好，不然编译时是一定会报错的。

## Fragment
在说完Activity之后，我们要说一下Fragment这个重要的component。在各种Android的档案定义里，都会反复出现对于Fragment的定义：A Fragment represents a behavior or a portion of user interface in a FragmentActivity. You can combine multiple fragments in a single activity to build a multi-pane UI and reuse a fragment in multiple activities. 但是我一直就觉得这个解释非常不明确，为什么我们需要用Fragment呢？我个人的理解是：当在一个Activity内有多个单独显示的页面，并且每个页面与用户交互的逻辑都各不相同时，Fragment可以帮助我们减少很多麻烦，将整个Activity的架构变得十分清晰。还是以前面的MyPhoneActivity为例，在这个打电话场景中，可能会分为三个阶段：接收到来电（InComingCall），电话被接通（Calling），通话结束（CallEnded）。根据需求不同，这三个阶段可能会有完全不同的布局，按钮，每个按钮也会有不同的功能，但是它们同属于一个打电话的功能之中（DialingActivity），并且一般来说这三个界面的风格，背景也都基本相同。此时如果我们使用Activity，当然是可以的，这不仅意味着我们需要在各个activity之间传递各种各样的参数，而且整个DialingActivity会变得完全无法使用（因为Activity是不能有inner activity的）。此时就轮到Fragment出场了，我们可以在DialingActivity里定义三个Fragment：InComingCallFragment，CallingFragment以及CallEndedFragment，每个Fragment都可以有自己的model和presenter（也就是都可以使用MVP pattern）而在DialingActivity里，我们可以定义所有包含在其中的Fragment的共同行为。整个的结构大概是这样：

{% asset_img MyPhoneFragment.png %}
Activity和Fragment都是关于UI部分的重要组件。一般来说对于比较大型的App来说，是需要多个Activity和多个Fragment来展示用户界面的。所以还是要多多设计和使用这些组件来熟悉。