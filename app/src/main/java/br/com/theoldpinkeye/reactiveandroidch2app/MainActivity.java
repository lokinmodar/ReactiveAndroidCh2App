package br.com.theoldpinkeye.reactiveandroidch2app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Observable.just("First item", "Second item")
                .doOnNext(e -> Log.d("APP", "on-next:" +
                        Thread.currentThread().getName() + ":" + e))
                .subscribe(e -> Log.d("APP", "subscribe:" +
                        Thread.currentThread().getName() + ":" + e));

        Observable.just("First item", "Second item")
                .subscribeOn(Schedulers.io())
                .doOnNext(e -> Log.d("APP", "on-next:" +
                        Thread.currentThread().getName() + ":" + e))
                .subscribe(e -> Log.d("APP", "subscribe:" +
                        Thread.currentThread().getName() + ":" + e));

        Observable.just("First item", "Second item")
                .subscribeOn(Schedulers.io())
                .doOnNext(e -> Log.i("APP", "on-next:" +
                        Thread.currentThread().getName() + ":" + e))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(e -> Log.i("APP", "subscribe:" +
                        Thread.currentThread().getName() + ":" + e));

        Observable.just("One", "Two")
                .subscribeOn(Schedulers.io())
                .doOnDispose(() -> log("doOnDispose"))
                .doOnComplete(() -> log("doOnComplete"))
                .doOnNext(e -> log("doOnNext", e))
                .doOnEach(e -> log("doOnEach"))
                .doOnSubscribe((e) -> log("doOnSubscribe"))
                .doOnTerminate(() -> log("doOnTerminate"))
                .doFinally(() -> log("doFinally"))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(e -> log("subscribe", e));


        PublishSubject<Integer> observable = PublishSubject.create();
        observable.toFlowable(BackpressureStrategy.MISSING)
                .observeOn(Schedulers.computation())
                .subscribe(v -> log("s", v.toString()), this::logx);
        for (int i = 0; i < 1000000; i++) {
            observable.onNext(i);
        }

    }

    private void logx(Throwable throwable) {
        Log.e("APP", "Error", throwable);
    }

    private void log(String stage, String item) {
        Log.e("APP", stage + ":" + Thread.currentThread().getName() + ":" +
                item);
    }
    private void log(String stage) {
        Log.e("APP", stage + ":" + Thread.currentThread().getName());
    }
}
