package com.sad.architecture.api;

import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import android.content.Context;
import androidx.annotation.NonNull;
import com.sad.architecture.api.init.LogPrinterUtils;

import com.sad.architecture.annotation.ComponentResponse;
import com.sad.architecture.annotation.NameUtils;
import com.sad.architecture.api.componentization.IComponentInstanceConstructor;
import com.sad.architecture.api.componentization.IComponentInstanceFactory;
import com.sad.architecture.api.componentization.IComponentRequest;
import com.sad.architecture.api.componentization.IRequester;
import com.sad.architecture.api.componentization.impl.AbstractDynamicComponent;
import com.sad.architecture.api.componentization.impl.ComponentRequestImpl;
import com.sad.architecture.api.componentization.impl.TargetsImpl;
import com.sad.architecture.api.componentization.impl.TargetsLocalImpl;
import com.sad.architecture.api.componentization.internal.ComponentInstanceFactoryCallerImpl;
import com.sad.architecture.api.componentization.internal.ComponentInstanceFactoryPosterImpl;
import com.sad.architecture.api.componentization.internal.ComponentsStorage;
import com.sad.architecture.api.componentization.internal.RequesterImpl;
import com.sad.architecture.api.componentization.ipc.ComponentSticky;
import com.sad.architecture.api.componentization.ipc.IPCBridge;
import com.sad.architecture.api.componentization.ipc.IPCStickyEnvLevel;
import com.sad.architecture.api.componentization.ipc.IPCStorage;
import com.sad.architecture.api.componentization.ipc.IStickyStrategy;
import com.sad.architecture.api.init.ContextHolder;
import com.sad.architecture.api.init.Initializer;
import com.sad.basic.utils.app.AppInfoUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2019/3/22 0022.
 */

public class SCore {

    /*public static void main(String[] s) throws Exception {

        //sss c = (sss) SCore.exposePostedInterface().require("sss");


        SCore.asRemote(ComponentRequestImpl.ComponentRequestRemoteImpl.newInstance(new Bundle()))
                .targetRemote("xxx.xx.xxxx",
                        TargetsProcess.newBuilder()
                        .add("yyy.yyyyy.yy",TargetsLocal.includeAllComponents())
                        .build()
                )
                .callback(new IComponentCallback() {
                    @Override
                    public void onComponentInvokeCompleted(IComponentResponse response) {

                    }
                })
                .visit(ComponentInstanceFactoryPosterImpl.newInstance())
                .launchNode()
                .incorporateLocal(ComponentRequestImpl.ComponentRequestLocalImpl.newInstance("本地请求"))
                .addInterceptors(null)
                .singleThreadExecutor(SADExecutor.createDefaultThreadPool())
                .targetLocal("name")
                .visit(ComponentInstanceFactoryCallerImpl.newInstance())
                .launchNode()

        ;

    }*/

    /*public static IRequester.IRequesterRemote asRemote(IComponentRequest.IComponentRequestRemote request){
        return RequesterImpl.RequesterRemoteImpl.newInstance(request);
    }

    public static IRequester.IRequesterLocal asLocal(IComponentRequest.IComponentRequestLocal request){
        return RequesterImpl.RequesterLocalImpl.newInstance(request);
    }



    public static IExposer exposePostedInterface() throws Exception {
        return ExposerImpl.newInstance(ComponentInstanceFactoryPosterImpl.newInstance());
    }

    public static IExposer.ICalledExposer exposeCalledInterface() throws Exception {
        return ExposerImpl.newInstance(ComponentInstanceFactoryCallerImpl.newInstance());
    }*/



    public static IRequester newRequester(IComponentRequest request){
        return RequesterImpl.newInstance(request);
    }

