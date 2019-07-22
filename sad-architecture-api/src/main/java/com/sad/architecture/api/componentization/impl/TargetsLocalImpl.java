package com.sad.architecture.api.componentization.impl;

import android.os.Parcel;
import android.os.Parcelable;

import com.sad.architecture.api.componentization.ITargetLocal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/5/10 0010.
 */

public class TargetsLocalImpl implements ITargetLocal<TargetsLocalImpl>,ITargetLocal.Api,Serializable{

    protected List<String> nameList=new ArrayList<>();
    protected boolean isAll=false;

    protected TargetsLocalImpl(){}
    public static ITargetLocal newInstance(){
        return new TargetsLocalImpl();
    }

    @Override
    public TargetsLocalImpl addLocals(List<String> list) {
        nameList.addAll(list);
        return this;
    }

    @Override
    public TargetsLocalImpl addLocal(String name) {
        nameList.add(name);
        return this;
    }

    @Override
    public TargetsLocalImpl allLocal(boolean all) {
        this.isAll=all;
        return this;
    }

    @Override
    public Api api() {
        return this;
    }

    @Override
    public List<String> locals() {
        return this.nameList;
    }

    @Override
    public boolean allLocal() {
        return this.isAll;
    }

    public static final Parcelable.Creator<ITargetLocal> CREATOR= new Creator<ITargetLocal>() {
        @Override
        public ITargetLocal createFromParcel(Parcel source) {
            ITargetLocal targetLocal= new TargetsLocalImpl();
            targetLocal.readFromParcel(source);
            return targetLocal;
        }

        @Override
        public ITargetLocal[] newArray(int size) {
            return new ITargetLocal[size];
        }
    };
}
