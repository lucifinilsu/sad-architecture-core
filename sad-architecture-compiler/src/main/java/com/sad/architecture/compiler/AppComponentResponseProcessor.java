package com.sad.architecture.compiler;

import com.google.auto.service.AutoService;
import com.sad.architecture.annotation.AppComponent;
import com.sad.architecture.annotation.ComponentResponse;
import com.sad.architecture.annotation.Constant;
import com.sad.architecture.annotation.NameUtils;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.List;
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
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;


/**
 * Created by Administrator on 2019/4/8 0008.
 */
@AutoService(Processor.class)
@SupportedSourceVersion(value = SourceVersion.RELEASE_8)
@SupportedOptions({"moduleName","log"})
@SupportedAnnotationTypes({
        Constant.PACKAGE_SAD_ARCHITECTURE_ANNOTATION+".ComponentResponse"
})
public class AppComponentResponseProcessor extends AbsSADProcessor{

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        moduleName=env.getOptions().get("moduleName").replaceAll("[^0-9a-zA-Z_]+", "");
        isLog=Boolean.valueOf(env.getOptions().getOrDefault("log","false"));
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (!CollectionUtils.isEmpty(annotations)){
            Set<? extends Element> allElements=roundEnv.getElementsAnnotatedWith(ComponentResponse.class);
            for (Element e_method:allElements
                    ){
                if (e_method.getKind() != ElementKind.METHOD) {
                    error(e_method, "错误的注解类型，只有【方法】才能够被该 @%s 注解处理", ComponentResponse.class.getSimpleName());
                    return true;
                }
                Set<Modifier> modifiers=e_method.getModifiers();
                if (!modifiers.contains(Modifier.PUBLIC)){
                    error(e_method, e_method.getSimpleName().toString()+"方法使用了不恰当的作用域，只有Public方法才能够被该 @%s 注解处理", ComponentResponse.class.getSimpleName());
                    return true;
                }
                ExecutableElement executable_e_method= (ExecutableElement) e_method;

                ComponentResponse annotation_eventResponse=e_method.getAnnotation(ComponentResponse.class);
                TypeElement e_class= (TypeElement) e_method.getEnclosingElement();
                generateNewAbstractDynamicComponent(annotation_eventResponse,executable_e_method,e_class);
            }
        }

