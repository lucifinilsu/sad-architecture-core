package com.sad.architecture.api.componentization.internal;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;

import com.sad.architecture.api.componentization.IComponentCallback;
import com.sad.architecture.api.componentization.IComponentResponse;
import com.sad.architecture.api.componentization.impl.ComponentResponseImpl;
import com.sad.architecture.api.componentization.ipc.IPCConst;

/**
 * Created by Administrator on 2019/4/11 0011.
 */

public class InternalRelayComponentCallback implements IComponentCallback{

    private Messenger replyMessenger;

    private InternalRelayComponentCallback(){}
    private InternalRelayComponentCallback(Messenger replyMessenger){
        this.replyMessenger=replyMessenger;
    }

    public static IComponentCallback newInstance(Messenger replyMessenger){
        return new InternalRelayComponentCallback(replyMessenger);
    }

    @Override
    public void onComponentInvokeCompleted(IComponentResponse response) {
        Message message=Message.obtain();
        message.what= IPCConst.POST_COMPONENT_RESPONSE;
        Bundle bundle=new Bundle();
        //IComponentResponse response= ComponentResponseImpl.newBuilder().build();
        bundle.putSerializable(IPCConst.BUNDLE_KEY_COMPONENT_RESPONSE,response);
        message.setData(bundle);
        try {
            replyMessenger.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*@Override
    public void onComponentInvokeException(Exception e) {
        Message message=Message.obtain();
        message.what=IPCConst.POST_COMPONENT_RESPONSE;
        Bundle bundle=new Bundle();
        bundle.putString(IPCConst.BUNDLE_KEY_COMPONENT_EXCEPTION_CONTENT,e.getMessage());
        message.setData(bundle);
        try {
            replyMessenger.send(message);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }*/



}
