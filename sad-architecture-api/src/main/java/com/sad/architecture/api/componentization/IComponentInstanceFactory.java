package com.sad.architecture.api.componentization;


import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Created by Administrator on 2019/1/18 0018.
 */

public interface IComponentInstanceFactory extends IComponentNamesGetter,Serializable{

    public <C> List<C> require(String name, IComponentInstanceConstructor componentInstanceConstructor) throws Exception;

    default public boolean onComponentInstanceUsedUp(IComponent appComponent){return true;};

    public Collection<String> names();


}
