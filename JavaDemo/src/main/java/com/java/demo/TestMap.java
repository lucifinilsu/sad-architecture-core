package com.java.demo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TestMap {
    public static void main(String[] args) {
        /*HashMap<String,Object> s=new HashMap<>();
        s.put("cs",16);
        Integer cs= (Integer) s.get("css");
        System.out.println("-------->s.get="+(cs==16));*/

        String s="cccc";
        ArrayList<String> sl=new ArrayList<>();
        sl.add(null);
        sl.add(null);
        sl.add(2,s);
        sl.add(0,"851");
        sl.add(2,"cscscs");
        sl.add(2,"xxx");
        sl.add(null);
        List<String> sl2=new ArrayList<>();
        sl2.add("xxx");
        sl2.add("cccc");
        System.out.println("-------->sl="+sl);
    }
}
