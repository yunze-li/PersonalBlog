---
title: Lambda Expression Basic
date: 2020-11-01 08:03:52
thumbnail: /thumbnails/Lambda.jpg
toc: true
categories:
  - 技术博客
  - Kotlin
tags:
  - English
  - Lambda
---

Lambda Expression is a very useful tool and it makes the code concise and elegant. It's not a very hard concept to understand but still need some kind of conclusion or summary. So in this post I will have a brief introduction on what is lambda expression with several examples how to use it. My example are using **Kotlin** lanuage, but it has the same pattern or other languages like Java and C++.

<!-- more -->

## What is Lambda Expression

>**Lambdas Expressions are essentially anonymous functions that we can treat as values**

That's all, as simple as it is. The most important of this defination is: `treat as values`, what is that mean? Actually that means three things:

1. You can assign lambda expression as a value to any variable like you do `val a = 1`;
2. You can pass lambda expression as an **argument in a method** call;
3. You can use lambda expression as **a return value of method**.

A generic lambda expression structure looks like this:

<img src="https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/Lambda.png" style="zoom:90%;" />

Only the body is required, **all other parts can be optional in some cases**:

- When we **pass lambda as argument**, no need lambda expression name;
- Return type **can be inferred** by Kotlin complier which is the **last command within the body**;
- Argument list also not needed when there is **only one argument**, we can use **`it`**  in body which refer to the only argument.



## Type Declaration and Inference

When we define lambda expression, like all another type, we need to **declare it's type**. Such as:

```kotlin
val square: (Int) -> Int = {num: Int -> num * num}

val sum: (Int, Int) -> Int = {num1: Int, num2: Int -> num1 + num2}

val printNumber: (Int) -> Unit = {num: Int -> println(num)}
```

As we can see from above, lambda expression type pattern is **`Input -> Output`**, **need using braces** if more than one input or output like `(String, Int) -> (Double, Long)  and **Unit** can be used if no return value.

But it is too verbose sometime to have all parts of lambda expression, as we said above, **a lot part of it can be optional**. For example, we have `square` lambda expression like:

```kotlin
val square: (Int) -> Int = {num: Int -> num * num}
```

Since `num` is an Int, Kotlin complier can infer that `num * num` will be an Int as well. So **return type can be optional** like:

```kotlin
val square = {num: Int -> num * num} // This used a lot when pass the whole lambda as argument
```

Another way to simplify is: since we know `square` will have a Int argument and return an Int, **argument list can be skipped**:

```kotlin
val square: (Int) -> Int = { it * it} // This used more like a defination of lambda
```

Note that these two ways **can't do together**, since Kotlin complier will have too less information to infer and complie.



## Pass Lambda as Argument

Now after we know how to  declare a lambda expression, next thing is pass it as an argument. In general we have **f options** to do it. Consider we have an `invokeLambda` function which needs **pass in a lambda expression and an Int as arguments**:

```kotlin
fun invokeLambda(lambda: (Int) -> Int, num: Int) : Int {
    return lambda(num)
}
```

Let's see how these five options works and what's the difference.

 ### 1. Pass as Lambda Object Variable

In this way, we assign the lambda into a variable and **pass the variable as usual**:

```kotlin
val square: (Int) -> Int = {num: Int -> num * num}
val result = invokeLambda(square, 4) // this will return 16 
```

### 2. Pass as Lambda Literal

This way we don't need assign lambda to any variable, it can be **passed directly and literally**:

```kotlin
val result = invokeLambda({ it * it}, 4) // this will return 16
```

### 3. Pass as Lambda Literal without Brackets

This is a bit tricky, to use this, we need make sure:

1. Lambda argument is the **last argument** in high-order function;
2. There **only one lambda argument** is this high-order function.

So let's do some refactor, `invokeLambda` will looks like:

```kotlin
fun invokeLambda(num: Int, lambda: (Int) -> Int) : Int {
    return lambda(num)
}
```

  Then we can move the lambda expression out of the brackets like:

```kotlin
val result1 = invokeLambda(4) { num -> num * num } // no need return type, thanks to Kotlin Inference
val result2 = invokeLambda(4) { it * it } // even no need argument list since only one argument
```

### 4. Kotlin Extension Method Reference

The last option is kind of more special case, it needs to use reference of **Kotlin Extension Method** as the argument. Consider the same `invokeLambda` as option 3, we need add a `sqaure` extension method:

```kotlin
// define a Int class extension method
fun Int.sqaure(): Int {
  return this * this
}
```

 then we can **pass the method reference of this extension as lambda**:

```kotlin
val result = invokeLambda(4, Int::sqaure) // this will return 16
```

The type of extension method reference `Int::sqaure` is `KFunction1<Int, Int>`, it can be treated as `(Int) -> Int` type which is required. Even if the lambda needs more arguments, we can just **add it in extension method arguments list**. 



## Annoymous Inner Class VS Lambda Expression

To be honest these two concepts doesn't share much features, but since I saw people asking the diff so I want to add them here as a reference. To short, there are 

- Anonymous Inner classes can be used in case of **more than one abstract method** while a lambda expression specifically used for **functional interfaces**.
- Anonymous Inner classes can use **instance variables** and thus **can have state**, lambdas **cannot**.
- Anonymous inner classes have support to **access variables from the enclosing context**. It has access to all final variables of its enclosing context.
- When use lambda expression as **functional interface**, two conditions need to meet: 1. it needs to be a **Java interface** (not a Kotlin one); 2. the number of method arguments need to **have a limit**.



### Useful Link

[Kotlin Lambda Expression](https://www.baeldung.com/kotlin-lambda-expressions)

[Introduction to Kotlin Lambdas Getting Started](https://www.raywenderlich.com/2268700-introduction-to-kotlin-lambdas-getting-started)

[Kotlin Offical Documents](https://kotlinlang.org/docs/reference/lambdas.html)

[Lambda Expression VS Anonymous Inner Class](https://medium.com/@knoldus/lambda-expression-vs-anonymous-inner-class-31adb0b3e482)

