package com.sad.architecture.api.init;

import android.content.Context;

/**
 * Created by Administrator on 2019/3/22 0022.
 */

public class ContextHolder {

    public static Context context;

    /*private volatile static Singleton uniqueSingleton;

    private Singleton() {
    }

    public Singleton getInstance() {
        if (null == uniqueSingleton) {
            synchronized (Singleton.class) {
                if (null == uniqueSingleton) {
                    uniqueSingleton = new Singleton();
                }
            }
        }
        return uniqueSingleton;
    }*/

}
