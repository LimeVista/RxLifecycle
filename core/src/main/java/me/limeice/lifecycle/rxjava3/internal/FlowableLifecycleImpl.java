package me.limeice.lifecycle.rxjava3.internal;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Objects;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableSubscriber;
import io.reactivex.rxjava3.exceptions.CompositeException;
import io.reactivex.rxjava3.exceptions.Exceptions;
import io.reactivex.rxjava3.internal.subscribers.StrictSubscriber;
import io.reactivex.rxjava3.internal.subscriptions.SubscriptionHelper;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import me.limeice.lifecycle.rxjava3.FlowableLifecycleSource;
import me.limeice.lifecycle.rxjava3.Scope;

final class FlowableLifecycleImpl<@NonNull T> implements FlowableLifecycleSource<T> {

    private final Flowable<T> upStream;
    private final Scope scope;

    FlowableLifecycleImpl(Flowable<T> upStream, Scope scope) {
        this.upStream = upStream;
        this.scope = scope;
    }

    @Override
    public void subscribe(@NonNull Subscriber<? super T> subscriber) {
        Objects.requireNonNull(subscriber, "subscriber is null");
        if (subscriber instanceof FlowableSubscriber) {
            //noinspection unchecked
            subscribe((FlowableSubscriber<@NonNull ? super T>) subscriber);
        } else {
            subscribe(new StrictSubscriber<>(subscriber));
        }
    }

    @Override
    public void subscribe(@NonNull FlowableSubscriber<? super T> subscriber) {

        Objects.requireNonNull(subscriber, "subscriber is null");
        try {
            Subscriber<@NonNull ? super T> flowableSubscriber =
                    RxJavaPlugins.onSubscribe(upStream, subscriber);

            Objects.requireNonNull(
                    flowableSubscriber,
                    "The RxJavaPlugins.onSubscribe hook returned a null FlowableSubscriber. " +
                            "Please check the handler provided to RxJavaPlugins.setOnFlowableSubscribe " +
                            "for invalid null returns. " +
                            "Further reading: https://github.com/ReactiveX/RxJava/wiki/Plugins"
            );

            subscribeActual(flowableSubscriber);
        } catch (NullPointerException e) {
            throw e;
        } catch (Throwable e) {
            Exceptions.throwIfFatal(e);
            // can't call onError because no way to know if a Subscription has been set or not
            // can't call onSubscribe because the call might have set a Subscription already
            RxJavaPlugins.onError(e);

            NullPointerException npe = new NullPointerException("Actually not, but can't throw other exceptions due to RS");
            npe.initCause(e);
            throw npe;
        }
    }

    private void subscribeActual(Subscriber<? super T> subscriber) {
        upStream.onTerminateDetach()    // 中断上游与下游的引用
                .subscribe(new LifecycleSubscriber<>(subscriber, scope));
    }

    static final class LifecycleSubscriber<T> extends AbsLifecycleDisposable<Subscription>
            implements FlowableSubscriber<T>, Subscription {

        private final Subscriber<? super T> subscriber;

        LifecycleSubscriber(Subscriber<? super T> subscriber, Scope scope) {
            super(scope);
            this.subscriber = subscriber;
        }

        @Override
        public void onSubscribe(@NonNull Subscription s) {
            if (SubscriptionHelper.setOnce(this, s)) {
                try {
                    enter();
                    subscriber.onSubscribe(s);
                } catch (Throwable ex) {
                    Exceptions.throwIfFatal(ex);
                    s.cancel();
                    onError(ex);
                }
            }
        }

        @Override
        public void onNext(T t) {
            if (!isDisposed()) {
                try {
                    subscriber.onNext(t);
                } catch (Throwable e) {
                    Exceptions.throwIfFatal(e);
                    get().cancel();
                    onError(e);
                }
            }
        }

        @Override
        public void onError(Throwable t) {
            if (get() != SubscriptionHelper.CANCELLED) {
                lazySet(SubscriptionHelper.CANCELLED);
                try {
                    exit();
                    subscriber.onError(t);
                } catch (Throwable e) {
                    Exceptions.throwIfFatal(e);
                    RxJavaPlugins.onError(new CompositeException(t, e));
                }
            } else {
                RxJavaPlugins.onError(t);
            }
        }

        @Override
        public void onComplete() {
            if (get() != SubscriptionHelper.CANCELLED) {
                lazySet(SubscriptionHelper.CANCELLED);
                try {
                    exit();
                    subscriber.onComplete();
                } catch (Throwable e) {
                    Exceptions.throwIfFatal(e);
                    RxJavaPlugins.onError(e);
                }
            }
        }

        @Override
        public void dispose() {
            cancel();
        }

        @Override
        public boolean isDisposed() {
            return get() == SubscriptionHelper.CANCELLED;
        }

        @Override
        public void request(long n) {
            get().request(n);
        }

        @Override
        public void cancel() {
            SubscriptionHelper.cancel(this);
        }
    }
}
