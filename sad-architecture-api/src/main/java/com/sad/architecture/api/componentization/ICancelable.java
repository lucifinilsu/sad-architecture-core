package com.sad.architecture.api.componentization;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/9/3 0003.
 */
public interface ICancelable extends Serializable{

     default boolean cancel(boolean isForce) throws Exception{return false;};

}
