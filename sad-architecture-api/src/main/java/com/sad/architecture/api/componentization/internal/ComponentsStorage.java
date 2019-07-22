package com.sad.architecture.api.componentization.internal;

import android.text.TextUtils;

import com.sad.architecture.api.componentization.IComponent;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Administrator on 2019/3/25 0025.
 */

public class ComponentsStorage {

    protected static final ConcurrentMap<String, Class<?>> COMPONENTS = new ConcurrentHashMap<>();

    protected static final ConcurrentMap<String,IComponent> COMPONENTS_INSTANCE = new ConcurrentHashMap<>();

    public static int componentsClassSize(){
        return COMPONENTS.size();
    }
    public static int componentsInstanceSize(){
        return COMPONENTS_INSTANCE.size();
    }

    protected static IComponent  getComponentInstance(String name){
        return COMPONENTS_INSTANCE.get(name);
    }

    protected static <C> Class<C> getAppComponentClass(String key) throws Exception{
        if (TextUtils.isEmpty(key)){throw new Exception("the subscriberName of AppComponent is null!!!!!!");}
        Class appComponentClass= COMPONENTS.get(key);

        if (null==appComponentClass){
            throw new Exception("The annotation corresponding to the subscriberName '"+key+"' has the class of AppComponent that does not exist or is not registered");
        }
        return appComponentClass;
    }

    protected static <C> C getNewAppComponentInstance(String key, Class[] classes, Object... objects) throws Exception{
        Class<C> appComponentClass=getAppComponentClass(key);
        C appComponent=null;
        if (objects!=null && classes!=null && classes.length==objects.length){
            appComponent=appComponentClass.getConstructor(classes).newInstance(objects);
        }
        else{
            appComponent=appComponentClass.newInstance();
        }
        return appComponent;
    }


    public static void registerComponentClass(String name,Class<?> cls){
        COMPONENTS.put(name,cls);
    }
    public static void registerComponentInstance(String name,IComponent component){
        COMPONENTS_INSTANCE.put(name,component);
    }

    public static void unregisterComponentClass(String name){
        COMPONENTS.remove(name);
    }
    public static void unregisterComponentInstance(String name){
        COMPONENTS_INSTANCE.remove(name);
    }

    public static void clearComponentClass(){
        COMPONENTS.clear();
    }
    public static void clearComponentInstance(){
        COMPONENTS_INSTANCE.clear();
    }

    protected static Collection<String> getComponentNames(){
        return COMPONENTS.keySet();
    }

    protected static Collection<String> getComponentInstanceNames(){
        return COMPONENTS_INSTANCE.keySet();
    }
}
