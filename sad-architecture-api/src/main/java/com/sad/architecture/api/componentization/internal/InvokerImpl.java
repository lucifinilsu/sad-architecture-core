package com.sad.architecture.api.componentization.internal;

import android.os.Handler;
import android.os.Looper;
import com.sad.architecture.api.init.LogPrinterUtils;

import com.sad.architecture.annotation.TheradEnvironment;
import com.sad.architecture.api.componentization.IComponent;
import com.sad.architecture.api.componentization.IComponentCallback;
import com.sad.architecture.api.componentization.IComponentInstanceFactory;
import com.sad.architecture.api.componentization.IComponentInterceptor;
import com.sad.architecture.api.componentization.IComponentRequestInterceptorChain;
import com.sad.architecture.api.componentization.IComponentNamesGetter;
import com.sad.architecture.api.componentization.IComponentRequest;
import com.sad.architecture.api.componentization.IComponentResponse;
import com.sad.architecture.api.componentization.IInvoker;
import com.sad.architecture.api.componentization.INotifier;
import com.sad.architecture.api.componentization.IRequester;
import com.sad.architecture.api.componentization.ITargetLocal;
import com.sad.architecture.api.componentization.ITargets;
import com.sad.architecture.api.componentization.IVisitor;
import com.sad.architecture.api.componentization.impl.TargetsLocalImpl;
import com.sad.architecture.api.componentization.ipc.ComponentSticky;
import com.sad.architecture.api.componentization.ipc.IPCBridge;
import com.sad.architecture.api.componentization.ipc.IPCStickyEnvLevel;
import com.sad.architecture.api.componentization.ipc.IStickyStrategy;
import com.sad.architecture.api.init.ContextHolder;
import com.sad.basic.utils.app.AppInfoUtil;
import com.sad.core.async.SADExecutor;
import com.sad.core.async.SADHandlerAssistant;
import com.sad.core.async.SafeDispatchHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Created by Administrator on 2019/5/9 0009.
 */

public class InvokerImpl implements IInvoker {

    protected InvokerImpl(){}

