package com.sad.architecture.core.demo2;

import android.util.Log;

import com.sad.architecture.annotation.AppComponent;
import com.sad.architecture.api.componentization.IComponent;
import com.sad.architecture.api.componentization.IComponentRequest;
import com.sad.architecture.api.componentization.INotifier;
import com.sad.architecture.api.componentization.IResult;
import com.sad.architecture.api.componentization.impl.ResultImpl;

/**
 * Created by Administrator on 2019/5/13 0013.
 */
@AppComponent(name = "xxx")
public class App2C implements IComponent {

    @Override
    public IResult onComponentResponse(IComponentRequest request, INotifier notifier) {
        Log.e("ipc","------------------->组件xxx正常运行,接收到请求："+request.api().body());
        notifier.notifyCallCompeleted(ResultImpl.asDone());
        return ResultImpl.asDone();
    }
}

