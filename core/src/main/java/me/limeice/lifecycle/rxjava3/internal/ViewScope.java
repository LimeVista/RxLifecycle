package me.limeice.lifecycle.rxjava3.internal;

import android.view.View;

import java.lang.ref.WeakReference;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;
import io.reactivex.rxjava3.disposables.Disposable;
import me.limeice.lifecycle.rxjava3.RxLifecycleException;
import me.limeice.lifecycle.rxjava3.Scope;

final class ViewScope implements Scope, View.OnAttachStateChangeListener {

    private final WeakReference<View> reference;
    private final boolean ignoreAttached;
    private volatile Disposable disposable;

    private ViewScope(View view, boolean ignoreAttached) {
        this.reference = new WeakReference<>(view);
        this.ignoreAttached = ignoreAttached;
    }

    public static ViewScope get(@NonNull View view, boolean ignoreAttached) {
        return new ViewScope(view, ignoreAttached);
    }

    @Override
    public boolean needRunMainThread() {
        return true;
    }

    @Override
    public void onEnterScope(@NonNull Disposable disposable) {
        this.disposable = disposable;
        final View view = reference.get();
        if (view == null) throw new NullPointerException("View is recycled!");
        if (!view.isAttachedToWindow() && !ignoreAttached) {
            throw new RxLifecycleException("Scope Error: View is not attached to window!");
        }
        view.addOnAttachStateChangeListener(this);
    }

    @Override
    public void onExitScope(@Nullable Disposable disposable) {
        final View view = reference.get();
        if (view == null) return;
        view.removeOnAttachStateChangeListener(this);
    }

    @Override
    public void onViewAttachedToWindow(View v) {
        // 不需要执行
    }

    @Override
    public void onViewDetachedFromWindow(View v) {
        final View view = reference.get();
        if (view != null) {
            view.removeOnAttachStateChangeListener(this);
        }
        if (disposable == null) return;
        disposable.dispose();
        disposable = null;
    }
}
