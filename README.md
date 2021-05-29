# RxLifecycle[![](https://jitpack.io/v/LimeVista/RxLifecycle.svg)](https://jitpack.io/#LimeVista/RxLifecycle)

一个用于 Android RxJava 生命周期管理的轻量级库。以正确的方式解决 RxJava 使用过程中的内存泄漏。自动化管理 Disposable。

## 引入

```groovy
allprojects {
    repositories {
        // ...
        maven { url "https://jitpack.io" }
    }
}

// ...

dependencies {
    // core
    implementation 'com.github.LimeVista.RxLifecycle:core:0.0.9'

    // ktx
    implementation 'com.github.LimeVista.RxLifecycle:ktx:0.0.9'
}
```

## 使用

使用非常简单，如下所示。

### Java

```java
// view
Observable.just(1)
    .to(RxLifecycle.with(view))
    .subscribe(r->{ /* TODO */ });

// Activity, Fragment, LifecycleOwner
Observable.just(1)
    .to(RxLifecycle.with(your))
    .subscribe(r->{ /* TODO */ });

// custom scope
Observable.just(1)
    .to(RxLifecycle.with(scope))
    .subscribe(r->{ /* TODO */ });
```

### Kotlin

```kotlin
// view
Observable.just(1)
    .withContext(view)
    .subscribe{/* TODO */ }

// Activity, Fragment, LifecycleOwner
Observable.just(1)
    .withContext(your)
    .subscribe{ /* TODO */ }

// custom scope
Observable.just(1)
    .withContext(scope)
    .subscribe{ /* TODO */ }
```

## 题外话

本库的设计部分借鉴于 [trello/RxLifecycle](https://github.com/trello/RxLifecycle)
