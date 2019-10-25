package com.sad.architecture.api.componentization.impl;

import com.sad.architecture.api.componentization.ComponentState;

/**
 * Created by Administrator on 2019/4/17 0017.
 */

public class ResultException extends Result<Exception, ResultException> {

    protected ResultException(){
        this.state= ComponentState.EXCEPTION;
    }

    public ResultException exception(Exception data){
        this.data=data;
        return this;
    }
}
