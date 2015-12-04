package com.litesuits.tidy;

import android.app.Activity;

/**
 * @author MaTianyu @http://litesuits.com
 * @date 2015-11-23
 */
public class LiteTidy {
    private static final LiteTidy instance = new LiteTidy();

    private LiteTidy() {}

    public static LiteTidy getInstance() {return instance;}

    //public synchronized void init() {}

    public static LiteTidy inject(Activity activity) {
        instance.inject();
        return instance;
    }

    public LiteTidy inject() {
        System.out.println("inject");
        return instance;
    }

}
