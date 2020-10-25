---
title: Android开发笔记（四）- Layout布局
date: 2020-07-02
thumbnail: /thumbnails/AndroidBasic.jpg
toc: true
categories:
  - Android
tags:
  - Chinese
---

在Android开发的大部分场景里，我们需要构建各种各样的UI界面用于与用户进行交互。很多UI设计看起来很简单也很清晰，但一旦开始上手设计，就会发现：设计师轻松画出的各个UI组件，它们的排列，间隔，对齐，大小，其实没有那么容易摆放正确。再加上对于安卓系统的手机，屏幕大小尺寸更是多种多样。这是我们就需要用到**页面布局**（UI Layout）这个工具。那么今天就来看看Android中关于页面布局的一些概念和知识。

<!-- more -->

## 四种常见的Android Layout布局
在Android开发环境中，一个UI layout布局页面以xml文件的格式存于：**你的工程路径/main/res/layout**里面。xml文件的具体格式这里不再赘述，但是要具体对于不同种类的Layout稍微解释一下：

### LinearLayout

第一种是[LinearLayout](https://developer.android.com/reference/android/widget/LinearLayout)，这种布局一般来说是最简单，直接，快速的一种，当新建一个layout布局文件时，Android Studio默认的布局文件即为这种LinearLayout。顾名思义，这种布局最适用于线性的UI呈现方式，比如如下的布局：

<img src="https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/LinearLayout.png?token=AOJCUF75UEG6J4TJXJT7Y4C65W7Q6" style="zoom:67%;" />

对于这种**只需要横向(Horizontal)或纵向(Vertical)排列**的布局，LinearLayout可以说是最适合不过的选择了，它减少了很多处理各部件之前位置关系的参数声明，只需按需调整当前部件和上一个部件之间的位置关系，直到完成布局即可。简单来说，LinearLayout就像堆积木，你只需要沿某一个方向不断堆砌你的subview，并给每个subview规定好边距等参数即可。但是需要注意的是：这种Layout类型**不允许在同一层内堆多个subview**。如果需要的话，要使用下面的RelativeLayout。

### RelativeLayout

第二种是[RelativeLayout](https://developer.android.com/reference/android/widget/RelativeLayout)，这种布局的特点在于：各部件之间的位置关系主要靠定义与其同布局的其他部件(Sibling)的位置关系来决定。比如我们在定义下面这样一个页面：

<img src="https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/RelativeLayout.png?token=AOJCUFYLQNLRLBAQLWMAHDS65W7UM" style="zoom:67%;" />

类似如上的界面一般会出现在列表（List）中，对于这种布局我们希望view 1和view 3的左边界对齐，而view 2和view 3的右边界对齐。至于整体布局的高度我们并不是十分介意（一般这种布局会采用上滑显示更多的[RecyclerListView](https://developer.android.com/jetpack/androidx/releases/recyclerview)，这个以后会继续讲到）。所以此时RelativeLayout就可以通过声明 **android:layout_toLeftOf** 以及 **android:layout_toRightOf** 来限制组件间位置关系，从而达到我们想要的效果。

**RelativeLayout**顾名思义，在定义同一个subview内的不同组件间**相对位置关系**时非常有效。但在使用时需要处理好相对位置关系间的冲突，一般需要花费一些时间用于调整相对的位置关系。这种布局的使用方法在面对稍微复杂一点的UI界面时相比LinearLayout更灵活，更高效。

### FrameLayout

第三种是[FrameLayout](https://developer.android.com/reference/android/widget/FrameLayout)，这种布局一般应用于**多层嵌套**的视图布局。其实上面介绍的两种视图也可以用于层叠（Overlap）效果，对于到底使用哪种布局在一些特定的应用场景下也会有所不同。但我的理解是: **FrameLayout对于层叠场景会有更好的表现和处理方式**，在FrameLayout中也会有更多不同的声明方式来进行布局，而一般的RelativeLayout来定义层叠的位置关系会变得很复杂。所以不妨分离出所设计的页面中的层叠部分，来用FrameLayout完成，而其他的部分则采用RelativeLayout。

<img src="https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/FrameLayout.png?token=AOJCUFZJ7VIEVVXJTBIWXFS65W7QQ" style="zoom:67%;" />

将你的页面进行分离，使用不同的UI布局框架来定义不同的子页面，也是UI设计布局中十分重要的一步。决定好使用的框架会让你的UI设计变得简单易懂，并且易于改动。相信很多人都遇到过改UI的时候一改就要动到所有的框架这种十分痛苦的事情。

### ConstraintLayout

最后一个，也是我认为最为重要，功能最为强大的一个，就是[ConstraintLayout](https://developer.android.com/reference/android/support/constraint/ConstraintLayout)。ConstraintLayout和其它布局方式最明显的区别在于，**它需要对于每一个组件声明布局的constraint**，这些constraints会按照优先级被执行并布局。但是必须声明得比较完备，才能获得想要呈现的效果（也正是因为需要声明的constraint比较多，所以ConstraintLayout可以比较好的大部分在App中我们想要呈现的布局效果）。

![](https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/ConstraintLayout.png?token=AOJCUF3DFSGT4D4O3PRIMDS65W7P6)

在ConstraintLayout中，当定义一个组件的时候，可以通过声明**app:layout_constraintXX_toXXOf**来定义其相对于另一个组建的位置，同时也可以通过声明**android:layout_marginXX**来定义其相对边距，其用法是十分灵活的。ConstranitLayout在进行诸如边缘对齐，居中，各子视图(subview)平均分配等功能时十分有效，目前可以说是我最喜欢使用，也最经常使用的一种布局框架。

