package com.sad.architecture.api.componentization.impl;

import com.sad.architecture.annotation.AppComponent;
import com.sad.architecture.annotation.ComponentResponse;
import com.sad.architecture.api.componentization.IComponent;

import java.lang.annotation.Annotation;

/**
 * Created by Administrator on 2019/3/20 0020.
 */

public abstract class AbstractDynamicComponent<H> implements IComponent{

    private H host;

    public H getHost() {
        return host;
    }

    public AbstractDynamicComponent(H host, ComponentResponse componentResponse){
        this.host=host;
        this.componentResponse=componentResponse;
    }

    private AppComponent appComponent;
    private ComponentResponse componentResponse;
    @Override
    public AppComponent componentInfo() {
        if (appComponent==null){
            appComponent=new AppComponent(){
                @Override
                public Class<? extends Annotation> annotationType() {
                    return null;
                }

                @Override
                public String name() {
                    return componentResponse.componentName();
                }

                @Override
                public int priority() {
                    return componentResponse.priority();
                }

                @Override
                public int threadEnvironment() {
                    return componentResponse.threadEnvironment();
                }

                @Override
                public String path() {
                    return "";
                }
            };
        }
        return appComponent;
    }
}
