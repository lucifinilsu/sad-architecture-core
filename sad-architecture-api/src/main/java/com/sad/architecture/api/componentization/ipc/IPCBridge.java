package com.sad.architecture.api.componentization.ipc;

import android.content.ComponentName;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import com.sad.architecture.api.init.LogPrinterUtils;

import com.sad.architecture.api.componentization.IComponentCallback;
import com.sad.architecture.api.componentization.IComponentInstanceFactory;
import com.sad.architecture.api.componentization.IComponentRequest;
import com.sad.architecture.api.componentization.ITargets;
import com.sad.architecture.api.init.ContextHolder;
import com.sad.architecture.api.test.TestPacelable;
import com.sad.basic.utils.app.AppInfoUtil;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2019/3/25 0025.
 */

/**
 *
 * 主要逻辑：每个接入本框架的App在主进程初始化后，都会启动事件分发服务IPCMessageDispatcherService，注意这个服务只存在于主进程。
 * 主进程持有：一个动态服务端信使和两个关键的信使集合：
 * 一、事件分发服务里持有一个关键静态集合：ServerHandler.clientMessengerMap。这个集合用于存储来自本App各个进程的信使clientMessenger（包括主进程本身的）。
 * 二、IPCStorage.SERVER_MESSENGERS集合持有来自不同App主进程的服务端信使（包括主进程本身的）。
 * 三、事件分发服务里的服务端信使，这个信使只有一个使用途径：下面会提到。
 *
 * 注册的主要步骤：
 * 首先，主进程初始化，这里做了两件事：
 * 一、绑定式启动事件分发服务，将上面提到的服务端信使存储进IPCStorage.SERVER_MESSENGERS备用，实际上除了注册之外自身用不到
 * 二、如果服务绑定成功：新建一条message,用replyTo的方式携带自己的静态客户端信使（clientMessenger）通过IPCStorage.SERVER_MESSENGERS里存储的自己的服务端信使发送给ServerHandler，
 * ServerHandler接收到message后取出clientMessenger存进上面说的ServerHandler.clientMessengerMap。。。好像多此一举（其实直接put就行），但实际上这是为了统一主进程
 * 和子进程的注册过程而妥协的。
 * 三、如果服务绑定失败：删除IPCStorage.SERVER_MESSENGERS存储的自身的服务端信使，然后递归注册方法（有点变态，后续需要优化）。
 * 然后，子进程初始化，这里也做了两件事：
 * 一、绑定主进程的事件分发服务，成功后拿到主进程给的服务端信使，存储进自己的IPCStorage.SERVER_MESSENGERS备用，
 * 二、和主进程一样，用主进程给的服务端信使把自己的clientMessenger交给主进程，这里凸显了一点服务端信使永远是主进程给的，不管是自己App给的还是其他App给的。
 * 三、一样，绑定失败：删除IPCStorage.SERVER_MESSENGERS存储的相应的服务端信使，然后递归。
 *
 * 通过上述的注册我们可以总结出：所有要跨进程的请求，本质上都是从当前进程的IPCStorage.SERVER_MESSENGERS取出目标App的服务端信使作为发射器，发送message，交给目标App主进程的
 * ServerHandler处理分发，在ServerHandler里解析message，根据解析出的Target里含有的目标process信息，再在ServerHandler.clientMessengerMap里找到相应的之前其他子进程注册进来的
 * clientMessenger，再进行二次分发。后面的事情就很明确了：最终由目标进程的ClientHandler处理事件
 *
 */
public class IPCBridge {
    private static ClientHandler clientHandler=new ClientHandler();//本进程客户端信息处理器
    protected static Messenger clientMessenger = new Messenger(clientHandler);//本地客户端信使
    public static IPCBridge newInstance(){
        return new IPCBridge();
    }

    protected IPCBridge(){

    }
    public static boolean registerCurrClientMessengerToMainProcessServer(){
        String app= ContextHolder.context.getPackageName();
        //下面的连接主要是针对当前是非主进程时将自身
        ServiceConnector.connect(app, new IServiceConnectionCallback() {
            @Override
            public void onConnected(ComponentName name, Messenger serverMessenger) {
                registerServerMessenger(app,serverMessenger);
                LogPrinterUtils.logE("ipc","------------------->已经连接到本App目标服务："+app);
                registerClientMessengerToApp(app);
            }

            @Override
            public void onDisconnected(ComponentName name) {
                LogPrinterUtils.logE("ipc","------------------->本App目标服务的连接断开："+app);
                unregisterServerMessenger(name.getPackageName());
                //serverMessenger=null;
                registerCurrClientMessengerToMainProcessServer();
            }
        });
        return false;
    }

