package com.java.demo;

/**
 * Created by Administrator on 2019/5/8 0008.
 */

public abstract class AbsParent<A extends AbsParent<A>> implements InterfaceParent<A>,InterfaceParent.InternalInterfaceSuper{

    protected String str="";

    @Override
    public void setStr(String s) {
        this.str=str;
    }

    @Override
    public InternalInterfaceSuper internalInterface() {
        return this;
    }

    @Override
    public String getStr() {
        return this.str;
    }

}
