package com.sad.architecture.api.componentization.ipc;

import com.sad.architecture.annotation.AppComponent;
import com.sad.architecture.api.componentization.ComponentState;
import com.sad.architecture.api.componentization.IComponent;
import com.sad.architecture.api.componentization.IComponentRequest;
import com.sad.architecture.api.componentization.INotifier;
import com.sad.architecture.api.componentization.impl.Result;

/**
 * Created by Administrator on 2019/3/26 0026.
 */
@Deprecated
@AppComponent(name = IPCConst.COMPONENT_IPC_RELAY)
public class IPCRelayComponent implements IComponent{

    /*private Targets setTargets;
    private int visitMode=IComponentRequest.CALLER;

    public void setRequestMode(@IComponentRequest.RequestModeIntDef int visitMode) {
        this.visitMode = visitMode;
    }

    public void setTargets(Targets setTargets){
        this.setTargets=setTargets;
    }

    @Override
    public Result onComponentResponse(IComponentRequest request, INotifier notifier) {
        IPCBridge.requestRemote(request,setTargets);
        return Result.newBuilder().state(ComponentState.WORKING).build();
    }*/

}
