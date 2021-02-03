---
title: Android Architecture(MVC, MVP and MVVM)
date: 2021-01-15
thumbnail: /thumbnails/AndroidArchitecture.jpeg
toc: true
categories:
  - Technical
  - Android
tags:
  - Kotlin
  - Android Architecture
  - English
---

To make our Android application **easy to extend, modify, refactor, test or even just read** it, the architecture of the application is very important. For sure we can't have a single class **MainApplication** or **MainActivity** and put everything inside, it will be a nightmare to just read the code and understand. There are three main architectures to use in Android to build our application and gives us power to do all operations we mentioned above: `Model-View-Controller(MVC)`, `Model-View-PResenter(MVP)` and `Model-View-VIewModel(MVVM)`. Let's talk about them one by one.

<!-- more -->

## MVC

- **`Model`** contains **all data models and states** inside, it also has **business logic** as well;
- **`View`** responsible for **rendering UI** and it contains **everything that interact with user** like Views/Layouts;
- **`Controller`** is like a bridge in between, it **handles the communication between model and view** to finish the workflow.

A normal MVC workflow looks like this:

<img src="https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/MVC.png" style="zoom:110%;" />

 ### Pros

- Decoupling View and Model, Model itself is easy to test
- Faster development process

### Cons

- Controller contains a lot of code, which makes it **hard to extends and modify**
- Controller usually is Fragment/Activity and it's **hard to do unit test**
- Model is hard to change and maintain when **View changes frequently** (change from View is hard to adapt)



## MVP

- **`Model`** contains **all data models and states** inside, it also has **business logic** as well (**same as MVC**);
- **`View`** responsible for **rendering UI** and it contains **an implementation of View interface** which will be used by presenter;
- **`Presenter`** contains the **View interface** and communicate to View through **interface methods call**, it also **communicate to Model same as MVC**.

MVP workflow is like:

<img src="https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/MVP.png" style="zoom:120%;" />

### Pros

- Presenter and View **communicate through `View Interface`** which makes Presenter **easy to test**
- Model and View **totally decoupled**, View can be modified without change Model 
- Presenter is **easy to extends, modify and reuse**
- View has only **pure dumb UI functions** and it's easy to swap different views

### Cons

- Presenter and View **still tightly coupling**
- Code size is excessive and **need create lots of View Interface** for each MVP sturcture 



## MVVM

- **`Model`** is **same as MVC/MVP architecture**;
- **`View`** is **same as MVP architecture**, but normally it is a XML file or **DataBinding**;
- **`ViewModel`** has same responsible as **MVP architecture**, but by using **Observable and DataBinding**, it can update view without knowing what it will do (**decoupling from View**).

MVVM workflow shown like:

<img src="https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/MVVM.png" style="zoom:120%;" />

### Pros

- View and ViewModel is **loosely coupling through DataBinding**
- **Easy to test ViewModel** and **higher code coverage** (XML file no need to test at all)

### Cons

- Sometimes for complex UI, XML file **can be very large and complicated**
- Code size is still quite excessive



## Compare and diff

In conclusion, here is a comparsion chart of all these three architectures:

<img src="https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/MVC%26MVP%26MVVM.png" style="zoom:120%;" />

### Reference link

[Common Android Architecture](https://anmolsehgal.medium.com/common-android-architectures-mvc-vs-mvp-vs-mvvm-afd8461e1fee)

[MVC vs MVP vs MVVM Architecture in Android](https://blog.mindorks.com/mvc-mvp-mvvm-architecture-in-android)

