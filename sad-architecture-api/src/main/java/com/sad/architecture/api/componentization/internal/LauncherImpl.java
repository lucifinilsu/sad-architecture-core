package com.sad.architecture.api.componentization.internal;

import android.util.Log;

import com.sad.architecture.api.componentization.IComponentRequest;
import com.sad.architecture.api.componentization.IInvoker;
import com.sad.architecture.api.componentization.ILauncher;
import com.sad.architecture.api.componentization.IRequester;
import com.sad.architecture.api.componentization.IVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/4/23 0023.
 */

public class LauncherImpl implements ILauncher<LauncherImpl>,ILauncher.Api {

    private List<IVisitor.Api> visitors=new ArrayList<>();
    private IInvoker invoker=new InvokerImpl();

    protected LauncherImpl(){

    }

    public LauncherImpl addVisitor(IVisitor.Api visitorApiGetter){
        visitors.add(visitorApiGetter);
        return this;
    }

    @Override
    public LauncherImpl invoker(IInvoker invoker) {
        this.invoker=invoker;
        return this;
    }

    @Override
    public void submit() {
        Log.e("ipc","------------------->开始提交请求");
        this.invoker.invokeVisitors(visitors);
    }



    @Override
    public IRequester addNewRequester(IComponentRequest request) {
        return new RequesterImpl(request,this);
    }

    @Override
    public Api api() {
        return this;
    }

    @Override
    public List<IVisitor.Api> visitors() {
        return this.visitors;
    }

    @Override
    public IInvoker invoker() {
        return this.invoker;
    }

}
