---
title: clearArchitecture - Desgin Principles & Component Principles
date: 2020-07-06
thumbnail: /thumbnails/CleanArchitecture.jpg
toc: true
categories:
  - Technical
  - Reading
tags:
  - English
  - Clean Architecture
---

In last post we talked about programming paradigms and **SOLID** principles. This is far more than enough to understand about architecture, so today let's continue on "Design Principles" and see more of them that need to follow when thinking like an architect.

<!-- more -->

## Part IV Components Principles

### Components

The **samllest entities that can be deployed** as part of a system. In java, they are **jar** files; In Ruby, they are **gem** files and in complied languages, they are **aggregation of binary** files. It can be **dynamically linked** together at runtime, which can be used as **plug-in**.

### Components Internal Principles

#### Reuse/Release Equivalence Principle(REP)

> To **resue software components**, it need to be tracked through a **release process** and are given **release numbers**. Because developer needs to know which **cohesive group** is the current compoent belong to.

In another word, component cannot be easily plug-in or pull-out **without version control**, it will mess up the whole project without clearly knowing **which version contains what feature**.

#### Common Closure Principle(CCP)

> Gather into components those classes that change **for the same reasons** and **at the same times**. Separate into different components those classes that changed for different reasons and at different times.

This is component level of **SRP **(single responsible principle), which means **a component should not have multiple reasons to change**. If you find two components are always changed together, try **merge them** into one.

#### Common Reuse Principle(CRP) 

> Don't force users of a component to depend on things they **don't need**

This is component level of **ISP** (Interface Segregation Principle) and a bit more generic. If there are not used dependencies exists in component, that means **when anything changed in those dependency, the component is highly possibly to do corresponding changes and then recompiled, revalidated and redeployed.** So be careful to only depend on the things you **really need**.

#### Component Tension Diagram

So after known those three component principle above, we can draw a tension diagram like this:

<img src="https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/component_tension_diagram.png" style="zoom:90%;" />

As we can see here, these three principle are **balanced to each other** and when you try to apply two of them, you will sacrifice the another. Normally we start from **CCP and CRP**, to make the component **working and changing fast**. As the component become more and more stable, we will start to **move left** and apply REP to make it easier to reuse and maintain until we find a **perfect balance in middle**. The final balance point are varied team by team.

### Components External Principle (Between Components)

#### Acyclic Dependencies Principle (ASP)

> Allow no cycles in the componet dependency graph

Dependency cycle **can work**, but **very difficult to change** because when new change apply to one componet, all other components need to **change to be compatiable**. The cycle itself performed like a "super big component" which a lot other component will depend on. 

Two ways to eliminate the cycle in component dependency graph:

1. Apply **Dependency Inversion Principle** (DIP): create an interface **inside the depender** component (the one **need depend on the other**) so the **dependee** will implement this interface. This UML will explain more clearly:

   ![](https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/ASP_interface_solution.png)

2.  Create a new component that **both depender and dependee should depend on**. In this way the cycle will be break since the new component **will not depend on** any of them. The UML should looks like:

   ![](https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/ASP_new_component_solution.png)

#### Stable Dependencies Principle (SDP)

> Depend in the direction of stability.

This principle is short and concise, but might not be easy to understand. A better description might be: Any component that we expect to be **volatile** should not be depended on by a component that is **hard to change**, which means **stable**. 

To measure the instabillity, we need use **I-metric** to meature. I-metric can be calulated by: `I = Fan-out / (Fan-in + Fan-out)`, for example:

<img src="https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/SDP_I_Metric.png" style="zoom:150%;" />

In this UML, we can see that both component A, B, C has **I-metric = 1**, which makes them **unstable and easy to change** because their changes **will not influence any other componets**. For component E, since **I-metric = 0**, which means it's **hard to change** because to change it, it might need to **change all components depend on it** such as B, C and D, which might **need change A as well** since A depends on D!  

For Componet D, **I-metric=0.25** which means it's still stable and hard to change, but the **effort to change it is less** than component E. So basically I-metric can give us a hint that **how easily this component can be changed**.

To make a stable component easy to change, we need to create something called **abstract component**. It is the same concept **as interface at class level**. so we will create a new component which contains interfaces and make both component depends on it, like:

![](https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/SDP_Abstract_Component.png)

As we can see before compoent Authorizer **I-metric = 0** which is hard to change, and after we using **abstract component Permissions**, it has **I-metric = 1** and **as easy to change as component Entities**. 

Before we finish this principle, there are two points need to mention:

1. SDP suggests that, in dependency graph, the I metric of a component **should be larger than** the I metric of the component that it depends on, which means the **I metric should keep decreasing in the direction of dependency chain**;
2. Abstract components are **very stable** (usually I-metric = 0), so they are **ideal target** for less stable components to depend on.  

#### Stable Abstraction Principle (SAP)

> A component should be as abstract as it is stable.

This is also too "abstract" to understand. So a better explanation should be: 

1. A **stable** component **should also be abstract** so that it's stability does not prevent it from **being extended**;
2. An **unstable** component **should be concrete** since its instability allows the concrete code within it to be **easily changed**.

Since we know that dependencies should run in the direction of stability, and **stable component should be abstract**, we can also know: **dependencies should run in the direction of abstraction**. For stability, we have **I-metric** to measure, so do we have similar **measurement for abstraction**? Of course yes! Come on, **A-metric**!

**A-metric** can be calculated by: `A = Na / Nc`, `Na` is **number of classes** inside the component and `Nc` is **number of abstract classes and interfaces** in the component. For an abstract component like we said above, `A-metric = 1` which means **totally abstract**; and for a componet without any abstract classes and interfaces, `A-metric = 0` which means **totally concrete**, pretty straightforward👍.

#### I-metric/A-metric Graph

Now we can two metrics to describe a component's stability and abstractness. But what's the relation between them? How can we evaluate it? This is the time **I-metric/A-metric Graph** start to use:

<img src="https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/IA_Graph.png" style="zoom:80%;" />

In this graph, we are using **A-metric** as vertical axis and **I-metric** as horizontal axis. In this way, **each component wil fall into a point with coordinates** in this graph. There are some points need to mention in this graph:

1. **Zone of Pain** is a zone that **not expected to put componets**, because in this zone components are **rigid,concrete and very hard to change**. The only excluded case might be Databse schema or some utility libraries;
2. **Zone of Uselessness** is also a **not expected** zone, because in this zone compoents are totally **abstract and dependent** which means might **nobody using them** and can be removed.
3. The **most desirable** position for a component is at **one of the two endpoints** of the Main Sequence. They are **stable and abstract**, or **flexible and concrete**.

Once the I/A graph is done, we can fill the components inside the graph and got this:

<img src="https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/IA_Graph_fill.png" style="zoom:80%;" />

By calculating the **Distance** between a point to Main Sequence: `D = |I + A - 1|`, we can get the result of **how far away a component is from ideal**. and then we can draw the **standard deviation line**(`Z = 1`) and focus on the component that **beyond these lines** and start refactor them. This is how these metrics can help us find the correct components to improve.

 