---
title: Clear Architecture - Programming Paradigms & Design Principles
date: 2020-05-11
thumbnail: /thumbnails/CleanArchitecture.jpg
toc: true
tags:
  - Clean Architecture
  - Reading 
---

*Clean Architecture* is one of the series book that written by "Uncle Bob". In last post, I writed a summary of the *Clean Code*. It is super useful for begineers. But as we going deeper, we need take a step back and see the "big picture" -- software architecture, which is all this book talk about.

<!-- more -->

## Part II Starting with the Bricks: Programming Paradigms

### Structured Programming: direct transfer of control

Constructed from **sequence**, **selection** and **iteration** to replace old ***goto*** keyword.

A typical structured programming example:

```kotlin
// sequence execution
val sum = 0
val current = 1
// iteration
while (current < 10) {
  sum += current
  current += 1
}
// selection
if (needNegative) {
  return sum *= -1
} else {
  return sum
}
return sum
```

>All programs can be constructed from just these three structures: sequence, selection and iteration.

### Object-Oriented Programming: indirect transfer of control

Through the use of polymorphism to ***gain absolute control*** over every source code dependency in the system.
To better understnad about power of polymorphism. also check **Dependency Inversion Principle** below.

A typical object-oriented programming example:

```kotlin
// define Vehicle interface
interface Vehicle {
  fun alarm()
  fun turnOn()
  fun turnOff()
}

// different brand of car, should all implement the same interface
class BMW: Vehicle {
  override fun alarm() {
    makrSound("di~di~")
  }
  override fun turnOn() {
    ...
  }
  override fun turnOff() {
    ...
  }
}

// but they can have diffrent implementation (such as differnt alram sound)
class Ford: Vehicle {
  override fun alarm() {
    makrSound("do~do~")
  }
  override fun turnOn() {
    ...
  }
  override fun turnOff() {
    ...
  }
}

// when client code is using them, they don't need to know the implementation details
Ford().turnOn()
BMW().ring()
```

> Any source code dependency, no matter where it is, can be inverted.

### Functional Programming: variable assignment

Variables in functional languages **do not vary**, which will causing ***none of*** race condition, deadlock or concurrent update problems.

A typical functional programming example:

```kotlin
class Person constructor (
  private val name: String
) {
  fun getName(): String {
    return name
  }
}
// define a function to get person's name
val personName = fun(person: Person) = person.getName
```

> Concurrent problems can be eliminated by segregate the application into mutable and immutable components.

## Part III Design Principles

### Single Responsibility Principle (SRP)

> A module should have one, and only one reason to change

A common way to impose this principle is: Separate shared functional code blocks from specific logic. For example, here is a Employee class from payroll application:

```kotlin
class Employee {
    private val employeeDB = EmployeeDatabase()
    fun getWorkingHours(): Int {
        return employeeDB.queryWorkingHours()
    }
    fun calculatePayroll(): Int {
        val workingHours = getWorkingHours()
        val salaryRatePerHour = employeeDB.querySalaryRatePerHour()
        return workingHours * salaryRatePerHour
    }
    fun saveEmployee() {
        employeeDB.updateEmpployee(this)
    }
}
```

It's clear that this Employee class has too many responsibilities including query working hour, query salary rate, calculate payroll, update employee info etc. And typical problem can be:

1. It's hard to extends, when introduce different ways to calculate payroll for example;
2. Who wants to change **ONLY** working hours fetching may also touch payroll calculation accidentally;
3. *EmployeeDatabase* is exposed to Employee class, which is dangerous for open visibility.

To solve these issues, we can do:

```kotlin
class Employee {
    private val hourReporter = HourReporter()
    private val payCalculator = PayCalculator()
    private val employeeSaver = EmployeeSaver()
    fun getWorkingHours(): Int {
        return hourReporter.reportHours()
    }
    fun calculatePayroll(): Int {
        return payCalculator.calculatePay()
    }
    fun saveEmployee() {
        employeeSaver.saveEmpployee(this)
    }
}
```

As we can see, after we create ***HourReporter***, ***PayCalculator*** and ***EmployeeSaver***, we separate different responsibilities to different class so:

1. if we want to have diffrent ways to calculate payroll, it can be updated inside ***PayCalculator*** and no need to update Employee class;
2. when working hours fetching need to change, no need to touch payroll calculation logic;
3. no more database expose to Employee class since it's handled by these three classes **internally**.

### Open-Closed Principle (OCP)

> A software artifact should be open for extension but closed for modification.

This is a common case that sometimes we want to extends part of our code and extends without having to modify that artifact. To make this possible, we need to implement component hierarchy structure so **code change in lower level component will not effect higher level component**. In other word, higher level component is being ***protected*** from code change chain.

