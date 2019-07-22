package com.sad.architecture.api.componentization;

import java.util.List;

/**
 * Created by Administrator on 2019/4/23 0023.
 */

public interface ILauncher<L extends ILauncher<L>> {

    IRequester addNewRequester(IComponentRequest request);

    Api api();

    L addVisitor(IVisitor.Api api);

    L invoker(IInvoker invoker);

    void submit();

    public interface Api {

        List<IVisitor.Api> visitors();

        IInvoker invoker();

        /*List<IVisitor.Api> requesterRemote();

        List<IRequester.IRequesterLocal> requesterLocal();*/

    }

    /*L visitors(IVisitor visitors);

    L requester(IRequester requester);

    Api apiGetter();

    public interface Api{

        IVisitor visitors();

        IRequester requester();

    }

    public IComponentResponse submit() throws Exception;

    public ICancelable submitForCancelable() throws Exception;

    public <C extends IComponent> C expose();*/

}
