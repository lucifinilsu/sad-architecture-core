package com.java.demo;

/**
 * Created by Administrator on 2019/5/8 0008.
 */

public class AbsChild extends AbsParent<AbsChild> implements InterfaceA<AbsChild>,InterfaceA.InternalInterfaceChild {


    @Override
    public InternalInterfaceChild internalInterface() {
        return this;
    }

    @Override
    public void setInt(int i) {

    }

    @Override
    public int getInt() {
        return 0;
    }
}
