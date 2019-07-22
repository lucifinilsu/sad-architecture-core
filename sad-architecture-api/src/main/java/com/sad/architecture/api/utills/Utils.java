package com.sad.architecture.api.utills;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by Administrator on 2019/4/22 0022.
 */

public class Utils {

    /**
     * 获得栈中最顶层的Activity
     *
     * @param context
     * @return
     */
    public static String getTopActivity(Context context) {
        android.app.ActivityManager manager = (android.app.ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

        if (runningTaskInfos != null) {
            return (runningTaskInfos.get(0).topActivity).toString();
        } else
            return null;
    }

}
