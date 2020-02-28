---
title: Dependency Injection
tags:
  - Android
---
## What is Dependency Injection

Before answering this question, a better question to ask might be:

### What is **dependency**

To answer this question, let's take a look at this Kotlin code here:

```kotlin
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

This [Dagger 2](https://dagger.dev/) library are powerful for android application dependency injection, by using this, all we need to do it's define each class's dependency list and the scope to use it. All other stuff will be handled by this library automatically and anonymously. So how to use it? First, we need to have a module class:

```kotlin
@Module
interface MainModule {

    @Binds
    fun providesBusinessLogicDelegate(businessLogicDelegateImpl: BusinessLogicDelegateImpl): BusinessLogicDelegate

    @Binds
    fun providesValidator(validatorImpl: ValidatorImpl): Validator

    @Binds
    fun providesListener(listenerImpl: ListenerImpl): Listener

    @Binds
    fun providesHelper(helperImpl: HelperImpl): Helper

}
```

What is this **Module** use for? Basically this is the place you want dagger helps you to initialize all your dependency. There are two ways to initial dependency in dagger, one is through **@Bind** annotation and another is **@Provides**. In here I'm using @Binds since it's much simpler if all dependencies has implement an interface.

Then we need another class which is called componenet class:

```kotlin
@Component (modules = [MainModule::class])
interface MainComponent {

    fun inject(activity: MainActivity)

    fun inject(feature: SimpleFeature)
}
```

Inside this **Component**, the inject() method defines "where those dependencies should be injected to". And following the **@Component** annotation we can declare all modules that can be included, so we can inject multiple module's dependencies inside one single class that called by inject() method.

So far so good! After create these two classes above, we defined `What is the dependency` and `Where should we inject into`. Now the final step will be refactor our **SimpleFeature** class:

```kotlin
class SimpleFeature @Inject constructor(
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

Since all parameters inside the constructor are initialized in module class and the injection of this feature class also declared in component class, the only change needed in here is add **@Inject** annotation before `constructor`, then dagger knows that all parameters can be find in module and injected by component magically.

Done! Now enjoy inject any dependency you want!

Now there are still some notes worth mention here:

1. What we using here is just a very brief introduction of Dagger, it has a lot powerful methods and tools that worth to try and play with it. More detail should be found in [here](https://dagger.dev/)

2. One of the very useful feature of Dagger is **@Scope** annotation. It can be used in complicated structured project to define different scope for different dependencies to access. This part will be added in future as a follow-up of our DI topic.

3. The main purpose of DI is align with one of the basic [SOLID](https://en.wikipedia.org/wiki/SOLID) principle in softwate development, which is know as [Dependency Inversion Rules](https://en.wikipedia.org/wiki/Dependency_inversion_principle). This principle states:

    * High-level Module should not depends on low-level module, both should depend on abstractions (interface);
    * Abstractions should not depend on details. Details (concrete implementation) should depends on abstraction.

    We will cover all these principles one by one in future, they are very important in industrial software development world.

4. Although there are tons of advantages in DI, but it has its limit or disadvantage for sure. For example, when dependency is missing, it's hard to find out which dependency is missing by checking the build output. So at this time, log is very useful to check.



