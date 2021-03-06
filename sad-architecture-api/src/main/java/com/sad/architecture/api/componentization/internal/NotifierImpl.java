package com.sad.architecture.api.componentization.internal;

import com.sad.architecture.api.componentization.IComponentInterceptor;
import com.sad.architecture.api.componentization.IComponentRequest;
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

public class NotifierImpl implements INotifier{
    public static NotifierImpl newInstance(List<IComponentInterceptor> interceptors){
        return new NotifierImpl(interceptors);
    }

    private NotifierImpl(List<IComponentInterceptor> interceptors){
        this.interceptors=interceptors;
    }
    private int currIndex=0;
    private List<IComponentInterceptor> interceptors;
    private IComponentRequest request;
    public NotifierImpl request(IComponentRequest request){
        this.request=request;
        return this;
    }

    @Override
    public IComponentResponse notifyCallCompeleted(IResult result) {
        IComponentResponse componentResponse=ComponentResponseImpl.newCreator()
                .cancelable(null)//INotifier本身是在已经完成任务的情况下的回调，所以取消方法并没有意义，置空。
                .body(result)
                .request(request)
                .create();
        if (interceptors!=null){
            IComponentInterceptor interceptor=interceptors.get(this.currIndex);
            try {
                ComponentResponseInterceptorChainImpl componentResponseInterceptorChain=new ComponentResponseInterceptorChainImpl(interceptors,this.currIndex,request);
                componentResponseInterceptorChain.setRequest(request);
                componentResponseInterceptorChain.setResponse(componentResponse);
                componentResponseInterceptorChain.proceedResponse(componentResponse);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return componentResponse;
    }

    @Override
    public void interceptorIndex(int index) {
        this.currIndex=index;
    }

}
