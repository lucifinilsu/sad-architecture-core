package com.sad.architecture.api.init;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import com.sad.architecture.api.init.LogPrinterUtils;

import com.sad.architecture.annotation.Constant;
import com.sad.architecture.api.componentization.internal.ComponentsStorage;
import com.sad.assistant.datastore.sharedPreferences.implemention.VersionUtils;
import com.sad.basic.utils.clazz.ClassScannerClient;
import com.sad.basic.utils.clazz.ClassScannerFilter;

import org.apache.commons.collections4.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * Created by Administrator on 2019/4/8 0008.
 */

public class Initializer {

    private Initializer(Context context){
        ContextHolder.context=context;
    }

    public static Initializer config(Context context){
        return new Initializer(context);
    }

    protected ClassScannerClient classScannerClient;
    protected boolean useRegisterCache=false;
    protected String targetPackage= Constant.PACKAGE_SAD_ARCHITECTURE_API_COMPONENTIZATION_COMPONENT_REGISTER;
    protected static boolean openLog=false;

    public Initializer openLog(boolean openLog){
        Initializer.openLog=openLog;
        return this;
    }

    public Initializer classScannerClient(ClassScannerClient classScannerClient){
        this.classScannerClient=classScannerClient;
        return this;
    }
    public Initializer useRegisterCache(boolean useRegisterCache){
        this.useRegisterCache=useRegisterCache;
        return this;
    }
    public Initializer targetPackage(String targetPackage){
        this.targetPackage=targetPackage;
        return this;
    }

    public String getTargetPackage() {
        return targetPackage;
    }

    public ClassScannerClient getClassScannerClient() {
        return classScannerClient;
    }

    public void setTargetPackage(String targetPackage) {
        this.targetPackage = targetPackage;
    }

    public void init(){
        initByERM();
    }
    private void initByERM(){
        doScanERM(ASSETS_DIR);
    }
    private final static String ASSETS_DIR="erm";
    private void doScanERM(String path){
        try {
            String s=readStringFrom(ContextHolder.context,path);
            //LogPrinterUtils.logE("ipc","ERM扫描注册表路径:"+path);
            if (!TextUtils.isEmpty(s)){
                JSONObject jsonObject=new JSONObject(s);
                JSONArray names=jsonObject.names();
                for (int i = 0; i < names.length(); i++) {
                    String name=names.getString(i);
                    String value=jsonObject.optString(name);
                    LogPrinterUtils.logE("ipc","ERM扫描"+name+":"+value);
                    Class c=Class.forName(value);
                    ComponentsStorage.registerComponentClass(name,c);
                }
            }
            else {
                String[] nextPlist=ContextHolder.context.getAssets().list(path);
                for (String nextPath:nextPlist
                ) {
                    doScanERM(path+File.separator+nextPath);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private String readStringFrom(Context context,String fn){
        try {
            StringBuffer sb=new StringBuffer();
            InputStream stream=context.getAssets().open(fn);
            int l=stream.available();
            byte[]  buffer = new byte[l];
            stream.read(buffer);
            String result =new String(buffer, "utf-8");
            return result;
        }catch (Exception e){
            //e.printStackTrace();
            LogPrinterUtils.logE("sad",">>>>"+fn+" is not file");
            return null;
        }
    }

    private void initByClassScanner(){
        if (classScannerClient==null){
            classScannerClient=
                    ClassScannerClient.with(ContextHolder.context)
                            .openLog(false)
                            .instantRunSupport(true)
                            .build()
            ;
        }
        try {
            Set<String> providerRegisterClasses = IModuleComponentRegister.readCacheModuleRegisters(ContextHolder.context);
            if (!useRegisterCache || providerRegisterClasses==null || CollectionUtils.isEmpty(providerRegisterClasses) || VersionUtils.isDiffVersion(ContextHolder.context,IModuleComponentRegister.SHAREDPERFERENCES_APPCOMPONENT_MODULEREGISTERS)){

                ClassScannerFilter classScanFilter=new ClassScannerFilter() {
                    @Override
                    public boolean accept(Class<?> cls) {
                        return IModuleComponentRegister.class.isAssignableFrom(cls);
                    }
                };
                providerRegisterClasses=classScannerClient.scan(targetPackage,classScanFilter);
                IModuleComponentRegister.cacheModuleRegisters(ContextHolder.context,providerRegisterClasses);
                VersionUtils.storeLastVersion(ContextHolder.context,IModuleComponentRegister.SHAREDPERFERENCES_APPCOMPONENT_MODULEREGISTERS);
            }
            for (String prcName:providerRegisterClasses
            ) {
                try{
                    Class c=Class.forName(prcName);
                    Object o=c.newInstance();
                    if (o!=null){
                        IModuleComponentRegister register=(IModuleComponentRegister) o;
                        register.registerIn();//(ComponentsStorage.);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
