package com.sad.architecture.compiler;

import com.sad.architecture.annotation.AppComponent;
import com.sad.architecture.annotation.Constant;
import com.sad.architecture.annotation.NameUtils;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import com.squareup.javapoet.WildcardTypeName;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

/**
 * Created by Administrator on 2019/4/22 0022.
 */

public abstract class AppAbsRouterProcessor extends AbsSADProcessor{


    public abstract Class routeAnnotationClass();

    public abstract String componentName(Element e_class);

    public abstract String componentClsName(TypeElement type_e_class);

    public abstract String componentSuperClassName();

    public abstract String annotatedClassSuperClass();

    public abstract String targetClassMethodName();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!CollectionUtils.isEmpty(annotations)){
            Set<? extends Element> allElements=roundEnv.getElementsAnnotatedWith(routeAnnotationClass());
            for (Element e_class:allElements
                    ){
                if (e_class.getKind() != ElementKind.CLASS) {
                    error(e_class, "错误的注解类型，只有【类】才能够被该 @%s 注解处理", routeAnnotationClass().getSimpleName());
                    return true;
                }
                Set<Modifier> modifiers=e_class.getModifiers();
                if (!modifiers.contains(Modifier.PUBLIC)){
                    error(e_class, e_class.getSimpleName().toString()+"方法使用了不恰当的作用域，只有Public方法才能够被该 @%s 注解处理", routeAnnotationClass().getSimpleName());
                    return true;
                }

                TypeMirror tmRouter=e_class.asType();
                Element elementIAC=elementUtils.getTypeElement(annotatedClassSuperClass());
                if(!typeUtils.isSubtype(typeUtils.erasure(tmRouter),typeUtils.erasure(elementIAC.asType()))){
                    error(e_class,
                            "错误的注解对象， @%s要求被注解的对象是"+ annotatedClassSuperClass()+"的子类，请检查。",
                            routeAnnotationClass().getSimpleName()
                    );
                    return true;
                }

                TypeElement type_e_class= (TypeElement) e_class;
                String name=componentName(e_class);
                generateNewAndroidRouterProxyComponent(type_e_class,name);
            }
        }
        return false;
    }


    private void generateNewAndroidRouterProxyComponent(TypeElement type_e_class, String componentName){
        try {
            String componentClsName= componentClsName(type_e_class);
            String componentClsPackageName=elementUtils.getPackageOf(type_e_class).getQualifiedName().toString();

            MethodSpec ms_targetClass=MethodSpec.methodBuilder(targetClassMethodName())

                    .returns(
                            ParameterizedTypeName.get(
                                    ClassName.get(Class.class),
                                    WildcardTypeName.subtypeOf(ClassName.bestGuess(annotatedClassSuperClass()))
                            )
                    )
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("return $T.class",ClassName.bestGuess(type_e_class.getQualifiedName().toString()))
                    .addAnnotation(Override.class)
                    .addAnnotation(ClassName.bestGuess("androidx.annotation.NonNull"))
                    .build();

            TypeSpec.Builder tb=TypeSpec.classBuilder(componentClsName)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(AnnotationSpec.builder(AppComponent.class)
                            .addMember("name","$S",componentName)
                            .addMember("priority","1")
                            .addMember("threadEnvironment","0")
                            .build())
                    .addMethod(ms_targetClass)
                    .superclass(
                            /*ParameterizedTypeName.get(ClassName.bestGuess(componentSuperClassName()),
                            TypeVariableName.get(type_e_class.asType()))*/
                            ClassName.bestGuess(componentSuperClassName())
                    )
                    ;

            JavaFile.Builder jb= JavaFile.builder(componentClsPackageName,tb.build())
                    ;
            jb.build().writeTo(filer);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
