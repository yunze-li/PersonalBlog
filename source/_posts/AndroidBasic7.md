---
title: Android开发笔记（七）- Data Storage & Content Provider组件
date: 2021-02-04
thumbnail: /thumbnails/AndroidBasic.jpg
toc: true
categories:
  - Technical
  - Android
tags:
  - Kotlin
  - Java
  - Chinese
---

这篇文章将会介绍继[Activity组件](https://yunze-li.github.io/2020/06/16/AndroidBasic2/)，[Service组件](https://yunze-li.github.io/2021/01/11/AndroidBasic5/)，[BroadcastReceiver组件](https://yunze-li.github.io/2021/01/13/AndroidBasic6/)之后的最后一个组件：[ContentProvider组件](https://developer.android.com/reference/android/content/ContentProvider)。相比于前三个组件，ContentProvider的概念和用法都相对简单，所以在这里再补充比较一下安卓开发中比较常见的数据存储方式及其异同。

<!-- more -->

## Content Provider

Google开发文档中给出对于ContentProvider的介绍是：

>**Content providers** are one of the primary building blocks of Android applications, providing content to applications. **They encapsulate data and provide it to applications** through the single **`ContentResolver`** interface

这里要注意几个问题：

1. ContentProvider适用于**不同Application之间共享数据的情况**，比如有多个App需要同时读取通讯录中的联系人信息，因为实际上ContentProvider只是**提供一种可供多个App进行数据共享的“桥梁”**。
2. **数据提供方**需要实现ContentProvider中定义的 **`onCreate / getType / insert / delete / update / query`** 这几个方法，**通过某种具体的存储数据的方式**（Sqlite数据库，文件存储或者网络存储等）来实现这些方法；
3. **数据访问方**需要通过`Context.getContentResolver()`获得一个`ContentResolver`实例，然后使用提供的**`query / insert / delete / update`**等方法进行共享数据的CRUD操作；
4. ContentProvider中的数据通过**唯一的统一资源标识符(URI）**来标识其来源，具体格式为：**`URI = content://authority/path/id`** ，各部分的具体含义在下面的使用实例中介绍。

## ContentProvider使用实例

#### 1. 定义Contract帮助类，声明需要的常量

```kotlin
internal object DemoDatabaseContract {
    
    // SqlLiteDB相关常量
    internal object DemoDataEntry : BaseColumns {
        const val TABLE_NAME = "demoData"
        const val COLUMN_NAME = "name"
        const val COLUMN_COUNT = "count"
    }
    internal const val SQL_CREATE_ENTRIES =
        "CREATE TABLE ${DemoDataEntry.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${DemoDataEntry.COLUMN_NAME} TEXT," +
                "${DemoDataEntry.COLUMN_COUNT} TEXT)"
    internal const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${DemoDataEntry.TABLE_NAME}"
    
    // ContentProvider相关常量
    internal const val AUTHORITY = "com.arctos.demo.contentProvider"
    internal const val BASE_PATH = "demo/data"
    internal const val MATCH_CODE = 1

    val baseUri = Uri.parse("content://${AUTHORITY}/$BASE_PATH")
}
```

这里的常量主要用于： 1. 通过`SQLiteOpenHelper`类来创建**具体的数据存储实例**来实现`ContentProvider`中的各种方法；2. 在`ContentProvider`类中用于定义**URI**以及进行**匹配操作（URI matching）**。统一资源标识符的具体格式为：

<img src="https://raw.githubusercontent.com/Yunze-Li/BlogPictures/master/BlogPictures/pictures/URI-Android%20Basic.png" style="zoom:70%;" />

- 主题名（Schema）：ContentProvider的URI前缀，**Android统一规定**；
- 授权信息（Authority）：ContentProvider的唯一标识符，**用于区分不同的ContentProvider**；
- 表名（Path）：ContentProvider内单个表格的唯一标识符，**用于区分不同表格**；
- 记录（ID）：表格内的某一条记录，**如果为空则返回全部记录**。

#### 2. 实现具体的ContentProvider类

```kotlin
class DemoContentProvider : ContentProvider() {

    private lateinit var dbHelper: DemoDBHelper
    private lateinit var baseContext: Context
    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    override fun onCreate(): Boolean {
        return if (context == null) {
            false
        } else {
            baseContext = context!!
            dbHelper = DemoDBHelper(baseContext)

            // Add a URI to match and the matched code
            uriMatcher.addURI(AUTHORITY, BASE_PATH, MATCH_CODE)
            true
        }
    }
  	...
}
```

这里需要注意，通过`uriMatcher.addURI()`方法可以添加一个需要进行匹配的URI并赋予其一个**MATCH_CODE**，在使用`uriMatcher.match(uri)`方法时，**如果传入的uri和已经添加的URI匹配时，则会返回这个MATCH_CODE**。通过这个方法，我们可以进行更加精准的URI匹配确认，例如：

```kotlin
override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val db = dbHelper.writableDatabase

        // check the match code to verify
        return when (uriMatcher.match(uri)) {
            MATCH_CODE -> db.query(
                DemoDatabaseContract.DemoDataEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
            )
            else -> null
        }
    }
```

从这个query()方法可以看到，**只有当传入的uri与已添加的URI进行匹配并返回对应的MATCH_CODE时**，才会对数据库进行查找操作。

#### 3. 实现一个数据访问方实例

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cursor = this.contentResolver.query(baseUri, null, null, null, null)

        val columnList = mutableListOf<Column>()
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val columnName =
                    cursor.getString(cursor.getColumnIndex(DemoDatabaseContract.DemoDataEntry.COLUMN_NAME))
                val columnCount =
                    cursor.getString(cursor.getColumnIndex(DemoDatabaseContract.DemoDataEntry.COLUMN_COUNT))
                columnList.add(Column(columnName, columnCount))
            }

            // remember close the cursor after query is done
            cursor.close()
        }

        // columnList has all queried data here
        println(columnList.size)
    }

    data class Column(
        private val name: String,
        private val count: String
    )
}
```

由此可见，通过`this.contextResolver`可以获得一个`ContentResolver`实例，**而通过这个`ContentResolver`实例，就可以在不同的Activity，Application之间对于同一个ContentProvider内的数据进行增删改查等操作**。

## Other Data Storage

除了上面提到的ContentProvider之外，Android中还提供其它几种数据存储的方式，这里简单介绍一下各自的特点，以后会逐一详细介绍。

### Share Preference

**SharedPreferences**是Android平台上的一个**比较轻量级**的存储类， 它使用xml文件存放数据，非常适合用于存放配置参数。它的特性包括：

- xml文件**按key-value对存储**，可保存int，boolean，String，float，long和StringSet共**六种类型**；

- 模式参数包括：`MODE_PRIVATE`（只能在APP内部被访问，**覆盖已有内容**），`MODE_APPEND`（只能APP内部访问，**追加已有内容**）, `MODE_WORLD_READABLE`（外部程序可读，**但不可写**），`MODE_WORLD_WRITEABLE`（外部程序**可读且可写**）；
- `apply()`和`commit()`均可用于**提交内容修改**，但是apply()方法没有返回值，而commit()方法会返回boolean表示是否修改成功；
- `apply()`方法只是**原子提交到内存**，并未立刻提交到磁盘。而`commit()`方法是**同步提交磁盘**，效率较低，对结果不关心且无后续操作的话，**建议使用apply()**。

### Internal Storage

Ineternal Storage是指APP内部的存储空间。它**只对当前APP开放**（private to app），并且**当APP被卸载时，其内容会被全部清除**。它主要包含两个部分：cache directory（通过`getCacheDir()`方法调用）以及permanent file directory（通过`getFilesDir()`方法调用）。

### 其他存储方式

除了上面提到的两种方式之外，还有例如**SQlite数据库存储**，**网络云端存储**等方式，在以后的过程中一一介绍。这篇主要针对ContentProvider组件介绍的文章希望对你有帮助，谢谢！

### 参考文章

[Google Doc: ContentProvider](https://developer.android.com/reference/android/content/ContentProvider)

[Google Doc: ContentProvider Basics](https://developer.android.com/guide/topics/providers/content-provider-basics)

[Android ContentProvider使用](https://www.jianshu.com/p/ac40ed95d577)

[Android：关于ContentProider的知识都在这里了！](https://www.jianshu.com/p/ea8bc4aaf057)

