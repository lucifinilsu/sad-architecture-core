package com.sad.architecture.api.componentization.internal;


import android.util.Log;

import com.sad.architecture.api.componentization.IComponent;
import com.sad.architecture.api.componentization.IComponentInterceptor;
import com.sad.architecture.api.componentization.IComponentRequestInterceptorChain;
import com.sad.architecture.api.componentization.IComponentRequest;
import com.sad.architecture.api.componentization.IComponentResponse;
import com.sad.architecture.api.componentization.IComponentResponseInterceptorChain;
import com.sad.architecture.api.componentization.INotifier;
import com.sad.architecture.api.componentization.IResult;
import com.sad.architecture.api.componentization.impl.ComponentResponseImpl;
import com.sad.architecture.api.componentization.impl.ResultImpl;

/**
 * Created by Administrator on 2019/1/21 0021.
 */

public class ComponentInterceptorInternalTerminalImpl implements IComponentInterceptor {
    private IComponent appComponent;
    public ComponentInterceptorInternalTerminalImpl(IComponent appComponent){
        this.appComponent=appComponent;
        Log.e("ipc","------------------->终端拦截器初始化时组件:"+appComponent.getClass().getSimpleName());
    }

    @Override
    public IComponentResponse OnRequestIntercepted(IComponentRequestInterceptorChain chain, INotifier notifier) throws Exception {
        IResult result= ResultImpl.asUnworked();
        IComponentRequest request=chain.request();
        if (appComponent==null){
            result=ResultImpl.asException().exceptionString("AppComponent is null !!!");
        }
        else {
            Log.e("ipc","------------------->组件执行前夕:"+appComponent.getClass().getSimpleName());
            result= appComponent.onComponentResponse(request,notifier);

        }

        IComponentResponse response=ComponentResponseImpl.newCreator()
                .cancelable(appComponent)
                .body(result)
                .create();
        return response;
    }


    @Override
    public void onResponseIntercepted(IComponentResponseInterceptorChain chain) throws Exception {
        chain.proceedResponse(chain.response());
    }

    @Override
    public int interceptorPriority() {
        return Integer.MIN_VALUE;
    }

    @Override
    public String description() {
        return "";
    }

    @Override
    public int id() {
        return interceptorPriority();
    }



    /************************************************************************************************/



}
