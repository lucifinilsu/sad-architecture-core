package com.sad.architecture.api.componentization.impl;

import com.sad.architecture.api.componentization.IComponent;

/**
 * Created by Administrator on 2019/3/20 0020.
 */

public abstract class AbstractDynamicComponent<H> implements IComponent{

    private H host;

    public H getHost() {
        return host;
    }

    public AbstractDynamicComponent(H host){
        this.host=host;
    }


}
