package com.sad.architecture.compiler;

import com.google.auto.service.AutoService;
import com.sad.architecture.annotation.ActivityRoute;
import com.sad.architecture.annotation.Constant;
import com.sad.architecture.annotation.NameUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * Created by Administrator on 2019/4/22 0022.
 */
@AutoService(Processor.class)
@SupportedSourceVersion(value = SourceVersion.RELEASE_8)
@SupportedOptions({"moduleName","log"})
@SupportedAnnotationTypes({
        Constant.PACKAGE_SAD_ARCHITECTURE_ANNOTATION+".ActivityRoute",
        //Constant.PACKAGE_SAD_ARCHITECTURE_ANNOTATION+".ServiceRoute"
})
public class AppActivityRouterProcessor extends AppAbsRouterProcessor{
    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        moduleName=env.getOptions().get("moduleName").replaceAll("[^0-9a-zA-Z_]+", "");
        isLog=Boolean.valueOf(env.getOptions().getOrDefault("log","false"));
    }
    public Class routeAnnotationClass(){
        return ActivityRoute.class;
    }

    public String componentName(Element e_class){
        return e_class.getAnnotation(ActivityRoute.class).name();
    }

    @Override
    public String componentClsName(TypeElement e_class) {
        return NameUtils.getActivityRouteProxyComponentClassSimpleName(e_class.getQualifiedName().toString(),componentName(e_class),"$$");
    }

    @Override
    public String componentSuperClassName() {
        return Constant.PACKAGE_SAD_ARCHITECTURE_API_COMPONENTIZATION_EXTENSION_ROUTER_ACTIVITY+".AbstractAndroidActivityRouterProxyComponent";
    }

    public String annotatedClassSuperClass(){
        String activityClassName="android.app.Activity";
        return activityClassName;
    }

    public String targetClassMethodName(){
        return "targetActivityClass";
    }
}
