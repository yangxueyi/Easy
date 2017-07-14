package com.example.zhang.easy;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class MainActivity1 extends AppCompatActivity {

    private static final String TAG = "MainActivity1";
    private int hour;
    private EasyPickerView epvH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // hours
        initHours();

    }
    private void initHours() {
        epvH = (EasyPickerView) findViewById(R.id.epv_h);
        final ArrayList<String> hDataList = new ArrayList<>();
        for (int i = 0; i < 24; i++)
            hDataList.add("" + i);

        epvH.setDataList(hDataList);

    }

}
