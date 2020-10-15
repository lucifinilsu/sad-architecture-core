package com.sad.architecture.api.componentization.extension.router.activity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;

import com.sad.architecture.api.componentization.extension.router.basic.IRouterParams;
import com.sad.architecture.api.utills.IDebugPrinter;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/5/6 0006.
 */

public interface IActivityRouterParams<IR extends IActivityRouterParams<IR>> extends IRouterParams<IR>,Serializable,IDebugPrinter,Parcelable{

    IR transition(int inTransition,int outTransition);

    IR transitionBundle(Bundle transitionBundle);

    IR requestCode(int requestCode);

    IR forResult(boolean forResult);

    IR forResultByCallback(boolean forResultByCallback);

    IR resultCallback(ActivityResultCallback<ActivityResult> resultCallback);

    @Override
    IRouterActivityParamsApiGetter routerParamsApiGetter();

    interface IRouterActivityParamsApiGetter extends IRouterParamsApiGetter,Serializable{

        int inTransition();

        int outTransition();

        Bundle transitionBundle();

        int requestCode();

        boolean forResult();

        boolean forResultByCallback();

        ActivityResultCallback<ActivityResult> resultCallback();

    }


    /*interface IRouterActivityParamsRemote<IR extends IRouterActivityParamsRemote<IR>> extends IActivityRouterParams<IR>,Serializable,Parcelable{

        @Override
        IRouterActivityParamsRemoteApiGetter routerParamsApiGetter();

        @Override
        default int describeContents() {
            return IActivityRouterParams.super.describeContents();
        }

        @Override
        default void writeToParcel(Parcel dest, int flags) {
            IActivityRouterParams.super.writeToParcel(dest,flags);
        }

        interface IRouterActivityParamsRemoteApiGetter extends IRouterActivityParamsApiGetter{

        }


    }

    interface IRouterActivityParamsLocal<IR extends IRouterActivityParamsLocal<IR>> extends IActivityRouterParams<IR>,Serializable {


        @Override
        IRouterActivityParamsLocalApiGetter routerParamsApiGetter();

        interface IRouterActivityParamsLocalApiGetter extends IRouterActivityParamsApiGetter{


        }

    }*/



    @Override
    default int describeContents() {
        return IRouterParams.super.describeContents();
    }

    @Override
    default void writeToParcel(Parcel dest, int flags) {
        IRouterParams.super.writeToParcel(dest,flags);
        dest.writeInt(routerParamsApiGetter().inTransition());
        dest.writeInt(routerParamsApiGetter().outTransition());
        dest.writeBundle(routerParamsApiGetter().transitionBundle());
        dest.writeInt(routerParamsApiGetter().forResult()?0:1);
        dest.writeInt(routerParamsApiGetter().requestCode());
        dest.writeInt(routerParamsApiGetter().forResultByCallback()?0:1);
        dest.writeValue(routerParamsApiGetter().resultCallback());
    }

    @Override
    default void readFromParcel(Parcel source) {
        IRouterParams.super.readFromParcel(source);
        transition(source.readInt(),source.readInt());
        bundle(source.readBundle());
        forResult(source.readInt()==0);
        requestCode(source.readInt());
        forResultByCallback(source.readInt()==0);
        resultCallback((ActivityResultCallback<ActivityResult>) source.readValue(getClass().getClassLoader()));
    }

    @Override
    default String outputContent(){
        String s="action="+routerParamsApiGetter().action()
                +",flags="+routerParamsApiGetter().flags()
                +",bundle="+routerParamsApiGetter().bundle()
                +",uri="+routerParamsApiGetter().uri()
                +",targetPkg="+routerParamsApiGetter().targetPackage()
                +",ti="+routerParamsApiGetter().inTransition()
                +",to="+routerParamsApiGetter().outTransition()
                +",tb="+routerParamsApiGetter().transitionBundle()
                +",fr="+routerParamsApiGetter().forResult()
                +",rc="+routerParamsApiGetter().requestCode()
                ;
        return s;
    };
}
