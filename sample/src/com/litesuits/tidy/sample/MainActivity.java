package com.litesuits.tidy.sample;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.litesuits.tidy.$;
import com.litesuits.tidy.R;

public class MainActivity extends BaseActivity {

    @$ TextView tvLabel;
    @$ TextView tvLabel1;
    @$ TextView tvLabel2;
    @$ TextView tvLabel3;
    @$ TextView tvLabel4;
    @$ TextView tvLabel5;
    @$ TextView tvLabel6;
    @$ TextView tvLabel7;
    @$ TextView tvLabel8;
    @$ TextView tvLabel9;
    @$ TextView tvLabel10;
    @$ TextView tvLabel11;
    @$ TextView tvLabel12;
    @$ TextView tvLabel13;
    @$ TextView tvLabel14;
    @$ TextView tvLabel15;
    @$ TextView tvLabel16;
    @$ TextView tvLabel17;
    @$ TextView tvLabel18;
    @$ TextView tvLabel19;
    private String b;
    protected int b1;
    float b2;
    public View b3, b4, b5, b6, b7;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_ioc);
        long start = System.currentTimeMillis();
        tvLabel = (TextView) findViewById(R.id.tvLabel);
        long cost = System.currentTimeMillis() - start;
        System.out.println("find cost : " + cost);
        tvLabel.setText("Hello Lite 1!");
        tvLabel3.setText("Hello Lite 3!");
        tvLabel7.setText("Hello Lite 7!");
        tvLabel11.setText("Hello Lite 11!");
        tvLabel16.setText("Hello Lite 16!");
        tvLabel19.setText("Hello Lite 19!");

    }
}