Let's continue on the Employee example:

```kotlin
class Employee {
    private val hourReporter = HourReporter()
    private val payCalculator = PayCalculator()
    private val employeeSaver = EmployeeSaver()
    fun getWorkingHours(): Int {
        return hourReporter.reportHours()
    }
    fun calculatePayroll(): Int {
        return payCalculator.calculatePay()
    }
    fun saveEmployee() {
        employeeSaver.saveEmpployee(this)
    }
}
```

It's easy to understand that `HourReporter`, `PayCalculator`, `EmployeeSaver` classes has higher prority then `Employee` class and we want to avoid code change on them when we need change Employee class. The UML of structure is like:

![](https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/employee_uml.jpg?token=AOJCUF3KSFOTDP2ZTUL7N7S65W72I)

Note that an arrow pointing from class A (*Employee*) to class B(*HourReporter*, *PayCalculator*, *EmployeeSaver*) means: **the source code of class A mentionas the name of class B, but class B mentions nothing about class A**. In this XML, `Employee` depends on these three classes, so those three classes is protected from changes in `Employee`.

### Liskov Substitution Principle (LSP)

> Subclass should be substitutable for their base class

A typical example of this principle is **square/rectangle problem**, let's briefly recall the problem first by UML:

![](https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/rectangle_square_uml.jpg?token=AOJCUF276FUZ7ZXLNXHICRS65W73O)

As common sense, a `Square` should be treated as a special `Rectangle` which means all operations or parameters for a rectangle object should also effect on a square object. Let's see the code block below:

```kotlin
val rectangle = Rectangle()
rectangle.setWidth(2)
rectangle.setHeight(5)
assert(rectangle.getArea() == 10)
```

This should work fine and the assertion should pass as well, but when we do the same thing to a `Square` like:

```kotlin
val square= Square()
square.setSide(2) // how can width and height change at same time???
square.setSide(5)
assert(rectangle.getArea() == 10) // this will failed and area will be 25
```

The core problem behind this case is : **`square has a feature which rectangle don't: need set both width and height always at same time with same value, they can't be changed separately`**. It may causing a lot problems becuase of this.

### Interface Segregation Principle (ISP)

> No client should be forced to implement methods it doesn't use

When we play with abstract interfaces and implementation, a common problem is we find there are extra methods that the implementation not used at all. The easiest way to handle it is just override it and make it empty. But by doing this, there is a risk that it might be touched by other maintainers or even yourself in future since you may not remember. A good fix on this should be separate it into multiple specific interfaces like:

![](https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/ISP_fix.jpg?token=AOJCUF2KLODQUOIT6PAFFAC65W74Q)

In my opinion this is a good way to minimum the risk and separate interface for different class if they not use all of them, but also note that **this might causing a lot interfaces to be generated, this is the trade-off**.

### Dependency Inversion Principle (DIP)

> High-level module shouldn't depend on low-level, but both should only depend on abstraction, not on concretions

This is the most information principle in my opinion and also the hardest one to understand. To understand this, first question is: what is abstraction and why we need it? To answer this, let's see this example UML:

<img src="https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/DIP_concrete.jpg?token=AOJCUF2ORMBUUTVHECG6SWK65W75S"  />

This is the concrete implementation for an application with a simple service. `Service` is created by `ServiceFactory` by calling `serviceFactory.createService()`. It works fine but it has several problems:

1. `Application` can access **everthing** inside `Service` and `ServiceFactory`, it including something they don't use (violation of ISP);
2. Both `Service` and `ServiceFactory` are dependencies of `Application`, which means when their code changed, `Application` will need to re-comple and re-generated everytime (violation of OCP);
3. If we want to add new type of service in future, both `Application` and `ServiceFactory` need change the code and still hard to extends (violation of OCP again).

So according to what we learned before, here is a better solution for all three points above:

![](https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/DIP_abstract_factory.jpg?token=AOJCUF3HOV4GQEWOL4STCLK65W77I)

As you can see in the solution UML, we make both `ServiceFactory` and `Service` as **Interface** and give them implemnentations for each interface. Note that **implementation is hided from `Application` so it only communicate with interfaces**. Why? Look back into all three problems above and you will find all of them is successfully solved by using **`Interface`**!

Now let's understand the description of this principle with the example: consider `Application` as low-level module and `ServiceFactory`, `Service` as high-level module, `Application` should not depends on `ServiceFactory` and `Service` because of the violation of ISP, OCP and DIP, and they should all depends on the abstraction, which is **`Interface`** in this case. And by using interface, the control flow is successfully inverted from `Application -> ServiceFactory` to `ServiceFactoryImpl -> Application`, same for Service as well. This is how **Inversion** come from.
