package com.sad.architecture.api.componentization.ipc;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.sad.architecture.api.componentization.IComponentCallback;
import com.sad.architecture.api.componentization.IComponentResponse;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/4/11 0011.
 */

public class ResultReceiverHandler extends Handler {


    private IComponentCallback clientCallback;

    private ResultReceiverHandler(IComponentCallback clientCallback){
        this.clientCallback=clientCallback;
    }
    public static Handler newInstance(IComponentCallback clientCallback){
        return new ResultReceiverHandler(clientCallback);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        int opt=msg.what;
        switch (opt){
            case IPCConst.POST_COMPONENT_RESPONSE:
                //发送端收到接收端的回执
                Log.e("ipc","------------------->接收到了回执");
                callback(msg);
                break;
            case IPCConst.REGISTER_CLIENT_MESSENGER_RESPONSE:
                //onReply(Message.obtain(msg),IPCConst.REGISTER_CLIENT_MESSENGER_RESPONSE);
                break;
            case IPCConst.UNREGISTER_CLIENT_MESSENGER_RESPONSE:
                //onReply(Message.obtain(msg),IPCConst.UNREGISTER_CLIENT_MESSENGER_RESPONSE);
                break;
            default:break;
        }
    }

    protected void callback(Message msg){
        if (clientCallback!=null){
            if (msg.what==IPCConst.POST_COMPONENT_RESPONSE){
                Bundle bundle = msg.getData();
                if (bundle!=null){
                    IComponentResponse response=bundle.getParcelable(IPCConst.BUNDLE_KEY_COMPONENT_RESPONSE);
                    if (response!=null){
                        clientCallback.onComponentInvokeCompleted(response);
                    }
                }
            }
        }
    }
}
