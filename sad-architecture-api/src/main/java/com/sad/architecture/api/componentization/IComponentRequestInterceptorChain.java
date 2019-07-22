package com.sad.architecture.api.componentization;


import java.io.Serializable;

/**
 * Created by Administrator on 2018/8/31 0031.
 */
public interface IComponentRequestInterceptorChain extends Serializable{

    public <T extends IComponentResponse> T proceedRequest(IComponentRequest request)  throws Exception;

    default public IComponentRequest request()throws Exception {throw new Exception("u have not override mathod 'request',or not ensure request is not null.");};

}
