---
title: ProGuard, D8, R8编译器介绍
date: 2020-11-01 08:03:52
thumbnail: /thumbnails/ProGuard.png
toc: true
categories:
  - 技术博客
  - Android
tags:
  - Chinese
  - Proguard
---

在编译代码，生成Android APK文件时，为了缩减生成安装包apk文件的大小，Google官方在Android Gradle插件中提供了几种不同的优化方式：**Proguard**，**D8**，**R8**。它们主要用于对生成的apk文件进行**代码缩减**（Code shrinking），**资源缩减**（Resource shrinking），**混淆处理**(Obfuscation)和**优化**(Optimization)。Java或Kotlin代码在经过编译器后生成的class文件，会在缩减，优化后编译成Android Dalvik环境中可以运行的dex文件。这篇文章会首先介绍一下这些编译器的产生顺序以及原因，然后再介绍一下`proguard-rules.pro`的具体使用方法。

<!-- more -->

## 1. Proguard

Proguard是Google Android SDK中提供的优化代码和缩减apk文件的编译器，其工作流程是：

<img src="https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/proguard.png" style="zoom:130%;" />

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





###参考文章

[Proguard Offical Document](https://www.guardsquare.com/en/products/proguard/manual/gradleplugin)

[代码缩减Google官方说明文档](https://developer.android.com/studio/build/shrink-code?hl=zh-cn)

[Android Journey: Proguard, D8, R8 what are they?](https://imstudio.medium.com/android-journey-proguard-d8-r8-what-are-they-e8f2bfe079a7)