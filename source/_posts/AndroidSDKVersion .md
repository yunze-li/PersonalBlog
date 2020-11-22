---
title: Android SDK Version
date: 2021-01-07 09:43:05
thumbnail: /thumbnails/ProGuard.png
toc: true
categories:
  - Technical
  - Android
tags:
  - English
  - Gradle
---

In Android development, I'm always confused about the properties in `build.gradle` like **compileSdkVersion**, **targetSdkVersion**, **minSdkVersion** etc. So here is a short post to compare all of them and clarify the diff.

<!-- more -->

### minSdkVersion

Like the name suggests, `minSdkVersion` is the **minimum sdk version that your app should running at**. For example, if the minSdkVersion of your app is 24 (Android 7), then trying to install the app at an Android 6 device or lower will failed because you declared that **the minimum accepted SDK version of your app is Android 7**.

Another important usage is with the help of [Lint check](https://developer.android.com/studio/write/lint?utm_campaign=adp_series_sdkversion_010616&utm_source=medium&utm_medium=blog) during development, you will get warning about using any API that required higher than your `minSdkVersion`. For example, if your minSdkVersion is 24 (Android 7) and you are trying to call an API introduced on Android 8, **compiler will give you a warning and ask you to add a SDK version check at runtime before calling that API like:**

```kotlin
private fun setUpActionBar() {
    // Make sure we're running on Honeycomb (Android 11) or higher to use ActionBar APIs
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        actionBar.setDisplayHomeAsUpEnabled(true)
    }
}
```

Also need aware that: the `minSdkVersion` of your app should be **at least the highest `minSdkVersion` of all your dependency library or modules**. For example, if your app has libraries required 9, 11, 14, then the `minSdkVersion` of your app **must be at least 14**. There are some ways to override this, but not recommended and should be carefully use.



## maxSdkVersion

Similar to `minSdkVersion`, it also can define the **maximum sdk version that your app should running at**. But since the Android platform is **fully backward-compatitable**, is attribute is not recommended by Google. Also when the Android version of device updated and exceed this `maxSdkVersion`, your app will be uninstalled automatically.



## complieSdkVersion

Start from here, it is a bit hard to understand. So in general, `compileSdkVersion` is **the android sdk version you tell the compiler to compile your app with**. The most important thing here is: `compileSdkVersion` is not included in your APK, it is used **purely at compile time, so it has no effect on runtime behaviour**. 

So what `compileSdkVersion` effect? It effects **the newest API available to you**, for example, if a new API is introduced at Android version X, then **you can only using this API when you set `compileSdkVersion` to X or higher**. At this point, this is why **it is strongly recommended to always set your `compileSdkVersion` to the latest**, because by doing this you will get all benefits of new compilation checks on existing code, avoid using newly deprecated API and ready to use any new API. 

One more thing need mention here is: if you use the **Android Support library**, `complieSdkVersion` needs to be **the same level or higher than the support library main version (first number)**. For example, if your app is using suport library version `23.1.1`, then the `compileSdkVersion` needs to be at least 23 to make this compiled correctly. And to using [AndroidX](https://developer.android.com/jetpack/androidx/#using_androidx_libraries_in_your_project) library as a replacement of Android support library, `compileSdkVersion` needs to be **28 or higher**.



## targetSdkVersion

The last version, and also most "interesting" version is `targetSdkVersion`. As we said before, `complieSdkVersion` has no effect on runtime behaviour, but `targetSdkVersion` has, which means OS will decide how it should handle your app in term of OS features. It is **more like a certification of sign off that indicate you have fully tested your app on this version**. For example, when `targetSdkVersion` is higher than 23, Android OS will enable the runtime permission model to your app because **it is the new feature introduced at that version**. So please **make sure you fully tested your app with that version before set `targetSdkVersion` into that number**.

Since `targetSdkVersion` will change your app runtime behaviour, it is suggested to always try to updated this number **after fully test it**. 



## Compare & Diff





###  

### 参考文章

[Google Android Developer Doc - SDK Versions](https://developer.android.com/ndk/guides/sdk-versions)

[Google Manifest <uses-sdk> tag](https://developer.android.com/guide/topics/manifest/uses-sdk-element.html)

[Picking your compileSdkVersion, minSdkVersion, targetSdkVersion](https://medium.com/androiddevelopers/picking-your-compilesdkversion-minsdkversion-targetsdkversion-a098a0341ebd)

[Using androidx libraries in your project ](https://developer.android.com/jetpack/androidx/#using_androidx_libraries_in_your_project)