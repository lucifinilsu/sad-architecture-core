package com.sad.architecture.api.componentization.impl;

import com.sad.architecture.api.componentization.ComponentState;
import com.sad.architecture.api.componentization.IResult;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/1/21 0021.
 */

public abstract class Result<S,R extends Result<S,R>> implements IResult<S>,Serializable{
    protected Result(){}
    protected S data;
    protected ComponentState state= ComponentState.UNWORKED;
    public ComponentState state(){
        return state;
    }
    public S data(){
        return  data;
    }

    /*protected Result(S data,ComponentState state){
        this.data =data;
        this.state=state;
    }*/

    /*public static <S> Builder<S> newBuilder(){
        return new Builder();
    }*/


   /* public static <S> Builder<S> asDone(){
        return new Builder<S>(ComponentState.DONE);
    }
    public static Builder<String> asException(){
        return new Builder<String>(ComponentState.EXCEPTION);
    }
    public static <S> Builder<S> asWorking(){
        return new Builder<S>(ComponentState.WORKING);
    }
    public static <S> Builder<S> asUnworked(){
        return new Builder<S>(ComponentState.UNWORKED);
    }
    public static <S> Builder<S> asCanceled(){
        return new Builder<S>(ComponentState.CANCELED);
    }
    public static <S> Builder<S> asIntercepted(){
        return new Builder<S>(ComponentState.INTERCEPTED);
    }


    private Result(Builder<S> builder){
        this.data =builder.data;
        this.state=builder.state;
    }

    private S data;
    private ComponentState state= ComponentState.UNWORKED;
    public  S data(){
        return  data;
    };

    public ComponentState state(){
        return state;
    }

    public static class Builder<S> implements Serializable{
        private S data;
        private ComponentState state= ComponentState.UNWORKED;


        private Builder(ComponentState state) {
            this.state=state;
        }

        private Builder() {

        }

        public Builder data(S successData){
            this.data =successData;
            return this;
        }
        *//*public Builder state(ComponentState state){
            this.state=state;
            return this;
        }*//*
        public Result build(){
            return new Result(this);
        }
    }*/

}
