package com.sad.architecture.api.componentization;

import android.os.Parcel;


import com.sad.architecture.api.componentization.internal.ComponentInstanceFactoryCallerImpl;
import com.sad.architecture.api.componentization.internal.ComponentInstanceFactoryPosterImpl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Created by Administrator on 2019/3/28 0028.
 */

public interface IRequester<R extends IRequester<R>>{

    Api api();

    R callback(IComponentCallback callback);
    
    R setTargets(String toApp, ITargets targets);

    R addTargets(String toApp, String toProcess, ITargetLocal targetLocal);

    R addTarget(String toApp,String toProcess,String componentName);

    R addCurrAppTargets(ITargets targets);

    R addCurrProcessTargets(ITargetLocal targetLocal);

    R addCurrProcessTarget(String componentName);

    R addTargetsMap(Map<String, ITargets> targetsMap);

    R interceptors(List<IComponentInterceptor> interceptors);

    R addInterceptors(IComponentInterceptor interceptor);

    R singleThreadExecutor(Executor executor);

    IVisitor visit(IComponentInstanceFactory factory);

    default IVisitor post(){
        return visit(ComponentInstanceFactoryPosterImpl.newInstance());
    };

    default IVisitor call(){
        return visit(ComponentInstanceFactoryCallerImpl.newInstance());
    };

    interface Api extends Serializable{

        IComponentRequest request();

        IComponentCallback callback();

        ILauncher getLauncher();

        ITargets targets(String toApp);

        Map<String, ITargets> targetsMap();

        List<IComponentInterceptor> interceptors();

        Executor singleThreadExecutor();

    }
    

    /**
     * 远程
     * @param <R>
     *//*
    interface IRequesterRemote<R extends IRequesterRemote<R>> extends IRequester<R>{

        R targetRemote(String app, TargetsProcess targetProcess);

        @Override
        IRequesterRemoteApiGetter api();

        interface IRequesterRemoteApiGetter extends Api{

            TargetsProcess targetRemote(String app);

            Map<String,TargetsProcess> targetRemoteMap();

            @Override
            IComponentRequest.IComponentRequestRemote request();
        }

    }

    *//**
     * 本地
     * @param <R>
     *//*
    interface IRequesterLocal<R extends IRequesterLocal<R>> extends IRequester<R>{


        R targetLocal(TargetsLocal targetLocal);


        default R targetLocal(String... name){
            if (name!=null && name.length>0){
                List<String> names= Arrays.asList(name);
                return targetLocal(names);
            }
            return targetLocal(new ArrayList<String>());
        }

        default R targetLocal(List<String> names){
            TargetsLocal targetLocal = TargetsLocal.newBuilder().build();
            if (api()!=null){
                targetLocal= api().targetLocal();
                TargetsLocal.Builder builder= TargetsLocal.newBuilder().addAll(names);
                if (targetLocal!=null){
                    builder.addAll(targetLocal.getTable());
                }
                return targetLocal(builder.build());
            }

            return targetLocal(targetLocal);
        }

        R interceptors(List<IComponentInterceptor> interceptors);

        R addInterceptors(IComponentInterceptor interceptor);

        *//*R performance(IComponentPerformance performance);*//*

        R addComponentConstructor(String name,IComponentInstanceConstructor componentInstanceConstructor);

        R addComponentConstructors(Map<String,IComponentInstanceConstructor> constructorMap);

        @Override
        IRequesterLocal.IRequesterLocalApiGetter api();

        R singleThreadExecutor(Executor executor);

        interface IRequesterLocalApiGetter extends Api {

            List<IComponentInterceptor> interceptors();

            *//*IComponentPerformance performance();*//*

            Executor singleThreadExecutor();

            TargetsLocal targetLocal();

            @Override
            IComponentRequest.IComponentRequestLocal request();


        }
    }*/


}
