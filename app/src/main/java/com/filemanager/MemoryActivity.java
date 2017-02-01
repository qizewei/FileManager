package com.filemanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.blankj.utilcode.utils.DeviceUtils;
import com.blankj.utilcode.utils.PhoneUtils;
import com.umeng.analytics.MobclickAgent;

public class MemoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);
        initView();

    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.Detial_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        TextView total = (TextView) findViewById(R.id.detial_total);
        TextView free = (TextView) findViewById(R.id.detial_free);
        total.setText(getIntent().getStringExtra("total"));
        free.setText(getIntent().getStringExtra("free"));

        String model = DeviceUtils.getModel();
        String manufacturer = DeviceUtils.getManufacturer();
        boolean root = DeviceUtils.isDeviceRoot();
        int version = DeviceUtils.getSDKVersion();
        String imei = PhoneUtils.getIMEI(this);

        TextView DetialModel = (TextView) findViewById(R.id.detial_model);
        DetialModel.setText(model);
        TextView DetialFactory = (TextView) findViewById(R.id.detial_factory);
        DetialFactory.setText(manufacturer);
        TextView DetialImei = (TextView) findViewById(R.id.detial_imei);
        DetialImei.setText(imei);

        TextView DetialRoot = (TextView) findViewById(R.id.detial_root);
        if (root) {
            DetialRoot.setText("你的设备已Root");
        } else
            DetialRoot.setText("您的设备未Root");
        TextView DetialVersion = (TextView) findViewById(R.id.detial_version);
        if (version == 23) {
            DetialVersion.setText("6.0.0");
        } else if (version == 22) {
            DetialVersion.setText("5.1.0");
        } else if (version == 21) {
            DetialVersion.setText("5.0.0");
        } else if (version == 20) {
            DetialVersion.setText("4.4.0 W");
        } else if (version == 19) {
            DetialVersion.setText("4.4.0");
        } else if (version == 18) {
            DetialVersion.setText("4.3.0");
        } else if (version == 17) {
            DetialVersion.setText("4.2.0");
        } else if (version == 16) {
            DetialVersion.setText("4.1.0");
        } else if (version == 15) {
            DetialVersion.setText("4.0.0");
        } else if (version <= 14) {
            DetialVersion.setText("恭喜，你是地球上Android版本最低的3%的人之一。");
        }
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
