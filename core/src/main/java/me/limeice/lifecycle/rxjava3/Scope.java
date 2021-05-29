package me.limeice.lifecycle.rxjava3;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * 领域、作用域，生命周期传达协议
 *
 * @author LimeVista
 * <a href="https://github.com/LimeVista/RxLifecycle">RxLifecycle</a>
 */
public interface Scope {

    /**
     * 约定 {@link #onEnterScope(Disposable)}，{@link #onExitScope(Disposable)} 是否必须在主线程运行。
     *
     * @return 是否需要在主线程运行
     */
    boolean needRunMainThread();

    /**
     * 进入领域
     *
     * @param disposable {@link Disposable} 资源回收
     */
    void onEnterScope(@NonNull Disposable disposable);

    /**
     * 离开领域
     *
     * @param disposable {@link Disposable} 资源回收
     */
    void onExitScope(@Nullable Disposable disposable);
}
