package com.fileManager.util;


import android.graphics.drawable.Drawable;

/**
 * Created by 齐泽威 on 2017/1/30.
 */

public class ApkDetial {
    private String name;
    private String version;
    private Drawable icon;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
