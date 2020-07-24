package com.hop.pirate.base;

import com.hop.pirate.Constants;
import com.hop.pirate.callback.ResultCallBack;

import java.util.concurrent.TimeUnit;

import androidLib.AndroidLib;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @description:
 * @author: Mr.x
 * @date :   2020/7/10 7:58 AM
 */
public class WaitTxBaseModel extends BaseModel {
    public void queryTxStatus(final String tx, final ResultCallBack<Boolean> resultCallBack) {
        schedulers(Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                AndroidLib.waitMined(tx);
                emitter.onNext(true);
                emitter.onComplete();

            }
        })).timeout(Constants.BLOCK_CHAIN_TIME_OUT, TimeUnit.SECONDS).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
                addSubscribe(d);
            }

            @Override
            public void onNext(Boolean success) {
                resultCallBack.onSuccess(success);
            }

            @Override
            public void onError(Throwable e) {
                resultCallBack.onError(e);
            }

            @Override
            public void onComplete() {
                resultCallBack.onComplete();
            }
        });
    }
}
