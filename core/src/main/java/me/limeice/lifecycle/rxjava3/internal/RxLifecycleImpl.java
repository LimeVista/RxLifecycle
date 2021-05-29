package me.limeice.lifecycle.rxjava3.internal;

import android.view.View;

import androidx.lifecycle.Lifecycle;

import io.reactivex.rxjava3.annotations.NonNull;
import me.limeice.lifecycle.rxjava3.FusionLifecycleConverter;
import me.limeice.lifecycle.rxjava3.Scope;

public final class RxLifecycleImpl {

    private RxLifecycleImpl() {
        throw new AssertionError();
    }

    public static <T> FusionLifecycleConverter<T> with(
            @NonNull View view,
            boolean ignoreAttached
    ) {
        Scope scope = ViewScope.get(view, ignoreAttached);
        return new FusionLifecycleConverterImpl<>(scope);
    }

    public static <T> FusionLifecycleConverter<T> with(
            @NonNull Lifecycle lifecycle,
            Lifecycle.Event event
    ) {
        Scope scope = LifecycleScope.get(lifecycle, event);
        return new FusionLifecycleConverterImpl<>(scope);
    }

    public static <T> FusionLifecycleConverter<T> with(@NonNull Scope scope) {
        return new FusionLifecycleConverterImpl<>(scope);
    }
}
