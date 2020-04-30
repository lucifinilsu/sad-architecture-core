package com.java.demo;

import java.util.HashMap;

public class TestMap {
    public static void main(String[] args) {
        HashMap<String,Object> s=new HashMap<>();
        s.put("cs",16);
        Integer cs= (Integer) s.get("css");
        System.out.println("-------->s.get="+(cs==16));
    }
}
