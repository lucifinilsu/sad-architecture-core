package com.sad.architecture.api.componentization.extension.router.basic;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.sad.architecture.api.componentization.IComponentRequest;
import com.sad.architecture.api.init.ContextHolder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/5/6 0006.
 */

public interface IRouterParams<I extends IRouterParams<I>> extends Serializable{

    I action(String action);

    I addflag(int flag);

    I addFlags(List<Integer> flags);

    I bundle(Bundle bundle);

    I targetPackage(String pkg);

    I uri(Uri uri);

    IRouterParamsApiGetter routerParamsApiGetter();

    IComponentRequest createRequest(int requestId);

    interface IRouterParamsApiGetter extends Serializable{

        String action();

        String targetPackage();

        int flag(int pos);

        List<Integer> flags();

        Bundle bundle();

        Uri uri();

        default Context context(){
            return ContextHolder.context;
        };
    }



    default int describeContents() {
        return 0;
    }


    default void writeToParcel(Parcel dest, int flags) {
        dest.writeString(routerParamsApiGetter().action());
        dest.writeList(routerParamsApiGetter().flags());
        dest.writeBundle(routerParamsApiGetter().bundle());
        dest.writeParcelable(routerParamsApiGetter().uri(),flags);
        dest.writeString(routerParamsApiGetter().targetPackage());
    }

    default void readFromParcel(Parcel source){
        action(source.readString());
        addFlags(source.readArrayList(ArrayList.class.getClassLoader()));
        bundle(source.readBundle());
        uri(source.readParcelable(Uri.class.getClassLoader()));
        targetPackage(source.readString());
    }


}
