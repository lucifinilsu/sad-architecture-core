package com.sad.architecture.api.componentization.internal;

import com.sad.architecture.api.componentization.IComponentCallback;
import com.sad.architecture.api.componentization.IComponentInstanceConstructor;
import com.sad.architecture.api.componentization.IComponentInstanceFactory;
import com.sad.architecture.api.componentization.IComponentInterceptor;
import com.sad.architecture.api.componentization.IComponentRequest;
import com.sad.architecture.api.componentization.ILauncher;
import com.sad.architecture.api.componentization.IRequester;
import com.sad.architecture.api.componentization.ITargetLocal;
import com.sad.architecture.api.componentization.ITargets;
import com.sad.architecture.api.componentization.IVisitor;
import com.sad.architecture.api.componentization.impl.TargetsImpl;
import com.sad.architecture.api.componentization.impl.TargetsLocalImpl;
import com.sad.architecture.api.init.ContextHolder;
import com.sad.basic.utils.app.AppInfoUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Created by Administrator on 2019/4/24 0024.
 */

public class RequesterImpl implements IRequester<RequesterImpl>,IRequester.Api {

    protected IComponentRequest request;
    protected IComponentCallback callback;
    protected ILauncher launcher;
    protected Map<String, ITargets> targetsMap=new HashMap<>();
    protected List<IComponentInterceptor> interceptors=new ArrayList<>();
    protected Executor executor;


    public static IRequester newInstance(IComponentRequest request){
        return new RequesterImpl(request,null);
    }

    /*protected static RequesterImpl newInstance(IComponentRequest request, ILauncher launcher) {
        return new RequesterImpl(request,launcher);
    }*/

    protected RequesterImpl(IComponentRequest request,ILauncher launcher){
        this.launcher=launcher;
        this.request=request;
    }

    @Override
    public RequesterImpl callback(IComponentCallback callback) {
        this.callback=callback;
        return this;
    }

    @Override
    public RequesterImpl setTargets(String toApp, ITargets targets) {
        this.targetsMap.put(toApp,targets);
        return this;
    }

    @Override
    public RequesterImpl addTargets(String toApp, String toProcess, ITargetLocal targetLocal) {
        ITargets targets=this.targetsMap.get(toApp);
        if (targets==null){
            targets= TargetsImpl.newInstance();
        }
        if (!targets.api().allProcess()){
            targets.addProcess(toProcess,targetLocal);
        }
        return setTargets(toApp,targets);
    }

    @Override
    public RequesterImpl addTarget(String toApp, String toProcess, String componentName) {
        ITargets targets=this.targetsMap.get(toApp);
        if (targets==null){
            targets= TargetsImpl.newInstance();
        }
        if (!targets.api().allProcess()){
            ITargetLocal targetLocal=targets.api().processes().get(toProcess);
            if (targetLocal==null){
                targetLocal= TargetsLocalImpl.newInstance();
            }
            if (!targetLocal.api().allLocal()){
                targetLocal.addLocal(componentName);
            }
            targets.addProcess(toProcess,targetLocal);

        }
        return setTargets(toApp,targets);
    }

    @Override
    public RequesterImpl addCurrAppTargets(ITargets targets) {
        return setTargets(ContextHolder.context.getPackageName(),targets);
    }

    @Override
    public RequesterImpl addCurrProcessTargets(ITargetLocal targetLocal) {
        return addTargets(ContextHolder.context.getPackageName(), AppInfoUtil.getCurrAppProccessName(ContextHolder.context),targetLocal);
    }

    @Override
    public RequesterImpl addCurrProcessTarget(String componentName) {
        return addTarget(ContextHolder.context.getPackageName(), AppInfoUtil.getCurrAppProccessName(ContextHolder.context),componentName);
    }

    @Override
    public RequesterImpl addTargetsMap(Map<String, ITargets> targetsMap) {
        this.targetsMap.putAll(targetsMap);
        return this;
    }

    @Override
    public RequesterImpl interceptors(List<IComponentInterceptor> interceptors) {
        this.interceptors.addAll(interceptors);
        return this;
    }

    @Override
    public RequesterImpl addInterceptors(IComponentInterceptor interceptor) {
        this.interceptors.add(interceptor);
        return this;
    }

    @Override
    public RequesterImpl singleThreadExecutor(Executor executor) {
        this.executor=executor;
        return this;
    }


    @Override
    public IVisitor visit(IComponentInstanceFactory factory) {
        return new VisitorImpl(factory,this);
    }

