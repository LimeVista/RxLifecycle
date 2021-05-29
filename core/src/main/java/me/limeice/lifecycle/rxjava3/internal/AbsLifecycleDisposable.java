package me.limeice.lifecycle.rxjava3.internal;

import android.os.Looper;

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import me.limeice.lifecycle.rxjava3.Scope;

abstract class AbsLifecycleDisposable<T> extends AtomicReference<T> implements Disposable {

    protected final Scope scope;
    private volatile boolean isEnter = false;

    AbsLifecycleDisposable(Scope scope) {
        super();
        this.scope = scope;
    }

    protected void enter() {
        if (scope.needRunMainThread() && Looper.getMainLooper() != Looper.myLooper()) {
            postEnter();

            return;
        }
        enterImmediately();
    }

    protected void exit() {
        if (scope.needRunMainThread() && Looper.getMainLooper() != Looper.myLooper()) {
            AndroidSchedulers.mainThread().scheduleDirect(this::exitImmediately);
            return;
        }
        exitImmediately();
    }

    private void enterImmediately() {
        scope.onEnterScope(this);
    }

    private void exitImmediately() {
        scope.onExitScope(this);
    }

    private void postEnter() {
        // 等待主线完成领域进入
        AndroidSchedulers.mainThread().scheduleDirect(() -> {
            enterImmediately();
            synchronized (AbsLifecycleDisposable.this) {
                isEnter = true;
                AbsLifecycleDisposable.this.notifyAll();
            }
        });

        synchronized (this) {
            while (!isEnter) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
