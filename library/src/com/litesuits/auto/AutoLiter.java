package com.litesuits.auto;

/**
 * Created by MaTianyu on 15/12/7.
 */
public interface AutoLiter<T> {
    void onCreate(T target);

    void onDestroy(T target);
}
