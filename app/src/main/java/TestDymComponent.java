import android.support.v7.app.AppCompatActivity;

import com.sad.architecture.api.componentization.ComponentState;
import com.sad.architecture.api.componentization.IComponentRequest;
import com.sad.architecture.api.componentization.INotifier;
import com.sad.architecture.api.componentization.impl.Result;
import com.sad.architecture.api.componentization.impl.AbstractDynamicComponent;
import com.sad.architecture.api.componentization.impl.ResultDone;
import com.sad.architecture.api.componentization.impl.ResultImpl;
import com.sad.architecture.core.demo.MainActivity;

/**
 * Created by Administrator on 2019/4/8 0008.
 */

public class TestDymComponent extends AbstractDynamicComponent<MainActivity>{

    public TestDymComponent(MainActivity host) {
        super(host);
    }

    @Override
    public Result onComponentResponse(IComponentRequest request, INotifier notifier) {
        try {
            int returnData=getHost().onTestDym(request,notifier);
            return ResultImpl.asDone().data(returnData);
        }catch (Exception e){
            e.printStackTrace();
            return ResultImpl.asException().exceptionString(e.getMessage());
        }


    }
}
