package com.sad.architecture.api.componentization.internal;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.util.Log;

import com.sad.architecture.api.componentization.IComponentCallback;
import com.sad.architecture.api.componentization.IComponentRequest;
import com.sad.architecture.api.componentization.IComponentResponse;
import com.sad.architecture.api.componentization.impl.ComponentResponseImpl;
import com.sad.architecture.api.componentization.ipc.IPCConst;

import java.io.Serializable;
import java.lang.reflect.Field;

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
        //置空非序列化对象
        IComponentRequest request=response.request();
        if (request!=null){
            Object b=request.api().body();
            if (!(b instanceof Serializable) && !(b instanceof Parcelable) && !isPrimitive(b)){
                request.body(null);
            }
            else {
                setNullFromUnSerializableAttribute(b);
                request.body(b);
            }
            //request.body(null);
            request.sourceLooper(null);
            response=response.creator()
                    .request(request)
                    .create();
        }

        response=response.creator()
                .cancelable((response.cancelable() instanceof Serializable)?response.cancelable():null)
                .create();

        bundle.putParcelable(IPCConst.BUNDLE_KEY_COMPONENT_RESPONSE,response);
        message.setData(bundle);
        try {
            replyMessenger.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setNullFromUnSerializableAttribute(Object o){
        Field[] field = o.getClass().getDeclaredFields();
        for (Field f:field
             ) {
            try{
                f.setAccessible(true);
                Object v=f.get(o);
                if (!(v instanceof Serializable) && !(v instanceof Parcelable) && !isPrimitive(v)){
                    Log.e("ipc","------------------->存在非序列化对象，类型："+f.getType()+",值："+v+",名称："+f.getName());
                    f.set(o, null);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    /**判断一个对象是否是基本类型或基本类型的封装类型*/
    private boolean isPrimitive(Object obj) {
        try {
            return ((Class<?>)obj.getClass().getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
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
