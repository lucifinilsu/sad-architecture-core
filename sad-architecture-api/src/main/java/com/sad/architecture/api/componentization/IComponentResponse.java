package com.sad.architecture.api.componentization;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/3/22 0022.
 */

public interface IComponentResponse extends IBodyOwner,Serializable,ICancelable{

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

        Creator body(IResult body);

        Creator cancelable(ICancelable cancelable);

        //Creator sourceName(String sourceName);

        Creator request(IComponentRequest request);

        IComponentResponse create();

    }

}
