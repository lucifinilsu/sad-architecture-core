package com.sad.architecture.api.componentization.internal;

import com.sad.architecture.api.componentization.IComponentInterceptor;
import com.sad.architecture.api.componentization.IComponentRequestInterceptorChain;
import com.sad.architecture.api.componentization.IComponentRequest;
import com.sad.architecture.api.componentization.IComponentResponse;
import com.sad.architecture.api.componentization.INotifier;

import java.util.List;

/**
 * Created by Administrator on 2019/1/21 0021.
 */

public class ComponentRequestInterceptorChainImpl implements IComponentRequestInterceptorChain {
    private List<IComponentInterceptor> interceptors;
    private int currIndex=0;
    private IComponentRequest request;
    private INotifier notifier;
    public ComponentRequestInterceptorChainImpl(List<IComponentInterceptor> interceptors, int currIndex,INotifier notifier){
        this.interceptors=interceptors;
        this.currIndex=currIndex;
        this.notifier=notifier;
        this.notifier.interceptorIndex(this.currIndex-1);
    }

    @Override
    public <T extends IComponentResponse> T proceedRequest(IComponentRequest request) throws Exception {
        this.request=request;
        T future=null;
        if (currIndex>-1 && currIndex<interceptors.size()){
            IComponentInterceptor interceptor = interceptors.get(currIndex);
            ComponentRequestInterceptorChainImpl chain=new ComponentRequestInterceptorChainImpl(interceptors,currIndex+1,this.notifier);
            chain.setRequest(request);
            future = interceptor.OnRequestIntercepted(chain,notifier);
        }
        else {
            throw new Exception("IComponentInterceptors's currIndex is invalid,it is "+currIndex);
        }

        return future;
    }

    public void setRequest(IComponentRequest request) {
        this.request = request;
    }

    @Override
    public IComponentRequest request() throws Exception {
        return this.request;
    }
}
