package com.sad.architecture.api.componentization;

import com.sad.architecture.api.componentization.impl.Result;

/**
 * Created by Administrator on 2019/3/25 0025.
 */

public interface INotifier {

    <IC extends IComponentResponse> IC notifyCallCompeleted(IResult result);

    void interceptorIndex(int index);

}
