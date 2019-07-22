package com.sad.architecture.api.componentization.impl;

import com.sad.architecture.annotation.ComponentResponse;
import com.sad.architecture.api.componentization.ICancelable;
import com.sad.architecture.api.componentization.IComponentRequest;
import com.sad.architecture.api.componentization.IComponentResponse;
import com.sad.architecture.api.componentization.IResult;
import com.sad.architecture.api.componentization.IVisitor;
import com.sad.architecture.api.componentization.impl.Result;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/3/25 0025.
 */

public class ComponentResponseImpl implements IComponentResponse,Serializable {
    private IResult result;
    private ICancelable cancelable;
    private String sourceName="";
    //private int requestMode=IVisitor.CALLER;

    private ComponentResponseImpl(Builder builder){
        this.result=builder.result;
        this.cancelable=builder.cancelable;
        this.sourceName=builder.sourceName;
        //this.requestMode=builder.requestMode;
    }

    public static Builder newBuilder(){
        return new Builder();
    }

    protected ComponentResponseImpl(String sourceName, ICancelable cancelable, IResult result){
        this.result=result;
        this.cancelable=cancelable;
        this.sourceName=sourceName;
        //this.requestMode=requestMode;
    }

    public void setCancelable(ICancelable cancelable) {
        this.cancelable = cancelable;
    }

    public void setResult(IResult result) {
        this.result = result;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    @Override
    public IResult body() {
        return result;
    }

    @Override
    public boolean cancel(boolean isForce) throws Exception {
        if (cancelable !=null){
            return cancelable.cancel(isForce);
        }
        return false;
    }

    @Override
    public String sourceName() {
        return sourceName;
    }

    /*@Override
    public int requestMode() {
        return this.requestMode;
    }*/




    public static class Builder extends ResponseApi<Builder> implements Serializable{


        private Builder(){}

        public IComponentResponse build(){
            return new ComponentResponseImpl(this);
        }


    }

    public static class ResponseApi<A extends ResponseApi<A>> implements Serializable{
        protected IResult result;
        protected ICancelable cancelable;
        protected String sourceName="";
        //protected int requestMode=IVisitor.CALLER;
        public A result(IResult result){
            this.result=result;
            return (A) this;
        }

        public A cancelable(ICancelable cancelable){
            this.cancelable=cancelable;
            return (A) this;
        }
        public A sourceName(String sourceName){
            this.sourceName=sourceName;
            return (A) this;
        }
        /*public A requestMode(@IVisitor.RequestModeIntDef int requestMode){
            this.requestMode=requestMode;
            return (A) this;
        }*/
    }

}
