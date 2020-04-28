---
title: Clean Code Summary
date: 2020-04-28
tags: 
  - Clean Code
---

## Chapter 2 Meangingful Names

- **Use Intention-Revealing Names**: avoid nonsense naming  
- **Avoid Disinformation**:
  - using FULLNAME, avoid abbrevation
  - `accountList` > `accounts` > `List`

- **Make Meaningful Distinctions**: `ProductInfo` and `ProductData`, `Customer` and `CustomerObject` has no difference at all!

- **Use Pronounceable, Searchable Names**: the length of a name should correspond to the size of its scope

- **Interface and Implementtations**: avoid passing `IShapeFactory` as interface, using `ShapeFactoryImpl` in implementation

- **Class Name**: Avoid using `Manager`, `Processor`, `Info`, `Data`as class name, using noun or noun phrase word

- **One Word Per Concept**: Don't use `retrieve`, `fetchr`, `get` all at same time! It's horrible!

## Chapter 3 Functions

- **Small!**: 
  - Funtions should be **very small**
  - Avoid nested structures and each block should be one line
  
- **Function should do one thing**
  - can't extract another function from it witha nmae that is not merely a restatement of its implementation
  - **Switch statements**: using polymorphism with *abstract factory* pattern
  
- **Common Monadic Forms**
  - Ask question about argument like `boolean isFileExist(File file)`
  - Operating on argument, transforming it into something and return like `InputStream fileOpen(File file)`
  - Interpret function call as *event* and use argument to alter state of system without output argument like `void passwordAttemptFailedNtimes(int attempts)`
  - Try to avoid any other form besides those three above

- **Dyadic Forms**
  - Sometime reasonable if it's a natural cohesion or ordering like `new Point(0, 0)` or `assertEquals(expected, actual)`
  - It itn's evil, but it has cost and there always a way to at least convert it to **Monadic**, just depends on whether it worth or not

- **Argument Objects**
  - Using wrapper to wrap into a class of their own
  - see these two following declarations:

    ```kotlin
    Circle makeCircle(double x, double y, double radius);
    Circle makeCircle(Point center, double radius);
    ```
  
- **Output Arguments**
  - Try not using output argument, if must change state of something, make it change inside it's own

- **Prefer Exception to Returning Error Codes**
  - Error code always force caller to deal with it immediately
  - using using *try-catch* block, the code to deal with succeed or error will be separated
  - extract try and catch block to its own method should be more clear

## Chapter 4 Comments

- **Comment should be no need at all**
  - It's always the compensate for failure to express in code
  - Programmer always forget to maintain, which makes them misleading
  - Save the energy to write better and clearer code

## Chapter 6 Objects and Data Structures

- **Data Abstraction**
  - Expose abstract interfaces to allow user manipulate data without knowing its implementation
  - Consider serious which data should be exposed

- **Data/Object Anti-Symmetry**
  - ***Objects***: Hide data behind abstractions and expose funtions that operate their data
  - ***Data***: Expose data and have no meaningful functions
  - ***Procedural code***: Easy to add new functions, hard to change data structure
  - ***Object Oriented code*** Easy to add new classes, hard to add new functions
  - Need to use **both of them** properly to make clean code, but not in one class (***hybird*** is the worst!)

## Chapter 7 Error Handling

- **Use Exceptions Rather Than Return Codes**
  - Error code needs to be returned every single case, make the code very complicated and cultter caller
  - Separate detection logic with handle logic

- **Define Exception Classes in Terms of Caller's Needs**
  - ***Wrapping third-party API*** is a best practice, easy testing, easy migration and less dependencies
  - Define your own exception type inside wrapper to make handling logic easier outside the wrapper(only one type outside)

- **Null Handling**
  - Don't Return *Null*: throw an exception or using **Special Case Pattern**
  - Don't Pass *Null*: never pass null in methods

## Chapter 8 Boundaries

- **Using Third-Party Code**
  - Hide the interface inside a class that we can *control*
  - Avoid when change comes, we need change everywhere
  - Make a `Wrapper` or `Adapter` to hide thrid-party interface 

- **Learning Test / Boundary Test**
  - It's free and can be used to learning thrid-party code
  - It can be used to detect ***breaking changes***

## Chapter 10 Classes

- **Class Organization**
  - Public static constants
  - Private static constants
  - Public instance variables (if any)
  - Private instance variables
  - Public functions
  - Private function should directly after their caller

- **Class should be small**
  - The more ambiguous the class name, the more likely it has too many responsibilities
  - Avoid weasel words like `Manager`, `Processor` or `Super` which will aggregate responsibilities

- **Single Responsibility Principle**
  - A class or module should have one and only one reason to change
  - Identity ***reason to change*** often helps for better abstraction of our code
  - A system composed of many small , well-structured classes is *always better* than a few large, multipurpose classes

## Chapter 11 System (*Need revisit in future*)

- **Separate Constructing a System from Use it**
  - System should not know or care about startup logic, it assume everything should be constructed correctly when start running
  - Using *Dynamic Factory* pattern to hide the building logic and allow application to decide **when** and **what** to build during runtime
  - ***Dependency Injection***: a way to achieve *IOC (Inversion of Control)* principle: the responsibility of instantiating dependencies will delegate to another "authoritative" mechanism
