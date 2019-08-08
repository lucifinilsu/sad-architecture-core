package com.sad.architecture.core.demo;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sad.architecture.annotation.ComponentResponse;
import com.sad.architecture.api.SCore;
import com.sad.architecture.api.componentization.IComponentCallback;
import com.sad.architecture.api.componentization.IComponentRequest;
import com.sad.architecture.api.componentization.IComponentResponse;
import com.sad.architecture.api.componentization.INotifier;
import com.sad.architecture.api.componentization.extension.router.activity.ActivityRouterParams;
import com.sad.architecture.api.componentization.impl.ComponentRequestImpl;
import com.sad.architecture.api.componentization.impl.TargetsImpl;
import com.sad.architecture.api.componentization.impl.TargetsLocalImpl;
import com.sad.architecture.api.componentization.internal.ComponentInstanceFactoryCallerImpl;
import com.sad.architecture.api.componentization.internal.ComponentInstanceFactoryPosterImpl;

import java.io.Serializable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SCore.registerReceiverHost(this);

        int id = getIntent().getIntExtra("id",0);
        // Example of a call to a native method
        tv = (TextView) findViewById(R.id.sample_text);
        //tv.setText(stringFromJNI());
        try{

            IUIDesign design=SCore.exposerPostedInterface("UI1");
            design.design(this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     * cn.jpush.android.intent.CONNECTION
     */
    public native String stringFromJNI();

    public void testComponentCall(View v){
        try {
            nav();
            /*SCore.newRequester()
                    .addInterceptor(new IComponentInterceptor() {
                        @Override
                        public IComponentResponse OnRequestIntercepted(IComponentRequestInterceptorChain chain) throws Exception {
                            return chain.proceedRequest(chain.request());
                        }
                    })
                    .request(ComponentRequestImpl.<String>newInstance().body("App1来了哈哈哈"))
                    .asPoster()
                    .remote(
                            Targets.newBuilder()
                                    .add("com.sad.architecture.core.demo2",
                                            TargetsProcess.newBuilder()
                                                    .add("com.sad.architecture.core.demo2",
                                                    TargetsLocal.newBuilder()
                                                            .add("App2Test")
                                                            .add("App2Test2")
                                                            .build()
                                                    )
                                            .build()
                                    )
                                    .build()
                            , new IComponentCallback() {
                                @Override
                                public void onComponentInvokeCompleted(IComponentResponse response) {
                                    Log.e("ipc","------------------->IPC级回调生效,来自："+response.sourceName());
                                    ResultDone<String> reuslt=response.body();
                                    tv.setText("IPC级回调，携带的数据："+reuslt.data());
                                }

                            }
                    )*/
            ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @ComponentResponse(componentName = "TD")
    public int onTestDym(IComponentRequest request, INotifier notifier){
        Toast.makeText(getApplicationContext(),"-------->获取到请求了："+request.api().body(),Toast.LENGTH_LONG).show();
        return 2;
    }


    public void nav(){
        Fragment fragment=new Fragment();

        String targetComponentName="App2Activity2";
        Bundle bundle=new Bundle();
        bundle.putString("a","哈哈哈哈");

        IComponentRequest request1 = ActivityRouterParams.newInstance(this)
                .requestCode(8888)
                .bundle(bundle)
                .action("行为哈哈哈")
                .addflag(16)
                .addflag(8)
                .uri(Uri.parse("https://www.baidu.com"))
                .createRequest(999);

        IComponentRequest request2 = ComponentRequestImpl.newInstance().body("哈哈哈哈哈")
                .id(16161L);

        SCore.newRequester(request1)
                .addTargets("com.sad.architecture.core.demo2","com.sad.architecture.core.demo2",
                        TargetsLocalImpl.newInstance()
                        .addLocal("App2Activity2")
                        .addLocal("xxx")
                )
                .callback(new IComponentCallback() {
                    @Override
                    public void onComponentInvokeCompleted(IComponentResponse response) {
                        Log.e("ipc","------------------->IPC级回调生效,来自："+response.request().api().id());
                        tv.setText("IPC级回调生效,来自："+response.request().api().id());
                    }
                })
                .call()
                .launchNode()
                .addNewRequester(request2)
                .addTarget("com.sad.architecture.core.demo2","com.sad.architecture.core.demo2","App2Test2")
                .callback(new IComponentCallback() {
                    @Override
                    public void onComponentInvokeCompleted(IComponentResponse response) {
                        Log.e("ipc","------------------->IPC级回调生效,Post请求，来自："+response.request().api().id());
                    }
                })
                .post()
                .launchNode()
                .addNewRequester(ComponentRequestImpl.newInstance().body("给当前页面的请求")
                        .id(146)
                )
                .addCurrProcessTarget("TD")
                .post()
                .launchNode()
                .submit();
                ;
    }


}
