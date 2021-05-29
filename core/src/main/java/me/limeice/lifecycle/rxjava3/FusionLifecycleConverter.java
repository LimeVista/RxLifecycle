package me.limeice.lifecycle.rxjava3;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.CompletableConverter;
import io.reactivex.rxjava3.core.FlowableConverter;
import io.reactivex.rxjava3.core.MaybeConverter;
import io.reactivex.rxjava3.core.ObservableConverter;
import io.reactivex.rxjava3.core.SingleConverter;

/**
 * 多用型转换器
 *
 * @param <T> 传入数据类型
 * @author LimeVista
 * <a href="https://github.com/LimeVista/RxLifecycle">RxLifecycle</a>
 */
public interface FusionLifecycleConverter<@NonNull T> extends
        /* 普通模式 */
        ObservableConverter<T, ObservableLifecycleSource<T>>,

        /* 背压模式 */
        FlowableConverter<T, FlowableLifecycleSource<T>>,

        /* 并行背压模式，尚未实现 ParallelFlowable */
        // ParallelFlowableConverter<T, ParallelFlowableLifecycleSource<T>>,

        /* 单一模式 */
        SingleConverter<T, SingleLifecycleSource<T>>,

        /* 完成模式、送达模式 */
        CompletableConverter<CompletableLifecycleSource>,

        /* 可能也许大概差不多 */
        MaybeConverter<T, MaybeLifecycleSource<T>> {

}
