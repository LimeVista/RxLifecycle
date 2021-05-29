package me.limeice.lifecycle

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class ScopeRxLifecycleInstrumentedTest {

    @Test
    fun useAppContext() {
        // val instrumentation = InstrumentationRegistry.getInstrumentation()
        val mainActivity: ActivityScenario<MainActivity> =
            ActivityScenario.launch(MainActivity::class.java)

        mainActivity.onActivity {
            it.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_layout, TestFragment.newInstance())
                .commit()

            AndroidSchedulers.mainThread().scheduleDirect({
                it.supportFragmentManager.popBackStack()
            }, 2, TimeUnit.SECONDS)
        }
    }
}