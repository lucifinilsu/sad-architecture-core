package com.sad.architecture.api.componentization;


import java.io.Serializable;

/**
 * Created by Administrator on 2018/8/31 0031.
 */
public interface IComponentResponseInterceptorChain extends Serializable{

    public void proceedResponse(IComponentResponse response) throws Exception;

    default public IComponentResponse response()throws Exception {throw new Exception("u have not override mathod 'response',or not ensure response is not null.");};

    default public IComponentRequest request()throws Exception {throw new Exception("u have not override mathod 'request',or not ensure request is not null.");};

}
