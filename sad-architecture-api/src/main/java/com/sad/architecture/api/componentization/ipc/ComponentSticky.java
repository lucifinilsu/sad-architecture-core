package com.sad.architecture.api.componentization.ipc;

import com.sad.architecture.api.componentization.IComponentInstanceFactory;
import com.sad.architecture.api.componentization.IComponentRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/4/3 0003.
 */

public class ComponentSticky {
    private List<String> processed=new ArrayList<>();
    private IComponentRequest request;
    private IComponentInstanceFactory factory;
    //private @IComponentRequest.RequestModeIntDef int requestMode=IComponentRequest.CALLER;

    public ComponentSticky(IComponentRequest request,IComponentInstanceFactory factory/*,@IComponentRequest.RequestModeIntDef int requestMode*/){
        this.request=request;
        this.factory=factory;
        //this.requestMode=requestMode;
    }

    public IComponentRequest getRequest() {
        return request;
    }

    public void setRequest(IComponentRequest request) {
        this.request = request;
    }

    public IComponentInstanceFactory getFactory() {
        return factory;
    }

    public void setFactory(IComponentInstanceFactory factory) {
        this.factory = factory;
    }

    /*public @IComponentRequest.RequestModeIntDef int getRequestMode() {
        return requestMode;
    }

    public void setRequestMode(@IComponentRequest.RequestModeIntDef int requestMode) {
        this.requestMode = requestMode;
    }*/
}