    @Override
    public IComponentCallback callback() {
        return this.callback;
    }

    @Override
    public ILauncher getLauncher() {
        return this.launcher;
    }

    @Override
    public ITargets targets(String toApp) {
        return this.targetsMap.get(toApp);
    }

    @Override
    public Map<String, ITargets> targetsMap() {
        return this.targetsMap;
    }

    @Override
    public List<IComponentInterceptor> interceptors() {
        return this.interceptors;
    }

    @Override
    public Executor singleThreadExecutor() {
        return this.executor;
    }

    @Override
    public Api api() {
        return this;
    }

    @Override
    public IComponentRequest request() {
        return this.request;
    }




    /*public static class RequesterRemoteImpl extends RequesterImpl<RequesterRemoteImpl> implements IRequester.IRequesterRemote<RequesterRemoteImpl>,IRequester.IRequesterRemote.IRequesterRemoteApiGetter{

        protected Map<String, TargetsProcess> targetRemoteMap=new HashMap<>();

        public static IRequesterRemote newInstance(IComponentRequest.IComponentRequestRemote request){
            return new RequesterRemoteImpl(request,null);
        }

        protected RequesterRemoteImpl(IComponentRequest.IComponentRequestRemote request, ILauncher launcher) {
            super(request, launcher);
        }

        @Override
        public RequesterRemoteImpl targetRemote(String app, TargetsProcess targetProcess) {
            this.targetRemoteMap.put(app,targetProcess);
            return this;
        }

        @Override
        public IRequesterRemote.IRequesterRemoteApiGetter api() {
            return this;
        }

        @Override
        public IComponentRequest.IComponentRequestRemote request() {
            return (IComponentRequest.IComponentRequestRemote) super.request();
        }



        @Override
        public TargetsProcess targetRemote(String app) {
            return this.targetRemoteMap.get(app);
        }

        @Override
        public Map<String, TargetsProcess> targetRemoteMap() {
            return this.targetRemoteMap;
        }
    }


    public static class RequesterLocalImpl extends RequesterImpl<RequesterLocalImpl> implements IRequesterLocal<RequesterLocalImpl>,IRequesterLocal.IRequesterLocalApiGetter{


        protected TargetsLocal targetLocal;
        protected List<IComponentInterceptor> interceptors=new ArrayList<>();
        //protected IComponentPerformance performance=ComponentPerformanceImpl;
        protected Executor executor;


        @Override
        public IComponentRequest.IComponentRequestLocal request() {
            return (IComponentRequest.IComponentRequestLocal) this.request;
        }

        public static IRequesterLocal newInstance(IComponentRequest.IComponentRequestLocal request){
            return new RequesterLocalImpl(request,null);
        }

        protected RequesterLocalImpl(IComponentRequest.IComponentRequestLocal request, ILauncher launcher) {
            super(request, launcher);
        }

        @Override
        public RequesterLocalImpl interceptors(List<IComponentInterceptor> interceptors) {
            this.interceptors=interceptors;
            return this;
        }

        @Override
        public RequesterLocalImpl addInterceptors(IComponentInterceptor interceptor) {
            this.interceptors.add(interceptor);
            return this;
        }

        *//*@Override
        public RequesterLocalImpl performance(IComponentPerformance performance) {
            this.performance=performance;
            return this;
        }*//*

        @Override
        public RequesterLocalImpl addComponentConstructor(String name,IComponentInstanceConstructor componentInstanceConstructor) {
            this.constructorMap.put(name,componentInstanceConstructor);
            return this;
        }

        @Override
        public RequesterLocalImpl addComponentConstructors(Map<String, IComponentInstanceConstructor> constructorMap) {
            this.constructorMap.putAll(constructorMap);
            return this;
        }

        @Override
        public RequesterLocalImpl singleThreadExecutor(Executor executor) {
            this.executor=executor;
            return this;
        }

        @Override
        public List<IComponentInterceptor> interceptors() {
            return this.interceptors;
        }

        *//*@Override
        public IComponentPerformance performance() {
            return this.performance;
        }*//*

        @Override
        public Executor singleThreadExecutor() {
            return this.executor;
        }

        @Override
        public IRequesterLocalApiGetter api() {
            return this;
        }

        @Override
        public RequesterLocalImpl targetLocal(TargetsLocal targetLocal) {
            this.targetLocal=targetLocal;
            return this;
        }

        @Override
        public TargetsLocal targetLocal() {
            return this.targetLocal;
        }
    }*/

}
