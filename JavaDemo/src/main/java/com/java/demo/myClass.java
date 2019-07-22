package com.java.demo;

import java.util.ArrayList;
import java.util.List;

public class myClass {
    static class C{
        public C(String p){
            this.p=p;
        }
        String p="";
        public String getP() {
            return p;
        }
        public void setP(String p) {
            this.p = p;
        }
    }
    static class L {
         List<C> strings=new ArrayList<>();
    }

    public static void main(String[] s){
        L l=new L();
        l.strings.add(new C("A"));
        l.strings.add(new C("B"));
        List<C> cs=l.strings;
        C c=cs.get(1);
        c.setP("46584646416");

        C cc=l.strings.get(1);

        System.out.println("----------->l.strings.get(1)="+cc.p);
    }
}
