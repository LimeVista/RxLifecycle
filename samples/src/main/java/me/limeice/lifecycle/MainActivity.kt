package me.limeice.lifecycle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import me.limeice.lifecycle.databinding.ActivityMainBinding
import me.limeice.rxlifecycle.rxjava3.ktx.withContext
import java.lang.AssertionError
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        testViewLifecycle()
    }

    private fun testViewLifecycle() {
        val view = View(this)
        binding.root.addView(view)

        view.post {
            val set = mutableSetOf<Int>()

            Single.just(1)
                .withContext(view)
                .subscribe { it -> set.add(it) }

            Single.timer(2, TimeUnit.SECONDS, Schedulers.io())
                .subscribeOn(Schedulers.io())
                .withContext(view)
                .subscribe { _ ->
                    // view is removed
                    throw AssertionError()
                }
            binding.root.removeView(view)
            assert(set.contains(1))
        }
    }
}