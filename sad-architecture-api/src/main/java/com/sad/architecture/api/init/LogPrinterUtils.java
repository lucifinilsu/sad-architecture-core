package com.sad.architecture.api.init;


import android.util.Log;

public class LogPrinterUtils {

    public static void logE(String tag,String log){
        if (Initializer.openLog){
            Log.e(tag,log);
        }
    }
}
