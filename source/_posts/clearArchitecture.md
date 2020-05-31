---
title: Clear Architecture Summary
date: 2020-05-11
tags:
    - Clean Architecture
---

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

It's easy to understand that `HourReporter`, `PayCalculator`, `EmployeeSaver` classes has higher prority then `Employee` class and we want to avoid code change on them when we need change Employee class. The XML of structure is like:

{% asset_img clear_architecture_1.jpg %}

Note that an arrow pointing from class A (*Employee*) to class B(*HourReporter*, *PayCalculator*, *EmployeeSaver*) means: **the source code of class A mentionas the name of class B, but class B mentions nothing about class A**. In this XML, `Employee` depends on these three classes, so those three classes is protected from changes in `Employee`.

### Liskov Substitution Principle (LSP)

> Subclass should be substitutable for their base class

A typical example of this principle is **square/rectangle problem**, let's briefly recall the problem by first:




### Interface Segregation Principle (ISP)

>


### Dependency Inversion Principle (DIP)

>
