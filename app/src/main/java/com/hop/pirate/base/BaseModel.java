package com.hop.pirate.base;

import com.hop.pirate.Constants;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BaseModel {
    protected CompositeDisposable mCompositeDisposable;

    protected <T> Observable<T> schedulers(Observable<T> observable) {
        return observable.timeout(Constants.TIME_OUT, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    protected void addSubscribe(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
    }


    protected void removeAllDisposable() {
        if (null != mCompositeDisposable) {
            mCompositeDisposable.clear();
        }
    }
}
