package me.limeice.lifecycle.rxjava3.internal;

import java.util.Objects;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.MaybeObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.exceptions.CompositeException;
import io.reactivex.rxjava3.exceptions.Exceptions;
import io.reactivex.rxjava3.internal.disposables.DisposableHelper;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import me.limeice.lifecycle.rxjava3.MaybeLifecycleSource;
import me.limeice.lifecycle.rxjava3.Scope;

final class MaybeLifecycleImpl<@NonNull T> implements MaybeLifecycleSource<T> {

    private final Maybe<T> upStream;
    private final Scope scope;

    MaybeLifecycleImpl(Maybe<T> upStream, Scope scope) {
        this.upStream = upStream;
        this.scope = scope;
    }

    @Override
    public void subscribe(@NonNull MaybeObserver<? super T> observer) {
        Objects.requireNonNull(observer, "observer is null");

        observer = RxJavaPlugins.onSubscribe(upStream, observer);

        Objects.requireNonNull(
                observer,
                "The RxJavaPlugins.onSubscribe hook returned a null MaybeObserver. " +
                        "Please check the handler provided to RxJavaPlugins.setOnMaybeSubscribe " +
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

    private void subscribeActual(@NonNull MaybeObserver<? super T> observer) {
        upStream.onTerminateDetach()
                .subscribe(new MaybeCallbackObserver<>(observer, scope));
    }

    final static class MaybeCallbackObserver<T> extends AbsLifecycleDisposable<Disposable>
            implements MaybeObserver<T>, Disposable {

        private final MaybeObserver<? super T> observer;

        MaybeCallbackObserver(MaybeObserver<? super T> observer, Scope scope) {
            super(scope);
            this.observer = observer;
        }

        @Override
        public void dispose() {
            DisposableHelper.dispose(this);
        }

        @Override
        public boolean isDisposed() {
            return DisposableHelper.isDisposed(get());
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
        public void onComplete() {
            lazySet(DisposableHelper.DISPOSED);
            try {
                exit();
                observer.onComplete();
            } catch (Throwable ex) {
                Exceptions.throwIfFatal(ex);
                RxJavaPlugins.onError(ex);
            }
        }
    }
}
