package me.limeice.lifecycle.rxjava3.internal;

import java.util.Objects;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import me.limeice.lifecycle.rxjava3.CompletableLifecycleSource;
import me.limeice.lifecycle.rxjava3.FlowableLifecycleSource;
import me.limeice.lifecycle.rxjava3.FusionLifecycleConverter;
import me.limeice.lifecycle.rxjava3.MaybeLifecycleSource;
import me.limeice.lifecycle.rxjava3.ObservableLifecycleSource;
import me.limeice.lifecycle.rxjava3.Scope;
import me.limeice.lifecycle.rxjava3.SingleLifecycleSource;

final class FusionLifecycleConverterImpl<@NonNull T> implements FusionLifecycleConverter<T> {

    private final Scope scope;

    FusionLifecycleConverterImpl(@NonNull Scope scope) {
        this.scope = Objects.requireNonNull(scope);
    }

    @NonNull
    @Override
    public final CompletableLifecycleSource apply(@NonNull Completable upstream) {
        return new CompletableLifecycleImpl(upstream, scope);
    }

    @NonNull
    @Override
    public final FlowableLifecycleSource<T> apply(@NonNull Flowable<T> upstream) {
        return new FlowableLifecycleImpl<>(upstream, scope);
    }

    @NonNull
    @Override
    public final ObservableLifecycleSource<T> apply(@NonNull Observable<T> upstream) {
        return new ObservableLifecycleImpl<>(upstream, scope);
    }

    @NonNull
    @Override
    public final SingleLifecycleSource<T> apply(@NonNull Single<T> upstream) {
        return new SingleLifecycleImpl<>(upstream, scope);
    }

    @Override
    public @NonNull MaybeLifecycleSource<T> apply(@NonNull Maybe<T> upstream) {
        return new MaybeLifecycleImpl<>(upstream, scope);
    }
}
