package me.limeice.lifecycle.rxjava3.internal;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import java.lang.ref.WeakReference;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;
import io.reactivex.rxjava3.disposables.Disposable;
import me.limeice.lifecycle.rxjava3.RxLifecycleException;
import me.limeice.lifecycle.rxjava3.Scope;

final class LifecycleScope implements Scope, LifecycleEventObserver {

    private final WeakReference<Lifecycle> reference;
    private final Lifecycle.Event event;
    private volatile Disposable disposable;

    private LifecycleScope(Lifecycle lifecycle, Lifecycle.Event event) {
        this.reference = new WeakReference<>(lifecycle);
        this.event = event;
    }

    public static LifecycleScope get(@NonNull Lifecycle lifecycle, Lifecycle.Event event) {
        return new LifecycleScope(lifecycle, event);
    }

    @Override
    public boolean needRunMainThread() {
        return true;
    }

    @Override
    public void onEnterScope(@NonNull Disposable disposable) {
        this.disposable = disposable;
        final Lifecycle lifecycle = reference.get();
        if (lifecycle == null) throw new RxLifecycleException("Lifecycle is recycled!");
        lifecycle.addObserver(this);
    }

    @Override
    public void onExitScope(@Nullable Disposable disposable) {
        final Lifecycle lifecycle = reference.get();
        // 这是不可能的，除非非法调用
        if (lifecycle == null) throw new RxLifecycleException("Lifecycle is recycled!");
        lifecycle.removeObserver(this);
    }

    @Override
    public void onStateChanged(
            @androidx.annotation.NonNull LifecycleOwner source,
            @androidx.annotation.NonNull Lifecycle.Event event
    ) {
        if (event == this.event) {
            if (disposable != null) {
                disposable.dispose();
                disposable = null;
            }
            source.getLifecycle().removeObserver(this);
        }
    }
}
