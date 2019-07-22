package com.sad.architecture.api.componentization;

import android.os.Parcel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/5/10 0010.
 */

public interface ITargetLocal<T extends ITargetLocal<T>> extends IParcelable,Serializable{

    T addLocals(List<String> list);

    T addLocal(String name);

    T allLocal(boolean all);

    Api api();

    interface Api{

        List<String> locals();

        boolean allLocal();

    }

    @Override
    default void writeToParcel(Parcel dest, int flags){
        dest.writeStringList(api().locals());
        dest.writeInt(api().allLocal()?0:1);
    };

    @Override
    default void readFromParcel(Parcel in){
        List<String> list=in.readArrayList(getClass().getClassLoader());
        addLocals(list);
        allLocal(in.readInt()==0);
    };

    @Override
    default int describeContents() {
        return 0;
    }
}
