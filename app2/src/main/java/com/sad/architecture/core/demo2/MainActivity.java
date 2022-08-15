package com.sad.architecture.core.demo2;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.sad.architecture.api.init.LogPrinterUtils;
import android.widget.TextView;

import com.sad.architecture.annotation.ActivityRoute;
import com.sad.architecture.annotation.ComponentResponse;
import com.sad.architecture.api.SCore;
import com.sad.architecture.api.componentization.IComponentRequest;
import com.sad.architecture.api.componentization.INotifier;
import com.sad.architecture.api.componentization.impl.ResultImpl;
import com.sad.architecture.api.componentization.internal.ComponentsStorage;
@ActivityRoute(name="App2AMainActivity")
public class MainActivity extends AppCompatActivity {
    TextView textView;
    TextView textView2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=findViewById(R.id.tv);
        textView2=findViewById(R.id.tv2);
        SCore.registerReceiverHost(this);
        textView.setText("当前订阅者实例库："+ ComponentsStorage.componentsInstanceSize());
    }

    @ComponentResponse(componentName = "App2Test")
    public void onResponseOtherApp(IComponentRequest request, INotifier notifier){
        if (textView!=null){
            textView.setText("收到了来自"+request.api().fromProcess()+"的消息："+request.api().body());
            notifier.notifyCallCompeleted(ResultImpl.<String>asDone().data("App2调用成功"));
        }
        else {
            LogPrinterUtils.logE("ipc","------------------->textView还未初始化");
        }
    }
    @ComponentResponse(componentName = "App2Test2")
    public int onResponseOtherApp2(IComponentRequest request, INotifier notifier){
        textView2.setText("收到了来自"+request.api().fromProcess()+"的消息："+request.api().body());
        notifier.notifyCallCompeleted(ResultImpl.asDone().data(666));
        return 666;
    }
}
