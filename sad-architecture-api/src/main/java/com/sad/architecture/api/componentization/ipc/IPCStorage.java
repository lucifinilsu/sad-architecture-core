package com.sad.architecture.api.componentization.ipc;

import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.sad.architecture.api.componentization.IComponentRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2019/3/25 0025.
 */

public class IPCStorage {

    protected final static HashMap<String,List<ComponentSticky>> LOCAL_STICKY_REQUESTS =new HashMap<>();

    protected final static ConcurrentHashMap<String, List<Message>> REMOTE_APP_STICKY_MESSAGES =new ConcurrentHashMap<>();

    //粘性事件集合，当出于某些原因，事件到达本服务时，找不到目标信使(例如目标进程尚未启动)，则将事件存储在此集合，当信使到达时，先检测本集合是否有未超期的待处理事件。
    protected final static ConcurrentHashMap<String, List<Message>> REMOTE_PROCESS_STICKY_MESSAGES = new ConcurrentHashMap<>();

    protected final static ConcurrentHashMap<String,Messenger> SERVER_MESSENGERS = new ConcurrentHashMap<>();

    protected static void cacheAppStickyMessage(String app, Message message){
        Log.e("ipc","------------------->开始存储粘性事件，目标App："+app);
        cacheStickyMessage(REMOTE_APP_STICKY_MESSAGES,app,message);
    }

    protected static List<Message> obtainAppStickyMessage(String app){
        return REMOTE_APP_STICKY_MESSAGES.get(app);
    }

    protected static void cacheProcessStickyMessage(String process, Message message){
        Log.e("ipc","------------------->开始存储粘性事件，目标Process："+process);
        cacheStickyMessage(IPCStorage.REMOTE_PROCESS_STICKY_MESSAGES,process,message);
    }

    protected static List<Message> obtainProcessStickyMessage(String process){
        return REMOTE_PROCESS_STICKY_MESSAGES.get(process);
    }

    protected static void cacheLocalStickyRequest(String componentName, ComponentSticky sticky){
        Log.e("ipc","------------------->开始存储粘性事件，目标componentName："+componentName);
        List<ComponentSticky> requests=LOCAL_STICKY_REQUESTS.get(componentName);
        if (requests==null){
            requests=new ArrayList<>();
        }
        requests.add(sticky);
        LOCAL_STICKY_REQUESTS.put(componentName,requests);
    }

    public static List<ComponentSticky> obtainLocalStickyRequest(String componentName){
        return LOCAL_STICKY_REQUESTS.get(componentName);
    }

    public static void putLocalSticky(String name ,List<ComponentSticky> stickies){
        LOCAL_STICKY_REQUESTS.put(name,stickies);
    }

    private static void cacheStickyMessage(ConcurrentHashMap<String, List<Message>> table,String key, Message message){
        List<Message> messages=table.get(key);
        if (messages==null){
            messages=new ArrayList<>();
        }
        messages.add(message);
        table.put(key,messages);
    }
}
