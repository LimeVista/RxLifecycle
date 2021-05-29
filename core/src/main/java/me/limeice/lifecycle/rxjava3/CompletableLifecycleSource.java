package me.limeice.lifecycle.rxjava3;

import java.util.Objects;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.SchedulerSupport;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.CompletableSource;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.BiConsumer;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.internal.observers.CallbackCompletableObserver;

/**
 * 用于送达型（完成型）观察者的生命周期管理
 *
 * @author LimeVista
 * <a href="https://github.com/LimeVista/RxLifecycle">RxLifecycle</a>
 */
public interface CompletableLifecycleSource extends CompletableSource {

    /**
     * 将给定的 {@link Action}、{@link Consumer<Throwable>} 包裹成观察者 {@link CompletableObserver}，
     * 订阅到被观察者 {@link CompletableLifecycleSource <T>}。
     *
     * @param onComplete {@link BiConsumer} 事件完成回调
     * @param onError    {@link Consumer<Throwable>} 事件错误回调
     * @return {@link Disposable} 资源回收句柄，默认情况下根据生命周期自行管理
     * @see io.reactivex.rxjava3.core.Observable#subscribe(Consumer)
     */
    @NonNull
    @SchedulerSupport(SchedulerSupport.NONE)
    default Disposable subscribe(
            @NonNull Action onComplete,
            @NonNull Consumer<? super Throwable> onError
    ) {
        Objects.requireNonNull(onError, "onError is null");
        Objects.requireNonNull(onComplete, "onComplete is null");

        CallbackCompletableObserver observer = new CallbackCompletableObserver(onError, onComplete);
        subscribe(observer);
        return observer;
    }

    /**
     * 将给定的 {@link Action} 包裹成观察者 {@link CompletableObserver}，
     * 订阅到被观察者 {@link CompletableLifecycleSource <T>}。
     *
     * @param onComplete {@link Action} 事件完成回调
     * @return {@link Disposable} 资源回收句柄，默认情况下根据生命周期自行管理
     * @see io.reactivex.rxjava3.core.Observable#subscribe(Consumer)
     */
    @NonNull
    @SchedulerSupport(SchedulerSupport.NONE)
    default Disposable subscribe(@NonNull Action onComplete) {
        Objects.requireNonNull(onComplete, "onComplete is null");

        CallbackCompletableObserver observer = new CallbackCompletableObserver(onComplete);
        subscribe(observer);
        return observer;
    }
}
