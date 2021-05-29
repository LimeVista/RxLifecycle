package me.limeice.lifecycle.rxjava3.internal;

import java.util.Objects;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.exceptions.CompositeException;
import io.reactivex.rxjava3.exceptions.Exceptions;
import io.reactivex.rxjava3.internal.disposables.DisposableHelper;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import me.limeice.lifecycle.rxjava3.ObservableLifecycleSource;
import me.limeice.lifecycle.rxjava3.Scope;

final class ObservableLifecycleImpl<@NonNull T> implements ObservableLifecycleSource<T> {

    private final Observable<T> upStream;
    private final Scope scope;

    public ObservableLifecycleImpl(@NonNull Observable<T> upStream, @NonNull Scope scope) {
        super();
        this.upStream = upStream;
        this.scope = scope;
    }

    @Override
    public void subscribe(@NonNull Observer<? super T> observer) {
        Objects.requireNonNull(observer, "observer is null");

        try {
            observer = RxJavaPlugins.onSubscribe(upStream, observer);

            Objects.requireNonNull(observer,
                    "The RxJavaPlugins.onSubscribe hook returned a null Observer. " +
                            "Please change the handler provided to RxJavaPlugins." +
                            "setOnObservableSubscribe for invalid null returns. " +
                            "Further reading: https://github.com/ReactiveX/RxJava/wiki/Plugins"
            );
            subscribeActual(observer);
        } catch (NullPointerException e) {
            throw e;
        } catch (Throwable e) {
            Exceptions.throwIfFatal(e);
            // can't call onError because no way to know if a Disposable has been set or not
            // can't call onSubscribe because the call might have set a Subscription already
            RxJavaPlugins.onError(e);

            NullPointerException npe = new NullPointerException("Actually not, but can't throw other exceptions due to RS");
            npe.initCause(e);
            throw npe;
        }
    }

    private void subscribeActual(@NonNull Observer<? super T> observer) {
        upStream.onTerminateDetach()    // 中断上游与下游的引用
                .subscribe(new LifecycleObserver<>(observer, scope));
    }

    static final class LifecycleObserver<T> extends AbsLifecycleDisposable<Disposable>
            implements Observer<T> {

        private final Observer<? super T> observer;

        public LifecycleObserver(Observer<? super T> observer, Scope scope) {
            super(scope);
            this.observer = observer;
        }

        @Override
        public void onSubscribe(@NonNull Disposable d) {
            if (DisposableHelper.setOnce(this, d)) {
                try {
                    enter();
                    observer.onSubscribe(d);
                } catch (Throwable ex) {
                    Exceptions.throwIfFatal(ex);
                    d.dispose();
                    onError(ex);
                }
            }
        }

        @Override
        public void onNext(@NonNull T t) {
            if (!isDisposed()) {
                try {
                    observer.onNext(t);
                } catch (Throwable e) {
                    Exceptions.throwIfFatal(e);
                    get().dispose();
                    onError(e);
                }
            }
        }

        @Override
        public void onError(@NonNull Throwable t) {
            if (!isDisposed()) {
                lazySet(DisposableHelper.DISPOSED);
                try {
                    exit();
                    observer.onError(t);
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
            if (!isDisposed()) {
                lazySet(DisposableHelper.DISPOSED);
                try {
                    exit();
                    observer.onComplete();
                } catch (Throwable e) {
                    Exceptions.throwIfFatal(e);
                    RxJavaPlugins.onError(e);
                }
            }
        }

        @Override
        public void dispose() {
            DisposableHelper.dispose(this);
        }

        @Override
        public boolean isDisposed() {
            return get() == DisposableHelper.DISPOSED;
        }
    }
}


