package com.sad.architecture.api.componentization;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.sad.architecture.annotation.AppComponent;
import com.sad.architecture.api.componentization.impl.Result;
import com.sad.architecture.api.componentization.impl.ResultImpl;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/3/22 0022.
 */

public interface IComponent extends ICancelable,Serializable,Comparable<IComponent>{



    default public IResult onComponentResponse(IComponentRequest request,INotifier notifier) {
        return ResultImpl.asUnworked();
    };

    default public AppComponent componentInfo(){
        AppComponent info=getClass().getAnnotation(AppComponent.class);
        return info;
    }

    @Override
    default boolean cancel(boolean isForce) throws Exception {
        return false;
    }

    default int priority(){return componentInfo().priority();}

    @Override
    default int compareTo(@NonNull IComponent o){
        return o.priority()-this.priority();
    }
}
