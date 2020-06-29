package com.hop.pirate.callback;

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/23 6:30 PM
 */
public interface ResultCallBack<T> {
   void onError(Throwable e);
    void onSuccess(T t);
    void onComplete();
}
