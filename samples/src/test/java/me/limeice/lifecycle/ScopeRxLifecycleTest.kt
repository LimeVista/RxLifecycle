package me.limeice.lifecycle

import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import me.limeice.lifecycle.rxjava3.Scope
import me.limeice.rxlifecycle.rxjava3.ktx.withContext
import org.junit.Assert
import org.junit.Test
import java.lang.AssertionError
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class ScopeRxLifecycleTest {

    private val testNameOne = "t1"
    private val testNameTwo = "t2"

    @Test
    fun testObservable() {
        val map = ConcurrentHashMap<String, Int>()
        val scope = CustomScope()
        val latch = CountDownLatch(1)

        map[testNameOne] = 0
        map[testNameTwo] = 0

        Observable.just(Pair(testNameOne, 2))
            .delay(3, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.newThread())
            .withContext(scope)
            .subscribe { map[it.first] = it.second }

        Observable.just(Pair(testNameTwo, 2))
            .subscribeOn(Schedulers.io())
            .withContext(scope)
            .subscribe {
                map[it.first] = it.second
                latch.countDown()
            }

        latch.await()
        scope.clean()

        Assert.assertEquals(map[testNameOne], 0)
        Assert.assertEquals(map[testNameTwo], 2)
    }

    @Test
    fun testMaybe() {
        val map = ConcurrentHashMap<String, Int>()
        val scope = CustomScope()
        val latch = CountDownLatch(1)

        map[testNameOne] = 0
        map[testNameTwo] = 0

        Maybe.just(Pair(testNameOne, 2))
            .delay(3, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.newThread())
            .withContext(scope)
            .subscribe { map[it.first] = it.second }

        Maybe.just(Pair(testNameTwo, 2))
            .subscribeOn(Schedulers.io())
            .withContext(scope)
            .subscribe {
                map[it.first] = it.second
                latch.countDown()
            }

        latch.await()
        scope.clean()

        Assert.assertEquals(map[testNameOne], 0)
        Assert.assertEquals(map[testNameTwo], 2)
    }

    @Test
    fun testFlowable() {
        val map = ConcurrentHashMap<String, Int>()
        val scope = CustomScope()
        val latch = CountDownLatch(1)

        map[testNameOne] = 0
        map[testNameTwo] = 0

        Flowable.just(Pair(testNameOne, 2))
            .delay(3, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.newThread())
            .withContext(scope)
            .subscribe { map[it.first] = it.second }

        Flowable.just(Pair(testNameTwo, 2))
            .subscribeOn(Schedulers.io())
            .withContext(scope)
            .subscribe({
                map[it.first] = it.second
                latch.countDown()
            }, {}, {})

        latch.await()
        scope.clean()

        Assert.assertEquals(map[testNameOne], 0)
        Assert.assertEquals(map[testNameTwo], 2)
    }

    @Test
    fun testSingle() {
        val map = ConcurrentHashMap<String, Int>()
        val scope = CustomScope()
        val latch = CountDownLatch(1)

        map[testNameOne] = 0
        map[testNameTwo] = 0

        Single.just(Pair(testNameOne, 2))
            .delay(2, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.newThread())
            .withContext(scope)
            .subscribe { it -> map[it.first] = it.second }

        Single.just(Pair(testNameTwo, 2))
            .subscribeOn(Schedulers.io())
            .withContext(scope)
            .subscribe { it, _ ->
                map[it.first] = it.second
                latch.countDown()
            }

        latch.await()
        scope.clean()

        Assert.assertEquals(map[testNameOne], 0)
        Assert.assertEquals(map[testNameTwo], 2)
    }

    @Test
    fun testCompletable() {
        val scope = CustomScope()
        val latch = CountDownLatch(1)

        Completable.fromCallable { }
            .delay(3, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.newThread())
            .withContext(scope)
            .subscribe { throw AssertionError() }

        Completable.fromCallable { }
            .subscribeOn(Schedulers.io())
            .withContext(scope)
            .subscribe { latch.countDown() }

        latch.await()
        scope.clean()
    }

    private class CustomScope : Scope {

        private val disposables = CompositeDisposable()

        override fun needRunMainThread(): Boolean {
            return false
        }

        override fun onEnterScope(disposable: Disposable) {
            disposables.add(disposable)
        }

        override fun onExitScope(disposable: Disposable?) {
            disposable ?: return
            disposables.delete(disposable)
        }

        fun clean() {
            disposables.clear()
        }
    }
}