package com.sad.architecture.api.componentization;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/3/22 0022.
 */

public interface IComponentResponse extends IBodyOwner,Serializable,ICancelable{

    public String sourceName();

}
