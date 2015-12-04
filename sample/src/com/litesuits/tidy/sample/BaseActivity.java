package com.litesuits.tidy.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import com.litesuits.tidy.$;
import com.litesuits.tidy.R;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * @author MaTianyu @http://litesuits.com
 * @date 2015-11-30
 */
public class BaseActivity extends Activity {
    //private String c;
    //protected int c1;
    //float c2;
    //public View c3;
    //ActivityManager c4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void setContentView(int layoutResID) {
        long start = System.currentTimeMillis();
        super.setContentView(layoutResID);
        long cost = System.currentTimeMillis() - start;
        System.out.println("setContentView cost : " + cost);
        start = System.currentTimeMillis();
        //XmlResourceParser parser = getResources().getLayout(layoutResID);
        System.out.println("-----------------");
        //Field[] fields1 = R.id.class.getDeclaredFields();
        //for (Field f : fields1) {
        //    System.out.println(f.getName());
        //}
        ArrayList<Field> fields2 = getFieldsRecursive(this.getClass(), BaseActivity.class);
        int id = getResources().getIdentifier("tvLabel", "id", getPackageName());
        System.out.println("id: " + id);
        for (Field f : fields2) {
            System.out.println(f.getName());
            try {
                int resID = R.id.class.getDeclaredField(f.getName()).getInt(R.class);
                System.out.println("resID: " + resID);
                f.setAccessible(true);
                f.set(this, findViewById(resID));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cost = System.currentTimeMillis() - start;
        System.out.println("reflect time: " + cost);
    }

    /**
     * Populates {@code result} with fields defined by this class, its
     * superclasses.
     */
    private ArrayList<Field> getFieldsRecursive(Class<?> fromClasss, Class<?> toClass) {
        ArrayList<Field> result = new ArrayList<Field>();
        boolean go = true;
        for (Class<?> c = fromClasss; go && c != null; c = c.getSuperclass()) {
            for (Field field : c.getDeclaredFields()) {
                $ anno = field.getAnnotation($.class);
                if (anno != null && View.class.isAssignableFrom(field.getType())) {
                    result.add(field);
                }
            }
            go = c != toClass;
        }
        return result;
    }

    /**
     * Populates {@code result} with fields defined by this class, its
     * superclasses.
     */
    private ArrayList<Field> getFieldsRecursive(Class<?> claxx) {
        ArrayList<Field> result = new ArrayList<Field>();
        for (Class<?> c = claxx; c != null; c = c.getSuperclass()) {
            for (Field field : c.getDeclaredFields()) {
                result.add(field);
            }
        }
        return result;
    }
}
