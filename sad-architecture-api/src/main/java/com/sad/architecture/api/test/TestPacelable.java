package com.sad.architecture.api.test;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import com.sad.architecture.api.init.LogPrinterUtils;

/**
 * Created by Administrator on 2019/5/13 0013.
 */

public class TestPacelable implements Parcelable {
    //private long l=0L;
    public String s1="";
    public String s2="";

    public TestPacelable(String s1,String s2){
        this.s1=s1;
        this.s2=s2;
    }
    /*private Context context=null;

    public void setContext(Context context) {
        this.context = context;
    }*/

    /*public void setL(long l) {
        this.l = l;
    }*/

    public void setS1(String s1) {
        this.s1 = s1;
    }

    public void setS2(String s2) {
        this.s2 = s2;
    }

    public String getS1() {
        return this.s1;
    }

    /*public long getL() {
        return l;
    }*/

    public String getS2() {
        return s2;
    }

    /*public Context getContext() {
        return context;
    }*/

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(s1);
        dest.writeString(s2);
        //dest.writeLong(l);
    }

    public void readFromParcel(Parcel in){
        LogPrinterUtils.logE("ipc","------------------->请求开始被反序列化");
        setS1(in.readString());
        LogPrinterUtils.logE("ipc","------------------->反序列化:s1="+s1);
        setS2(in.readString());
        LogPrinterUtils.logE("ipc","------------------->反序列化:s2="+s2);
        //setL(in.readLong());
        //LogPrinterUtils.logE("ipc","------------------->反序列化:l="+l);
    }

    public static final Parcelable.Creator<TestPacelable> CREATOR = new Parcelable.Creator<TestPacelable>(){

        @Override
        public TestPacelable createFromParcel(Parcel source) {
            TestPacelable componentRequest= new TestPacelable(source.readString(),source.readString());
            //componentRequest.readFromParcel(source);
            return componentRequest;
        }

        @Override
        public TestPacelable[] newArray(int size) {
            return new TestPacelable[size];
        }
    };
}