    @Override
    public void invokeVisitors(List<IVisitor.Api> visitorApiGetters) {
        for (IVisitor.Api api:visitorApiGetters
                ) {
            try {
                LogPrinterUtils.logE("ipc","------------------->处理Visitor");
                handleVisitor(api);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleVisitor(IVisitor.Api visitorApi) throws Exception {
        IRequester.Api requesterApi=visitorApi.requesterApi();
        if (requesterApi==null){
            LogPrinterUtils.logE("ipc","------------------->请求Api为空");
            return;
        }

        Map<String,ITargets> targetsMap=requesterApi.targetsMap();
        Iterator<Map.Entry<String,ITargets>> iterator=targetsMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String,ITargets> entry=iterator.next();
            String toApp=entry.getKey();
            ITargets targets=entry.getValue();
            if (targets==null){
                continue;
            }

            LogPrinterUtils.logE("ipc","------------------->Visitor的App目标："+toApp);

            handleByApp(toApp,targets,visitorApi);
        }
    }

    private void handleByApp(String app,ITargets targets,IVisitor.Api visitorApi)throws Exception{
        if (!ContextHolder.context.getPackageName().equals(app)){
            remoteTo(app,targets,visitorApi);
        }
        else{
            remoteToCurrApp(targets,visitorApi);
        }

    }


    private void remoteTo(String app, ITargets targets,IVisitor.Api visitorApi){
        //如果targets里的map没有内容，即没有写明目标App的具体进程目标，则执行远程没有意义
        if (!targets.api().allProcess() && targets.api().processes().isEmpty()){
            LogPrinterUtils.logE("ipc","------------------->目标App:"+app+"没有明确的目标进程");
            return ;
        }
        IRequester.Api api=visitorApi.requesterApi();
        IPCBridge.newInstance()
                .targetApp(app)
                .request(api.request())
                .factory(visitorApi.factory())
                .callback(api.callback())
                .targetProcess(targets)
                .remote();

    }

    private void remoteToCurrApp(ITargets targets,IVisitor.Api visitorApi)throws Exception{
        IRequester.Api api=visitorApi.requesterApi();
        if (targets.api().allProcess()){
            LogPrinterUtils.logE("ipc","------------------->Visitor的目标是当前App所有进程");
            //当前App的所有进程
            //本地
            ITargetLocal targetLocal= TargetsLocalImpl.newInstance().allLocal(true);
            local(targetLocal,visitorApi);
            //远程
            remoteTo(ContextHolder.context.getPackageName(),targets,visitorApi);
            ;

        }
        else{
            Map<String,ITargetLocal> map=targets.api().processes();
            LogPrinterUtils.logE("ipc","------------------->Visitor的精准Process目标："+map);
            ITargetLocal localProcess=map.get(AppInfoUtil.getCurrAppProccessName(ContextHolder.context));
            if (localProcess!=null){
                //本地进程
                local(localProcess,visitorApi);
            }
            //当前App其他进程
            targets.removeProcess(AppInfoUtil.getCurrAppProccessName(ContextHolder.context));
            LogPrinterUtils.logE("ipc","------------------->Visitor的剩余其他进程目标："+targets.api().processes());
            if (!targets.api().processes().isEmpty()){
                remoteToCurrAppOtherProcess(targets,visitorApi);
            }

        }


    }

    private void remoteToCurrAppOtherProcess(ITargets targets,IVisitor.Api visitorApi)throws Exception{
        remoteTo(ContextHolder.context.getPackageName(),targets,visitorApi);
    }

    private void local(ITargetLocal targetLocal,IVisitor.Api visitorApi)throws Exception{
        handleLocal(targetLocal,visitorApi);
    }


    private void handleLocal(ITargetLocal targetLocal,IVisitor.Api visitorApi) throws Exception {
        IRequester.Api api=visitorApi.requesterApi();
        List<IComponentInterceptor> interceptors=api.interceptors();
        IComponentRequest request=api.request();
        IComponentCallback callback=api.callback();
        IComponentNamesGetter namesGetter=visitorApi.factory();
        Executor executor=api.singleThreadExecutor();
        if (targetLocal!=null){
            Collection<String> componentNames=new ArrayList<>();
            if (targetLocal.api().allLocal()){
                componentNames=namesGetter.names();
            }
            else{
                componentNames=targetLocal.api().locals();
            }
            List<IComponent> components=new ArrayList<>();
            for (String sn:componentNames
                    ) {
                List subscribers=visitorApi.require(sn);
                LogPrinterUtils.logE("ipc","------------------->组件实例仓库中name="+sn+"的实例组："+subscribers);
                if (subscribers==null || subscribers.isEmpty()){
                    //sn对应的实例组不存在，有可能是因为名为sn的组件尚未注册进动态订阅者集合里，则根据粘性策略做相应选择
                    cacheSticky(request,visitorApi.factory(),sn);
                    continue;
                }
                for (Object subscriber:subscribers
                     ) {
                    if (!(subscriber instanceof IComponent)){
                        continue;
                    }
                    if (subscriber!=null){
                        components.add((IComponent) subscriber);
                    }
                    /*else {
                        //名为sn的组件尚未注册进动态订阅者集合里，则根据粘性策略做相应选择
                        cacheSticky(request,visitorApi.factory(),sn);
                    }*/

                }
            }
            Collections.sort(components);
            LogPrinterUtils.logE("ipc","------------------->本地事件批量执行:"+componentNames);
            for (IComponent componentTest:components){
                LogPrinterUtils.logE("ipc","------------------->即将执行的组件:"+componentTest.getClass().getSimpleName());
            }
            for (IComponent component:components
                    ) {
                int threadMode=component.componentInfo().threadEnvironment();
                Looper sourceLooper=Looper.getMainLooper();
                boolean isAcrossProcess=false;
                if (request!=null){
                    isAcrossProcess=!currIs(request.api().fromApp(),request.api().fromProcess());
                    sourceLooper=request.api().looper();
                }

                switch (threadMode){
                    case TheradEnvironment.MAIN:
                        LogPrinterUtils.logE("ipc","------------------->主线程执行:"+component.componentInfo().name()+",request="+request);
                        callInMainThread(component,request,interceptors,callback);

                        break;
                    case TheradEnvironment.BACKGROUND:
                        LogPrinterUtils.logE("ipc","------------------->后台主线程执行:"+component.componentInfo().name());
                        if (sourceLooper!=null &&  Looper.getMainLooper()!= sourceLooper && !isAcrossProcess){
                            //如果事件不是跨进程的，且来源的线程不在主线程则处理环境应该和其在同一线程中
                            callInSourceLooper(component,request,interceptors,callback,sourceLooper);
                        }
                        else{
                            //否则，新开一个子线程运行处理过程
                            callInSingleThread(component,request,interceptors,callback,executor);
                        }
                        break;
                    case TheradEnvironment.SINGLE:
                        LogPrinterUtils.logE("ipc","------------------->独立线程执行:"+component.componentInfo().name());
                        callInSingleThread(component,request,interceptors,callback,executor);
                        break;
                    case TheradEnvironment.SOURCE:
                        LogPrinterUtils.logE("ipc","------------------->来源线程执行:"+component.componentInfo().name());
                        if (sourceLooper!=null && !isAcrossProcess){
                            callInSourceLooper(component,request,interceptors,callback,sourceLooper);
                        }
                        else {
                            performance(component,request,interceptors,callback);
                        }
                        break;
                    default:
                        break;
                }
            }
        }

    }

    protected boolean isAcrossProcess(IComponentRequest request){
        if (request==null){
            return false;
        }
        return !AppInfoUtil.getCurrAppProccessName(ContextHolder.context).equals(request.api().fromProcess());
    }

    protected static boolean isCurrentApp(String app){
        return ContextHolder.context.getPackageName().equals(app);
    }
    protected static boolean isCurrentProcess(String process){
        return AppInfoUtil.getCurrAppProccessName(ContextHolder.context).equals(process);
    }

    private boolean currIs(String app,String process){
        return isCurrentApp(app) && isCurrentProcess(process);
    }

    /*private void visitInThreadEnv(IComponentRequest.IComponentRequestLocal request, IComponent component, IComponentCallback callback) throws Exception{

    }*/

    private void callInMainThread(IComponent appComponent,
                                  IComponentRequest request,
                                  List<IComponentInterceptor> interceptors,
                                  IComponentCallback callback){

        if (Looper.myLooper() != Looper.getMainLooper()){
            SADHandlerAssistant.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        performance(appComponent,request,interceptors,callback);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        else {
            try {
                performance(appComponent,request,interceptors,callback);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void callInSourceLooper(IComponent appComponent,
                                    IComponentRequest  request,
                                    List<IComponentInterceptor> interceptors,
                                    IComponentCallback callback,
                                    Looper sourceLooper){
        if (sourceLooper!=null){
            Handler handler = new SafeDispatchHandler(sourceLooper);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        performance(appComponent,request,interceptors,callback);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        else{
            callInMainThread(appComponent,request,interceptors,callback);
        }


    }

    private void callInSingleThread(IComponent appComponent,
                                    IComponentRequest request,
                                    List<IComponentInterceptor> interceptors,
                                    IComponentCallback callback,
                                    Executor executor){
        if (executor==null){
            executor=new SADExecutor()
            ;
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    performance(appComponent,request,interceptors,callback);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 缓存粘性请求
     * @param componentName
     */
    private void cacheSticky(IComponentRequest request, IComponentInstanceFactory factory, String componentName){
        //根据粘性策略选择是否存储粘性请求
        if (request!=null){
            IStickyStrategy stickyStrategy=request.api().stickyStrategy();
            if (stickyStrategy!=null){
                stickyStrategy.put(
                        IPCStickyEnvLevel.COMPONENT,
                        ContextHolder.context.getPackageName(),
                        AppInfoUtil.getCurrAppProccessName(ContextHolder.context),
                        componentName,
                        new ComponentSticky(request,factory)
                );
            }
        }
    }


    private IComponentResponse performance(
            IComponent appComponent,
            IComponentRequest request,
            List<IComponentInterceptor> interceptors,
            IComponentCallback callback
    ) throws Exception{
        boolean isAcr=isAcrossProcess(request);
        LogPrinterUtils.logE("ipc","------------------->进入拦截器之前:"+appComponent.getClass().getSimpleName());
        LogPrinterUtils.logE("ipc","------------------->是否跨进程："+isAcr);
        IComponentInterceptor internalStartinterceptor=new ComponentInterceptorInternalOriginImpl(callback);
        IComponentInterceptor internalEndInterceptor=new ComponentInterceptorInternalTerminalImpl(appComponent);
        //LogPrinterUtils.logE("ipc","------------------->终端拦截器内存地址(外)："+internalEndInterceptor);
        Collections.sort(interceptors);
        List<IComponentInterceptor> interceptorsNewInstances=new ArrayList<>();
        interceptorsNewInstances.addAll(interceptors);
        interceptorsNewInstances.add(0,internalStartinterceptor);
        interceptorsNewInstances.add(internalEndInterceptor);
        int start=0;
        INotifier notifier=NotifierImpl.newInstance(interceptorsNewInstances)
                .request(request)
                ;
        ComponentRequestInterceptorChainImpl requestChain=new ComponentRequestInterceptorChainImpl(interceptorsNewInstances,start,notifier);
        requestChain.setRequest(request);
        return requestChain.proceedRequest(request);
    }




    /*private void handleRemote(IVisitor.Api visitor){
        IComponentInstanceFactory factory=visitor.factory();
        IRequester.IRequesterRemote.IRequesterRemoteApiGetter requestRemoteApiGetter=visitor.requesterApi();
        IComponentRequest.IComponentRequestRemote request=requestRemoteApiGetter.request();
        LogPrinterUtils.logE("ipc","------------------->Requester调用IPCBridge发送远程事件："+request);
        //IPCBridge.requestRemote(request,targetRemote,requestMode(),callback);
        Map<String ,TargetsProcess> targetProcessMap=requestRemoteApiGetter.targetRemoteMap();
        Iterator<Map.Entry<String,TargetsProcess>> iterator=targetProcessMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String,TargetsProcess> entry=iterator.next();
            String targetApp=entry.getKey();
            TargetsProcess targetProcess=entry.getValue();
            IPCBridge.newInstance()
                    .callback(visitor.requesterApi().callback())
                    .factory(factory)
                    .request(request)
                    .targetApp(targetApp)
                    .targetProcess(targetProcess)
                    .remote();
        }

    }*/
}
