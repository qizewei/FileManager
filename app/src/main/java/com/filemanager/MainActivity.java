package com.fileManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;

/**
 * 启动页，线程睡眠 2 秒后跳到主页面
 */
public class MainActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CheckOpen();
    }

    private void CheckOpen() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(2000);
                startActivity(new Intent(MainActivity.this,FileActivity.class));
                finish();
            }
        }).start();
    }

}
