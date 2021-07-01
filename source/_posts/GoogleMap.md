## 前置步骤:

按照[官方文档](https://developers.google.com/maps/documentation/android-sdk/map-with-marker)的步骤完成SDK的setup，包括：

1. ***local.properties***中声明你的**GOOGLE_API_KEY**；
2. ***AndroidManifest.xml***中添加<meta-data>元素并映射前面加入的api key； 
3. 在root-level的***build.gradle***中添加如下依赖：

```groovy
buildscript {
    dependencies {
        // ...
        classpath "com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:1.2.0"
    }
}
```

4. 在app-level的***build.gradle***中添加如下依赖：

```groovy
plugins {
    // ...
    id 'com.google.android.libraries.mapsplatform.secrets-gradle-plugin'
}

dependencies {
    // ...
    implementation "com.google.android.gms:play-services-maps:17.0.1"
    implementation "com.google.maps.android:android-maps-utils:0.5"
}
```
5. Sync Gradle

## Google Map Fragment

首先，在需要显示GoogleMap的Layout中加入<fragment>元素，例如：

```xml
<fragment
    android:id="@+id/google_map_fragment"
    android:name="com.google.android.gms.maps.SupportMapFragment"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_marginTop="20dp"
    tools:context="com.arctos.mochabear.mapdemo.GoogleMapDemoActivity" />
```

这里注意两点：

1. `android:name="com.google.android.gms.maps.SupportMapFragment`设置fragment的类型为SupportMapFragment，**不设会报错**；
2. `tools:context="com.arctos.mochabear.mapdemo.GoogleMapDemoActivity"`设置fragment对应的activity为GoogleMapDemoActivity，**不设会报错**。

然后进入显示GoogleMap的Activity，这里也就是GoogleMapDemoActivity，此时需要注意两点：

1. 这个Activity需要实现[OnMapReadyCallback](https://developers.google.com/android/reference/com/google/android/gms/maps/OnMapReadyCallback)接口，在onMapReady(GoogleMap googleMap)中就可以获得对应的googleMap实例；
2. `onCreate()`方法中需要调用supportFragmentManager.findFragmentById(R.id.google_map_fragment)`去获得fragment的实例并转型成SupportMapFragment；    
实例代码如下：

```kotlin
class GoogleMapDemoActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

	// 获得SupportMapFragment实例并在googleMap加载好时发送notification
        val mapFragment = supportFragmentManager.findFragmentById(R.id.google_map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        // 获得加载好的GoogleMap实例
        this.googleMap = googleMap
    }
}
```

## Marker

Marker可以对于地图上任一位置进行标记和说明，简单的加载Marker的示例代码：

```kotlin
val melbourneLocation = LatLng(-37.813, 144.962)
val melbourne = map.addMarker(
    MarkerOptions().position(melbourneLocation)
)
```

### Marker设置

对于marker的设置通过调用`MarkerOptions`类的各种方法来完成，一些常用的设置方法如下：

```kotlin
val melbourneLocation = LatLng(-37.813, 144.962)
val melbourne = map.addMarker(
    MarkerOptions()
        .position(melbourneLocation)
        .anchor(0.5f, 0.5f)  // 设置Marker锚点, (0.0,0.0)代表左上，(1.0,1.0)代表右下
        .rotation(90.0f)  // 设置旋转角度
	.flat(true)  // 设置Marker是否随地图旋转角度
	.zIndex(1.0f) // 设置Z轴坐标，值越大，渲染时marker越在上层
	.alpha(0.7f)  // 设置透明度
	.title("custom title")	 // 自定义title
	.snippt("any random snippt")  // 自定义snippt
        .icon(BitmapDescriptorFactory.fromBitmap(bitmap))  // 自定义bitmap图标
)
```

### Marker自定义布局

使用自定义的marker_layout.xml布局来设置marker图标其实就是多一步：需要**把layout布局文件绑定数据并生成对应的bitmap**，实例代码如下：

```kotlin
val customMarkerView = layoutInflater.inflate(R.layout.marker_layout, null)

// 绑定自定义布局中各视图的数据，例如title，description等 
customMarkerView.findViewById<TextView>(R.id.custom_marker_title)?.apply { text = title }
customMarkerView.findViewById<TextView>(R.id.customer_marker_description)?.apply { text = description }

// 调用Google Map SDK里自带的IconGenerator类
val iconGenerator = IconGenerator(context)	// 此处需要Acvitity的context实例
iconGenerator.setContentView(customMarkerView)
iconGenerator.setBackground(null)

// 这里调用makeIcon()就可以获得对应的bitmap
val customIconBitMap = iconGenerator.makeIcon()

// 调用icon()方法来使用生成的bitmap作为marker icon
googleMap.addMarker(
    MarkerOptions()
        .position(destination)
  	.anchor(0.0f, 1.0f)
        .icon(BitmapDescriptorFactory.fromBitmap(customIconBitMap))
)
```

## InfoWindow

InfoWindow用于对于一个marker进行更详细的介绍和说明，通过点击marker可以打开/关闭一个infoWindow，要注意**同一时间只会有一个InfoWindow打开，点击开启新的InfoWindow会关闭上一个**。要显示默认的InfoWindow，代码如下：

```kotlin
val melbourneLocation = LatLng(-37.813, 144.962)
val melbourne = map.addMarker(
    MarkerOptions()
        .position(melbourneLocation)
        .title("this is info window title")
  	.snippet("this is info window snippt")
)
melbourne.showInfoWindow()
```

### InfoWindow设置

InfoWindow本身的数据是通过调用`MarkerOptions.title()`和`MarkerOptions.snippt()`方法来设置，除此以外并没有更多的支持调整的built-in方法。如果需要进行更多的设置，只能自定义InfoWindow的视图来进行调整。

### 自定义InfoWindow视图

自定义InfoWindow的关键在于：**创建一个子类实现GoogleMap.InfoWindowAdapter()接口**，实例代码如下：

```kotlin
googleMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
    override fun getInfoWindow(p0: Marker): View {
      	// 这里返回的view会使用整个InfoWindow的空间，包括frame和background
        val view: View = layoutInflater.inflate(R.layout.google_map_custom_infowindow, null)
        val titleView = view.findViewById<View>(R.id.infowindow_title) as TextView
        val descriptionView = view.findViewById<View>(R.id.infowindow_description) as TextView
        titleView.text = marker.title
        descriptionView.text = marker.description
        return view
    }

    override fun getInfoContents(p0: Marker): View? {
        // 这里返回的view会使用InfoWindow本身默认的frame和background
        return null
    }
})
```

要注意的是：这个实现`GoogleMap.InfoWindowAdapter()`的子类是作用于**整个GoogleMap**的，也就是说它对于**同一个map上的各个marker**都是有作用的，所以如果对于不同的marker需要实现不同的InfoWindow的话，需要在`getInfoWindow()`或`getInfoContents()`里对于marker进行一下判断。

## 调整镜头视角

最简单的方法：可以通过创建一个`LatLngBounds`实例并传入southwest, northeast的`LatLng`坐标来调整视角，代码如下：

```kotlin
val sw = LatLng(39.907935, 116.392183)	// 视图southwest坐标（左下）
val ne = LatLng(39.923541, 116.402244)	// 视图northeast坐标（右上）
val latLngBounds = LatLngBounds(sw, ne)

