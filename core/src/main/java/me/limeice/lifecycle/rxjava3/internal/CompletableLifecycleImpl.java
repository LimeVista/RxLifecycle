package me.limeice.lifecycle.rxjava3.internal;

import java.util.Objects;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.exceptions.CompositeException;
import io.reactivex.rxjava3.exceptions.Exceptions;
import io.reactivex.rxjava3.internal.disposables.DisposableHelper;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import me.limeice.lifecycle.rxjava3.CompletableLifecycleSource;
import me.limeice.lifecycle.rxjava3.Scope;

final class CompletableLifecycleImpl implements CompletableLifecycleSource {

    private final Completable upStream;
    private final Scope scope;

    CompletableLifecycleImpl(Completable upStream, Scope scope) {
        this.upStream = upStream;
        this.scope = scope;
    }

    @Override
    public void subscribe(@NonNull CompletableObserver observer) {
        Objects.requireNonNull(observer, "observer is null");
        try {

            observer = RxJavaPlugins.onSubscribe(upStream, observer);

            Objects.requireNonNull(
                    observer,
                    "The RxJavaPlugins.onSubscribe hook returned a null CompletableObserver. " +
                            "Please check the handler provided to " +
                            "RxJavaPlugins.setOnCompletableSubscribe for invalid null returns. " +
                            "Further reading: https://github.com/ReactiveX/RxJava/wiki/Plugins"
            );

            subscribeActual(observer);
        } catch (NullPointerException ex) {
            throw ex;
        } catch (Throwable ex) {
            Exceptions.throwIfFatal(ex);
            RxJavaPlugins.onError(ex);
            throw toNpe(ex);
        }
    }

    private void subscribeActual(@NonNull CompletableObserver observer) {
        upStream.onTerminateDetach()
                .subscribe(new LifeCompletableObserver(observer, scope));
    }

    private static NullPointerException toNpe(Throwable ex) {
        NullPointerException npe = new NullPointerException("Actually not, but can't pass out an exception otherwise...");
        npe.initCause(ex);
        return npe;
    }

    static final class LifeCompletableObserver extends AbsLifecycleDisposable<Disposable>
            implements CompletableObserver {

        private final CompletableObserver observer;

        LifeCompletableObserver(CompletableObserver observer, Scope scope) {
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
