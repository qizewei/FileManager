package com.filemanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.filemanager.util.ACache;
import com.filemanager.util.Fab;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.umeng.analytics.MobclickAgent;
import com.yalantis.guillotine.animation.GuillotineAnimation;
import com.yalantis.guillotine.interfaces.GuillotineListener;

import java.text.DecimalFormat;

public class FileActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    public static boolean isNight;
    private TextView mFreeView;
    private TextView mTotalView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private long exitTime = 0;
    private GuillotineAnimation mAnimation;
    private SharedPreferences mTable;
    private String mFreeS;
    private String mToalS;

    private MaterialSheetFab materialSheetFab;

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

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.activity_file);
        mTable = getSharedPreferences("table", MODE_PRIVATE);
        //断头台菜单设置
        ImageView menus = (ImageView) findViewById(R.id.content_hamburger);
        View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.guillotine, null);
        drawerLayout.addView(guillotineMenu);
        mAnimation = new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), menus)
                .setStartDelay(250)
                .setActionBarViewForAnimation(toolbar)
                .setClosedOnStart(true)
                .setGuillotineListener(new GuillotineListener() {
                    @Override
                    public void onGuillotineOpened() {
                        mTable.edit().putBoolean("menuOpen", true).commit();
                    }

                    @Override
                    public void onGuillotineClosed() {
                        mTable.edit().putBoolean("menuOpen", false).commit();
                    }
                })
                .build();


        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.file_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        findViewById(R.id.file_bottom).setOnClickListener(this);

        LinearLayout image = (LinearLayout) findViewById(R.id.file_image);
        LinearLayout music = (LinearLayout) findViewById(R.id.file_music);
        LinearLayout video = (LinearLayout) findViewById(R.id.file_video);
        LinearLayout word = (LinearLayout) findViewById(R.id.file_word);
        LinearLayout apk = (LinearLayout) findViewById(R.id.file_apk);
        LinearLayout zip = (LinearLayout) findViewById(R.id.file_zip);
        zip.setOnClickListener(this);
        apk.setOnClickListener(this);
        video.setOnClickListener(this);
        music.setOnClickListener(this);
        image.setOnClickListener(this);
        word.setOnClickListener(this);

        MaterialRippleLayout.on(image).rippleColor(Color.BLACK).rippleOverlay(true).rippleAlpha((float) 0.7).create();
        MaterialRippleLayout.on(apk).rippleColor(Color.BLACK).rippleOverlay(true).rippleAlpha((float) 0.7).create();
        MaterialRippleLayout.on(zip).rippleColor(Color.BLACK).rippleOverlay(true).rippleAlpha((float) 0.7).create();
        MaterialRippleLayout.on(video).rippleColor(Color.BLACK).rippleOverlay(true).rippleAlpha((float) 0.7).create();
        MaterialRippleLayout.on(word).rippleColor(Color.BLACK).rippleOverlay(true).rippleAlpha((float) 0.7).create();
        MaterialRippleLayout.on(music).rippleColor(Color.BLACK).rippleOverlay(true).rippleAlpha((float) 0.7).create();

        LinearLayout clear = (LinearLayout) findViewById(R.id.menu_clear);
        LinearLayout check = (LinearLayout) findViewById(R.id.menu_check);
        LinearLayout about = (LinearLayout) findViewById(R.id.menu_about);
        LinearLayout quit = (LinearLayout) findViewById(R.id.menu_quit);
        RelativeLayout title = (RelativeLayout) findViewById(R.id.menu_title);
        check.setOnClickListener(this);
        clear.setOnClickListener(this);
        about.setOnClickListener(this);
        quit.setOnClickListener(this);
        title.setOnClickListener(this);
        MaterialRippleLayout.on(check).rippleColor(Color.BLACK).rippleOverlay(true).rippleAlpha((float) 0.7).create();
        MaterialRippleLayout.on(clear).rippleColor(Color.BLACK).rippleOverlay(true).rippleAlpha((float) 0.7).create();
        MaterialRippleLayout.on(about).rippleColor(Color.BLACK).rippleOverlay(true).rippleAlpha((float) 0.7).create();
        MaterialRippleLayout.on(quit).rippleColor(Color.BLACK).rippleOverlay(true).rippleAlpha((float) 0.7).create();
        MaterialRippleLayout.on(title).rippleColor(Color.BLACK).rippleOverlay(true).rippleAlpha((float) 0.7).create();


        //底边栏存储空间显示
        mFreeView = (TextView) findViewById(R.id.free_number);
        mTotalView = (TextView) findViewById(R.id.total_number);
        getFreeSpace();
        getTotalSpace();


        Fab fab = (Fab) findViewById(R.id.fab);
        View sheetView = findViewById(R.id.fab_sheet);
        View overlay = findViewById(R.id.overlay);
        int sheetColor = getResources().getColor(R.color.textColor2);
        int fabColor = getResources().getColor(R.color.colorAccent);

        // Initialize material sheet FAB
        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay,
                sheetColor, fabColor);
        TextView name_search = (TextView)findViewById(R.id.name_search);
        TextView type_search = (TextView)findViewById(R.id.type_search);
        type_search.setOnClickListener(this);
        name_search.setOnClickListener(this);
        MaterialRippleLayout.on(name_search).rippleColor(R.color.colorAccent).rippleOverlay(true).rippleAlpha((float) 0.7).create();
        MaterialRippleLayout.on(type_search).rippleColor(R.color.colorAccent).rippleOverlay(true).rippleAlpha((float) 0.7).create();

    }


    @Override
    public void onClick(View v) {
        final Intent intent = new Intent();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (v.getId()) {
            case R.id.name_search:
                final EditText userId = new EditText(this);
                userId.setHint("请输入关键字");
                userId.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
                builder.setTitle("请输入文件名：")
                        .setCancelable(false)
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String FileName = userId.getText().toString().trim();
                                if (FileName.equals("")) {
                                    Toast.makeText(FileActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
                                } else {
                                    Intent intent = new Intent(FileActivity.this, ShowActivity.class);
                                    intent.putExtra("class", "filename");
                                    intent.putExtra("filename",FileName);
                                    startActivity(intent);
                                }
                            }
                        })
                        .setView(userId, 150, 17, 70, 20)
                        .show();
                materialSheetFab.hideSheet();
                break;
            case R.id.type_search:
                final EditText type_id = new EditText(this);
                type_id.setHint("例如:mp4");
                type_id.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
                builder.setTitle("请输入文件类型：")
                        .setCancelable(false)
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String FileType = type_id.getText().toString().trim();
                                if (FileType.equals("")) {
                                    Toast.makeText(FileActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
                                } else {
                                    Intent intent = new Intent(FileActivity.this, ShowActivity.class);
                                    intent.putExtra("class", "filetype");
                                    intent.putExtra("filetype",FileType);
                                    startActivity(intent);
                                }
                            }
                        })
                        .setView(type_id, 150, 17, 70, 20)
                        .show();
                materialSheetFab.hideSheet();
                break;
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
                intent.putExtra("total", mToalS);
                intent.putExtra("free", mFreeS);
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
        mTable = getSharedPreferences("table", MODE_PRIVATE);
        switch (item.getItemId()) {
            case R.id.toolbar_find:
                isNight = mTable.getBoolean("night", false);
                if (isNight) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    mTable.edit().putBoolean("night", false).commit();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    mTable.edit().putBoolean("night", true).commit();
                }
                recreate();
                break;
        }
        return true;
    }

    //    获取总的内存空间并控制显示
    public void getTotalSpace() {
        StatFs sf = new StatFs(Environment.getExternalStorageDirectory().getPath());
        float i = 1024 * 1024 * 1024;
        float bytes = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bytes = sf.getTotalBytes() / i;
        } else {
            Toast.makeText(this, "您的手机版本太低，暂时不支持内存查询", Toast.LENGTH_SHORT).show();
        }
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数
        mToalS = df.format(bytes);
        mTotalView.setText(mToalS);
    }

    //    获取剩余的内存空间并控制显示
    public void getFreeSpace() {
        StatFs sf = new StatFs(Environment.getExternalStorageDirectory().getPath());
        float i = 1024 * 1024 * 1024;
        float bytes = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bytes = sf.getFreeBytes() / i;
        }
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数
        mFreeS = df.format(bytes);
        mFreeView.setText(mFreeS);
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
            boolean open = mTable.getBoolean("menuOpen", false);
            if (open) {
                mAnimation.close();
                return true;
            }
            if (materialSheetFab.isSheetVisible()) {
                materialSheetFab.hideSheet();
                return true;
            }

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

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
