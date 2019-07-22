package com.sad.architecture.api.componentization;

import com.sad.architecture.api.componentization.impl.Result;

/**
 * Created by Administrator on 2019/3/25 0025.
 */

public interface INotifier {

    public void notifyCallCompeleted(IResult result);

    public void interceptorIndex(int index);
    //public void notifyCallException(Exception e);

    //public void notifyCallCancel(boolean isForce);

}
