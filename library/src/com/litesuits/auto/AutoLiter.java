package com.litesuits.auto;

/**
 * 自动化代码产生接口
 *
 * @author MaTianyu @http://litesuits.com
 * @date 2015-12-17 16:05
 */
public interface AutoLiter<T> {
    void onCreate(T target);

    void onDestroy(T target);
}
