package com.sad.architecture.api.componentization.ipc;

import android.content.ComponentName;
import android.os.Messenger;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/3/12 0012.
 */

public interface IServiceConnectionCallback extends Serializable {

    public void onConnected(ComponentName name, Messenger serverMessenger);

    public void onDisconnected(ComponentName name);
}
