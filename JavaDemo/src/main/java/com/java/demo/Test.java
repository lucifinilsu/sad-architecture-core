package com.java.demo;

public class Test {

    public static interface Go{
        default void hello(){
            System.out.println("hhhhhhhhhello");
        }
    }

    public static class GoImpl implements Go{

    }

    public static void main(String[] s){
        try {
            GoImpl.class.newInstance().hello();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

}
