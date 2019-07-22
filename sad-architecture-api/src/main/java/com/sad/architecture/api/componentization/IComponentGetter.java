package com.sad.architecture.api.componentization;

/**
 * Created by Administrator on 2019/5/9 0009.
 */

public interface IComponentGetter {

    <IC> IC require(String name) throws Exception;

}
