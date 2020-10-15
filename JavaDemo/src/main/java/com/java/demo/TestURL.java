package com.java.demo;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class TestURL {
    public static void main(String[] args) throws MalformedURLException {
        String u="http://a.x/b/c/ec.op?y=0";
        URI uri=URI.create(u);
        URL url=new URL(u);
        String u2="http://a.x/b/c/ec.op?y=0";
        System.out.println("u hashCode="+new TestURL().hashCode());
        System.out.println("u2 hashCode="+new TestURL().hashCode());
        //System.out.println(uri.);
        //System.out.println(url.getRef());

    }
}
