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
Person p = new Student();	// at complie time, type of p is Person; at run time, type of p is Student!
if (p instanceof Student) {
    p.study();
}
```

But introspection is only a way to kind of "**check**" or "**verify**" object type. Java reflection is something **one step furthur**: it can **manipulate the object** at runtime.



## How to use Java Reflection

Now let's see how to use Java Reflection API with a sample case. Here we have a `Studying` inferface:

```java
public interface Studying {
    String study();
}
```

then we have an abstract class `Student` which implement this `Studying` interface：

```java
public abstract class Student implements Studying {
    private String name;
    protected abstract String getStudentName();
}
```

and we have another interface `Person`:

```java
public interface Person {
    String walk();
}
```

Finally, we create a `Master` class which extends abstract class `Student` and implements interface `Person`:

```java
public class Master extends Student implements Person {
    @Override
    public String walk() {
        return "I'm walking!";
    }

    @Override
    protected String getStudentName() {
        return "arctos";
    }

    @Override
    public String study() {
        return "I'm studying!";
    }
}
```

 Now we are ready to ask Java reflection's help to inspecting classes, constructor, fields and methods separately!

### Inspecting Classes

#### Class/Package Name

To inspect class name or package name, we can:

1. use `getClass()` **from the object**;
2. call `Class.forName()` and pass the **full class name string**;
3. after get the Class object, call `class.getPackage()` to get the pakcage and then call `package.getName()`.

```java
		@Test
    public void givenObject_whenGetsClassName_thenCorrect() {
        Object master = new Master();
        Class<?> clazz = master.getClass();

        assertEquals("Master", clazz.getSimpleName());
        assertEquals("com.arctos.sample.reflection.Master", clazz.getName());
        assertEquals("com.arctos.sample.reflection.Master", clazz.getCanonicalName());
    }

    @Test
    public void givenClassName_whenCreatesObject_thenCorrect(){
        Class<?> clazz = null;
        try {
            clazz = Class.forName("com.arctos.sample.reflection.Master");
            assertEquals("Master", clazz.getSimpleName());
            assertEquals("com.arctos.sample.reflection.Master", clazz.getName());
            assertEquals("com.arctos.sample.reflection.Master", clazz.getCanonicalName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

		@Test
    public void givenClass_whenGetsPackageInfo_thenCorrect() {
        Object master = new Master();
        Class<?> masterClass = master.getClass();
        Package pkg = masterClass.getPackage();

        assertEquals("com.arctos.sample.reflection", pkg.getName());
    }
```

Note:

- `getSimpleName()` will return the basic name of class, `getName()` and `getCanonicalName()` will return the fully qualified name including package name;
- `Class.forName("your_full_class_name")` needs the full class name including package name, and it may throw **`ClassNotFoundException`** if the given class name is not found.

#### Class Modifiers

To inspect class modifiers like `public`, `final`, `volatile`, `abstract`, we can use `class.getModifiers()` which returns an **int** flag and pass it to `Modifier.isXXX()` as an argument:

```java
		@Test
    public void givenClass_whenRecognisesModifiers_thenCorrect() {
        try {
            Class<?> masterClass = Class.forName("com.arctos.sample.reflection.Master");
            Class<?> studentClass = Class.forName("com.arctos.sample.reflection.Student");

            int masterModifiers = masterClass.getModifiers();
            int studentModifiers = studentClass.getModifiers();

            assertTrue(Modifier.isPublic(masterModifiers));
            assertFalse(Modifier.isFinal(masterModifiers));
            assertTrue(Modifier.isAbstract(studentModifiers));
            assertTrue(Modifier.isPublic(studentModifiers));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
```

Note:

- `getModifiers()` will return an **int flag** which including all infos of this class' modifier. Check the code inside `java.lang.Modifier` class and we can found all modifier checking is performed like `(modifiers & PUBLIC) != 0` which PUBLIC is just another static int constant.

#### Super Class

When we have the class object, we can get the superclass by calling `class.getSuperclass()`

```java
		@Test
    public void givenClass_whenGetsSuperClass_thenCorrect() {
        Master master = new Master();
        String str = "any string";

        Class<?> masterClass = master.getClass();
        Class<?> masterSuperClass = masterClass.getSuperclass();

        assertEquals("Student", masterSuperClass.getSimpleName());
        assertEquals("com.arctos.sample.reflection.Student", masterSuperClass.getCanonicalName());
        assertEquals("String", str.getClass().getSimpleName());
        assertEquals("Object", str.getClass().getSuperclass().getSimpleName());
        assertNull(str.getClass().getSuperclass().getSuperclass());
    }
```

Note:

- `getSuperclass()` will return a Class object, same as when we call `object.getClass()` or `Class.forName("your_class_name")`;
- If there is no super class (such as **Object** class), `null` will be returned.

#### Implemented Interfaces





### Inspecting Constructor



### Inspecting Fields



### Inspecting Methods









### Useful link

[Guide to Java Reflection](https://www.baeldung.com/java-reflection)

[Type Introspection Wikipedia](https://en.wikipedia.org/wiki/Type_introspection)

