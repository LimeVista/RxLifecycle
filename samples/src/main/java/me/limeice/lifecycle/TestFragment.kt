package me.limeice.lifecycle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import me.limeice.rxlifecycle.rxjava3.ktx.withContext
import java.lang.AssertionError
import java.util.concurrent.TimeUnit

class TestFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        testLifeCycle()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun testLifeCycle() {
        Single.just(1)
            .withContext(this)
            .subscribe { _ ->
                // do somethings
            }

        Single.timer(5, TimeUnit.SECONDS, Schedulers.io())
            .subscribeOn(Schedulers.io())
            .withContext(this)
            .subscribe { _ ->
                // LifeCycle is removed
                throw AssertionError()
            }
    }

    companion object {

        fun newInstance(): TestFragment {
            val args = Bundle()
            val fragment = TestFragment()
            fragment.arguments = args
            return fragment
        }
    }
}