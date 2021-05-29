package me.limeice.lifecycle.rxjava3;


import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.Objects;

import io.reactivex.rxjava3.annotations.BackpressureKind;
import io.reactivex.rxjava3.annotations.BackpressureSupport;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.SchedulerSupport;
import io.reactivex.rxjava3.core.FlowableSubscriber;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.internal.functions.Functions;
import io.reactivex.rxjava3.internal.operators.flowable.FlowableInternalHelper;
import io.reactivex.rxjava3.internal.subscribers.LambdaSubscriber;

/**
 * 用于背压模式下的观察者的生命周期管理
 *
 * @param <T> 数据类型
 * @author LimeVista
 * <a href="https://github.com/LimeVista/RxLifecycle">RxLifecycle</a>
 */
public interface FlowableLifecycleSource<@NonNull T> extends Publisher<T> {

    /**
     * 将给定的 {@link Consumer<T>} 包裹成观察者 {@link Subscriber<T>}，
     * 订阅到被观察者 {@link FlowableLifecycleSource <T>}。
     *
     * @param onNext {@link Consumer<T>} 事件回调
     * @return {@link Disposable} 资源回收句柄，默认情况下根据生命周期自行管理
     * @see io.reactivex.rxjava3.core.Flowable#subscribe(Consumer)
     */
    @NonNull
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport(SchedulerSupport.NONE)
    default Disposable subscribe(@NonNull Consumer<? super T> onNext) {
        return subscribe(onNext, Functions.ON_ERROR_MISSING, Functions.EMPTY_ACTION);
    }

    /**
     * 将给定的 {@link Consumer<T>}，{@link Consumer<Throwable>}包裹成观察者 {@link Subscriber<T>}，
     * 订阅到被观察者 {@link FlowableLifecycleSource <T>}。
     *
     * @param onNext  {@link Consumer<T>} 事件回调
     * @param onError {@link Consumer<Throwable>} 异常回调
     * @return {@link Disposable} 资源回收句柄，默认情况下根据生命周期自行管理
     * @see io.reactivex.rxjava3.core.Flowable#subscribe(Consumer, Consumer)
     */
    @NonNull
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport(SchedulerSupport.NONE)
    default Disposable subscribe(
            @NonNull Consumer<? super T> onNext,
            @NonNull Consumer<? super Throwable> onError
    ) {
        return subscribe(onNext, onError, Functions.EMPTY_ACTION);
    }

    /**
     * 将给定的 {@link Consumer<T>}，{@link Consumer<Throwable>}，{@link Action}
     * 包裹成观察者 {@link Subscriber<T>}，订阅到被观察者 {@link FlowableLifecycleSource <T>}。
     *
     * @param onNext     {@link Consumer<T>} 事件回调
     * @param onError    {@link Consumer<Throwable>} 异常回调
     * @param onComplete {@link Action} 总事件完成回调
     * @return {@link Disposable} 资源回收句柄，默认情况下根据生命周期自行管理
     * @see io.reactivex.rxjava3.core.Flowable#subscribe(Consumer, Consumer, Action)
     */
    @NonNull
    @BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
    @SchedulerSupport(SchedulerSupport.NONE)
    default Disposable subscribe(
            @NonNull Consumer<? super T> onNext,
            @NonNull Consumer<? super Throwable> onError,
            @NonNull Action onComplete
    ) {
        Objects.requireNonNull(onNext, "onNext is null");
        Objects.requireNonNull(onError, "onError is null");
        Objects.requireNonNull(onComplete, "onComplete is null");

        LambdaSubscriber<T> ls = new LambdaSubscriber<>(
                onNext,
                onError,
                onComplete,
                FlowableInternalHelper.RequestMax.INSTANCE
        );

        subscribe(ls);
        return ls;
    }

    /**
     * 将给定的观察者 {@link FlowableSubscriber<T>} 订阅到被观察者 {@link FlowableLifecycleSource <T>}。
     *
     * @param subscriber {@link Consumer<T>} 订阅者，观察者
     * @see io.reactivex.rxjava3.core.Flowable#subscribe(FlowableSubscriber)
     */
    void subscribe(@NonNull FlowableSubscriber<@NonNull ? super T> subscriber);
}
