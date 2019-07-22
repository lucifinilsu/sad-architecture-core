package com.sad.architecture.api.componentization;

/**
 * Created by Administrator on 2019/4/26 0026.
 */

public interface IComponentInstanceConstructor{

    Class[] constructorClass();

    Object[] constructorParameters();

}
