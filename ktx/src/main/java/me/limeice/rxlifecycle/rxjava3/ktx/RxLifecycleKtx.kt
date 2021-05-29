@file:JvmName("RxLifecycleKtx")

package me.limeice.rxlifecycle.rxjava3.ktx

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import io.reactivex.rxjava3.core.*
import me.limeice.lifecycle.rxjava3.*

/**
 * @see RxLifecycle.with
 */
fun <T> Observable<T>.withContext(
    view: View,
    ignoreAttached: Boolean = false
): ObservableLifecycleSource<T> {
    return this.to(RxLifecycle.with(view, ignoreAttached))
}

/**
 * @see RxLifecycle.with
 */
fun <T> Observable<T>.withContext(
    owner: LifecycleOwner,
    event: Lifecycle.Event = Lifecycle.Event.ON_DESTROY
): ObservableLifecycleSource<T> {
    return this.to(RxLifecycle.with(owner, event))
}

/**
 * @see RxLifecycle.with
 */
fun <T> Observable<T>.withContext(scope: Scope): ObservableLifecycleSource<T> {
    return this.to(RxLifecycle.with(scope))
}

//////////////////////////////////////////////////////////////////////

/**
 * @see RxLifecycle.with
 */
fun <T> Single<T>.withContext(
    view: View,
    ignoreAttached: Boolean = false
): SingleLifecycleSource<T> {
    return this.to(RxLifecycle.with(view, ignoreAttached))
}

/**
 * @see RxLifecycle.with
 */
fun <T> Single<T>.withContext(
    owner: LifecycleOwner,
    event: Lifecycle.Event = Lifecycle.Event.ON_DESTROY
): SingleLifecycleSource<T> {
    return this.to(RxLifecycle.with(owner, event))
}

/**
 * @see RxLifecycle.with
 */
fun <T> Single<T>.withContext(scope: Scope): SingleLifecycleSource<T> {
    return this.to(RxLifecycle.with(scope))
}

//////////////////////////////////////////////////////////////////////

/**
 * @see RxLifecycle.with
 */
fun <T> Maybe<T>.withContext(
    view: View,
    ignoreAttached: Boolean = false
): MaybeLifecycleSource<T> {
    return this.to(RxLifecycle.with(view, ignoreAttached))
}

/**
 * @see RxLifecycle.with
 */
fun <T> Maybe<T>.withContext(
    owner: LifecycleOwner,
    event: Lifecycle.Event = Lifecycle.Event.ON_DESTROY
): MaybeLifecycleSource<T> {
    return this.to(RxLifecycle.with(owner, event))
}

/**
 * @see RxLifecycle.with
 */
fun <T> Maybe<T>.withContext(scope: Scope): MaybeLifecycleSource<T> {
    return this.to(RxLifecycle.with(scope))
}

//////////////////////////////////////////////////////////////////////

/**
 * @see RxLifecycle.with
 */
fun Completable.withContext(
    view: View,
    ignoreAttached: Boolean = false
): CompletableLifecycleSource {
    return this.to(RxLifecycle.with<Any>(view, ignoreAttached))
}

/**
 * @see RxLifecycle.with
 */
fun Completable.withContext(
    owner: LifecycleOwner,
    event: Lifecycle.Event = Lifecycle.Event.ON_DESTROY
): CompletableLifecycleSource {
    return this.to(RxLifecycle.with<Any>(owner, event))
}

/**
 * @see RxLifecycle.with
 */
fun Completable.withContext(scope: Scope): CompletableLifecycleSource {
    return this.to(RxLifecycle.with<Any>(scope))
}

//////////////////////////////////////////////////////////////////////

/**
 * @see RxLifecycle.with
 */
fun <T> Flowable<T>.withContext(
    view: View,
    ignoreAttached: Boolean = false
): FlowableLifecycleSource<T> {
    return this.to(RxLifecycle.with(view, ignoreAttached))
}

/**
 * @see RxLifecycle.with
 */
fun <T> Flowable<T>.withContext(
    owner: LifecycleOwner,
    event: Lifecycle.Event = Lifecycle.Event.ON_DESTROY
): FlowableLifecycleSource<T> {
    return this.to(RxLifecycle.with(owner, event))
}

/**
 * @see RxLifecycle.with
 */
fun <T> Flowable<T>.withContext(scope: Scope): FlowableLifecycleSource<T> {
    return this.to(RxLifecycle.with(scope))
}