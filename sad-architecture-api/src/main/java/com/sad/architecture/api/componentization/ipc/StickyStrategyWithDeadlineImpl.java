package com.sad.architecture.api.componentization.ipc;

import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;



/**
 * Created by Administrator on 2019/3/27 0027.
 */


public class StickyStrategyWithDeadlineImpl implements IStickyStrategy {

    protected StickyStrategyWithDeadlineImpl(Parcel in){
        this.deadLine_app=in.readLong();
        this.timeout_app=in.readLong();
        this.deadLine_process=in.readLong();
        this.timeout_process=in.readLong();
        this.deadLine_local=in.readLong();
        this.timeout_local=in.readLong();
    }

    public StickyStrategyWithDeadlineImpl(long timeout_app,long timeout_process,long timeout_local){
        this.timeout_app=timeout_app;
        this.timeout_process=timeout_process;
        this.timeout_local=timeout_local;
    }
    private long deadLine_app=0L;
    private long timeout_app=0L;

    private long deadLine_process=0L;
    private long timeout_process=0L;

    private long deadLine_local=0L;
    private long timeout_local=0L;
    @Override
    public void put(IPCStickyEnvLevel envLevel, String app, String process, String componentName, Object o) {

        switch (envLevel){
            case APP:
                if (o instanceof Message){
                    deadLine_app=System.currentTimeMillis()+timeout_app;
                    IPCStorage.cacheAppStickyMessage(app, (Message) o);
                }
                else {
                    Log.e("ipc"," sticky Onbject's type must be [Message] not ["+o.getClass().getCanonicalName()+"] for app："+app);
                }
                break;

            case PROCESS:
                if (o instanceof Message){
                    deadLine_process=System.currentTimeMillis()+timeout_process;
                    IPCStorage.cacheProcessStickyMessage(process, (Message) o);
                }
                else {
                    Log.e("ipc"," sticky Onbject's type must be [Message] not ["+o.getClass().getCanonicalName()+"] for process："+process);
                }
                break;
            case COMPONENT:
                if (o instanceof ComponentSticky){
                    deadLine_local=System.currentTimeMillis()+timeout_local;
                    IPCStorage.cacheLocalStickyRequest(componentName, (ComponentSticky) o);
                }
                else {
                    Log.e("ipc"," sticky Onbject's type must be [IComponentRequest] not ["+o.getClass().getCanonicalName()+"] for loacl："+componentName);
                }
                break;
            default:
                break;

        }
    }

    @Override
    public boolean isValid(IPCStickyEnvLevel envLevel, String app, String process, String componentName) {
        boolean isStickyValid=true;
        switch (envLevel){
            case APP:
                if (System.currentTimeMillis()>deadLine_app){
                    isStickyValid=false;
                }
                break;
            case PROCESS:
                if (System.currentTimeMillis()>deadLine_process){
                    isStickyValid=false;
                }
                break;
            case COMPONENT:
                long curr=System.currentTimeMillis();
                Log.e("ipc","------------------->当前时间："+curr);
                Log.e("ipc","------------------->死线："+deadLine_local);
                if (System.currentTimeMillis()>deadLine_local){
                    isStickyValid=false;
                }
                break;
            default:
                break;
        }
        return isStickyValid;
    }

    @Override
    public String debugContent() {
        return "deadLine_app="+deadLine_app+",timeout_app="+timeout_app+",deadLine_process="+deadLine_process+",timeout_process="+timeout_process+",deadLine_local="+deadLine_local+",timeout_local="+timeout_local;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(deadLine_app);
        dest.writeLong(timeout_app);
        dest.writeLong(deadLine_process);
        dest.writeLong(timeout_process);
        dest.writeLong(deadLine_local);
        dest.writeLong(timeout_local);
    }

    public static final Parcelable.Creator<IStickyStrategy> CREATOR = new Creator<IStickyStrategy>()
    {
        @Override
        public IStickyStrategy[] newArray(int size)
        {
            return new StickyStrategyWithDeadlineImpl[size];
        }

        @Override
        public IStickyStrategy createFromParcel(Parcel in)
        {
            StickyStrategyWithDeadlineImpl stickyStrategyWithDeadline = new StickyStrategyWithDeadlineImpl(in);
            return stickyStrategyWithDeadline;
        }
    };
}
