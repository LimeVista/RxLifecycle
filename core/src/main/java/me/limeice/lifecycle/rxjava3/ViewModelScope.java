package me.limeice.lifecycle.rxjava3;

import androidx.lifecycle.ViewModel;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * 利用 ViewMode 作为作用域
 * <p>
 * 不能是自己构造的 ViewModel，参照{@link androidx.lifecycle.ViewModelProvider}
 *
 * @author LimeVista
 * <a href="https://github.com/LimeVista/RxLifecycle">RxLifecycle</a>
 */
public class ViewModelScope extends ViewModel implements Scope {

    protected final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }

    @Override
    public boolean needRunMainThread() {
        return false;
    }

    @Override
    public void onEnterScope(@NonNull Disposable disposable) {
        disposables.add(disposable);
    }

    @Override
    public void onExitScope(@Nullable Disposable disposable) {
        if (disposable == null) return;

        // 因为是流程结束，所以仅删除
        disposables.delete(disposable);
    }
}
