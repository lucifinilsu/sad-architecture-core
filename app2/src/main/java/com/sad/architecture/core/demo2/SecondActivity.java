package com.sad.architecture.core.demo2;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.sad.architecture.annotation.ActivityRoute;
import com.sad.architecture.api.SCore;

/**
 * Created by Administrator on 2019/5/7 0007.
 */
@ActivityRoute(name = "App2Activity2")
public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        try {
            Fragment fragment= SCore.exposerCalledInterface("f-test");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
