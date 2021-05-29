package me.limeice.lifecycle.rxjava3;

import java.util.Objects;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.SchedulerSupport;
import io.reactivex.rxjava3.core.MaybeObserver;
import io.reactivex.rxjava3.core.MaybeSource;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.internal.functions.Functions;
import io.reactivex.rxjava3.internal.operators.maybe.MaybeCallbackObserver;

/**
 * 用于观察者的生命周期管理
 *
 * @param <T> 数据类型
 * @author LimeVista
 * <a href="https://github.com/LimeVista/RxLifecycle">RxLifecycle</a>
 */
public interface MaybeLifecycleSource<@NonNull T> extends MaybeSource<T> {

    /**
     * 将给定的 {@link Consumer <T>} 包裹成观察者 {@link MaybeObserver<T>}，
     * 订阅到被观察者 {@link ObservableLifecycleSource<T>}。
     *
     * @param onSuccess {@link Consumer<T>} 事件成功回调
     * @return {@link Disposable} 资源回收句柄，默认情况下根据生命周期自行管理
     * @see io.reactivex.rxjava3.core.Maybe#subscribe(Consumer)
     */
    @NonNull
    @SchedulerSupport(SchedulerSupport.NONE)
    default Disposable subscribe(@NonNull Consumer<? super T> onSuccess) {
        return subscribe(onSuccess, Functions.ON_ERROR_MISSING, Functions.EMPTY_ACTION);
    }

    /**
     * 将给定的 {@link Consumer<T>}，{@link Consumer<Throwable>}包裹成观察者 {@link MaybeObserver<T>}，
     * 订阅到被观察者 {@link ObservableLifecycleSource<T>}。
     *
     * @param onSuccess {@link Consumer<T>} 事件成功回调
     * @param onError   {@link Consumer<Throwable>} 异常回调
     * @return {@link Disposable} 资源回收句柄，默认情况下根据生命周期自行管理
     * @see io.reactivex.rxjava3.core.Maybe#subscribe(Consumer, Consumer)
     */
    @NonNull
    @SchedulerSupport(SchedulerSupport.NONE)
    default Disposable subscribe(
            @NonNull Consumer<? super T> onSuccess,
            @NonNull Consumer<? super Throwable> onError
    ) {
        return subscribe(onSuccess, onError, Functions.EMPTY_ACTION);
    }

    /**
     * 将给定的 {@link Consumer<T>}，{@link Consumer<Throwable>}，{@link Action}
     * 包裹成观察者 {@link MaybeObserver<T>}，订阅到被观察者 {@link ObservableLifecycleSource<T>}。
     *
     * @param onSuccess  {@link Consumer<T>} 成功回调
     * @param onError    {@link Consumer<Throwable>} 异常回调
     * @param onComplete {@link Action} 总事件完成回调
     * @return {@link Disposable} 资源回收句柄，默认情况下根据生命周期自行管理
     * @see io.reactivex.rxjava3.core.Maybe#subscribe(Consumer, Consumer, Action)
     */
    @NonNull
    @SchedulerSupport(SchedulerSupport.NONE)
    default Disposable subscribe(
            @NonNull Consumer<? super T> onSuccess,
            @NonNull Consumer<? super Throwable> onError,
            @NonNull Action onComplete
    ) {
        Objects.requireNonNull(onSuccess, "onSuccess is null");
        Objects.requireNonNull(onError, "onError is null");
        Objects.requireNonNull(onComplete, "onComplete is null");
        MaybeCallbackObserver<T> ob = new MaybeCallbackObserver<>(onSuccess, onError, onComplete);
        subscribe(ob);
        return ob;
    }
}
