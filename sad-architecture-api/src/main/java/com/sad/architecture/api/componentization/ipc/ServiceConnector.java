package com.sad.architecture.api.componentization.ipc;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Messenger;
import android.text.TextUtils;
import android.util.Log;

import com.sad.architecture.api.init.ContextHolder;

/**
 * Created by Administrator on 2019/3/12 0012.
 */

public class ServiceConnector {



    public static boolean connect(String app,IServiceConnectionCallback connectionCallback){
        Intent intent;
        boolean isBindMainProcess= TextUtils.isEmpty(app) || ContextHolder.context.getPackageName().equals(app);
        if (isBindMainProcess) {
            Log.e("ipc","------------------->连接当前App服务端："+app);
            intent = new Intent(ContextHolder.context,IPCMessageDispatcherService.class);
        }
        else {
            //跨App调起事件分发服务
            Log.e("ipc","------------------->连接其他App服务端："+app);
            intent = new Intent(app + ".ipc");
            //intent.setComponent(new ComponentName(app,"com.sad.core.component.api.ipc.EventDispatcherServer"));
            intent.setPackage(app);
        }
        return ContextHolder.context.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                //做该做的事情,将远程的binder包装成远程信使供本地存储
                Messenger serverMessenger = new Messenger(service);
                if (connectionCallback!=null){
                    connectionCallback.onConnected(name,serverMessenger);
                }

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                if (connectionCallback!=null){
                    connectionCallback.onDisconnected(name);
                }

            }
        }, Context.BIND_AUTO_CREATE);
    }

}
