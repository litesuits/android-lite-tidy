package com.litesuits.tidy.sample;

import com.litesuits.tidy.LiteTidy;

/**
 * @author MaTianyu @http://litesuits.com
 * @date 2015-11-23
 */
public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //LiteTidy.inject();
        LiteTidy.getInstance().inject();
    }

}
