package com.sad.architecture.api.componentization.impl;

import com.sad.architecture.api.componentization.ComponentState;

/**
 * Created by Administrator on 2019/4/17 0017.
 */

public class ResultUnworked extends Result<String,ResultUnworked> {

    protected ResultUnworked(){
        this.state= ComponentState.UNWORKED;
    }

}
