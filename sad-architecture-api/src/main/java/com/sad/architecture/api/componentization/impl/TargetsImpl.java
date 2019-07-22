package com.sad.architecture.api.componentization.impl;

import android.os.Parcel;
import android.os.Parcelable;

import com.sad.architecture.api.componentization.ITargetLocal;
import com.sad.architecture.api.componentization.ITargets;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019/5/10 0010.
 */

public class TargetsImpl implements ITargets<TargetsImpl>,ITargets.Api {

    protected HashMap<String, ITargetLocal> targetLocalMap=new HashMap<>();
    protected boolean isAll=false;

    protected TargetsImpl(){}

    public static ITargets newInstance(){
        return new TargetsImpl();
    }

    @Override
    public TargetsImpl addProcesses(Map<String, ITargetLocal> targetLocalMap) {
        this.targetLocalMap.putAll(targetLocalMap);
        return this;
    }

    @Override
    public TargetsImpl addProcess(String processName, ITargetLocal local) {
        this.targetLocalMap.put(processName,local);
        return this;
    }

    @Override
    public TargetsImpl removeProcess(String processName) {
        this.targetLocalMap.remove(processName);
        return this;
    }

    @Override
    public TargetsImpl allProcess(boolean all) {
        this.isAll=isAll;
        return this;
    }

    @Override
    public Api api() {
        return this;
    }

    @Override
    public HashMap<String, ITargetLocal> processes() {
        return this.targetLocalMap;
    }

    @Override
    public boolean allProcess() {
        return this.isAll;
    }

    public static final Parcelable.Creator<ITargets> CREATOR=new Creator<ITargets>() {

        @Override
        public ITargets createFromParcel(Parcel source) {
            ITargets targets= new TargetsImpl();
            targets.readFromParcel(source);
            return targets;
        }

        @Override
        public ITargets[] newArray(int size) {
            return new ITargets[size];
        }
    };
}
