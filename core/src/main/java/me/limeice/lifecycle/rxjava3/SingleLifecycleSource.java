package me.limeice.lifecycle.rxjava3;

import java.util.Objects;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.SchedulerSupport;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.core.SingleSource;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.BiConsumer;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.internal.functions.Functions;
import io.reactivex.rxjava3.internal.observers.BiConsumerSingleObserver;
import io.reactivex.rxjava3.internal.observers.ConsumerSingleObserver;

/**
 * 用于单一观察者的生命周期管理
 *
 * @param <T> 数据类型
 * @author LimeVista
 * <a href="https://github.com/LimeVista/RxLifecycle">RxLifecycle</a>
 */
public interface SingleLifecycleSource<@NonNull T> extends SingleSource<T> {

    /**
     * 将给定的 {@link BiConsumer} 包裹成观察者 {@link SingleObserver<T>}，
     * 订阅到被观察者 {@link SingleLifecycleSource<T>}。
     *
     * @param onCallback {@link BiConsumer} 回调
     * @return {@link Disposable} 资源回收句柄，默认情况下根据生命周期自行管理
     * @see io.reactivex.rxjava3.core.Observable#subscribe(Consumer)
     */
    @NonNull
    @SchedulerSupport(SchedulerSupport.NONE)
    default Disposable subscribe(
            @NonNull BiConsumer<? super T, ? super Throwable> onCallback
    ) {
        Objects.requireNonNull(onCallback, "onCallback is null");

        BiConsumerSingleObserver<T> observer = new BiConsumerSingleObserver<>(onCallback);
        subscribe(observer);
        return observer;
    }

    /**
     * 将给定的 {@link Consumer<T>} 包裹成观察者 {@link SingleObserver<T>}，
     * 订阅到被观察者 {@link SingleLifecycleSource<T>}。
     *
     * @param onSuccess {@link Consumer<T>} 事件回调
     * @return {@link Disposable} 资源回收句柄，默认情况下根据生命周期自行管理
     * @see io.reactivex.rxjava3.core.Observable#subscribe(Consumer)
     */
    @NonNull
    @SchedulerSupport(SchedulerSupport.NONE)
    default Disposable subscribe(@NonNull Consumer<? super T> onSuccess) {
        return subscribe(onSuccess, Functions.ON_ERROR_MISSING);
    }

    /**
     * 将给定的 {@link Consumer<T>}，{@link Consumer<Throwable>}包裹成观察者 {@link SingleObserver<T>}，
     * 订阅到被观察者 {@link SingleLifecycleSource<T>}。
     *
     * @param onSuccess {@link Consumer<T>} 事件回调
     * @param onError   {@link Consumer<Throwable>} 异常回调
     * @return {@link Disposable} 资源回收句柄，默认情况下根据生命周期自行管理
     * @see io.reactivex.rxjava3.core.Observable#subscribe(Consumer, Consumer)
     */
    @NonNull
    @SchedulerSupport(SchedulerSupport.NONE)
    default Disposable subscribe(
            @NonNull Consumer<? super T> onSuccess,
            @NonNull Consumer<? super Throwable> onError
    ) {
        Objects.requireNonNull(onSuccess, "onSuccess is null");
        Objects.requireNonNull(onError, "onError is null");

        ConsumerSingleObserver<T> ls = new ConsumerSingleObserver<>(onSuccess, onError);
        subscribe(ls);
        return ls;
    }
}
