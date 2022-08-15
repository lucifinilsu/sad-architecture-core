package com.sad.architecture.api.componentization.extension.router.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import com.sad.architecture.api.init.LogPrinterUtils;

import com.sad.architecture.annotation.AppComponent;
import com.sad.architecture.api.componentization.IComponent;
import com.sad.architecture.api.componentization.IComponentRequest;
import com.sad.architecture.api.componentization.INotifier;
import com.sad.architecture.api.componentization.IResult;
import com.sad.architecture.api.componentization.impl.ResultImpl;
import com.sad.architecture.api.init.ContextHolder;
import com.sad.basic.utils.app.AppInfoUtil;

import java.util.List;

/**
 * Created by Administrator on 2019/4/22 0022.
 */

public abstract class AbstractAndroidActivityRouterProxyComponent implements IComponent {

    public abstract @NonNull Class<? extends Activity> targetActivityClass();

    @Override
    public IResult onComponentResponse(IComponentRequest request, INotifier notifier)  {

        try{
            LogPrinterUtils.logE("ipc","------------------->Activity跳转开始动作");
            Intent intent=new Intent();
            if (request==null){
                throw new Exception("The ActivityRouter's action request is null.");
            }

            if (!(request.api().body() instanceof IActivityRouterParams)){
                LogPrinterUtils.logE("ipc","------------------->body="+request.api().body());
                throw new Exception("The ActivityRouter's request's body should be implement from IActivityRouterParams.");
            }

            IActivityRouterParams params=request.api().body();
            if (params==null){
                LogPrinterUtils.logE("ipc","------------------->Activity路由参数：空空");
                throw new Exception("The ActivityRouter's request's body is null.");
            }
            boolean isRemote= AppInfoUtil.getCurrAppProccessName(ContextHolder.context).equals(request.api().fromProcess());
            //debug
            LogPrinterUtils.logE("ipc","------------------->Activity路由参数："+params.outputContent());

            Context context=params.routerParamsApiGetter().context();
            if (context==null){
                context=ContextHolder.context;
            }
            if (targetActivityClass() != null) {

                intent.setClass(context, targetActivityClass());
            }
            else{
                throw new Exception("The target Activity is null.");
            }
            String action=params.routerParamsApiGetter().action();
            if (!TextUtils.isEmpty(action)){
                intent.setAction(action);
            }
            List<Integer> flags=params.routerParamsApiGetter().flags();
            if (flags!=null){
                for (Integer flag:flags
                        ) {
                    intent.addFlags(flag);
                }

            }
            Bundle bundle = params.routerParamsApiGetter().bundle();
            intent.setExtrasClassLoader(targetActivityClass().getClassLoader());
            if (bundle!=null){
                //bundle.setClassLoader(targetActivityClass().getClassLoader());
                intent.putExtras(bundle);
            }

            Uri uri=params.routerParamsApiGetter().uri();
            if (uri!=null){
                intent.setData(uri);
            }
            String targetPackage=params.routerParamsApiGetter().targetPackage();
            if (!TextUtils.isEmpty(targetPackage)){
                intent.setPackage(targetPackage);
            }

            boolean success=start(context,intent,params);
            if (success){
                IResult result=ResultImpl.<Boolean>asDone().data(success);
                notifier.notifyCallCompeleted(result);
                return result;
            }
            else {
                throw new Exception("The target Activity'"+targetActivityClass().getCanonicalName()+"' can not routed.");
            }
        }catch (Exception e){
            e.printStackTrace();
            return ResultImpl.asException().exceptionString(e.getMessage());
        }


    }

    private boolean start(Context context, Intent intent,IActivityRouterParams params) throws Exception{
        try{
            if(context!=null) {
                //intent.setClass(context, targetActivityClass());
                boolean isForResult=(params!=null && params.routerParamsApiGetter().forResult());
                int requestCode=params!=null?params.routerParamsApiGetter().requestCode():-99999;
                Bundle transitionBundle=params!=null?params.routerParamsApiGetter().transitionBundle():null;
                boolean isForResultByCallback=(params!=null && params.routerParamsApiGetter().forResultByCallback());
                ActivityResultLauncher activityResultLauncher=params!=null?params.routerParamsApiGetter().activityResultLauncher():null;

                if (!(context instanceof Activity)){
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (isForResult){
                        throw new Exception("you should use activity instead of context when you want to startForResult !!!!");
                    }
                    ContextCompat.startActivity(context,intent,transitionBundle);
                    return true;

                }
                else{
                    if (isForResult){
                        if (isForResultByCallback && activityResultLauncher!=null){
                            activityResultLauncher.launch(intent);
                        }
                        else {
                            ActivityCompat.startActivityForResult((Activity) context,intent,requestCode,transitionBundle);
                        }

                    }
                    else{
                        ActivityCompat.startActivity(context,intent,transitionBundle);
                    }
                    return true;
                }
            }
            else{
                throw new Exception("context == null !!!");
            }
        }catch (Exception e){
            e.printStackTrace();
            throw e;

        }

    }
}
