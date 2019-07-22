package com.sad.architecture.compiler;

import com.google.auto.service.AutoService;
import com.sad.architecture.annotation.AppComponent;
import com.sad.architecture.annotation.Constant;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Created by Administrator on 2019/4/8 0008.
 */
@AutoService(Processor.class)
@SupportedSourceVersion(value = SourceVersion.RELEASE_8)
@SupportedOptions({"moduleName","log"})
@SupportedAnnotationTypes({
        Constant.PACKAGE_SAD_ARCHITECTURE_ANNOTATION+".AppComponent"
})
public class AppComponentProcessor extends AbsSADProcessor{
    private String moduleName=null;
    private int flag=0;
    private CodeBlock codeBlockRegisterComponentProvider;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        moduleName=env.getOptions().get("moduleName").replaceAll("[^0-9a-zA-Z_]+", "");
        isLog=Boolean.valueOf(env.getOptions().getOrDefault("log","false"));
        info("==========>hhhh");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotationedElements, RoundEnvironment roundEnv) {
        flag+=1;
        if (CollectionUtils.isNotEmpty(annotationedElements)){
            //第一步，解析AppComponent注解的类t
            Set<? extends Element> listServiceElements=roundEnv.getElementsAnnotatedWith(AppComponent.class);
            if (isLog){
                info("-------------->"+moduleName+"本次注解："+annotationedElements+",含有AppComponentProvider的元素有"+listServiceElements.size()+"个："+listServiceElements+"。标记："+flag);
            }
            //遍历所有注解类元素
            for (Element serviceElement:listServiceElements
                    ){
                if (serviceElement.getKind() != ElementKind.CLASS) {
                    error(serviceElement, "错误的注解类型，只有【类】才能够被该 @%s 注解处理", AppComponent.class.getSimpleName());
                    return true;
                }
                //强转为类注解元素
                TypeElement serviceElementIntf= (TypeElement) serviceElement;
                String note="";//错误提示
                //判断一下是否是抽象类
                Set<Modifier> mod=serviceElementIntf.getModifiers();
                if (mod.contains(Modifier.ABSTRACT)){
                    note=serviceElement.getSimpleName().toString()+"是抽象类，故无法进行注册:";
                }
                //再判断被注解的类是否实现了IAppComponent
                //模拟一个Element进行判断
                //2019.5.10开放接口限制，不再局限IComponent实现
                /*Element elementIAC=elementUtils.getTypeElement(Constant.PACKAGE_SAD_ARCHITECTURE_API_COMPONENTIZATION+".IComponent");
                if ("".equals(note) && !typeUtils.isSubtype(typeUtils.erasure(serviceElementIntf.asType()),typeUtils.erasure(elementIAC.asType()))){
                    info("请检查"+serviceElementIntf.getSimpleName()+"的type是"+serviceElementIntf.asType()+",是否实现了IAppComponent接口:"+elementIAC.asType());
                    note="请检查"+serviceElementIntf.getSimpleName()+"是否实现了IAppCompoent接口,若否则无法注册:";
                }*/
                //编辑注册方法的代码块
                generateServiceRigesterCodeBlock(serviceElementIntf,note);
            }
        }

        if (roundEnv.processingOver()){
            //结束解析，生成注册类
            generateServiceRigester();
        }
        return false;
    }


    protected void generateServiceRigester(){
        try {
            TypeSpec.Builder tb=TypeSpec.classBuilder("ModuleComponentRegister$$"+moduleName)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(ClassName.bestGuess(Constant.PACKAGE_SAD_ARCHITECTURE_API_INIT+".IModuleComponentRegister"))
                    ;
            //ConcurrentHashMap<String, IAppComponent> AppComponents
            MethodSpec.Builder mb_RegisterIn=MethodSpec.methodBuilder("registerIn")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addCode(codeBlockRegisterComponentProvider)
                    ;
            tb.addMethod(mb_RegisterIn.build());
            JavaFile.Builder jb= JavaFile.builder(Constant.PACKAGE_SAD_ARCHITECTURE_API_COMPONENTIZATION_COMPONENT_REGISTER,tb.build());
            jb.build().writeTo(filer);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void generateServiceRigesterCodeBlock(TypeElement serviceRegisterElement,String note){
        AppComponent appService =serviceRegisterElement.getAnnotation(AppComponent.class);

        if (codeBlockRegisterComponentProvider ==null){

            codeBlockRegisterComponentProvider =CodeBlock.builder().build();

            ;
        }
        ClassName componentStorageCName=ClassName.bestGuess(Constant.PACKAGE_SAD_ARCHITECTURE_API_COMPONENTIZATION_INTERNAL+".ComponentsStorage");
        codeBlockRegisterComponentProvider = codeBlockRegisterComponentProvider.toBuilder().addStatement(
                (note!=null && !"".equals(note)?"//"+note:"")+"$T.registerComponentClass($S,$T.class)",
                componentStorageCName,
                appService.name(),
                ClassName.get(elementUtils.getPackageOf(serviceRegisterElement).toString(),serviceRegisterElement.getSimpleName().toString())
        ).build()
        ;
    }

}
