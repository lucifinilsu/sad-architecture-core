package com.sad.architecture.api.componentization;

import android.os.Parcel;
import com.sad.architecture.api.init.LogPrinterUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019/3/22 0022.
 */

public interface ITargets<IT extends ITargets<IT>> extends Serializable,IParcelable{


    IT addProcesses(Map<String,ITargetLocal> targetLocalMap);

    IT addProcess(String processName, ITargetLocal local);

    IT removeProcess(String processName);

    IT allProcess(boolean all);

    Api api();

    interface Api{

        HashMap<String,ITargetLocal> processes();

        boolean allProcess();

    }

    @Override
    default void writeToParcel(Parcel dest, int flags){
        dest.writeSerializable(api().processes());
        dest.writeInt(api().allProcess()?0:1);
    };

    @Override
    default void readFromParcel(Parcel in){
        LogPrinterUtils.logE("ipc","------------------->目标反序列化");
        HashMap<String,ITargetLocal> targetLocalMap = (HashMap<String, ITargetLocal>) in.readSerializable();//in.readHashMap(HashMap.class.getClassLoader());
        addProcesses(targetLocalMap);
       /* in.readMap(api().processes(),getClass().getClassLoader());*/
        LogPrinterUtils.logE("ipc","------------------->本都目标Map反序列化："+targetLocalMap);
        allProcess(in.readInt()==0);
    };

    @Override
    default int describeContents() {
        return 0;
    }
}
