---
title: ClearArchitecture - Architecture
date: 2020-07-09
thumbnail: /thumbnails/CleanArchitecture.jpg
toc: true
categories:
  - 技术博客
  - Reading
tags:
  - English
  - Clean Architecture
---

After we known all principles in class level and component level, finally we reached **Architecutre** level. In this post, we will summarize all architecture level knowedge concepts quickly and finish this book for now, this book has much more useful stuffs that we are not covered, so we will come back and revisit someday after we have more understanding about software architecture.

<!-- more -->

## Part V Architecutre

### Clear Architecture

By dividing the software into layers, the architecture has the following characteristics:

- **Independent of frameworks**: not depend on the existence of any library, use them like plug-in;
- **Independent of the UI**: not effected if UI changed, easy to replace with another UI;
- **Independent of the database**: not effected whatever DB is using like SQL or Mongo, CouchDB;
- **Independent of any external agency**: business rules don't know anything about outside world;
- **Testable**: business rule can be tested without UI, DB or web server, etc.

In this way, we have the diagram structure of a **Clear Architecture** like:

![](https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/clear_architecture.png)

From this diagram, we can see:

1. Inner circles represent higher level components and outer is lower, in another word, the **further in** you go, the **higher level** the software becomes;
2. Source code dependencies must **point only inward**, toward **higher-level** policies. Nothing in an inner circle can know anything at all about outer circle.
3. Inner circle has **higher abstraction and stability** while outer circle are **more concrete and easy to change**;
4. The lower right corner shown how to **cross circle boundaries**;
5. This four circles model is **not strict**, sometimes you may find **more than four layers** which is totally make sense: what important is **following the component rules** we introduced in last post [here](https://yunze-li.github.io/2020/07/09/ClearArchitecture2/);
6. When you want to pass data crosses boundaries, keep it **isolated, simple and easy to be used by inner circle**, which means: **always processing raw data in outer circle** before pass it inward.

Now let's review this architecture model layer by layer:

#### Entites

This is the **core layer** of the model which contains **enterprise-wide critical business rules**. Usually this should be shared by all different applications in the enterprise, it can be **business objects** or **a set of data structure**. These are the least likely to change and shouldn't be effected when something external changed.

#### User Case

This is the layer of **application-specific business rules** that should be different between applications like mobile apps or browser websites. Changes in this layer **should not affect Entites layer** and vice versa, but the **operation of the application will affect logic inside this layer for sure**.

#### Interface Adapters

This layer contains a set of adapters that **convert data from the format most convenient for the use cases and entities to the format most convenient for some external agency** such as database, web or UI. For example, all models, views and presenters of the MVP pattern should be inside this layer. Also adapters in this layer should be **bi-directional converting** which means is should also **convert data from external agency to use cases and entities as well**.

#### Frameworks And Drivers

This is the **outermost layer** of the model which contains **all frameworks and tools** such as database, web framework or UI, in general, **this is all the details go**. Usually you don't need write much code in here, other than **glue code that communicates to the next circle inward**.

### Architecture Decoupling

#### Indenpendence

A good architecture should support all the following:

- **Use cases and opearation of the system** (support the intent of the system)
- **Maintenance** of the system (support daily operations requirements)
- **Developement** of the system (support contributed by whole company without conflicts)
- **Deployment** of the system (immediate deployment)

To achieve this, we need to **decouple system into layers**. Different layer should have different reason and rate to change. This decoupling mode can have three levels:

1. **Source Level**: control the dependencies between **source code modules** so changes to one module do not force changes of others;
2. **Deployment Level**: control dependencies between **independently deployable units** like jar failes or shared libraries;
3. **Service Level**: control dependencies between **each services** so communicate **solely through network packets**.

As always, it' **hard to know which mode is best** during the early phase of a project. As the project grows, the **optimal mode may change**.

#### Boundary

Boundaries is **drawing a line to prevented each side of the line from knowing anything about the other side**. Drawing the boundary lines helped **delay** and **defer** decisions and saved amount of time and troubles.

To draw boundary lines in software architecture, we need follow these steps:

1. Partition the system into **components**, some components are **core business rules** and others are **plugins**;
2. **Arrange code** in those components such that arrows between them point in **one direction to core business**;
3. Apply **SRP**(Single Responsibility Principle) and it should tell **where to draw the boundary line**.

#### Details

##### Database

The database is a **utility that provides acccess to the data**, it is just a mechanism we use to **move the data back and forth** between the surface of the dask and RAM. The organizational structure of data, the data model, is **architecturally significant** and the **technologies and systems** that move data on and off are not.

##### Web

The web is an **I/O device**, it is a **GUI**, and GUI is a detail which only matters **input and output**.

##### Framework

> Don't marry the framework!

Frameworks are **not architecture**. You must make **a huge commitment** to the framework, but the framework author makes no commitment to you whatsoever. Use the framework but **do not couple to it** by **deriveing proxies** and keep those proxies in components that are **plugins to business rules**.