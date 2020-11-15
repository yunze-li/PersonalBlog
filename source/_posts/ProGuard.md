---
title: ProGuard, D8, R8编译器介绍
date: 2020-11-04 08:03:52
thumbnail: /thumbnails/ProGuard.png
toc: true
categories:
  - 技术博客
  - Android
tags:
  - Chinese
  - Proguard
---

在编译代码，生成Android APK文件时，为了缩减生成安装包apk文件的大小，Google官方在Android Gradle插件中提供了几种不同的优化方式：**Proguard**，**D8**，**R8**。它们主要用于对生成的apk文件进行**代码缩减**（Code shrinking），**资源缩减**（Resource shrinking），**混淆处理**(Obfuscation)和**优化**(Optimization)。这篇文章会首先介绍一下这些编译器的产生顺序以及原因，然后再介绍一下`proguard-rules.pro`中规则的定义方法。

<!-- more -->

## 1. Proguard

Proguard是Google Android SDK中提供的优化代码和缩减apk文件的编译器，其工作流程是：

<img src="https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/proguard.png" style="zoom:120%;" />

可以看到，首先.java文件会被Java编译器编译成.class文件，之后Proguard则会对.class文件**进行缩减和优化**，再通过Android运行环境中的**Dalvik虚拟机**将.class文件编译成可以运行的.dex文件。也就是：

> *SourceCode(**.java**)* — javac → J*ava Bytecode(**.class**)* — Proguard → *Optimized Java bytecode(**.class**)* — Dex → *Dalvik Optimized Bytecode**(.dex**)*