// 通过CameraUpdateFactory.newLatLngBounds()方法构造显示边界并移动镜头，30为padding
googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 30))
```

如果需要自适应镜头视角并包含所有地图上的marker，可以调用`SphericalUtil.computeOffset()`方法来计算每个marker的显示区域（通过计算东北坐标和西南坐标获得），然后将其通过`LatLngBounds.Builder().include()`方法进行添加，最后调用`Builder().build`来构造一个`LatLngBound`实例即可。示例代码：

```kotlin
val builder = LatLngBounds.Builder()
for (marker in markers) {
    // show marker on map here...
  
    // 计算每个marker的northeast坐标和southwest坐标
    val markerNorthEastBorder = marker.location.let {
        SphericalUtil.computeOffset(it, 500.0, 0.0)	// 正北方向扩展500米
        SphericalUtil.computeOffset(it, 500.0, 90.0)	// 正东方向扩展500米
    }
    val markerSouthWestBorder = marker.location.let {
        SphericalUtil.computeOffset(it, 500.0, 180.0)	// 正南方向扩展500米
        SphericalUtil.computeOffset(it, 500.0, 270.0)	// 正西方向扩展500米
    }
  
    // 添加marker显示区域边界
    builder.include(markerNorthEastBorder)
    builder.include(markerSouthWestBorder)
}

// 构造显示边界并移动镜头
googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 0))
```

## 代码示例
[Github MochaBear - GoogleMapDemoActivity](https://github.com/Yunze-Li/MochaBear/blob/main/app/src/main/java/com/arctos/mochabear/mapdemo/GoogleMapDemoActivity.kt)

## 参考链接
[Google Map SDK for Android官方说明文档](https://developers.google.com/maps/documentation/android-sdk/overview)
