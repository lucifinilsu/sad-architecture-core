package com.sad.architecture.api.componentization.impl;

/**
 * Created by Administrator on 2019/4/17 0017.
 */

public class ResultImpl {

    public static <S> ResultDone<S> asDone(){
        return new ResultDone<S>();
    }
    public static ResultExceptionString asException(){
        return new ResultExceptionString();
    }
    public static ResultUnworked asUnworked(){
        return new ResultUnworked();
    }
    /*public static <S> Builder<S> asWorking(){
        return new Builder<S>(ComponentState.WORKING);
    }

    public static <S> Builder<S> asCanceled(){
        return new Builder<S>(ComponentState.CANCELED);
    }
    public static <S> Builder<S> asIntercepted(){
        return new Builder<S>(ComponentState.INTERCEPTED);
    }*/

}
