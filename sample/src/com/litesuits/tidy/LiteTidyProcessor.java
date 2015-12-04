package com.litesuits.tidy;

import android.util.Log;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * @author MaTianyu @http://litesuits.com
 * @date 2015-11-24
 */
public class LiteTidyProcessor extends AbstractProcessor {
    private static final String TAG = LiteTidyProcessor.class.getSimpleName();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        Log.i(TAG, "init ---------------->");
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        System.out.println("process ---------------->");
        return false;
    }

}
