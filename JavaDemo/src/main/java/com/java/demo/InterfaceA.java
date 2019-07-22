package com.java.demo;

/**
 * Created by Administrator on 2019/5/8 0008.
 */

public interface InterfaceA<I extends InterfaceA<I>> extends InterfaceParent<I> {

    void setInt(int i);

    InternalInterfaceChild internalInterface();

    public interface InternalInterfaceChild extends InterfaceParent.InternalInterfaceSuper{

        int getInt();

    }
}
