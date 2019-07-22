package com.sad.architecture.api.componentization;

import java.util.List;

/**
 * Created by Administrator on 2019/4/23 0023.
 */

public interface IInvoker {

    void invokeVisitors(List<IVisitor.Api> visitorApiGetters);

}
