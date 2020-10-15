package com.sad.architecture.api.componentization.extension.router.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;

import com.sad.architecture.api.componentization.IComponentRequest;
import com.sad.architecture.api.componentization.extension.router.basic.AbstractRouterParamsImpl;
import com.sad.architecture.api.componentization.impl.ComponentRequestImpl;
import com.sad.basic.utils.app.AppInfoUtil;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/7/13 0013.
 */

public class ActivityRouterParams extends AbstractRouterParamsImpl<ActivityRouterParams> implements
        IActivityRouterParams<ActivityRouterParams>,
        IActivityRouterParams.IRouterActivityParamsApiGetter,
        Serializable
{

    protected int inTransition;
    protected int outTransition;
    protected Bundle transitionBundle;
    protected boolean forResult=false;
    protected int requestCode=0;
    protected boolean forResultByCallback=false;
    protected ActivityResultCallback<ActivityResult> resultCallback;

    private ActivityRouterParams(){}

    private ActivityRouterParams(Context context){
        this.context=context;
    }

    public static IActivityRouterParams newInstance(Context context){
        return new ActivityRouterParams(context);
    }

    @Override
    public ActivityRouterParams transition(int inTransition, int outTransition) {
        this.inTransition=inTransition;
        this.outTransition=outTransition;
        return this;
    }

    @Override
    public ActivityRouterParams transitionBundle(Bundle transitionBundle) {
        this.transitionBundle=transitionBundle;
        return this;
    }

    @Override
    public ActivityRouterParams requestCode(int requestCode) {
        this.requestCode=requestCode;
        return this;
    }

    @Override
    public ActivityRouterParams forResult(boolean forResult) {
        this.forResult=forResult;
        return this;
    }

    @Override
    public ActivityRouterParams forResultByCallback(boolean forResultByCallback) {
        this.forResultByCallback=forResultByCallback;
        return this;
    }

    @Override
    public ActivityRouterParams resultCallback(ActivityResultCallback<ActivityResult> resultCallback) {
        this.resultCallback=resultCallback;
        return this;
    }

    @Override
    public IActivityRouterParams.IRouterActivityParamsApiGetter routerParamsApiGetter() {
        return this;
    }

    @Override
    public IComponentRequest createRequest(int requestId) {
        return
                ComponentRequestImpl.newInstance()
                        .body(this)
                        .fromApp(this.context.getPackageName())
                        .fromProcess(AppInfoUtil.getCurrAppProccessName(this.context))
                        .id(requestId)
                        .sourceLooper(Looper.myLooper())
                ;
    }


    @Override
    public int inTransition() {
        return this.inTransition;
    }

    @Override
    public int outTransition() {
        return this.outTransition;
    }

    @Override
    public Bundle transitionBundle() {
        return this.transitionBundle;
    }

    @Override
    public int requestCode() {
        return this.requestCode;
    }

    @Override
    public boolean forResult() {
        return this.forResult;
    }

    @Override
    public boolean forResultByCallback() {
        return this.forResultByCallback;
    }

    @Override
    public ActivityResultCallback<ActivityResult> resultCallback() {
        return this.resultCallback;
    }

    public static final Parcelable.Creator<ActivityRouterParams> CREATOR = new Creator<ActivityRouterParams>()
    {
        @Override
        public ActivityRouterParams[] newArray(int size)
        {
            return new ActivityRouterParams[size];
        }

        @Override
        public ActivityRouterParams createFromParcel(Parcel in)
        {
            ActivityRouterParams remoteParams= new ActivityRouterParams();
            remoteParams.readFromParcel(in);
            return remoteParams;

        }
    };

}
