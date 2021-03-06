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
    private IComponentRequest request;
    public ComponentResponseInterceptorChainImpl(List<IComponentInterceptor> interceptors, int currIndex,IComponentRequest request){
        this.interceptors=interceptors;
        this.currIndex=currIndex;
        this.request=request;
    }

    public void setRequest(IComponentRequest request) {
        this.request = request;
    }

    public void setResponse(IComponentResponse response) {
        this.response = response;
    }

    @Override
    public void proceedResponse(IComponentResponse response) throws Exception{
        this.response=response;
        if (currIndex>-1 && currIndex<interceptors.size()){
            IComponentInterceptor interceptor = interceptors.get(currIndex);
            ComponentResponseInterceptorChainImpl chain=new ComponentResponseInterceptorChainImpl(interceptors,currIndex-1,request);
            chain.setResponse(response);
            interceptor.onResponseIntercepted(chain);
        }
        else {
            throw new Exception("IComponentInterceptors's currIndex is invalid,it is "+currIndex);
        }

    }


    @Override
    public IComponentResponse response() throws Exception {
        return this.response;
    }

    /*@Override
    public IComponentRequest request() throws Exception {
        return this.request;
    }*/
}
