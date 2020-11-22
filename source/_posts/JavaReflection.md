---
title: Java Relection 
date: 2020-11-15 08:03:52
thumbnail: /thumbnails/JavaReflection.jpg
toc: true
categories:
  - 技术博客
  - Java
tags:
  - English
  - Java Reflection
---

Java Reflection is a process of **examining the runtime behavior of a class**. This feature is also known as **dynamic programming** since all attributes, interfaces, constructors, fields and methods of a class can be inspecting and verifying during runtime. Futhurmore, we can **instantiate new objects, invoke methods, and get or set field values** using reflection as well. So let's see what is reflection and how to use it in this post.

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

    public static String GENDER = "male";
    private String name;

    public Student(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
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
    public Master(String name) {
        super(name);
    }

    @Override
    public String walk() {
        return "I'm walking!";
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
    Object master = new Master("arctos");
    Class<?> clazz = master.getClass();

    assertEquals("Master", clazz.getSimpleName());
    assertEquals("com.arctos.sample.reflection.Master", clazz.getName());
    assertEquals("com.arctos.sample.reflection.Master", clazz.getCanonicalName());
}

@Test
public void givenClassName_whenCreatesObject_thenCorrect() {
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
    Object master = new Master("arctos");
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
    Master master = new Master("arctos");
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

We also can use `getInterfaces()` to retriece a list of classes that current class **implements** as interface:

```java
@Test
public void givenClass_whenGetsImplementedInterfaces_thenCorrect() {
    try {
        Class<?> masterClass = Class.forName("com.arctos.sample.reflection.Master");
        Class<?> studentClass = Class.forName("com.arctos.sample.reflection.Student");

        Class<?>[] masterInterfaces = masterClass.getInterfaces();
        Class<?>[] studentInterfaces = studentClass.getInterfaces();

        assertEquals(1, masterInterfaces.length);
        assertEquals(1, studentInterfaces.length);
        assertEquals("Person", masterInterfaces[0].getSimpleName());
        assertEquals("Studying", studentInterfaces[0].getSimpleName());
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
    }
}
```

Note:

- `getInterfaces()` will only **return all interfaces directly implements by this class**. In code snippt above, `Master` class extends `Student` abstract class which implements `Studying` interface. But only `studentClass.getInterfaces()` will return `Studying` interface **even if Master class also need to implement methods of that interface**. In another word, it only returns interfaces that **directly declared with `implements` keyword**.

### Inspecting Constructor

Above inspection are all class-level or class-related, now let's go to constructor. Since **Java only allow single constructor with same signature in one class**, we will create a `Bachelor` class and adding three different constructors like:

```java
public class Bachelor extends Student {

    private boolean graduated;

    /**********************
     *    Constructor
     **********************/

    public Bachelor() {
        super("unknown");
    }

    public Bachelor(String name) {
        super(name);
    }

    public Bachelor(String name, boolean graduated) {
        super(name);
        setGraduated(graduated);
    }

    /**********************
     *   Static methods
     **********************/

    private static boolean isBachelor() {
        return true;
    }

    private static String degreeName() {
        return "Bachelor";
    }

    /**********************
     *  Setter and Getter
     **********************/

    public boolean isGraduated() {
        return graduated;
    }

    public void setGraduated(boolean graduated) {
        this.graduated = graduated;
    }

    /**********************
     * Implemented methods
     **********************/

    @Override
    public String study() {
        return getStudyString();
    }

    private String getStudyString() {
        return "I'm " + "studying!";
    }
}
```

Now let's first ensure it will **gives use all three constructors**:

```java
@Test
public void givenClass_whenGetsAllConstructors_thenCorrect() {
    try {
        Class<?> bachelorClass = Class.forName("com.arctos.sample.reflection.Bachelor");

        Constructor<?>[] constructors = bachelorClass.getConstructors();

        assertEquals(3, constructors.length);
        assertEquals("com.arctos.sample.reflection.Bachelor", constructors[0].getName());
        assertEquals("com.arctos.sample.reflection.Bachelor", constructors[1].getName());
        assertEquals("com.arctos.sample.reflection.Bachelor", constructors[2].getName());
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
    }
}
```

 Then we can retrieve each of constructor **by parameter types in order** like:

```java
@Test
public void givenClass_whenGetsEachConstructorByParamTypes_thenCorrect() {
    try {
        Class<?> bachelorClass = Class.forName("com.arctos.sample.reflection.Bachelor");

        Constructor<?> cons1 = bachelorClass.getConstructor();
        Constructor<?> cons2 = bachelorClass.getConstructor(String.class);
        Constructor<?> cons3 = bachelorClass.getConstructor(String.class, boolean.class);
    } catch (ClassNotFoundException | NoSuchMethodException e) {
        e.printStackTrace();
    }
}
```

Finally, we can use the constructor retrieved to **instanciate new objects**:

```java
@Test
public void givenClass_whenInstantiatesObjectsAtRuntime_thenCorrect() {
    try {
        Class<?> bachelorClass = Class.forName("com.arctos.sample.reflection.Bachelor");
        Constructor<?> cons1 = bachelorClass.getConstructor();
        Constructor<?> cons2 = bachelorClass.getConstructor(String.class);
        Constructor<?> cons3 = bachelorClass.getConstructor(String.class, boolean.class);

        Bachelor bachelor1 = (Bachelor) cons1.newInstance(); // casting is required here
        Bachelor bachelor2 = (Bachelor) cons2.newInstance("arctos"); // casting is required here
        Bachelor bachelor3 = (Bachelor) cons3.newInstance("arctos li", true); // casting is required here

        assertEquals("unknown", bachelor1.getName());
        assertFalse(bachelor1.isGraduated());
        assertEquals("arctos", bachelor2.getName());
        assertFalse(bachelor2.isGraduated());
        assertEquals("arctos li", bachelor3.getName());
        assertTrue(bachelor3.isGraduated());
    } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
        e.printStackTrace();
    }
}
```

Note:

- `constructor.getName()` will return fully qualified name  **including package name** together;
- `class.getConstructor()` will throw `NoSuchMethodException` if **parameters are not matching**, the order of parameters also need to be correct;
- since `getConstructor()` will return a **`?`** type of constructor, **type casting is required to instanciate new objects**, otherwise it will be **Object** at complie time (in this case it still will be **Bachelor** at runtime, but casting still required). 
- `constructor.newInstance()` will throw `IllegalAccessException`, `InstantiationException`, and `InvocationTargetException`. They all needs to be handled. 

### Inspecting Fields

For class fields, we can **retrieve it, check the name, type, and even modify the value**. Now let's see how to do this.

To get all available public fields of a class or search a public field by field name, we can use `getFields()` or `getField({field_name_string})`:

```java
@Test
public void givenClass_whenGetsPublicFields_thenCorrect() {
    try {
        Class<?> bachelorClass = Class.forName("com.arctos.sample.reflection.Bachelor");
        Field[] fields = bachelorClass.getFields();

        assertEquals(1, fields.length);
        assertEquals("GENDER", fields[0].getName());
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
    }
}

@Test
public void givenClass_whenGetsPublicFieldByName_thenCorrect() {
    try {
        Class<?> bachelorClass = Class.forName("com.arctos.sample.reflection.Bachelor");
        Field field = bachelorClass.getField("GENDER");

        assertEquals("GENDER", field.getName());
    } catch (ClassNotFoundException | NoSuchFieldException e) {
        e.printStackTrace();
    }
}
```

To get private fields with the class, we can use `getDeclaredFields()` or `getDevlaredField({field_name_string})`:

```java
@Test
public void givenClass_whenGetsDeclaredFields_thenCorrect() {
    try {
        Class<?> bachelorClass = Class.forName("com.arctos.sample.reflection.Bachelor");
        Field[] fields = bachelorClass.getDeclaredFields();

        assertEquals(1, fields.length);
        assertEquals("graduated", fields[0].getName());
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
    }
}

@Test
public void givenClass_whenGetsDeclaredFieldByName_thenCorrect() {
    try {
        Class<?> bachelorClass = Class.forName("com.arctos.sample.reflection.Bachelor");
        Field field = bachelorClass.getDeclaredField("graduated");

        assertEquals("graduated", field.getName());
    } catch (ClassNotFoundException | NoSuchFieldException e) {
        e.printStackTrace();
    }
}
```

Then when we have the `Field` object, we can use `getType()` to get the class of field type like:

```java
@Test
public void givenClass_whenGetsFieldType_thenCorrect() {
    try {
        Class<?> bachelorClass = Class.forName("com.arctos.sample.reflection.Bachelor");
        Field field = bachelorClass.getDeclaredField("graduated");
        Class<?> fieldClass = field.getType();

        assertEquals("boolean", fieldClass.getName());
    } catch (ClassNotFoundException | NoSuchFieldException e) {
        e.printStackTrace();
    }
}
```

Finally let's see how can we **modify fields inside the class or public in superclass**:

```java
@Test
public void givenClassField_whenSetsAndGetsValue_thenCorrect() {
    try {
        Class<?> bachelorClass = Class.forName("com.arctos.sample.reflection.Bachelor");
        Bachelor bachelor = (Bachelor) bachelorClass.getConstructor().newInstance();
        Field field = bachelorClass.getDeclaredField("graduated");
        field.setAccessible(true); // set it to be accessible so we can modify it in below

        assertEquals(false, field.get(bachelor)); // pass object to get() method to retrieve the value
        assertFalse(field.getBoolean(bachelor));
        assertFalse(bachelor.isGraduated());

        field.set(bachelor, true); // to set value of field, we need pass object and the new value

        assertEquals(true, field.get(bachelor));
        assertTrue(field.getBoolean(bachelor));
        assertTrue(bachelor.isGraduated());

    } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchFieldException e) {
        e.printStackTrace();
    }
}

@Test
public void givenClassField_whenGetsAndSetsWithNull_thenCorrect() {
    try {
        Class<?> bachelorClass = Class.forName("com.arctos.sample.reflection.Bachelor");
        Field field = bachelorClass.getField("GENDER");
        field.setAccessible(true); // set it to be accessible so we can modify it in below

        assertEquals("male", field.get(null)); // pass null as instance of class parameter

        field.set(null, "female");

        assertEquals("female", field.get(null));
    } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
        e.printStackTrace();
    }
}
```

Note:

- `getFields()` or `getField({field_name_string})` will retrieve **all public fields defined in the class and all superclasses**;
- `getDeclaredFields()` or `getDevlaredField({field_name_string})` will retrieve **all public and private fields defined in the class**, nothing from superclass;
- both `getField({field_name_string})` and `getDevlaredField({field_name_string})` will throw `NoSuchFieldException` **if given field name is wrong or not found**;
- `field.setAccessible(true)` needs to be called before **modify any private field**;
- `field.getBoolean(object)` will return **a boolean object** but `field.get(obj)` will return **a generic object**, that's the only difference;
- for **static fields**, only need pass `null` as the instance of class parameter will be fine.

### Inspecting Methods

The last thing we want to inspecting is class methods, by using Java reflection, we can **retrieve all methods** of a class or **invoke it with parameters** at runtime. So let's start from retrieve all public methods or declared methods:

```java
@Test
public void givenClass_whenGetsAllPublicMethods_thenCorrect() {
    try {
        Class<?> bachelorClass = Class.forName("com.arctos.sample.reflection.Bachelor");
        Method[] methods = bachelorClass.getMethods();

        List<String> methodNames = getMethodNames(methods);
        assertTrue(methodNames.containsAll(Arrays
                .asList("equals", "notify", "notifyAll", "hashCode", "toString", "getClass",
                        "study", "isGraduated", "setGraduated", "getName", "setName")));
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
    }
}

@Test
public void givenClass_whenGetsOnlyDeclaredMethods_thenCorrect() {
    try {
        Class<?> bachelorClass = Class.forName("com.arctos.sample.reflection.Bachelor");
        List<String> actualMethodNames = getMethodNames(bachelorClass.getDeclaredMethods());
        List<String> expectedMethodNames = Arrays.asList("study", "isGraduated", "setGraduated", "getStudyString", "isBachelor", "degreeName");

        assertEquals(expectedMethodNames.size(), actualMethodNames.size());
        assertTrue(expectedMethodNames.containsAll(actualMethodNames));
        assertTrue(actualMethodNames.containsAll(expectedMethodNames));
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
    }
}
```

Similar to fields, private methods need to set `accessible` flag to **true** before invoke:

```java
@Test
public void givenMethodName_whenGetsMethod_thenCorrect() throws Exception {
    Bachelor bachelor = new Bachelor();
    Method getStudyStringMethod = bachelor.getClass().getDeclaredMethod("getStudyString");

    assertFalse(getStudyStringMethod.isAccessible());

    getStudyStringMethod.setAccessible(true); // set it to be accessible so we can invoke it in below

    assertTrue(getStudyStringMethod.isAccessible());
    assertEquals("I'm studying!", getStudyStringMethod.invoke(bachelor));
}
```

Finally, we can pass parameter in `invoke` method as well:

```java
@Test
public void givenMethod_whenInvokes_thenCorrect() {
    try {
        Class<?> bachelorClass = Class.forName("com.arctos.sample.reflection.Bachelor");
        Bachelor bachelor = (Bachelor) bachelorClass.getConstructor().newInstance();
        Method isGraduatedMethod = bachelor.getClass().getDeclaredMethod("isGraduated");
        Method setGraduatedMethod = bachelor.getClass().getDeclaredMethod("setGraduated", boolean.class);
        boolean graduated = (boolean) isGraduatedMethod.invoke(bachelor);

        assertFalse(graduated);
        assertFalse(bachelor.isGraduated());

        setGraduatedMethod.invoke(bachelor, true);

        boolean graduated2 = (boolean) isGraduatedMethod.invoke(bachelor);
        assertTrue(graduated2);
        assertTrue(bachelor.isGraduated());
    } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
        e.printStackTrace();
    }
}

@Test
public void givenMethod_whenInvokesWithNull_thenCorrect() {
    try {
        Class<?> bachelorClass = Class.forName("com.arctos.sample.reflection.Bachelor");
        Bachelor bachelor = (Bachelor) bachelorClass.getConstructor().newInstance();
        Method isBachelorMethod = bachelor.getClass().getDeclaredMethod("isBachelor");
        Method degreeNameMethod = bachelor.getClass().getDeclaredMethod("degreeName");

        isBachelorMethod.setAccessible(true); // set it to be accessible so we can invoke it in below
        degreeNameMethod.setAccessible(true); // set it to be accessible so we can invoke it in below

        assertTrue((boolean)isBachelorMethod.invoke(null));
        assertEquals("Bachelor", degreeNameMethod.invoke(null));
    } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
        e.printStackTrace();
    }
}
```

Note:

- `class.getMethods()` will retrieve **all public methods defined in the class and all superclasses**;
- `class.getDeclaredMethods()` will retrieve **all public and private methods defined in the class**, nothing from superclass;
- `method.setAccessible(true)` needs to be called before **invoke any private methods**;
- for **static methods**, just same as static field, pass a `null` object as parameter to invoke will be fine.

### Useful link

All codes snippets in this post can be find in [here](https://github.com/Yunze-Li/PersonalBlog/tree/master/codes/sample)

[Guide to Java Reflection](https://www.baeldung.com/java-reflection)

[Type Introspection Wikipedia](https://en.wikipedia.org/wiki/Type_introspection)

[关于Java中的反射](https://blademastercoder.github.io/2014/11/15/java-reflect.html)