package com.sad.architecture.api.componentization.internal;

import com.sad.architecture.api.componentization.IComponent;
import com.sad.architecture.api.componentization.IComponentInstanceConstructor;
import com.sad.architecture.api.componentization.IComponentInstanceFactory;
import com.sad.architecture.api.componentization.IVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Administrator on 2019/1/22 0022.
 */

public class ComponentInstanceFactoryCallerImpl implements IComponentInstanceFactory {

    public static IComponentInstanceFactory newInstance(){
        return new ComponentInstanceFactoryCallerImpl();
    }

    private ComponentInstanceFactoryCallerImpl(){}
    @Override
    public <C> List<C> require(String name, IComponentInstanceConstructor componentInstanceConstructor) throws Exception {
        List<C> list=new ArrayList<C>();
        if (componentInstanceConstructor!=null){
            list.add(ComponentsStorage.getNewAppComponentInstance(name,componentInstanceConstructor.constructorClass(),componentInstanceConstructor.constructorParameters()));
            return list;
        }
        list.add(ComponentsStorage.getNewAppComponentInstance(name,null));
        return list;

    }

    @Override
    public Collection<String> names() {
        return ComponentsStorage.getComponentNames();
    }
}