        return false;
    }

    private void generateNewAbstractDynamicComponent(ComponentResponse annotation_eventResponse,ExecutableElement executable_e_method,TypeElement e_class){
        String dynamicComponentClsName= NameUtils.getDynamicComponentClassSimpleName(e_class.getQualifiedName().toString(),annotation_eventResponse.componentName(),"$$");
        String pkgName=elementUtils.getPackageOf(e_class).getQualifiedName().toString();
        List<? extends VariableElement> listParams=executable_e_method.getParameters();
        boolean isHasReturnData=false;
        //检查被注解的方法参数
        if (listParams.size()!=2){
            error(executable_e_method, "错误的方法参数数目，@%s方法只能有且仅有两个参数",executable_e_method.getSimpleName().toString());
            return;
        }
        Element elementIAC_0=elementUtils.getTypeElement(Constant.PACKAGE_SAD_ARCHITECTURE_API_COMPONENTIZATION+".IComponentRequest");
        Element elementIAC_1=elementUtils.getTypeElement(Constant.PACKAGE_SAD_ARCHITECTURE_API_COMPONENTIZATION+".INotifier");
        if (!typeUtils.isSubtype(typeUtils.erasure(listParams.get(0).asType()),typeUtils.erasure(elementIAC_0.asType()))){
            error(executable_e_method, "错误的方法参数类型，@%s方法第一个参数必须是IComponentRequest类型",executable_e_method.getSimpleName().toString());
            return;
        }
        if (!typeUtils.isSubtype(typeUtils.erasure(listParams.get(1).asType()),typeUtils.erasure(elementIAC_1.asType()))){
            error(executable_e_method, "错误的方法参数类型，@%s方法第二个参数必须是INotifier类型",executable_e_method.getSimpleName().toString());
            return;
        }
        //检查返回类型
        Element elementIAC_return=elementUtils.getTypeElement(Void.class.getCanonicalName());
        TypeMirror re =executable_e_method.getReturnType();
        /*info("--------------->Void："+typeUtils.erasure(elementIAC_return.asType()));
        info("--------------->实际返回类型："+typeUtils.erasure(re));*/
        isHasReturnData=!"void".equals(typeUtils.erasure(re).toString());//!typeUtils.isSubtype(typeUtils.erasure(re),typeUtils.erasure(elementIAC_return.asType()));
        ClassName cn_i_result=ClassName.bestGuess(Constant.PACKAGE_SAD_ARCHITECTURE_API_COMPONENTIZATION+".IResult");
        ClassName cn_result=ClassName.bestGuess(Constant.PACKAGE_SAD_ARCHITECTURE_API_COMPONENTIZATION_IMPL+".Result");
        ClassName cn_result_impl=ClassName.bestGuess(Constant.PACKAGE_SAD_ARCHITECTURE_API_COMPONENTIZATION_IMPL+".ResultImpl");
        //ClassName cn_componentState=ClassName.bestGuess(Constant.PACKAGE_SAD_ARCHITECTURE_API_COMPONENTIZATION+".ComponentState");
        CodeBlock codeInvokeHostMethod=null;
        if (isHasReturnData){
            codeInvokeHostMethod=CodeBlock.builder()
                    .addStatement("$T returnData=getHost().$L(request,notifier)",
                    re,
                    executable_e_method.getSimpleName()
                    )
                    .addStatement("return $T.asDone().data(returnData)",cn_result_impl)
                    .build();
        }
        else {
            codeInvokeHostMethod=CodeBlock.builder()
                    .addStatement("getHost().$L(request,notifier)",
                            executable_e_method.getSimpleName()
                    )
                    .addStatement("return $T.asDone()",cn_result_impl)
                    .build();
        }

        MethodSpec ms_onComponentResponse=MethodSpec.methodBuilder("onComponentResponse")
                .addAnnotation(Override.class)
                .returns(cn_i_result)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(ClassName.bestGuess(Constant.PACKAGE_SAD_ARCHITECTURE_API_COMPONENTIZATION+".IComponentRequest"),"request").build())
                .addParameter(ParameterSpec.builder(ClassName.bestGuess(Constant.PACKAGE_SAD_ARCHITECTURE_API_COMPONENTIZATION+".INotifier"),"notifier").build())
                .addStatement("$T ex=null;",Exception.class)
                .beginControlFlow("try")
                .addCode(codeInvokeHostMethod)
                .endControlFlow("catch($T e){e.printStackTrace();ex=e;}",
                        Exception.class
                )
                .addStatement("return $T.asException().exceptionString(ex.getMessage())",
                        cn_result_impl
                )
                .build();
        /*MethodSpec ms_componentInfo=MethodSpec.methodBuilder("componentInfo")
                .addAnnotation(Override.class)
                .returns(cn_i_result)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(ClassName.bestGuess(Constant.PACKAGE_SAD_ARCHITECTURE_API_COMPONENTIZATION+".IComponentRequest"),"request").build())
                .addParameter(ParameterSpec.builder(ClassName.bestGuess(Constant.PACKAGE_SAD_ARCHITECTURE_API_COMPONENTIZATION+".INotifier"),"notifier").build())
                .addStatement("$T ex=null;",Exception.class)
                .beginControlFlow("try")
                .addCode(codeInvokeHostMethod)
                .endControlFlow("catch($T e){e.printStackTrace();ex=e;}",
                        Exception.class
                )
                .addStatement("return $T.asException().exceptionString(ex.getMessage())",
                        cn_result_impl
                )
                .build();*/
        MethodSpec ms_c=MethodSpec.constructorBuilder()
                .addParameter(ParameterSpec.builder(TypeVariableName.get(e_class.asType()),"host").build())
                .addParameter(ComponentResponse.class,"componentResponse")
                .addStatement("super(host,componentResponse)")
                .build()
                ;

        TypeSpec.Builder tb=TypeSpec.classBuilder(dynamicComponentClsName)
                .addModifiers(Modifier.PUBLIC)
                /*.addAnnotation(AnnotationSpec.builder(AppComponent.class)
                        .addMember("name","$S",annotation_eventResponse.componentName())
                        .addMember("priority",annotation_eventResponse.priority()+"")
                        .addMember("threadEnvironment",annotation_eventResponse.threadEnvironment()+"")
                        .build())*/
                .addMethod(ms_c)
                .addMethod(ms_onComponentResponse)
                .superclass(ParameterizedTypeName.get(ClassName.bestGuess(Constant.PACKAGE_SAD_ARCHITECTURE_API_COMPONENTIZATION_IMPL+".AbstractDynamicComponent"),
                        TypeVariableName.get(e_class.asType())
                ))

                ;


        JavaFile.Builder jb= JavaFile.builder(pkgName,tb.build())
                //.addStaticImport(ClassName.bestGuess(Constant.PACKAGE_SAD_ARCHITECTURE_API_COMPONENTIZATION+".ComponentState"))
                ;
        try {
            jb.build().writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
