package com.sad.architecture.api.componentization.ipc;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/3/25 0025.
 */

public interface IStickyStrategy extends Parcelable,Serializable{

    public void put(IPCStickyEnvLevel envLevel,String app, String process, String componentName,Object o);

    public boolean isValid(IPCStickyEnvLevel envLevel,String app, String process, String componentName);

    public String debugContent();

}
