package com.sad.architecture.api.componentization.internal;

import android.text.TextUtils;
import android.util.Log;

import com.sad.architecture.api.componentization.IComponent;
import com.sad.basic.utils.assistant.LogUtils;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Administrator on 2019/3/25 0025.
 */

public class ComponentsStorage {

    protected static final ConcurrentMap<String, Class<?>> COMPONENTS = new ConcurrentHashMap<>();

    protected static final ConcurrentMap<String,List<IComponent>> COMPONENTS_INSTANCE = new ConcurrentHashMap<>();

    public static final String COMPONENT_INSTANCE_MAP_KEY_SEPARATOR="sca#bas%Scbkabf@_sad_@&";

    public static int componentsClassSize(){
        return COMPONENTS.size();
    }
    public static int componentsInstanceSize(){
        return COMPONENTS_INSTANCE.size();
    }

    public static List<IComponent> getComponentInstance(String name){
        List<IComponent> list=new ArrayList<>();
        Iterator<Map.Entry<String,List<IComponent>>> iterator=COMPONENTS_INSTANCE.entrySet().iterator();
        Log.e("ipc","------------------->开始遍历组件名");
        while (iterator.hasNext()){
            Map.Entry<String,List<IComponent>> entry=iterator.next();
            String k=entry.getKey();
            Log.e("ipc","------------------->遍历动态组件"+k);
            String[] n=k.split(ComponentsStorage.COMPONENT_INSTANCE_MAP_KEY_SEPARATOR);
            if (n!=null && n.length>1){
                String ns=n[0];
                if (ns.equals(name)){
                    Log.e("ipc","------------------->动态组件"+k+"符合需求，添加到结果集");
                    List<IComponent> components=entry.getValue();
                    list.addAll(components);
                }
            }
        }
        return list;
    }

    protected static <C> Class<C> getAppComponentClass(String key) throws Exception{
        if (TextUtils.isEmpty(key)){throw new Exception("the subscriberName of AppComponent is null!!!!!!");}
        Class appComponentClass= COMPONENTS.get(key);

        if (null==appComponentClass){
            Log.e("ipc","所有组件--->"+COMPONENTS);
            throw new Exception("The annotation corresponding to the subscriberName '"+key+"' has the class of AppComponent that does not exist or is not registered");
        }
        return appComponentClass;
    }

    protected static <C> C getNewAppComponentInstance(String key, Class[] classes, Object... objects) throws Exception{
        Class<C> appComponentClass=getAppComponentClass(key);
        C appComponent=null;
        if (objects!=null && classes!=null && classes.length==objects.length){
            Constructor<C> constructor=null;
            try {
                constructor=appComponentClass.getDeclaredConstructor(classes);
            }catch (Exception e){
                e.printStackTrace();
                constructor=appComponentClass.getConstructor(classes);

            }
            constructor.setAccessible(true);
            appComponent= constructor.newInstance(objects);
        }
        else{
            /*try {
                appComponent=appComponentClass.getDeclaredConstructor().newInstance();
            }catch (Exception e){
                e.printStackTrace();
                appComponent=appComponentClass.getConstructor().newInstance();
            }*/
            Constructor<C> constructor=null;
            try {
                constructor=appComponentClass.getDeclaredConstructor();
            }catch (Exception e){
                e.printStackTrace();
                constructor=appComponentClass.getConstructor();

            }
            constructor.setAccessible(true);
            appComponent= constructor.newInstance();

        }
        return appComponent;
    }


    public static void registerComponentClass(String name,Class<?> cls){
        COMPONENTS.put(name,cls);
    }
    public static void registerComponentInstance(String name,IComponent component){
        List<IComponent> components=COMPONENTS_INSTANCE.get(name);
        if (components==null){
            components=new ArrayList<>();
        }
        components.add(component);
        COMPONENTS_INSTANCE.put(name,components);
    }

    public static void unregisterComponentClass(String name){
        COMPONENTS.remove(name);
    }
    public static void unregisterComponentInstance(String name){
        List<IComponent> components=COMPONENTS_INSTANCE.get(name);
        if (components!=null){
            COMPONENTS_INSTANCE.get(name).clear();
        }
        COMPONENTS_INSTANCE.remove(name);
    }
    public static void unregisterComponentInstanceByHost(String name,String host){
        /*List<IComponent> components=COMPONENTS_INSTANCE.get(name);
        if (components!=null){
            COMPONENTS_INSTANCE.get(name).clear();
        }
        COMPONENTS_INSTANCE.remove(name);*/
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

    public static Collection<String> getComponentInstanceNames(){
        return COMPONENTS_INSTANCE.keySet();
    }
}
