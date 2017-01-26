package com.filemanager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class FileActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {

    private LinearLayout mFileImage, mFileMusic, mFileVideo, mFileWord, mFileApk, mFileZip, mFileBottom;
    private DrawerLayout mDrawerLayout;
    private TextView mFreeView;
    private TextView mTotalView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.file_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_file);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.file_menu3);
        }
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.file_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        
        mFileImage = (LinearLayout) findViewById(R.id.file_image);
        mFileMusic = (LinearLayout) findViewById(R.id.file_music);
        mFileVideo = (LinearLayout) findViewById(R.id.file_video);
        mFileWord = (LinearLayout) findViewById(R.id.file_word);
        mFileApk = (LinearLayout) findViewById(R.id.file_apk);
        mFileZip = (LinearLayout) findViewById(R.id.file_zip);
        mFileBottom = (LinearLayout) findViewById(R.id.file_bottom);
        mFileImage.setOnClickListener(this);
        mFileMusic.setOnClickListener(this);
        mFileVideo.setOnClickListener(this);
        mFileWord.setOnClickListener(this);
        mFileApk.setOnClickListener(this);
        mFileZip.setOnClickListener(this);
        mFileBottom.setOnClickListener(this);

        //底边栏存储空间显示
        mFreeView = (TextView) findViewById(R.id.free_number);
        mTotalView = (TextView) findViewById(R.id.total_number);
        getFreeSpace();
        getTotalSpace();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.file_image:
                intent.setClass(this, ShowActivity.class);
                intent.putExtra("class", "image");
                break;
            case R.id.file_music:
                intent.setClass(this, ShowActivity.class);
                intent.putExtra("class", "music");
                break;
            case R.id.file_video:
                intent.setClass(this, ShowActivity.class);
                intent.putExtra("class", "video");
                break;
            case R.id.file_word:
                intent.setClass(this, ShowActivity.class);
                intent.putExtra("class", "word");
                break;
            case R.id.file_apk:
                intent.setClass(this, ShowActivity.class);
                intent.putExtra("class", "apk");
                break;
            case R.id.file_zip:
                intent.setClass(this, ShowActivity.class);
                intent.putExtra("class", "zip");
                break;
            case R.id.file_bottom:
                intent.setClass(this, MemoryActivity.class);
                break;
        }
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_find:
                startActivity(new Intent(FileActivity.this,FindFileActivity.class));
                break;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return true;
    }

    //    获取总的内存空间并控制显示
    public void getTotalSpace() {
        StatFs sf = new StatFs(Environment.getExternalStorageDirectory().getPath());
        float i = 1024 * 1024 * 1024;
        float bytes = sf.getTotalBytes() / i;
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数
        String format = df.format(bytes);
        mTotalView.setText(format);
    }

    //    获取剩余的内存空间并控制显示
    public void getFreeSpace() {
        StatFs sf = new StatFs(Environment.getExternalStorageDirectory().getPath());
        float i = 1024 * 1024 * 1024;
        float bytes = sf.getFreeBytes() / i;
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数
        String format = df.format(bytes);
        mFreeView.setText(format);
    }

    /**
     * 双击返回键退出
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 侧滑菜单单击监听
     *
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_check:
                Toast.makeText(this, "已经是最新版本。", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_about:
                startActivity(new Intent(FileActivity.this, AboutActivity.class));
                break;
            case R.id.nav_quit:
                finish();
                break;
        }
        return false;
    }

    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getTotalSpace();
                        getFreeSpace();
                        mSwipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(FileActivity.this, "内存信息更新完成。", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }
}
