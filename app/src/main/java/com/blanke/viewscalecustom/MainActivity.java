package com.blanke.viewscalecustom;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private MyLayout mylayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mylayout = (MyLayout) findViewById(R.id.mylayout);
        mylayout.addRoom(new Room(200, 180, 300, 300));
    }
}
