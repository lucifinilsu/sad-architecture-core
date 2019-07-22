package com.sad.architecture.api.componentization.internal;

import com.sad.architecture.api.componentization.IComponentInterceptor;
import com.sad.architecture.api.componentization.IComponentRequestInterceptorChain;
import com.sad.architecture.api.componentization.IComponentResponse;
import com.sad.architecture.api.componentization.IComponentResponseInterceptorChain;
import com.sad.architecture.api.componentization.INotifier;
import com.sad.architecture.api.componentization.IResult;
import com.sad.architecture.api.componentization.impl.ComponentResponseImpl;

import java.util.List;

/**
 * Created by Administrator on 2019/3/25 0025.
 */

public class NotifierImpl extends ComponentResponseImpl.ResponseApi<NotifierImpl>implements INotifier{


    public static NotifierImpl newInstance(List<IComponentInterceptor> interceptors){
        return new NotifierImpl(interceptors);
    }

    private NotifierImpl(List<IComponentInterceptor> interceptors){
        this.interceptors=interceptors;
    }
    private int currIndex=0;
    private List<IComponentInterceptor> interceptors;

    @Override
    public void notifyCallCompeleted(IResult result) {
        if (interceptors!=null){
            IComponentResponse componentResponse=ComponentResponseImpl.newBuilder()
                    .cancelable(cancelable)
                    .sourceName(sourceName)
                    .result(result)
                    .build();
            IComponentInterceptor interceptor=interceptors.get(this.currIndex);
            try {
                IComponentResponseInterceptorChain componentResponseInterceptorChain=new ComponentResponseInterceptorChainImpl(interceptors,this.currIndex);
                componentResponseInterceptorChain.proceedResponse(componentResponse);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    @Override
    public void interceptorIndex(int index) {
        this.currIndex=index;
    }

}
