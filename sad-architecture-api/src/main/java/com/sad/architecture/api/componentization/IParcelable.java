package com.sad.architecture.api.componentization;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.commons.collections4.CollectionUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by Administrator on 2019/5/10 0010.
 */

public interface IParcelable extends Parcelable{

    @Override
    default int describeContents(){return 0;}

    void readFromParcel(Parcel in);

}
