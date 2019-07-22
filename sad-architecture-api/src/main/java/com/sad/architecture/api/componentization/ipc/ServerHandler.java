package com.sad.architecture.api.componentization.ipc;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.sad.architecture.api.componentization.IComponentRequest;
import com.sad.architecture.api.componentization.IParcelable;
import com.sad.architecture.api.componentization.ITargetLocal;
import com.sad.architecture.api.componentization.ITargets;
import com.sad.architecture.api.componentization.impl.ComponentRequestImpl;
import com.sad.architecture.api.init.ContextHolder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2019/3/4 0004.
 *
 * 注意：实际上服务内部只存储了本App的各个进程的信使
 */

public  class ServerHandler extends Handler{

    protected ServerHandler(){}

    /*private void feedback(int mode){
        //完成握手，向客户端反馈信息
        Message replyMsg=Message.obtain();
        replyMsg.what=mode;
        Bundle bundle=new Bundle();
        bundle
    }*/

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        int opt=msg.what;
        switch (opt){
            case IPCConst.POST_REQUEST:
                dispatchEvent(Message.obtain(msg));
                break;
            case IPCConst.REGISTER_CLIENT_MESSENGER:
                registerMessenger(msg);
                //feedback(opt);
                break;
            case IPCConst.UNREGISTER_CLIENT_MESSENGER:
                unregisterMessenger(msg);
                break;
            default:
                break;
        }

    }

    //来自各个进程客户端的信使集合，当收到事件信息时，遍历并过滤此集合，进行消息下发
    protected static ConcurrentHashMap<String,Messenger> clientMessengerMap = new ConcurrentHashMap<>();

    /**
     * 将远道而来的客户端信使注册进集合
     * @param msg
     */
    private void registerMessenger(Message msg){
        Messenger clientMessenger=msg.replyTo;
        Bundle bundle=msg.getData();
        if (bundle==null){
            return;
        }
        String app=bundle.getString(IPCConst.BUNDLE_KEY_MESSENGER_APPNAME);
        String process=bundle.getString(IPCConst.BUNDLE_KEY_MESSENGER_PROCESSNAME);
        Log.e("ipc","------------------->服务端开始存储客户端信使：app="+app+",process="+process);
        registerMessenger(process,clientMessenger);
    }

    private void registerMessenger(String process,Messenger clientMessenger){
        if (TextUtils.isEmpty(process)){
            return;
        }
        clientMessengerMap.put(process,clientMessenger);
        Log.e("ipc","------------------->服务端存储客户端信使完成，当前：clientMessengerMap="+clientMessengerMap);
        //注册完成以后，检查粘性事件表里是否有要补发的事件
        supplementToSend(process,clientMessenger);

    }

    /**
     * 补发粘性事件
     * @param clientMessenger
     */
    private void supplementToSend(String process,Messenger clientMessenger){
        List<Message> messageList= IPCStorage.REMOTE_PROCESS_STICKY_MESSAGES.get(process);
        if (messageList==null){
            return;
        }
        Iterator<Message> iterator=messageList.iterator();
        while (iterator.hasNext()){
            Message message=iterator.next();
            try {
                Bundle bundle=message.getData();
                Parcelable s_request=bundle.getParcelable(IPCConst.BUNDLE_KEY_COMPONENT_REQUEST);
                if (s_request==null){
                    iterator.remove();
                    continue;
                }
                IComponentRequest request= (IComponentRequest) s_request;
                //检查粘性事件是否还有效
                IStickyStrategy sticky=request.api().stickyStrategy();
                if (sticky==null){
                    iterator.remove();
                    continue;
                }
                boolean isValide=sticky.isValid(
                        IPCStickyEnvLevel.PROCESS,
                        ContextHolder.context.getPackageName(),
                        process,
                        ""
                        );
                //onCheckStickyEventValid(true,ContextHolder.context.getPackageName(),process,"",request.getEvent());
                if (isValide){
                    clientMessenger.send(message);
                }
                iterator.remove();

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        IPCStorage.REMOTE_PROCESS_STICKY_MESSAGES.put(process,messageList);
    }

    private void unregisterMessenger(Message msg){
        Messenger clientMessenger=msg.replyTo;
        Bundle bundle=msg.getData();
        if (bundle==null){
            return;
        }
        String app=bundle.getString(IPCConst.BUNDLE_KEY_MESSENGER_APPNAME);
        String process=bundle.getString(IPCConst.BUNDLE_KEY_MESSENGER_PROCESSNAME);
        unregisterMessenger(process);
    }


    private void unregisterMessenger(String process){
        if (TextUtils.isEmpty(process)){
            return;
        }
        clientMessengerMap.remove(process);
    }


    private void dispatchEvent(Message msg){
        Bundle bundle=msg.getData();
        if (bundle==null){
            return;
        }
        Log.e("ipc","------------------->开始分发事件");
        bundle.setClassLoader(getClass().getClassLoader());//IComponentRequest.class.getClassLoader()
        Parcelable s_request=bundle.getParcelable(IPCConst.BUNDLE_KEY_COMPONENT_REQUEST);
        Parcelable s_target=bundle.getParcelable(IPCConst.BUNDLE_KEY_COMPONENT_TARGETS);
        //Serializable s_factory=bundle.getSerializable(IPCConst.BUNDLE_KEY_COMPONENT_FACTORY);
        //int requestMode=bundle.getInt(IPCConst.BUNDLE_KEY_COMPONENT_REQUEST_MODE,IComponentRequest.CALLER);
        if (s_request==null || s_target==null){
            Log.e("ipc","------------------->事件或者目标为空");
            return;
        }

        IComponentRequest request= (IComponentRequest) s_request;

        Log.e("ipc","------------------->服务端接收到的请求："+request.outputContent());

        ITargets targetProcess= (ITargets) s_target;
        String app=ContextHolder.context.getPackageName();
        if (targetProcess.api().allProcess()){
            dispatchEventToAllProcess(msg);
        }
        else {
            Map<String,ITargetLocal> remoteProcessMap=targetProcess.api().processes();
            //需要注意的是，由于targetProcess是在发送前经过对目标群重新整合过的，所以可能会出现含有发送者本地进程的目标，为避免重复，所以要先剔除他们
            remoteProcessMap.remove(request.api().fromProcess());
            Log.e("ipc","------------------->事件精准目标："+remoteProcessMap);
            Iterator<Map.Entry<String,ITargetLocal>> iterator_process=remoteProcessMap.entrySet().iterator();
            while (iterator_process.hasNext()){
                Map.Entry<String,ITargetLocal> subscriberSpecEntry=iterator_process.next();
                String process=subscriberSpecEntry.getKey();
                ITargetLocal targetLocal=subscriberSpecEntry.getValue();
                Message messageToBeSent=new Message();
                messageToBeSent.copyFrom(msg);
                boolean hasProcessMessenger=clientMessengerMap.containsKey(process);
                if (!hasProcessMessenger){
                    //没有找到process相应的信使，根据粘性策略进行存储
                    IStickyStrategy sticky=request.api().stickyStrategy();
                    if (sticky!=null){
                        Log.e("ipc","------------------->开始存储粘性事件，目标进程："+process);
                        sticky.put(IPCStickyEnvLevel.PROCESS,ContextHolder.context.getPackageName(),process,"",messageToBeSent);
                    }
                    continue;
                }
                Messenger clientMessenger=clientMessengerMap.get(process);

                try {
                    Log.e("ipc","------------------->找到了相应信使，下发目标进程："+process);
                    clientMessenger.send(messageToBeSent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("ipc","------------------->目标进程："+process+"的信使可能已经废弃");
                    //android.os.DeadObjectException错误，有可能Messenger已经被废弃了，例如所在进程关闭了但没有及时注销信使，所以手动注销，然后考虑粘性
                    unregisterMessenger(process);
                    IStickyStrategy sticky=request.api().stickyStrategy();
                    if (sticky!=null){
                        sticky.put(IPCStickyEnvLevel.PROCESS,ContextHolder.context.getPackageName(),process,"",messageToBeSent);
                    }
                }
            }

        }
    }



    private void dispatchEventToAllProcess(Message msg){
        Log.e("ipc","------------------->分发事件给App="+ContextHolder.context.getPackageName()+"下所有进程："+clientMessengerMap);
        Iterator<Map.Entry<String,Messenger>> iterator=clientMessengerMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, Messenger> entry=iterator.next();
            String process=entry.getKey();
            Messenger clientMessenger=entry.getValue();
            Message m = new Message();
            m.copyFrom(msg);
            try {
                clientMessenger.send(m);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
