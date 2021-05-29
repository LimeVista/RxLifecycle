package me.limeice.lifecycle.rxjava3;

import android.view.View;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import java.util.Objects;

import io.reactivex.rxjava3.annotations.NonNull;
import me.limeice.lifecycle.rxjava3.internal.RxLifecycleImpl;

/**
 * 生命周期绑定
 *
 * @author LimeVista
 * <a href="https://github.com/LimeVista/RxLifecycle">RxLifecycle</a>
 */
public final class RxLifecycle {

    private RxLifecycle() {
        throw new AssertionError();
    }

    /**
     * 同视图绑定生命周期
     *
     * @param view {@link View} 视图
     * @param <T>  数据类型
     * @return 绑定转换器
     */
    @NonNull
    public static <T> FusionLifecycleConverter<T> with(@NonNull View view) {
        Objects.requireNonNull(view);
        return RxLifecycleImpl.with(view, false);
    }

    /**
     * 同视图绑定生命周期
     *
     * @param view           {@link View} 视图
     * @param ignoreAttached 忽略视图尚未装载到窗口 {@link View#isAttachedToWindow()}
     * @param <T>            数据类型
     * @return 绑定转换器
     */
    @NonNull
    public static <T> FusionLifecycleConverter<T> with(@NonNull View view, boolean ignoreAttached) {
        Objects.requireNonNull(view);
        return RxLifecycleImpl.with(view, ignoreAttached);
    }

    /**
     * 同 LifecycleOwner 绑定生命周期，默认在销毁是释放
     *
     * @param owner {@link LifecycleOwner}
     *              {@link androidx.appcompat.app.AppCompatActivity}
     *              {@link androidx.fragment.app.FragmentActivity}
     *              {@link androidx.activity.ComponentActivity}
     *              {@link androidx.fragment.app.Fragment}
     * @param <T>   数据类型
     * @return 绑定转换器
     */
    @NonNull
    public static <T> FusionLifecycleConverter<T> with(@NonNull LifecycleOwner owner) {
        Objects.requireNonNull(owner);
        return RxLifecycleImpl.with(owner.getLifecycle(), Lifecycle.Event.ON_DESTROY);
    }

    /**
     * 同 LifecycleOwner 绑定生命周期
     *
     * @param owner {@link LifecycleOwner}
     *              {@link androidx.appcompat.app.AppCompatActivity}
     *              {@link androidx.fragment.app.FragmentActivity}
     *              {@link androidx.activity.ComponentActivity}
     *              {@link androidx.fragment.app.Fragment}
     * @param event {@link androidx.lifecycle.Lifecycle.Event}
     * @param <T>   数据类型
     * @return 绑定转换器
     */
    @NonNull
    public static <T> FusionLifecycleConverter<T> with(
            @NonNull LifecycleOwner owner,
            Lifecycle.Event event
    ) {
        Objects.requireNonNull(owner);
        return RxLifecycleImpl.with(owner.getLifecycle(), event);
    }

    /**
     * 传入自定义 Scope 绑定生命周期。
     * 可用内置类 {@link ViewModelScope}, {@link AndroidViewModelScope}。
     *
     * @param scope {@link Scope} 作用域
     * @param <T>   数据类型
     * @return 绑定转换器
     */
    @NonNull
    public static <T> FusionLifecycleConverter<T> with(@NonNull Scope scope) {
        Objects.requireNonNull(scope);
        return RxLifecycleImpl.with(scope);
    }
}
