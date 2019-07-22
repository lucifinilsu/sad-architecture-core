package com.java.demo;

/**
 * Created by Administrator on 2019/5/8 0008.
 */

public interface InterfaceParent<I extends InterfaceParent<I>> {

    default void setStr(String s){

    };

    InternalInterfaceSuper internalInterface();

    public interface InternalInterfaceSuper{

        String getStr();

    }

}
