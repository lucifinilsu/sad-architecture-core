package com.sad.architecture.api.componentization;

import android.os.Parcel;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/3/22 0022.
 */

public interface IComponentResponse extends IBodyOwner,Serializable,ICancelable,IParcelable{

    //String sourceName();

    ICancelable cancelable();

    IComponentRequest request();

    Creator creator();

    @Override
    default boolean cancel(boolean isForce) throws Exception {
        if (cancelable()!=null){
            cancelable().cancel(isForce);
        }
        return false;
    }

    interface Creator{

        Creator body(Object body);

        Creator cancelable(ICancelable cancelable);

        //Creator sourceName(String sourceName);

        Creator request(IComponentRequest request);

        IComponentResponse create();

    }


    @Override
    default int describeContents(){return 0;};

    @Override
    default void writeToParcel(Parcel dest, int flags){
        dest.writeValue(body());
        dest.writeSerializable(cancelable());
        dest.writeParcelable(request(),flags);
    }

    @Override
    default void readFromParcel(Parcel in){
        creator()
                .body(in.readValue(getClass().getClassLoader()))
                .cancelable((ICancelable) in.readSerializable())
                .request(in.readParcelable(getClass().getClassLoader()))
        .create()
        ;
    }
}