    public static <IC> IC exposerCalledInterface(String name,IComponentInstanceConstructor componentInstanceConstructor)throws Exception{
        List<IC> icList=RequesterImpl.newInstance(null)
                .visit(ComponentInstanceFactoryCallerImpl.newInstance())
                .addComponentConstructor(name,componentInstanceConstructor)
                .api()
                .require(name)
                ;
        if (icList == null || icList.isEmpty()) {
            throw new Exception("components named '"+name+"' you called is not found");
        }
        return icList.get(0);
    }
    public static <IC> IC exposerCalledInterface(String name)throws Exception{
        return exposerCalledInterface(name,null);
    }

    public static <IC> List<IC> exposerPostedInterface(String name,IComponentInstanceConstructor componentInstanceConstructor)throws Exception{
        List<IC> icList = RequesterImpl.newInstance(null)
                .visit(ComponentInstanceFactoryPosterImpl.newInstance())
                .addComponentConstructor(name,componentInstanceConstructor)
                .api()
                .require(name)
                ;
        if (icList == null || icList.isEmpty()) {
            throw new Exception("components named '"+name+"' you posted is not found");
        }
        return icList;
    }
    public static <IC> List<IC> exposerPostedInterface(String name)throws Exception{
        return exposerPostedInterface(name,null);
    }






