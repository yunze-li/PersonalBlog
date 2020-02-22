---
title: Dependency Injection
---
#### Reading Time: 10min

## What is Dependency Injection

Before answering this question, a better question to ask might be:

### What is **dependency**

To answer this question, let's take a look at this Kotlin code here:

```kotlin
package com.yunze.fitnessadviser

class SimpleFeature constructor(
    val businessLogicDelegate: BusinessLogicDelegate,
    val validator: Validator,
    val listener: Listener
) {

    fun doSomeThing() {
        val helper = Helper()
        if (validator.validate()) {
            businessLogicDelegate.doSomething(helper, listener)
        }
    }
}
```

This is a very common secenario in real life: I have a class and serveral parameters in its constructor, I need call some methods of those parameters inside this class. At the same time, I also need initialize a helper class inside and use it. In this case, both the parameters and helper class will be considered as the `Dependency of SimplyFeature class`. Cause this `SimpleFeature` needs "_depend on_" something to finsh its own job. In another word, if any class's method is called inside a class A, those class will be considered as _A's dependency_.

### So now I know dependency, what is **dependency injection**?

Let's look back to this code again:

```kotlin
package com.yunze.fitnessadviser

class SimpleFeature constructor(
    val businessLogicDelegate: BusinessLogicDelegate,
    val validator: Validator,
    val listener: Listener
) {

    fun doSomeThing() {
        val helper = Helper()
        if (validator.validate()) {
            businessLogicDelegate.doSomething(helper, listener)
        }
    }
}
```

There are two main issues in this code block:

   1. Inside the doSomeThing(), we need to initialize a `Helper` class to help finishing business logic. This can work for sure, but if this helper needs to be a static class, or I need to using a `Signleton` helper class, here, how can I use it?
   2. When writing unit test with [Mockito](https://site.mockito.org/) or [Mockk](https://mockk.io/), a common way is passing in a "_mock_" instance and testing if the expected method of this mock is called with correct parameter. But how can we pass in the mock of Helper class if the `new` operator is called inside?

To solve these problems, what can we do? Just simply pass a helper instance in the constructor so it can become the dependency of SimpleFeature as well! The constructor will look like this:

```kotlin
class SimpleFeature constructor(
    val businessLogicDelegate: BusinessLogicDelegate,
    val validator: Validator,
    val listener: Listener,
    val helper: Helper
) {

    fun doSomeThing() {
        if (validator.validate()) {
            businessLogicDelegate.doSomething(helper, listener)
        }
    }
}
```

Now it looks great! I can pass in any Helper class and also mocking it and do the unit test! Nice! Now we can make a conclusion here:

> Dependency Injection means:
> **Passing the dependency into the class and use it**

One thing need to mention here is: passing through class's constructor is only one of the way to do DI. More details about this concept can be find [here](https://en.wikipedia.org/wiki/Dependency_injection).

### How can I use dependencies injection?

To use dependencies injection effectively, let me introduce this super-useful library here:

![alt text](https://i1.wp.com/codingsonata.com/wp-content/uploads/2017/12/Android-dagger-2.png?resize=300%2C208 "Dagger")

This [Dagger 2](https://dagger.dev/) library are powerful for android application dependency injection, by using this, all we need to do it's define each class's dependency list and the scope to use it. All other stuff will be handled by this library automatically and anonymously. Now let's see the code we have here:
