package com.sad.architecture.api.componentization.internal;

import com.sad.architecture.api.componentization.IComponentInterceptor;
import com.sad.architecture.api.componentization.IComponentRequest;
import com.sad.architecture.api.componentization.IComponentRequestInterceptorChain;
import com.sad.architecture.api.componentization.IComponentResponse;
import com.sad.architecture.api.componentization.IComponentResponseInterceptorChain;
import com.sad.architecture.api.componentization.INotifier;

import java.util.List;

/**
 * Created by Administrator on 2019/1/21 0021.
 */

public class ComponentResponseInterceptorChainImpl implements IComponentResponseInterceptorChain {
    private List<IComponentInterceptor> interceptors;
    private int currIndex=0;
    private IComponentResponse response;
    public ComponentResponseInterceptorChainImpl(List<IComponentInterceptor> interceptors, int currIndex){
        this.interceptors=interceptors;
        this.currIndex=currIndex;
    }
    @Override
    public void proceedResponse(IComponentResponse response) throws Exception{
        this.response=response;
        IComponentInterceptor interceptor = interceptors.get(currIndex);
        IComponentResponseInterceptorChain chain=new ComponentResponseInterceptorChainImpl(interceptors,currIndex-1);
        interceptor.onResponseIntercepted(chain);
    }


    @Override
    public IComponentResponse response() throws Exception {
        return this.response;
    }

}
