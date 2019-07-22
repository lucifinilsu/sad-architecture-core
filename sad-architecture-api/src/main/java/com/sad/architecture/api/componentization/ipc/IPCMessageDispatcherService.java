package com.sad.architecture.api.componentization.ipc;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Administrator on 2019/4/3 0003.
 */

public class IPCMessageDispatcherService extends Service{

    private Handler serverHandler=new ServerHandler();
    //服务端提供给客户端的信使，客户端通过在ServiceConnection里的回调OnServiceConnected里new Messenger(IBinder)拿到这个实例
    protected Messenger serverMessenger = new Messenger(serverHandler);
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return serverMessenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("ipc","---------------->服务端被调用");
        return START_NOT_STICKY;
    }

}
