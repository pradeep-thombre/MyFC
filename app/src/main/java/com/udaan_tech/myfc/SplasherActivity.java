package com.udaan_tech.myfc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SplasherActivity extends AppCompatActivity {
    Thread thread;
    int check=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splasher);
        getSupportActionBar().hide();
        thread = new Thread(){
            public void run() {
                try {
                    sleep(10000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (check==0) {
                        Intent intent = new Intent(SplasherActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        };thread.start();
    }

    public void skip(View view) {
        check=1;
        Intent intent = new Intent(SplasherActivity.this, MainActivity.class);
        startActivity(intent);
    }
}