package com.sad.architecture.api.componentization.internal;

import com.sad.architecture.api.componentization.IComponent;
import com.sad.architecture.api.componentization.IComponentInstanceConstructor;
import com.sad.architecture.api.componentization.IComponentInstanceFactory;
import com.sad.architecture.api.componentization.IVisitor;

import java.util.Collection;
import java.util.List;

/**
 * Created by Administrator on 2019/1/22 0022.
 */

public class ComponentInstanceFactoryPosterImpl implements IComponentInstanceFactory {
    public static IComponentInstanceFactory newInstance(){
        return new ComponentInstanceFactoryPosterImpl();
    }
    protected ComponentInstanceFactoryPosterImpl(){}
    @Override
    public List<IComponent> require(String name, IComponentInstanceConstructor componentInstanceConstructor) throws Exception {
        return ComponentsStorage.getComponentInstance(name);
    }

    @Override
    public Collection<String> names() {
        return ComponentsStorage.getComponentInstanceNames();
    }
}
