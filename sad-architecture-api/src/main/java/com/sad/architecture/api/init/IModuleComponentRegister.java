package com.sad.architecture.api.init;

import android.content.Context;

import com.sad.architecture.api.componentization.IComponent;
import com.sad.assistant.datastore.sharedPreferences.SharedPerferencesClient;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2018/7/12 0012.
 */

public interface IModuleComponentRegister extends Serializable{

    public void registerIn();//(ConcurrentHashMap<String, Class<? extends IComponent>> appComponents);

    public final static String SHAREDPERFERENCES_APPCOMPONENT ="APPCOMPONENT";

    public final static String SHAREDPERFERENCES_APPCOMPONENT_MODULEREGISTERS ="APPCOMPONENT_MODULEREGISTERS";

    public static void cacheModuleRegisters(Context context, Set<String> providerRegisterClasses){
        SharedPerferencesClient.with(context)
                .name(SHAREDPERFERENCES_APPCOMPONENT)
                .initValueWhenReadError(true)
                .mode(Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS)
                .build()
                .put(SHAREDPERFERENCES_APPCOMPONENT_MODULEREGISTERS,providerRegisterClasses)
                .end();
    }

    public static Set<String> readCacheModuleRegisters(Context context){
        return  SharedPerferencesClient.with(context)
                .name(SHAREDPERFERENCES_APPCOMPONENT)
                .initValueWhenReadError(true)
                .mode(Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS)
                .build()
                .get(SHAREDPERFERENCES_APPCOMPONENT_MODULEREGISTERS,new HashSet<>())
                ;
    }
}
