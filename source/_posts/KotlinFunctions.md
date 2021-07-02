---
title: your name
date: your date
thumbnail: /thumbnails/{your thumbnail}
toc: false
categories:
  - Technical
  - Android
tags:


---

## 【Kotlin篇】作用域函数（Scope Functions）解析

### 用途

首先直接上官方文档：

> The Kotlin standard library contains several functions whose sole purpose is to **execute a block of code within the context of an object**. When you call such a function on an object with a [lambda expression](https://kotlinlang.org/docs/lambdas.html) provided, it forms a temporary scope. In this scope, you can access the object without its name. Such functions are called ***scope functions***.

可见所谓作用域函数，其实就是**创建一个lambda表达式（作用域），在作用域内执行一个代码块**。这种方式可以使代码量缩短，整个代码块也会更简洁易懂，不同的scope function的主要区别在于：

1. 引用上下文对象的方式（`this`还是 `it`）;
2. 返回值的类型（上下文对象还是Lambda表达式结果）。

下面用一个例子来说明不同的作用域函数的实际区别：

### apply

```kotlin
inline fun <T> T.apply(block: T.() -> Unit): T 
```

`apply`函数通过**lambda表达式的接收者`this`**来引用上下文并**返回上下文对象**。示例代码：

```kotlin
		fun useApply() {
        val person = Person(name = "arctos", age = 29)

        // 1 year later...
        person.apply {
            age = 30	// 等同于this.age = 30
        }
        println(person) // 这里age = 30
    }
```

`apply`可用于“*将以下赋值操作应用于对象*”，也就是作用于**上下文对象内部的赋值操作**，比如这里设置person.age的值。

### also

```kotlin
inline fun <T> T.also(block: (T) -> Unit): T
```

`also`函数通过**lambda表达式的参数`it`**来引用上下文并**返回上下文对象**。示例代码：

```kotlin
		fun useAlso() {
        val person = Person(name = "arctos", age = 29)

        // 1 year later...
        person.also {
            it.age = 30
            playWith(it)
        }
        println(person) // 这里age = 30
    }
```

`also`可用于*“并且用该对象执行以下操作”*，也就是**执行一些将上下文对象作为参数的操作**，比如这里使用的`playWith(person)`方法。

### run

```kotlin
inline fun <R> run(block: () -> R): R
```

`run`函数通过**lambda表达式的接收者`this`**来引用上下文并**Lambda表达式结果**。示例代码：

```kotlin
    fun useRun() {
        val person = Person(name = "arctos", age = 29)

        // 1 year later...
        val updatedAge = person.run {
            age = 30	// 等同于this.age = 30
            age * 10  // 返回一个计算的结果
        }
        println(updatedAge) // 这里updatedAge = 300
    }
```

`run`函数还可以使用**非扩展函数**的方式执行一个由多个语句组成的代码块，例如：

```kotlin
		fun useRun2() {
        val person = Person(name = "arctos", age = 29)

        // 1 year later...
        run {
            person.age = 30
            println(person.age)
        }
    }
```

`run`可用于*"当 lambda 表达式同时包含对象初始化和返回值的计算时"*，*“对象初始化”*也就是类似`apply`的**上下文对象内部的赋值操作**，但`run`同时可以返回经过计算的表达式结果，类似于`let`。

### let

```kotlin
inline fun <T, R> T.let(block: (T) -> R): R
```

`let`函数通过**lambda表达式的参数`it`**来引用上下文并**Lambda表达式结果**。示例代码：

```kotlin
		fun useLet() {
        val someone: Person? = null
        
        someone?.let {
            it.name = "arctos"  // 仅当someone不为空时执行代码块
        }

        val person = Person(name = "arctos", age = 29)

        // 1 year later and I'm moving...
        someone?.let { arctos ->
            arctos.age = 30
            arctos.address = "new address"
        }
    }
```

`let`的常用情况包括：1. *非空值执行代码块*；2. *引入作用域受限的局部变量*，也就是**非空检查**和**将`it`重命名来替换 lambda 表达式参数**以提高代码的可读性。

### with

```kotlin
inline fun <T, R> with(receiver: T, block: T.() -> R): R
```

`with`函数和`run`函数相似，也是通过**lambda表达式的接收者`this`**来引用上下文并**Lambda表达式结果**。但其区别在于`with`函数是一个**非扩展函数**，它把上下文对象作为一个参数进行调用。示例代码：

```kotlin
		fun useWith() {
        val person = Person(name = "arctos", age = 29)

        // 1 year later...
        val personInfo = with(person) {
            age = 30
            PersonInfo(name, age, address) // lambda表达式结果为创建的PersonInfo对象
        }
        println(personInfo.age) // 这里age = 30
    }
```

`with`可用于*“对于这个对象，执行以下操作”*，也就是**关于对象的一组操作**， 比如这里创建`PersonInfo`对象并返回，另外因为`with`是非扩展函数，所以其**不支持上下文对象的非空检查**。

### 总结

总结成一张表格来说明作用域函数的区别和选择偏好：

|                     | 上下文对象引用方式 |    返回值类型    |              使用场景               |
| ------------------- | :----------------: | :--------------: | :---------------------------------: |
| **apply**           | Lambda接受者`this` |    上下文对象    |      上下文对象内部的赋值操作       |
| **also**            |   Lambda参数`it`   |    上下文对象    |     将上下文对象作为参数的操作      |
| **run(扩展函数)**   | Lambda接受者`this` | Lambda表达式结果 | 上下文对象内部的赋值操作 + 计算结果 |
| **run(非扩展函数)** |        ----        | Lambda表达式结果 |     在需要表达式的地方运行语句      |
| **let**             |   Lambda参数`it`   | Lambda表达式结果 |   非空检查；lambda参数`it`重命名    |
| **with**            | Lambda接受者`this` | Lambda表达式结果 |         非空对象的一组操作          |

最后说一下，个人认为这些作用域函数其实还是细微的使用场景的区别，很多时候它们之间也都是可以**强行通用**的，只是使用更合适的函数可以让你的代码更精炼，易读。这也是Kotlin所追求的一种更为简洁优雅的代码风格。

### 示例代码

[KotlinScopeFunctions.kt](https://github.com/Yunze-Li/MochaBear/blob/main/blog/src/main/kotlin/com/arctos/mochabear/blog/KotlinScopeFunctions.kt)

### 参考文章

[Kotlin Scope Funtion官方文档](https://kotlinlang.org/docs/scope-functions.html)

[差异化分析，let，run，with，apply及also](https://juejin.cn/post/6975384870675546126)

[Kotlin Cheatsheet: Scope Functions (let, run, apply, also, with)](https://medium.com/dont-code-me-on-that/kotlin-cheatsheet-scope-functions-let-run-apply-also-with-308c8e5533f4)

