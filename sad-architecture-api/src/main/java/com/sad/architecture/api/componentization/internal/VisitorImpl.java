package com.sad.architecture.api.componentization.internal;

import com.sad.architecture.api.componentization.IComponentGetter;
import com.sad.architecture.api.componentization.IComponentInstanceConstructor;
import com.sad.architecture.api.componentization.IComponentInstanceFactory;
import com.sad.architecture.api.componentization.ILauncher;
import com.sad.architecture.api.componentization.IRequester;
import com.sad.architecture.api.componentization.IVisitor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019/4/23 0023.
 */

public class VisitorImpl implements IVisitor<VisitorImpl>,IVisitor.Api {

    private IComponentInstanceFactory factory;
    private IRequester.Api requesterApiGetter;
    protected Map<String,IComponentInstanceConstructor> constructorMap=new HashMap<>();
    /*public static IVisitor newInstance(IComponentInstanceFactory factory,IRequester.Api requesterApiGetter){
        return new VisitorImpl(factory,requesterApiGetter);
    }*/

    /*public static IComponentGetter asExposer(IComponentInstanceFactory factory){
        return new VisitorImpl(factory, new RequesterImpl(null).api());
    }*/

    protected VisitorImpl(IComponentInstanceFactory factory,IRequester.Api requesterApiGetter){
        this.factory=factory;
        this.requesterApiGetter=requesterApiGetter;
    }


    @Override
    public Api api() {
        return this;
    }

    @Override
    public ILauncher launchNode() {
        if (this.requesterApiGetter!=null && this.requesterApiGetter.getLauncher()!=null){
            return this.requesterApiGetter.getLauncher().addVisitor(this);
        }
        return new LauncherImpl().addVisitor(this);
    }

    @Override
    public IComponentInstanceFactory factory() {
        return this.factory;
    }

    @Override
    public IRequester.Api requesterApi() {
        return this.requesterApiGetter;
    }

    @Override
    public <IC> IC require(String name) throws Exception {
        return Api.super.require(name);
    }

    @Override
    public Map<String, IComponentInstanceConstructor> componentConstructors() {
        return this.constructorMap;
    }

    @Override
    public IComponentInstanceConstructor componentConstructor(String name) {
        return this.constructorMap.get(name);
    }

    @Override
    public VisitorImpl addComponentConstructor(String name,IComponentInstanceConstructor componentInstanceConstructor) {
        this.constructorMap.put(name,componentInstanceConstructor);
        return this;
    }

    @Override
    public VisitorImpl addComponentConstructors(Map<String, IComponentInstanceConstructor> constructorMap) {
        this.constructorMap.putAll(constructorMap);
        return this;
    }

    /*public static class CallerImpl extends VisitorImpl<CallerImpl> implements ICaller<CallerImpl>,ICaller.ICallerApiGetter{

        public static ICaller newInstance(IRequester.Api api){
            return new CallerImpl(api);
        }

        protected CallerImpl(IRequester.Api api) {
            super(ComponentInstanceFactoryCallerImpl.newInstance(), api);
        }

        @Override
        public IRequester.Api requesterApi() {
            return super.requesterApi();
        }

        @Override
        public ICallerApiGetter api() {
            return this;
        }
    }

    public static class PosterImpl extends VisitorImpl<PosterImpl> implements IPoster<PosterImpl>,IPoster.IPosterApiGetter{

        public static IPoster newInstance(IRequester.Api api){
            return new PosterImpl(api);
        }

        protected PosterImpl(IRequester.Api api) {
            super(ComponentInstanceFactoryPosterImpl.newInstance(), api);
        }

        @Override
        public IRequester.Api requesterApi() {
            return super.requesterApi();
        }

        @Override
        public IPosterApiGetter api() {
            return this;
        }
    }

    public static abstract class ExposerImpl<E extends ExposerImpl<E>> extends VisitorImpl<E> implements IExposer<E>,IExposer.IExposerApiGetter{

        protected String name="";

        protected ExposerImpl(String name,IComponentInstanceFactory factory, IRequester.Api api) {
            super(factory, api);
            this.name=name;
        }

        @Override
        public String name() {
            return this.name;
        }

        @Override
        public IExposerApiGetter api() {
            return this;
        }
    }

    public static class CalledExposerImpl extends ExposerImpl<CalledExposerImpl> implements IExposer.ICalledExposer<CalledExposerImpl>,IExposer.ICalledExposer.ICalledExposerApiGetter{

        protected Class[] constructorClass;
        protected Object[] constructorParameters;

        public static ICalledExposer newInstance(String name, IRequester.Api api){
            return new CalledExposerImpl(name,api);
        }

        protected CalledExposerImpl(String name, IRequester.Api api) {
            super(name, ComponentInstanceFactoryCallerImpl.newInstance(), api);
        }

        @Override
        public CalledExposerImpl constructorClass(Class... constructorClass) {
            this.constructorClass=constructorClass;
            return this;
        }

        @Override
        public CalledExposerImpl constructorParameters(Object... constructorParameters) {
            this.constructorParameters=constructorParameters;
            return this;
        }

        @Override
        public Class[] constructorClass() {
            return this.constructorClass;
        }

        @Override
        public Object[] constructorParameters() {
            return this.constructorParameters;
        }

        @Override
        public ICalledExposerApiGetter api() {
            return this;
        }
    }

    public static class PostedExposerImpl extends ExposerImpl<PostedExposerImpl> implements IExposer.IPostedExposer<PostedExposerImpl>,IExposer.IPostedExposer.IPostedExposerApiGetter{

        public static IPostedExposer newInstance(String name, IRequester.Api api){
            return new PostedExposerImpl(name,api);
        }

        protected PostedExposerImpl(String name, IRequester.Api api) {
            super(name, ComponentInstanceFactoryPosterImpl.newInstance(), api);
        }

        @Override
        public IPostedExposerApiGetter api() {
            return this;
        }
    }*/
}
