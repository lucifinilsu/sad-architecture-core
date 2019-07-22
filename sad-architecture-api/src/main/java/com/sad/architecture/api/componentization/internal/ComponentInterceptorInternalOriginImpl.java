package com.sad.architecture.api.componentization.internal;

import com.sad.architecture.api.componentization.IComponentCallback;
import com.sad.architecture.api.componentization.IComponentInterceptor;
import com.sad.architecture.api.componentization.IComponentRequestInterceptorChain;
import com.sad.architecture.api.componentization.IComponentResponse;
import com.sad.architecture.api.componentization.IComponentResponseInterceptorChain;
import com.sad.architecture.api.componentization.INotifier;

public class ComponentInterceptorInternalOriginImpl implements IComponentInterceptor {
    private IComponentCallback callback;

    protected ComponentInterceptorInternalOriginImpl(IComponentCallback callback){
        this.callback=callback;
    }

    @Override
    public <T extends IComponentResponse> T OnRequestIntercepted(IComponentRequestInterceptorChain chain, INotifier notifier) throws Exception {
        return chain.proceedRequest(chain.request());
    }

    @Override
    public void onResponseIntercepted(IComponentResponseInterceptorChain chain) throws Exception {
        if (callback!=null){
            callback.onComponentInvokeCompleted(chain.response());
        }
    }

    @Override
    public int interceptorPriority() {
        return Integer.MAX_VALUE;
    }
}
