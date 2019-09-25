package com.sad.architecture.core.demo;

import androidx.multidex.MultiDexApplication;

import com.sad.architecture.api.SCore;

/**
 * Created by Administrator on 2019/4/11 0011.
 */

public class App extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        SCore.init(this);
    }
}
