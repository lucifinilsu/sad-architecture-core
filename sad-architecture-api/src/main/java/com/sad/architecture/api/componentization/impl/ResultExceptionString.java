package com.sad.architecture.api.componentization.impl;

import com.sad.architecture.api.componentization.ComponentState;

/**
 * Created by Administrator on 2019/4/17 0017.
 */

public class ResultExceptionString extends Result<String,ResultExceptionString> {

    protected ResultExceptionString(){
        this.state= ComponentState.EXCEPTION;
    }

    public ResultExceptionString exceptionString(String data){
        this.data=data;
        return this;
    }
}
