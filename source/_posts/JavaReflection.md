---
title: Java Relection 
date: 2020-11-15 08:03:52
thumbnail: /thumbnails/ProGuard.png
toc: true
categories:
  - 技术博客
  - Java
tags:
  - English
  - Java Reflection
---

Java Reflection is a process of **examining or modifying the run time behavior of a class** at runtime. It is super useful when runtime attributes' name of classes, interfaces, fields, and methods are not known during complie time. Futhurmore, we can **instantiate new objects, invoke methods, and get or set field values** using reflection as well. So let's see what is reflection and how to use it in this post.

<!-- more -->

## What is Java Reflection

> Java reflection is the ability for a program to manipulate the values, metadata, properties, and functions of an object at runtime.

When we creating a class in Java, it gives us an option to do `introspection`, which means the ability of a program to **examine the type or properties of an object at runtime**. A simple example of introspection is `instanceof` like:

```java
Person p = new Student();	// at complie time, type of p is Person; at run time, type of p is Student 
if (p instanceof Student) {
    p.study();
}
```

But introspection is only a way to kind of "**check**" or "**verify**" object type. Java reflection is something **one step furthur**: it can **manipulate the object** at runtime.



## How to use Java Reflection







## Use Case 1: 





### Useful link

[Guide to Java Reflection](https://www.baeldung.com/java-reflection)

[Type Introspection Wikipedia](https://en.wikipedia.org/wiki/Type_introspection)

