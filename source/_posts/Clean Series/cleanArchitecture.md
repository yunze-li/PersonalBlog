---
title: Clean Architecture Summary
date: 2020-04-28
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

### Open-Closed Principle (OCP)

### Liskov Substitution Principle (LSP)

### Interface Segregation Principle (ISP)

### Dependency Inversion Principle (DIP)