    private static void registerServerMessenger(String app,Messenger messenger){
        IPCStorage.SERVER_MESSENGERS.put(app, messenger);
        //检查一下是否有此app的粘性事件
        supplementToSend(app,messenger);
    }
    /**
     * 补发粘性事件
     * @param serverMessenger
     */
    private static void supplementToSend(String app,Messenger serverMessenger){
        //先检测

        LogPrinterUtils.logE("ipc","------------------->开始检测App级别"+app+"是否存有粘性事件");
        List<Message> messageList= IPCStorage.REMOTE_APP_STICKY_MESSAGES.get(app);
        if (messageList==null){
            LogPrinterUtils.logE("ipc","------------------->App级别"+app+"无待发送的粘性事件列表");
            return;
        }
        Iterator<Message> iterator=messageList.iterator();
        while (iterator.hasNext()){
            Message message=iterator.next();
            try {
                Bundle bundle=message.getData();
                Serializable s_request=bundle.getSerializable(IPCConst.BUNDLE_KEY_COMPONENT_REQUEST);
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
                boolean isValid=sticky.isValid(IPCStickyEnvLevel.APP,app,"","");
                if (isValid){
                    LogPrinterUtils.logE("ipc","------------------->"+app+"开始补发粘性事件，requestId="+request.api().id());
                    serverMessenger.send(message);
                }

                iterator.remove();

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        IPCStorage.REMOTE_APP_STICKY_MESSAGES.put(app,messageList);
    }
    private static void unregisterServerMessenger(String app){
        IPCStorage.SERVER_MESSENGERS.remove(app);
    }
    private static void optClientMessengerToServer(Messenger serverMessenger, Message message){
        Bundle subBundle=new Bundle();
        subBundle.putString(IPCConst.BUNDLE_KEY_MESSENGER_APPNAME,ContextHolder.context.getPackageName());
        subBundle.putString(IPCConst.BUNDLE_KEY_MESSENGER_PROCESSNAME, AppInfoUtil.getCurrAppProccessName(ContextHolder.context));
        //subBundle.putSerializable(IPCConst.BUNDLE_KEY_REPLYCALLBACK,replyCallback);

        message.setData(subBundle);
        try{
            /*ClientHandler clientHandler=new ClientHandler();
            Messenger clientMessenger=new Messenger(clientHandler);*/
            message.replyTo=clientMessenger;
            serverMessenger.send(message);
            LogPrinterUtils.logE("ipc","------------------->向本App服务端操作客户端信使，操作类型："+message.what);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private static void optClientMessengerToOtherAppServer(String app, int mode){
        if (TextUtils.isEmpty(app)){
            return;
        }
        //Handler replyHandler=new ReplyHandler(replyCallback);
        Message subscribeMsg = Message.obtain();//Message.obtain(replyHandler);
        subscribeMsg.what= mode;
        Messenger serverMessenger=IPCStorage.SERVER_MESSENGERS.get(app);
        if (serverMessenger!=null && serverMessenger.getBinder().isBinderAlive()){
            optClientMessengerToServer(serverMessenger,subscribeMsg);
        }
    }
    public static void registerClientMessengerToApp(String app){
        optClientMessengerToOtherAppServer(app,IPCConst.REGISTER_CLIENT_MESSENGER);
    }


    public static void unregisterClientMessengerFromOtherApp(String app){
        optClientMessengerToOtherAppServer(app,IPCConst.UNREGISTER_CLIENT_MESSENGER);
    }


    /*********************************实例化操作********************************/
    protected IComponentRequest request;
    protected IComponentCallback callback;
    protected IComponentInstanceFactory factory;
    protected ITargets targetProcess;
    protected String targetApp;

    public IPCBridge request(IComponentRequest requestRemote){
        this.request=requestRemote;
        return this;
    }

    public IPCBridge callback(IComponentCallback callback){
        this.callback=callback;
        return this;
    }
    public IPCBridge factory(IComponentInstanceFactory factory){
        this.factory=factory;
        return this;
    }
    public IPCBridge targetApp(String app){
        this.targetApp=app;
        return this;
    }
    public IPCBridge targetProcess(ITargets targetProcess){
        this.targetProcess=targetProcess;
        return this;
    }

    public void remote(){
        if (request==null || targetProcess==null){
            return;
        }

        Messenger serverMessenger=IPCStorage.SERVER_MESSENGERS.get(targetApp);
        if (serverMessenger==null || serverMessenger.getBinder().pingBinder()){
            //首先尝试连接目标App,如果失败则根据粘性策略进行缓存
            boolean isConnected=new ServiceConnector().connect(targetApp, new IServiceConnectionCallback() {
                @Override
                public void onConnected(ComponentName name, Messenger serverMessenger) {
                    registerServerMessenger(name.getPackageName(), serverMessenger);
                    requestToRemoteByNonNullMessenger(serverMessenger,request,targetProcess,factory,callback);
                }

                @Override
                public void onDisconnected(ComponentName name) {
                    unregisterServerMessenger(name.getPackageName());
                }
            });
            if (!isConnected){
                //出于某些原因，目标app无法连接（例如，app包名不存在），则根据粘性策略开始存储
                IStickyStrategy sticky=request.api().stickyStrategy();
                if (sticky!=null){
                    Message messageToBeSent=createMessage(request,targetProcess,factory,callback);
                    sticky.put(IPCStickyEnvLevel.APP,targetApp,"","",messageToBeSent);
                }
            }
        }
        else {
            requestToRemoteByNonNullMessenger(serverMessenger,request,targetProcess,factory,callback);
        }

    }



























    


    




    /*private static void requestAllApp(@NonNull IComponentRequest request,@IComponentRequest.RequestModeIntDef int requestMode,IComponentCallback callback){
        Iterator<Map.Entry<String,Messenger>> iterator=IPCStorage.SERVER_MESSENGERS.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, Messenger> entry=iterator.next();
            String app=entry.getKey();
            requestRemoteWithTargetProcess(app,request, TargetsProcess.includeAllProcesses(),requestMode,callback);
        }
    }*/
    //private static ConcurrentHashMap<String, List<Message>> IPCStorage.REMOTE_APP_STICKY_MESSAGES=new ConcurrentHashMap<>();

   /* private static void requestRemoteWithTargetProcess(String app, @NonNull IComponentRequest request, @NonNull TargetsProcess targetProcess,@IComponentRequest.RequestModeIntDef int requestMode,IComponentCallback callback){
        if (request==null || targetProcess==null){
            return;
        }
        Messenger serverMessenger=IPCStorage.SERVER_MESSENGERS.get(app);
        if (serverMessenger==null){
            //首先尝试连接目标App,如果失败则根据粘性策略进行缓存
            boolean isConnected=new ServiceConnector().connect(app, new IServiceConnectionCallback() {
                @Override
                public void onConnected(ComponentName name, Messenger serverMessenger) {
                    registerServerMessenger(name.getPackageName(), serverMessenger);
                    requestToRemoteByNonNullMessenger(serverMessenger,request,targetProcess,requestMode,callback);
                }

                @Override
                public void onDisconnected(ComponentName name) {
                    unregisterServerMessenger(name.getPackageName());
                }
            });
            if (!isConnected){
                //根据粘性策略开始存储
                IStickyStrategy sticky=request.stickyStrategy();
                if (sticky!=null){
                    Message messageToBeSent=createMessage(request,targetProcess,requestMode,callback);
                    sticky.put(IPCStickyEnvLevel.APP,app,"","",messageToBeSent);
                }
            }
        }
        else {
            requestToRemoteByNonNullMessenger(serverMessenger,request,targetProcess,requestMode,callback);
        }

    }*/



    private  Message createMessage(
            @NonNull IComponentRequest  request,
            @NonNull ITargets targetProcess,
            IComponentInstanceFactory factory,
            IComponentCallback callback
    ){
        Bundle bundle = new Bundle();
        LogPrinterUtils.logE("ipc","------------------->将远程请求生产message:"+request.outputContent());
        //bundle.setClassLoader(getClass().getClassLoader());
        bundle.putParcelable(IPCConst.BUNDLE_KEY_COMPONENT_REQUEST,request);
        bundle.putParcelable(IPCConst.BUNDLE_KEY_COMPONENT_TARGETS,targetProcess);
        bundle.putSerializable(IPCConst.BUNDLE_KEY_COMPONENT_FACTORY,factory);
        Message msg = Message.obtain();
        msg.what=IPCConst.POST_REQUEST;
        msg.setData(bundle);
        /*ClientHandler clientHandler=new ClientHandler();
        clientHandler.setClientCallback(callback);
        Messenger clientMessenger=new Messenger(clientHandler);
        msg.replyTo=clientMessenger;*/
        Handler handler=ResultReceiverHandler.newInstance(callback);
        Messenger replyMessenger=new Messenger(handler);
        msg.replyTo=replyMessenger;
        return msg;
    }

    private void requestToRemoteByNonNullMessenger(
            Messenger serverMessenger,
            @NonNull IComponentRequest  request,
            @NonNull ITargets targetProcess,
            IComponentInstanceFactory factory,
            IComponentCallback callback){

        try {
            LogPrinterUtils.logE("ipc","------------------->马上调用主服务端");
            serverMessenger.send(createMessage(request,targetProcess,factory,callback));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //public static void requestRemote(@NonNull IComponentRequest request, @NonNull Map<String,TargetsProcess> remoteAppMap, IComponentCallback callback){
        /*if (request==null || targetRemote==null){
            return;
        }
        if (targetRemote.all()){
            requestAllApp(request,requestMode,callback);
        }
        else {
            //遍历目标
            HashMap<String,TargetsProcess> remoteAppMap = targetRemote.getTable();
            Iterator<Map.Entry<String,TargetsProcess>> iterator=remoteAppMap.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<String,TargetsProcess> entry=iterator.next();
                String app=entry.getKey();
                TargetsProcess processSpec=entry.getValue();
                requestRemoteWithTargetProcess(app,request,processSpec,requestMode,callback);
            }
        }*/

    //}

}
