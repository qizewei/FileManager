package com.filemanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.filemanager.util.ACache;
import com.yalantis.guillotine.animation.GuillotineAnimation;

import java.text.DecimalFormat;

public class FileActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private DrawerLayout mDrawerLayout;
    private TextView mFreeView;
    private TextView mTotalView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private long exitTime = 0;
    private GuillotineAnimation mAnimation;

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
        
        ImageView menus = (ImageView)findViewById(R.id.content_hamburger); 
        View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.guillotine, null);
        mDrawerLayout.addView(guillotineMenu);
        mAnimation = new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), menus)
                .setStartDelay(250)
                .setActionBarViewForAnimation(toolbar)
                .setClosedOnStart(true)
                .build();

      
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.file_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        findViewById(R.id.file_image).setOnClickListener(this);
        findViewById(R.id.file_music).setOnClickListener(this);
        findViewById(R.id.file_video).setOnClickListener(this);
        findViewById(R.id.file_word).setOnClickListener(this);
        findViewById(R.id.file_apk).setOnClickListener(this);
        findViewById(R.id.file_zip).setOnClickListener(this);
        findViewById(R.id.file_bottom).setOnClickListener(this);
        
        findViewById(R.id.menu_clear).setOnClickListener(this);
        findViewById(R.id.menu_check).setOnClickListener(this);
        findViewById(R.id.menu_about).setOnClickListener(this);
        findViewById(R.id.menu_quit).setOnClickListener(this);
        findViewById(R.id.menu_title).setOnClickListener(this);
        
        
    

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
                startActivity(intent);
                break;
            case R.id.file_music:
                intent.setClass(this, ShowActivity.class);
                intent.putExtra("class", "music");
                startActivity(intent);
                break;
            case R.id.file_video:
                intent.setClass(this, ShowActivity.class);
                intent.putExtra("class", "video");
                startActivity(intent);
                break;
            case R.id.file_word:
                intent.setClass(this, ShowActivity.class);
                intent.putExtra("class", "word");
                startActivity(intent);
                break;
            case R.id.file_apk:
                intent.setClass(this, ShowActivity.class);
                intent.putExtra("class", "apk");
                startActivity(intent);
                break;
            case R.id.file_zip:
                intent.setClass(this, ShowActivity.class);
                intent.putExtra("class", "zip");
                startActivity(intent);
                break;
            case R.id.file_bottom:
                intent.setClass(this, MemoryActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_clear:
                ACache mCatch = ACache.get(FileActivity.this);
                mCatch.clear();
                SharedPreferences table = getSharedPreferences("table", MODE_PRIVATE);
                SharedPreferences.Editor edit = table.edit();
                edit.putBoolean("firstImage", true);
                edit.putBoolean("firstMusic", true);
                edit.putBoolean("firstVideo", true);
                edit.putBoolean("firstWord", true);
                edit.putBoolean("firstApk", true);
                edit.putBoolean("firstZip", true);
                edit.commit();
                Toast.makeText(this, "清理缓存成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_check:
                Toast.makeText(this, "已经是最新版本", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_about:
                startActivity(new Intent(FileActivity.this, AboutActivity.class));
                break;
            case R.id.menu_quit:
                finish();
                break;
            case R.id.menu_title:
                Toast.makeText(this, "别瞎点，我只是一排文字。", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_find:
                startActivity(new Intent(FileActivity.this, FindFileActivity.class));
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
     *
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