在这之后，Google决定将这一系列的步骤合并成一步，于是推出了[Jack & Jill](http://tools.android.com/tech-docs/jackandjill)编译器，它可以将以上步骤缩减成一步，即：

> SourceCode(**.java**) — Jack & Jill → Dalvik Optimized Bytecode**(.dex**)

然而Jack & Jill的效果并不理想，于是2017年Google决定重新使用之前的这套Proguard工作流程，但这次，Google将dx编译器进行了优化，产生了一个新的编译器： **D8**。从Android Studio 3.1开始，D8成为了默认的dex编译器。

## 2. D8

D8相对于Proguard而言，最大的变化就是Google优化了dx编译器，其工作流程是：

<img src="https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/d8.png" style="zoom:130%;" />

虽然工作流程得到了简化，但是此时由于新的编程语言：**Kotlin**的出现，使得Google不得不再次对于d8编译器进行改进和优化，于是就有了目前最主流常用的代码缩减优化编译器： **R8**。从Android Studio 3.4或Android Gradle 3.4.0开始，默认使用R8编译器进行缩减优化，其使用方法和proguard通用，都是通过`proguard-rules.pro`这个文件来声明编译规则并执行。

## 3. R8

首先是R8的工作流程图：

<img src="https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/r8.png" style="zoom:135%;" />

R8同时支持Java和Kotlin代码，通过Google提供的一系列测试（https://android-developers.googleblog.com/2018/11/r8-new-code-shrinker-from-google-is.html）可以看出，R8比不仅在编译时间上远快于Proguard将近一半，在生成的apk文件大小上也稍小于Proguard。这也就是为什么Google现在将R8作为默认的编译器的原因。下面我们就来看一下关于R8编译器在使用上的一些说明。

## 4. How to use R8 complier

### 启用R8编译器

首先启用R8编译器，需要在project根目录下的`build.gradle`加入：

```groovy
android {
    buildTypes {
        release {
            // Enables code shrinking, obfuscation, and optimization for only
            // your project's release build type.
            minifyEnabled true

            // Enables resource shrinking, which is performed by the
            // Android Gradle plugin.
            shrinkResources true

            // Includes the default ProGuard rules files that are packaged with
            // the Android Gradle plugin. To learn more, go to the section about
            // R8 configuration files.
            proguardFiles getDefaultProguardFile(
                    'proguard-android-optimize.txt'),
                    'proguard-rules.pro'
        }
    }
    ...
}
```

其中`minifyEnabled`用于启用**代码缩减，混淆处理和优化**，`shrinkResources`用于启用**资源缩减**， `proguard-android-optimize.txt`是Gradle PlugIn里面默认的处理规则文件，而`proguard-rules.pro`则是**项目根目录下**在创建时Android Studio自动生成的自定义处理规则文件。当需要添加一系列自定义规则时，只需要在项目根目录下的`proguard-rules.pro`中添加即可。

### 添加单独模块的proguard规则文件

对于一个多模块的项目，各个模块可以声明自己独立的proguard规则文件`proguard-rules.pro`在**模块的根目录下**，并在模块的`build.gradle`中加入如下内容：

```groovy
android {
    buildTypes {
        release {
            minifyEnabled true
            proguardFiles getDefaultProguardFile(
                    'proguard-android-optimize.txt'),
                    'proguard-rules.pro'
          
          	// Using module ProGuard rules files and add it into any module which
          	// is dependent on this module
          	consumerProguardFiles 'proguard-rules.pro'
        }
    }
    ...
}
```

简单来说，如果一个**Library Module**被**App Module**所依赖，那么通过在library module中声明`consumerProguardFiles`属性，app module就会将自己根目录下的`proguard-rules.pro`和library module的`proguard-rules.pro`**合并作为代码缩减，混淆等处理的规则**来运行。

### 自定义Proguard-rules.pro规则

#### default默认（R8 complier已启用）

在`proguard-rules.pro`文件中，最常用的是一系列`-keep`相关的规则。它主要是用于规定对于**类**（class）和**类的成员**（members）是否要进行缩减，混淆的操作。当没有自定义规则，也就是默认状态下，缩减和混淆都是开启的，也就是：

<img src="https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/proguard_norule.png" style="zoom:120%;" />

此时，R8会对这个类进行缩减（remove unused code）和混淆（rename things）操作。

#### -keep class

当在`proguard-rules.pro`中声明：`-keep class com.foo.library.** { *; } `时，**R8对类和类成员的所有操作都会被禁止**。也就是：

<img src="https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/proguard_keep.png" style="zoom:120%;" />

要注意这种情况**非常不推荐**，因为它禁止了所有对于这个类的操作。事实上在实际情况中总是可以**选择性**的进行一些需要的操作的，而这些**“选择性”**就是由下面的特殊的`-keep`规则来实现的。

#### -keepclassmembers

`keepclassmembers`会**禁止R8对类成员的操作**，但**允许对类本身进行缩减和混淆**：

<img src="https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/proguard_keepclassmembers.png" style="zoom:120%;" />

也就是说，如果这个类本身没有被使用，**它会被删掉**；如果它被使用了，则会将其**重命名**（混淆处理），而对于其中的类成员**没有任何操作**。

#### -keepnames

`keepnames`的逻辑很简单：只检查是否有没有被使用的类或类成员，如果有的话则**删掉它们**。但**不进行任何重命名**混淆操作。

<img src="https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/proguard_keepnames.png" style="zoom:120%;" />

#### -keepclassmembernames

`keepclassmembernames`的逻辑也很简单：检查没有被使用的类和类成员，删掉没有用的，然后**对类名进行重命名**，但保留**类成员的名字不变**。

<img src="https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/proguard_keepclassmembernames.png" style="zoom:120%;" />

#### 其他常用规则命令

`-verbose`：**打印**详细的混淆信息；

`-dontnote com.foo.bar.**`：不打印**foo.bar包内的**notes信息**，例如typo或者missing useful options；

`-dontwarn com.foo.bar.**`：**不打印**foo.bar包内的**warning信息**，轻易不推荐使用；

`-dontpreverify`：**不进行检查校验**，主要针对使用Java Micro Edition或Java 6+版本的Java Library需要进行检查校验，对于安卓平台上运行的Library来说可以添加这个规则来加快编译速度；

`-keepclasseswithmembers`：和`-keep`基本一致，唯一的区别就是**它只作用于存在类成员的类**，例如keep所有含有`main method`的Application Class；

`-keepclasseswithmembersnames`：同理，和`-keepnames`的唯一区别也是**它只作用于存在类成员的类**；

`-printusage[filename]`：在standard output或者文件中**打印出所有被缩减的内容**；

`-keepattributes[attribute_filter]`：**禁止重命名**class中的参数（attributes），比如使用第三方library时要禁止混淆`Exceptions`， `InnerClasses`和`Signature`；打印stack trace的时候要禁止混淆`SourceFiles`和`LineNumberTable`等；

`-dontusemixedcaseclassnames`：进行混淆时**不同时使用大小写**来重命名。



### 参考文章

[Proguard Offical Document](https://www.guardsquare.com/en/products/proguard/manual/introduction)

[代码缩减Google官方说明文档](https://developer.android.com/studio/build/shrink-code?hl=zh-cn)

[Android Journey: Proguard, D8, R8 what are they?](https://imstudio.medium.com/android-journey-proguard-d8-r8-what-are-they-e8f2bfe079a7)

[Distinguishing between the different ProGuard “-keep” directives](https://jebware.com/blog/?p=418)

[Proguard的使用](https://www.jianshu.com/p/7ab45e6a4b64)

