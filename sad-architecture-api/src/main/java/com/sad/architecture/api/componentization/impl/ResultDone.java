package com.sad.architecture.api.componentization.impl;

import com.sad.architecture.api.componentization.ComponentState;
import com.sad.architecture.api.componentization.impl.Result;

/**
 * Created by Administrator on 2019/4/17 0017.
 */

public class ResultDone<S> extends Result<S,ResultDone<S>> {

    protected ResultDone(){
        this.state=ComponentState.DONE;
    }

    public ResultDone<S> data(S data){
        this.data=data;
        return this;
    }
}
