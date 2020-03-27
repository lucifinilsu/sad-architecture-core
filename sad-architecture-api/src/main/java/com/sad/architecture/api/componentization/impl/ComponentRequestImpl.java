package com.sad.architecture.api.componentization.impl;

import android.annotation.SuppressLint;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.sad.architecture.api.componentization.IComponentRequest;
import com.sad.architecture.api.componentization.ipc.IStickyStrategy;
import com.sad.architecture.api.componentization.ipc.StickyStrategyWithDeadlineImpl;
import com.sad.architecture.api.init.ContextHolder;
import com.sad.basic.utils.app.AppInfoUtil;


/**
 * Created by Administrator on 2019/3/22 0022.
 */

public class ComponentRequestImpl implements IComponentRequest<ComponentRequestImpl>,IComponentRequest.Api {

    private long id=0L;
    private Object body;
    private String fromApp="";
    private String fromProcess="";
    private IStickyStrategy stickyStrategy=new StickyStrategyWithDeadlineImpl(5000,5000,5000);
    private Looper looper;
    private @RequestSource int requestSource=REQUEST_SOURCE_STANARD;


    public static IComponentRequest newInstance(){
        return new ComponentRequestImpl();
    }

    protected ComponentRequestImpl(){
        if (TextUtils.isEmpty(fromApp)){
            fromApp= ContextHolder.context.getPackageName();
        }
        if (TextUtils.isEmpty(fromProcess)){
            fromProcess= AppInfoUtil.getCurrAppProccessName(ContextHolder.context);
        }
    }

    public ComponentRequestImpl fromApp(String fromApp){
        this.fromApp=fromApp;
        return  this;
    }

    public ComponentRequestImpl fromProcess(String fromProcess){
        this.fromProcess=fromProcess;
        return  this;
    }

    @Override
    public ComponentRequestImpl body(Object body) {
        this.body=body;
        return this;
    }

    @Override
    public ComponentRequestImpl sourceLooper(Looper looper) {
        this.looper=looper;
        return this;
    }

    @Override
    public ComponentRequestImpl requestSource(int requestSouce) {
        this.requestSource=requestSouce;
        return this;
    }

    public ComponentRequestImpl stickyStrategy(IStickyStrategy stickyStrategy){
        this.stickyStrategy=stickyStrategy;
        return  this;
    }
    public ComponentRequestImpl id(long id){
        this.id=id;
        return  this;
    }


    @Override
    public Api api() {
        return this;
    }

    @Override
    public <C> C body() {
        return (C) this.body;
    }

    @Override
    public String fromApp() {
        return this.fromApp;
    }

    @Override
    public String fromProcess() {
        return this.fromProcess;
    }

    @Override
    public long id() {
        return this.id;
    }

    @Override
    public IStickyStrategy stickyStrategy() {
        return this.stickyStrategy;
    }

    @Override
    public Looper looper() {
        return this.looper;
    }

    @Override
    public int requestSouce() {
        return this.requestSource;
    }

    @Override
    public String outputContent() {
        return "body="+body()+",fromApp="+fromApp()+",fromProcess="+fromProcess()+",sticky=("+stickyStrategy()+"),id="+id();
    }

    public static final Parcelable.Creator<IComponentRequest> CREATOR = new Parcelable.Creator<IComponentRequest>(){

        @Override
        public IComponentRequest createFromParcel(Parcel source) {
            IComponentRequest componentRequest= new ComponentRequestImpl();
            componentRequest.readFromParcel(source);
            return componentRequest;
        }

        @Override
        public IComponentRequest[] newArray(int size) {
            return new ComponentRequestImpl[size];
        }
    };


}
