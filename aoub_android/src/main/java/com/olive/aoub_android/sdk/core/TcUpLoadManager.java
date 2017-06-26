package com.olive.aoub_android.sdk.core;

import android.content.Context;
import android.text.TextUtils;

import com.olive.aoub_android.sdk.constans.NetConfig;
import com.olive.aoub_android.sdk.db.helper.StaticsAgent;
import com.olive.aoub_android.sdk.http.TcHttpClient;
import com.olive.aoub_android.sdk.service.Platform;
import com.olive.aoub_android.sdk.util.JsonUtil;
import com.olive.aoub_android.sdk.util.NetworkUtil;
import com.olive.aoub_android.sdk.util.StatLog;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Stat UpLoadManager
 * Created by Tamic on 2016-03-24.
 */
public class TcUpLoadManager implements IUpLoadListener {


    /**
     * context
     */
    private Context mContext;
    /**
     * http client
     */
    private TcHttpClient mHttpClient;
    /**
     * UpLoadManager
     */
    private static TcUpLoadManager sInstance;

    private Boolean isRunning = false;

    private AtomicReference<TcNetEngine> atomic;

    private TcNetEngine netEngine;
    /**
     * Log TAG
     */
    private static final String TAG = TcNetEngine.class.getSimpleName();

    /**
     * getInstance
     *
     * @param aContext context
     * @return UpLoadManager
     */
    public static synchronized TcUpLoadManager getInstance(Context aContext) {
        if (sInstance == null) {
            sInstance = new TcUpLoadManager(aContext);
        }
        return sInstance;
    }

    /**
     * constructor
     *
     * @param aContext context
     */
    private TcUpLoadManager(Context aContext) {
        mContext = aContext;
        init();
    }

    /**
     * init
     */
    private void init() {
        mHttpClient = getHttpclient();
        atomic = new AtomicReference<>();
        netEngine = new TcNetEngine(mContext, this);
    }


    /**
     * report
     */
    public void report(String jsonString) {

        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            return;
        }

        if (TextUtils.isEmpty(jsonString)) {
            return;
        }
        //netEngine.setHttpClient(getHttpclient());
        atomic.set(netEngine);
        atomic.getAndSet(netEngine).startByRequestParm(jsonString);
    }

    public void reportLog(String jsonString) {

        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            return;
        }

        if (TextUtils.isEmpty(jsonString)) {
            return;
        }
        //netEngine.setHttpClient(getHttpclient());
        atomic.set(netEngine);
        atomic.getAndSet(netEngine).startByRequestParm(jsonString);
    }
    /**
     * cancel
     */
    public void cancel() {

        if (atomic.get() != null) {
            atomic.get().cancel();

        }

    }


    /**
     * get http client
     *
     * @return http client
     */
    public TcHttpClient getHttpclient() {
        if (mHttpClient == null) {
            // HttpClient
            mHttpClient = new TcHttpClient();
            mHttpClient.setTimeOut(NetConfig.TIME_OUT);
        }
        return mHttpClient;

    }


    @Override
    public void onStart() {

        isRunning = true;
    }

    @Override
    public void onUpLoad() {

        isRunning = true;
    }

    @Override
    public void onSuccess() {

        isRunning = false;
        // delete data
        StatLog.d(TAG, "DELETE  ：StaticsAgent.deleteTable()");
        // delete data
        Platform.get().execute(new Runnable() {
            @Override
            public void run() {
                StaticsAgent.deleteData();
                StatLog.d(TAG, "delete after :>>>>>>" + JsonUtil.toJSONString(StaticsAgent.getDataBlock()));
            }
        });

    }

    @Override
    public void onFailure() {

        isRunning = false;

    }

    @Override
    public void onCancel() {

        isRunning = false;
    }
}
