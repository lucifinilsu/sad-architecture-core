package com.sad.architecture.api.componentization.ipc;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.util.Log;

import com.sad.architecture.api.componentization.IComponentCallback;
import com.sad.architecture.api.componentization.IComponentInstanceFactory;
import com.sad.architecture.api.componentization.IComponentRequest;
import com.sad.architecture.api.componentization.ITargetLocal;
import com.sad.architecture.api.componentization.ITargets;
import com.sad.architecture.api.componentization.impl.ComponentRequestImpl;
import com.sad.architecture.api.componentization.impl.TargetsImpl;
import com.sad.architecture.api.componentization.impl.TargetsLocalImpl;
import com.sad.architecture.api.componentization.internal.ComponentInstanceFactoryCallerImpl;
import com.sad.architecture.api.componentization.internal.InternalRelayComponentCallback;
import com.sad.architecture.api.componentization.internal.RequesterImpl;
import com.sad.architecture.api.init.ContextHolder;
import com.sad.basic.utils.app.AppInfoUtil;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Administrator on 2019/3/4 0004.
 */

public class ClientHandler extends Handler{

    protected ClientHandler(){}

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        int opt=msg.what;
        Messenger replyMessenger=msg.replyTo;
        switch (opt){
            case IPCConst.POST_REQUEST:
                //接收端收到发送端的请求，开始处理
                Log.e("ipc","------------------->接收端收到发送端的请求，开始处理");
                dispatchEventToLocal(Message.obtain(msg), InternalRelayComponentCallback.newInstance(replyMessenger));
                break;
            default:
                break;
        }
    }

    @Deprecated
    public void onReply(Message message,int mode){
        Bundle bundle=message.getData();
        if (bundle==null){
            return;
        }
        String fromApp=bundle.getString(IPCConst.BUNDLE_KEY_MESSENGER_APPNAME);
        String fromProcess=bundle.getString(IPCConst.BUNDLE_KEY_MESSENGER_PROCESSNAME);

    }

    public void dispatchEventToLocal(Message msg,IComponentCallback callback){
        String currProcess= AppInfoUtil.getCurrAppProccessName(ContextHolder.context);
        Bundle bundle=msg.getData();
        if (bundle==null){
            return;
        }
        Log.e("ipc","------------------->Bundle正常存在，开始处理");
        //首先确定一下，是否是全部执行
        /*Serializable s_request=bundle.getSerializable(IPCConst.BUNDLE_KEY_COMPONENT_REQUEST);*/
        Parcelable s_request=bundle.getParcelable(IPCConst.BUNDLE_KEY_COMPONENT_REQUEST);
        Parcelable s_target=bundle.getParcelable(IPCConst.BUNDLE_KEY_COMPONENT_TARGETS);
        Serializable s_factory=bundle.getSerializable(IPCConst.BUNDLE_KEY_COMPONENT_FACTORY);
        //int requestMode=bundle.getInt(IPCConst.BUNDLE_KEY_COMPONENT_REQUEST_MODE,IComponentRequest.CALLER);
        if (s_request==null || s_target==null){
            return;
        }
        Log.e("ipc","------------------->请求和目标正常存在，开始处理");
        IComponentRequest request= (IComponentRequest) s_request;
        ITargets targetProcessSpec= (ITargets) s_target;
        ITargetLocal target=targetProcessSpec.api().processes().get(currProcess);
        IComponentInstanceFactory factory= ComponentInstanceFactoryCallerImpl.newInstance();
        if (s_factory!=null){
            factory= (IComponentInstanceFactory) s_factory;
        }
        if (targetProcessSpec.api().allProcess() || (target!=null && target.api().allLocal())){
            target= TargetsLocalImpl.newInstance().allLocal(true);
            Log.e("ipc","------------------->目标客户端"+currProcess+"下所有订阅组件，开始处理");
        }

        if (target!=null){
            Log.e("ipc","------------------->目标客户端"+currProcess+"的订阅组件："+target+"，开始处理");

            RequesterImpl.newInstance(
                    ComponentRequestImpl.newInstance()
                    .body(request.api().body())
                    .id(request.api().id())
                    .fromApp(request.api().fromApp())
                    .fromProcess(request.api().fromProcess())
                    .stickyStrategy(request.api().stickyStrategy())
            )
                    .setTargets(
                            ContextHolder.context.getPackageName(),
                            TargetsImpl.newInstance().addProcess(
                                    AppInfoUtil.getCurrAppProccessName(ContextHolder.context),
                                    target
                            )
                    )
                    .callback(callback)
                    .visit(factory)
                    .launchNode()
                    .submit();
            /*IRequester requester=SCore.newRequester().request(request);
            IRequester.IInvokerToLocal invoker=null;
            if (IComponentRequest.CALLER == requestMode){
                invoker=requester.asCaller();
            }
            else if(IComponentRequest.POSTER==requestMode){
                invoker=requester.asPoster();
            }
            try {
                invoker.local(target,callback);
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        }
    }
    @Deprecated
    private boolean isHandleAllSubscriber(String process,ITargets processSpec){
        if (processSpec==null){
            return true;
        }
        if (processSpec.api().allProcess()){
            return true;
        }
        else {
            Map<String,ITargetLocal> subscriberSpecHashMap=processSpec.api().processes();
            ITargetLocal subscriberSpec=subscriberSpecHashMap.get(process);
            if (subscriberSpec==null){
                return false;
            }
            else {
                return subscriberSpec.api().allLocal();
            }
        }
    }


}