    public static void init(Context context){
        Initializer.config(context)
        .init();
        ;
        IPCBridge.registerCurrClientMessengerToMainProcessServer();
    }
    public static <O> void registerReceiverHost(O host){
        registerReceiverHost(host,false);
    }
    public static <O> void registerReceiverHost(O host,boolean autoRecycleWhenHostIsLifecycleOwner){
        Class<?> cls=  host.getClass();
        String hostClsName=cls.getCanonicalName();
        Method[] methods=cls.getDeclaredMethods();
        for (Method method:methods
             ) {
            ComponentResponse componentResponse=method.getAnnotation(ComponentResponse.class);
            if (componentResponse!=null){
                String componentName=componentResponse.componentName();
                String dynamicComponentClsName= NameUtils.getDynamicComponentClassSimpleName(hostClsName,componentName,"$$");
                //生成实例对象
                try{
                    String className=cls.getPackage().getName()+"."+dynamicComponentClsName;
                    Class<AbstractDynamicComponent<O>> dc= (Class<AbstractDynamicComponent<O>>) Class.forName(className);
                    Constructor constructor=null;
                    try {
                        constructor=dc.getDeclaredConstructor(cls,ComponentResponse.class);
                    }catch (Exception e){
                        e.printStackTrace();
                        constructor=dc.getConstructor(cls,ComponentResponse.class);
                    }
                    constructor.setAccessible(true);
                    AbstractDynamicComponent<O> dynamicComponent= (AbstractDynamicComponent<O>) constructor.newInstance(host,componentResponse);
                    //存入集合
                    ComponentsStorage.registerComponentInstance(componentName+ComponentsStorage.COMPONENT_INSTANCE_MAP_KEY_SEPARATOR+host.hashCode(),dynamicComponent);
                    //检查粘性请求
                    supplementToSend(componentName);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        if (autoRecycleWhenHostIsLifecycleOwner && (host instanceof LifecycleOwner)){
            ((LifecycleOwner) host).getLifecycle().addObserver(new DefaultLifecycleObserver() {
                @Override
                public void onDestroy(@NonNull LifecycleOwner owner) {
                    unregisterReceiverHost(host);
                }
            });
        }


    }

    public static <O> void unregisterReceiverHost(O host){

        Class<?> cls=  host.getClass();
        String hostClsName=cls.getCanonicalName();
        Method[] methods=cls.getDeclaredMethods();

        for (Method method:methods
                ) {
            ComponentResponse componentResponse=method.getAnnotation(ComponentResponse.class);
            if (componentResponse!=null){
                String componentName=componentResponse.componentName();
                String dynamicComponentClsName= NameUtils.getDynamicComponentClassSimpleName(hostClsName,componentName,"$$");

                try{
                    LogPrinterUtils.logE("ipc","------------------->开始注销宿主"+hostClsName+"的事件接收器"+componentName);
                    ComponentsStorage.unregisterComponentInstance(componentName+ComponentsStorage.COMPONENT_INSTANCE_MAP_KEY_SEPARATOR+host.hashCode());


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 补发粘性事件
     */
    private static void supplementToSend(String componentName){
        //先检测

        LogPrinterUtils.logE("ipc","------------------->开始检测Local级别"+componentName+"是否存有粘性事件");
        List<ComponentSticky> stickyList= IPCStorage.obtainLocalStickyRequest(componentName);
        if (stickyList==null){
            LogPrinterUtils.logE("ipc","------------------->Local级别"+componentName+"无待发送的粘性事件列表");
            return;
        }
        LogPrinterUtils.logE("ipc","------------------->Local级别组件"+componentName+"需要处理粘性事件："+stickyList.size());
        Iterator<ComponentSticky> iterator=stickyList.iterator();
        while (iterator.hasNext()){
            ComponentSticky sticky=iterator.next();
            try {
                if (sticky==null || sticky.getRequest()==null){
                    iterator.remove();
                    continue;
                }
                IComponentRequest request=sticky.getRequest();
                //int requestMode=sticky.getRequestMode();
                IComponentInstanceFactory factory=sticky.getFactory();
                //检查粘性事件是否还有效
                IStickyStrategy stickyStrategy=sticky.getRequest().api().stickyStrategy();
                if (stickyStrategy==null){
                    iterator.remove();
                    continue;
                }
                boolean isValid=stickyStrategy.isValid(IPCStickyEnvLevel.COMPONENT,ContextHolder.context.getPackageName(), AppInfoUtil.getCurrAppProccessName(ContextHolder.context),componentName);
                if (isValid){
                    LogPrinterUtils.logE("ipc","------------------->"+componentName+"开始补发粘性事件，requestId="+sticky.getRequest().api().id());
                    //由于回调环境存在不确定性,为防止内存溢出，所以补发的信息不再监听回调
                    try {
                        RequesterImpl.newInstance(
                                ComponentRequestImpl.newInstance()
                                        .body(request.api().body())
                                        .id(request.api().id())
                                        .fromApp(request.api().fromApp())
                                        .requestSource(IComponentRequest.REQUEST_SOURCE_STICKY)
                                        .fromProcess(request.api().fromProcess())
                                        .stickyStrategy(request.api().stickyStrategy())
                        )
                                .setTargets(ContextHolder.context.getPackageName(),
                                        TargetsImpl.newInstance().addProcess(
                                                AppInfoUtil.getCurrAppProccessName(ContextHolder.context),
                                                TargetsLocalImpl.newInstance().addLocal(componentName)
                                        ))
                                .visit(factory)
                                .launchNode()
                                .submit();

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    /*IRequester requester=SCore.newRequester().request(request);
                    IRequester.IInvokerToLocal invoker=null;
                    if (IComponentRequest.CALLER == requestMode){
                        invoker=requester.asCaller();
                    }
                    else if(IComponentRequest.POSTER==requestMode){
                        invoker=requester.asPoster();
                    }
                    try {
                        //由于回调环境存在不确定性,为防止内存溢出，所以补发的信息不再监听回调
                        invoker.local(TargetsLocal.newBuilder().add(componentName).build(),null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/

                }
                else {
                    LogPrinterUtils.logE("ipc","------------------->Local级别组件"+componentName+"的粘性事件已经失效,来自进程："+request.api().fromProcess());
                }
                iterator.remove();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        IPCStorage.putLocalSticky(componentName,stickyList);
    }

    /*public static void registerComponentInstance(IComponent component){
        ComponentsStorage.registerComponentInstance(component.componentInfo().name(),component);
    }

    public static void unregisterComponentInstance(String name){
        ComponentsStorage.unregisterComponentInstance(name);
    }*/

}
