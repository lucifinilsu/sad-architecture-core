package com.sad.architecture.api.componentization;

import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.IntDef;

import com.sad.architecture.api.componentization.ipc.IStickyStrategy;
import com.sad.architecture.api.utills.IDebugPrinter;

import java.io.Serializable;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by Administrator on 2019/3/22 0022.
 */

public interface IComponentRequest<R extends IComponentRequest<R>> extends IParcelable, IDebugPrinter, Serializable {
    int REQUEST_SOURCE_STANARD=0;
    int REQUEST_SOURCE_STICKY=1;
    @Retention(SOURCE)
    @IntDef({REQUEST_SOURCE_STANARD,REQUEST_SOURCE_STICKY})
    @interface RequestSource{

    }

    //final static long ID_NO_REQUEST=-86865161L;


    R body(Object body);

    R id(long id);

    R fromApp(String app);

    R fromProcess(String process);

    R stickyStrategy(IStickyStrategy stickyStrategy);

    R sourceLooper(Looper looper);//本地持有，不参与序列化

    R requestSource(@RequestSource int requestSouce);

    Api api();

    interface Api extends IBodyOwner {

        long id();

        String fromApp();

        String fromProcess();

        IStickyStrategy stickyStrategy();

        Looper looper();

        @RequestSource int requestSouce();

    }

    @Override
    default void writeToParcel(Parcel dest, int flags) {
        dest.writeString(api().fromApp());
        dest.writeString(api().fromProcess());
        dest.writeLong(api().id());
        //body的类型不定
        dest.writeValue(api().body());
        //dest.writeParcelable(api().body(),flags);
        dest.writeParcelable(api().stickyStrategy(), flags);
        dest.writeInt(api().requestSouce());
    }

    @Override
    default int describeContents() {
        return 0;
    }

    ;

    @Override
    default void readFromParcel(Parcel in) {
        Log.e("ipc", "------------------->请求开始被反序列化");
        fromApp(in.readString());
        fromProcess(in.readString());
        id(in.readLong());
        body(in.readValue(getClass().getClassLoader()));
        //body(in.readParcelable(getClass().getClassLoader()));
        stickyStrategy(in.readParcelable(getClass().getClassLoader()));
        ;
        requestSource(in.readInt());
    }

    ;


}
