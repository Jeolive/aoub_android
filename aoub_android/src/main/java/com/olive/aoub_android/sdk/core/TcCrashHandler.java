package com.olive.aoub_android.sdk.core;

import android.content.Context;
import android.util.Log;

import com.olive.aoub_android.sdk.db.helper.StaticsAgent;
import com.olive.aoub_android.sdk.model.ExceptionInfo;
import com.olive.aoub_android.sdk.util.DeviceUtil;


/**
 * Created by null on 2016/9/22.
 */
public class TcCrashHandler implements Thread.UncaughtExceptionHandler {
    private Context context;
    public static TcCrashHandler INSTANCE;
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    private TcCrashHandler() {
    }

    public void init(Context context) {
        this.context = context;
        uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }


    public static TcCrashHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TcCrashHandler();
        }
        return INSTANCE;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (ex != null) {
            StackTraceElement[] stackTraceElements = ex.getStackTrace();
            Log.i("jiangTest", stackTraceElements.length + "---");
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(ex.getMessage()).append("\n");
            for (int i = stackTraceElements.length - 1; i >= 0; i--) {
                stringBuffer.append(stackTraceElements[i].getFileName()).append(":").append(stackTraceElements[i].getClassName()).append(stackTraceElements[i].getMethodName()).append("(").append(stackTraceElements[i].getLineNumber()).append(")").append("\n");
            }
            Log.i("jiangTest", stringBuffer.toString());
            StaticsAgent.storeObject(new ExceptionInfo(DeviceUtil.getPhoneModel(), DeviceUtil.getSystemModel(), String.valueOf(DeviceUtil.getSystemVersion()), stringBuffer.toString()));
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);

    }
}
