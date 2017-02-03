package com.filemanager;

import android.app.Application;

import com.umeng.analytics.MobclickAgent;



/**
 * Created by 齐泽威 on 2017/2/1.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        cn.bmob.v3.statistics.AppStat.i("9cefd91191d36d9023985dc24c860b39", "MoRen");

        MobclickAgent.UMAnalyticsConfig config = new MobclickAgent.UMAnalyticsConfig(this, "9cefd91191d36d9023985dc24c860b39", "MoRen");
        MobclickAgent.startWithConfigure(config);
        MobclickAgent.openActivityDurationTrack(false);
    }
}
