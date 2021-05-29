package me.limeice.lifecycle.rxjava3.internal;

import java.util.Objects;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.exceptions.CompositeException;
import io.reactivex.rxjava3.exceptions.Exceptions;
import io.reactivex.rxjava3.internal.disposables.DisposableHelper;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import me.limeice.lifecycle.rxjava3.Scope;
import me.limeice.lifecycle.rxjava3.SingleLifecycleSource;

final class SingleLifecycleImpl<@NonNull T> implements SingleLifecycleSource<T> {

    private final Single<T> upStream;
    private final Scope scope;

    SingleLifecycleImpl(Single<T> upStream, Scope scope) {
        this.upStream = upStream;
        this.scope = scope;
    }

    @Override
    public void subscribe(@NonNull SingleObserver<? super T> observer) {
        Objects.requireNonNull(observer, "observer is null");

        observer = RxJavaPlugins.onSubscribe(upStream, observer);

        Objects.requireNonNull(
                observer,
                "The RxJavaPlugins.onSubscribe hook returned a null SingleObserver. " +
                        "Please check the handler provided to RxJavaPlugins.setOnSingleSubscribe " +
                        "for invalid null returns. " +
                        "Further reading: https://github.com/ReactiveX/RxJava/wiki/Plugins"
        );

        try {
            subscribeActual(observer);
        } catch (NullPointerException ex) {
            throw ex;
        } catch (Throwable ex) {
            Exceptions.throwIfFatal(ex);
            NullPointerException npe = new NullPointerException("subscribeActual failed");
            npe.initCause(ex);
            throw npe;
        }
    }

    private void subscribeActual(@NonNull SingleObserver<? super T> observer) {
        upStream.onTerminateDetach()
                .subscribe(new LifecycleSingleObserver<>(observer, scope));
    }

    static final class LifecycleSingleObserver<T> extends AbsLifecycleDisposable<Disposable>
            implements SingleObserver<T> {

        private final SingleObserver<? super T> observer;

        LifecycleSingleObserver(SingleObserver<? super T> observer, Scope scope) {
            super(scope);
            this.observer = observer;
        }

        @Override
        public void onError(@NonNull Throwable e) {
            lazySet(DisposableHelper.DISPOSED);
            try {
                exit();
                observer.onError(e);
            } catch (Throwable ex) {
                Exceptions.throwIfFatal(ex);
                RxJavaPlugins.onError(new CompositeException(e, ex));
            }
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
        public void onSuccess(@NonNull T value) {
            lazySet(DisposableHelper.DISPOSED);
            try {
                exit();
                observer.onSuccess(value);
            } catch (Throwable ex) {
                Exceptions.throwIfFatal(ex);
                RxJavaPlugins.onError(ex);
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
