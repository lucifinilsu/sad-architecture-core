package com.sad.architecture.api.componentization.extension.router.basic;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/8/13 0013.
 */

public abstract class AbstractRouterParamsImpl<A extends AbstractRouterParamsImpl<A>> implements IRouterParams<A>,IRouterParams.IRouterParamsApiGetter,Serializable{
    protected String action;
    protected List<Integer> flags=new ArrayList<Integer>();
    protected Bundle bundle;
    protected Uri uri;
    protected String targetPackage;

    protected Context context;

    @Override
    public A action(String action) {
        this.action=action;
        return (A) this;
    }

    @Override
    public A addflag(int flag) {
        this.flags.add(flag);
        return (A) this;
    }

    @Override
    public A addFlags(List<Integer> flags) {
        this.flags.addAll(flags);
        return (A) this;
    }

    @Override
    public A bundle(Bundle bundle) {
        this.bundle=bundle;
        return (A) this;
    }

    @Override
    public A targetPackage(String pkg) {
        this.targetPackage=pkg;
        return (A) this;
    }

    @Override
    public A uri(Uri uri) {
        this.uri=uri;
        return (A) this;
    }

    @Override
    public IRouterParamsApiGetter routerParamsApiGetter() {
        return this;
    }

    @Override
    public String action() {
        return this.action;
    }

    @Override
    public String targetPackage() {
        return this.targetPackage;
    }

    @Override
    public int flag(int pos) {
        return this.flags.get(pos);
    }

    @Override
    public List<Integer> flags() {
        return this.flags;
    }

    @Override
    public Bundle bundle() {
        return this.bundle;
    }

    @Override
    public Uri uri() {
        return this.uri;
    }

    @Override
    public Context context() {
        if (this.context==null){
            return IRouterParamsApiGetter.super.context();
        }
        return this.context;
    }
}
