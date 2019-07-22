package com.sad.architecture.api.componentization.internal;

import com.sad.architecture.api.componentization.IComponent;
import com.sad.architecture.api.componentization.IComponentInstanceConstructor;
import com.sad.architecture.api.componentization.IComponentInstanceFactory;
import com.sad.architecture.api.componentization.IVisitor;

import java.util.Collection;

/**
 * Created by Administrator on 2019/1/22 0022.
 */

public class ComponentInstanceFactoryCallerImpl implements IComponentInstanceFactory {

    public static IComponentInstanceFactory newInstance(){
        return new ComponentInstanceFactoryCallerImpl();
    }

    private ComponentInstanceFactoryCallerImpl(){}
    @Override
    public <C> C require(String name, IComponentInstanceConstructor componentInstanceConstructor) throws Exception {
        if (componentInstanceConstructor!=null){
            return ComponentsStorage.getNewAppComponentInstance(name,componentInstanceConstructor.constructorClass(),componentInstanceConstructor.constructorParameters());
        }
        return ComponentsStorage.getNewAppComponentInstance(name,null);

    }

    @Override
    public Collection<String> names() {
        return ComponentsStorage.getComponentNames();
    }
}
