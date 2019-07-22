package com.sad.architecture.api.componentization;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/4/17 0017.
 */

public interface IResult<S> extends Serializable{

    public S data();

    public ComponentState state();


}
