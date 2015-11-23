package com.litesuits.tidy.sample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.litesuits.ioc.R;

public class IoCSampleActivity extends Activity {

    private TextView tvLabel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smaple_ioc);
        tvLabel = (TextView) findViewById(R.id.tvLabel);
        tvLabel.setText("Hello Lite!");
    }
}
