package com.sad.architecture.api.componentization;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/7/12 0012.
 */

public interface IComponentInterceptor extends Comparable<IComponentInterceptor>,Serializable{

    //请求拦截
    public <T extends IComponentResponse> T OnRequestIntercepted(IComponentRequestInterceptorChain chain, INotifier notifier) throws Exception;

    //异步响应拦截
    public void onResponseIntercepted(IComponentResponseInterceptorChain chain) throws Exception;

    default public int interceptorPriority(){
        return 0;
    };

    default public String description(){return "";};

    default public int id(){return 0;};

    @Override
    default int compareTo(@NonNull IComponentInterceptor o){return o.interceptorPriority()-this.interceptorPriority();};


}
