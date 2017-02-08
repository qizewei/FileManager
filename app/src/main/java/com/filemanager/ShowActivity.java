package com.filemanager;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.filemanager.fragment.ApkFragment;
import com.filemanager.fragment.FileNameFragment;
import com.filemanager.fragment.ImageFragment;
import com.filemanager.fragment.MusicFragment;
import com.filemanager.fragment.VideoFragment;
import com.filemanager.fragment.WordFragment;
import com.filemanager.fragment.ZipFragment;
import com.umeng.analytics.MobclickAgent;

public class ShowActivity extends AppCompatActivity {

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        String aClass = getIntent().getStringExtra("class");
        switch (aClass) {
            case "image":
                transaction.add(R.id.show_detial, new ImageFragment());
                break;
            case "music":
                transaction.add(R.id.show_detial, new MusicFragment());
                break;
            case "video":
                transaction.add(R.id.show_detial, new VideoFragment());
                break;
            case "word":
                transaction.add(R.id.show_detial, new WordFragment());
                break;
            case "apk":
                transaction.add(R.id.show_detial, new ApkFragment());
                break;
            case "zip":
                transaction.add(R.id.show_detial, new ZipFragment());
                break;
            case "filename":
                transaction.add(R.id.show_detial,new FileNameFragment());
                break;

        }
        transaction.commit();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
