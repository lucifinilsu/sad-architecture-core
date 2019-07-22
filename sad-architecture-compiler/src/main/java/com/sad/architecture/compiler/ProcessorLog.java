package com.sad.architecture.compiler;


import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

/**
 * Created by Administrator on 2018/5/21 0021.
 */

public class ProcessorLog {

    private Messager messager;
    public ProcessorLog(Messager messager){
        this.messager=messager;
    }
    //打印错误信息
    public void error(Element e, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }

    public void info(String info){
        messager.printMessage(Diagnostic.Kind.NOTE, info);
    }

}
