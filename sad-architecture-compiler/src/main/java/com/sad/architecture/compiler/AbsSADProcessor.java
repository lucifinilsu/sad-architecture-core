package com.sad.architecture.compiler;

/**
 * Created by Administrator on 2018/6/6 0006.
 */


import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;


public abstract class AbsSADProcessor extends AbstractProcessor {
    //在init里初始化这4个工具性质的类
    protected Types typeUtils;
    protected Elements elementUtils;//Element代表程序的元素，例如包、类或者方法。每个Element代表一个静态的、语言级别的构件。它只是结构化的文本，他不是可运行的.可以理解为一个签名 (public class Main 或者 private void fun(int a) {})，大家可以联系到XML的解析，或者抽象语法树 （AST）
    protected Filer filer;
    protected Messager messager;//因为在注解处理器里面不可以抛出Exception！为什么了？因为在编译阶段抛出错误后，注解处理器就不会运行完，也就没什么用了。所以Message就是为了输出错误信息。
    protected ProcessorLog log;
    protected boolean isLog=false;
    protected String moduleName=null;
    @Override
    public synchronized void init(ProcessingEnvironment env){
        elementUtils = env.getElementUtils();
        processingEnv=env;
        filer = env.getFiler();
        typeUtils = env.getTypeUtils();
        messager = env.getMessager();
        log=new ProcessorLog(messager);
        //log.info("初始化注解moduleName:"+env.getOptions().values());

    }
    //打印错误信息
    protected void error(Element e, String msg, Object... args) {
        log.error(e, msg, args);
    }

    public void info(String info){
        log.info(info);
    }

    //以下两个方法可以用注解来表示
    /*@Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new TreeSet<>();
        types.add(AutoCreateInterface.class.getCanonicalName());//返回注解类名（含包名）
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();//返回最新的版本
    }*/

    //关于Element,按照以下说明
    /*package com.example;        // PackageElement

    public class MyClass {      // TypeElement

        private int a;          // VariableElement

        private Foo other;      // VariableElement

        public Foo () {}        // ExecuteableElement

        public void setA (      // ExecuteableElement
                                int newA    // TypeElement
        ) {

        }
    }*/
}
