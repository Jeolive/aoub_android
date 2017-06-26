package com.olive.aoub_android.sdk.core;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.olive.aoub_android.sdk.constans.StaticConfig;
import com.olive.aoub_android.sdk.db.helper.DataConstruct;
import com.olive.aoub_android.sdk.db.helper.StaticsAgent;
import com.olive.aoub_android.sdk.model.DataBlock;
import com.olive.aoub_android.sdk.model.DataBlockList;
import com.olive.aoub_android.sdk.service.Platform;
import com.olive.aoub_android.sdk.util.JsonUtil;
import com.olive.aoub_android.sdk.util.NetworkUtil;
import com.olive.aoub_android.sdk.util.StatLog;

import java.io.InputStream;
import java.util.HashMap;

import cz.msebera.android.httpclient.util.EncodingUtils;

import static com.olive.aoub_android.sdk.core.TcNetEngine.TAG;


public class TcStatManagerImpl implements TcStatManager, TcObserverPresenter.ScheduleListener {
    /**
     * context
     */
    private Context mContext;
    /**
     * sInstance
     */
    private static TcStatManager sInstance;

    private static TcObserverPresenter paObserverPresenter;

    private StatListener eventInterface;

    private TcStatPollMgr statPollMgr;

    HashMap<String, String> pageIdMaps = new HashMap<String, String>();
    /**
     * Log TAG
     */
    private static final String LOG_TAG = TcStatPollMgr.class.getSimpleName();

    public TcStatManagerImpl(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public boolean onInit(int appId, String channel, String fileName) {

        // init  ObserverPresenter
        paObserverPresenter = new TcObserverPresenter(this);

        // init StaticsAgent
        StaticsAgent.init(mContext);

        // init CrashHandler
        TcCrashHandler.getInstance().init(mContext);

        // load pageIdMaps
        pageIdMaps = getStatIdMaps(fileName);

        // init  StatiPoll
        statPollMgr = new TcStatPollMgr(this);
        // init Header
        return initHeader(appId, channel);
    }

    @Override
    public void onSend() {
        // report data to server
        Platform.get().execute(new Runnable() {
            @Override
            public void run() {
                DataBlock dataBlock = StaticsAgent.getDataBlock();

                DataBlockList dataBlockList = StaticsAgent.getDataBlockList();

                if (dataBlock.getApp_action().isEmpty() &&
                        dataBlock.getEvent().isEmpty() &&
                        dataBlock.getPage().isEmpty()) {
                    return;
                }
                StatLog.d(TAG, "TcStatfacr >> report is Start");
                Log.d("json_report", JsonUtil.toJSONString(dataBlock));
                Log.d("json_report_list", JsonUtil.toJSONStringIncludeNull(dataBlockList));
                TcUpLoadManager.getInstance(mContext).report(JsonUtil.toJSONString(dataBlock));
            }
        });


    }

    @Override
    public void onStore() {
        DataConstruct.storeEvent();
        DataConstruct.storePage();
    }

    @Override
    public void onRelease() {
        if (paObserverPresenter != null) {
            paObserverPresenter.destroy();
        }

        stopSchedule();

    }

    @Override
    public void onRecordAppStart() {
        //send
        onSend();
        // store appAction
        DataConstruct.storeAppAction("1");
    }

    @Override
    public void onRecordPageEnd() {
        DataConstruct.storeEvent();
        DataConstruct.storePage();
        if (paObserverPresenter != null) {
            paObserverPresenter.onStop(mContext);
        }
        stopSchedule();
    }

    @Override
    public void onRecordPageStart(Context context) {

        if (context == null) {
            return;
        }

        //开始计时
        startSchedule();


        String pageId = checkValidId(context.getClass().getSimpleName());
        if (pageId == null) {
            pageId = context.getClass().getSimpleName();
        }

        // init page
        onInitPage(pageId, null);

        if (paObserverPresenter != null) {
            paObserverPresenter.init(mContext);
        }

        if (paObserverPresenter != null) {
            paObserverPresenter.onStart(mContext);
        }
    }


    @Override
    public void onRecordAppEnd() {

        //record APP exit
        DataConstruct.storeAppAction("2");

        // FIXME 结束也上报
        // onSend();

        onRelease();
    }

    @Override
    public void onInitPage(String... strings) {

        DataConstruct.initPage(mContext, eventInterface, strings[0], strings[1]);

    }

    @Override
    public void onPageParameter(String... strings) {

        DataConstruct.initPageParameter(strings[0], strings[1]);

    }


    @Override
    public void onInitEvent(String eventName) {

        DataConstruct.initEvent(eventInterface, eventName);
    }

    @Override
    public void onEventParameter(String... strings) {

        DataConstruct.onEvent(strings[0], strings[1]);

    }

    @Override
    public void onDeleteAll() {
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

    /**
     * init header
     */
    private boolean initHeader(int appId, String channel) {


        if (!TcHeadrHandle.isInit()) {
            return TcHeadrHandle.initHeader(mContext, appId, channel);
        }

        return false;

    }

    /**
     * onScheduleTimeOut
     */
    void onScheduleTimeOut() {

        StatLog.d(LOG_TAG, "onScheduleTimeOut  is sendData");

        onSend();
    }

    /**
     * startSchedule
     */
    public void startSchedule() {
        // if debug  time is 5 min
        if (StaticConfig.DEBUG &&
                TcStatInterface.uploadPolicy == TcStatInterface.UploadPolicy.UPLOAD_POLICY_DEVELOPMENT) {
            statPollMgr.start(5 * 1000);
            StatLog.d(LOG_TAG, "Schedule is startByRequestParm");
        } else {
            if (NetworkUtil.isWifi(mContext)) {
                statPollMgr.start(TcStatInterface.getIntervalRealtime() * 60 * 1000);
            } else {
                statPollMgr.start(TcStatInterface.UPLOAD_TIME_THIRTY * 60 * 1000);
            }

        }
    }

    /**
     * checkValidId
     *
     * @param name activityName
     * @return pageId
     */
    private String checkValidId(String name) {
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        if (name.length() <= 0) {
            return null;
        }

        return getPageId(name);
    }


    /**
     * getPageId
     *
     * @param clazz
     * @return
     */
    private String getPageId(String clazz) {
        if (mContext == null) {
            return null;
        }
        return pageIdMaps.get(clazz);
    }

    /**
     * stop Schedule
     */
    public void stopSchedule() {

        StatLog.d(LOG_TAG, "stopSchedule()");

        statPollMgr.stop();
    }

    @Override
    public void onStart() {
        StatLog.d(LOG_TAG, "startSchedule");

        startSchedule();

    }

    @Override
    public void onStop() {

        stopSchedule();
    }

    @Override
    public void onReStart() {
        // stopSchedule
        stopSchedule();
        // startSchedule
        startSchedule();
    }


    public HashMap<String, String> getStatIdMaps(String jsonName) {


        HashMap<String, String> map = null;
        if (getFromAsset(jsonName) != null) {
            map = (HashMap<String, String>) JSON.parseObject(getFromAsset("stat_id.json"), HashMap.class);
        }
        return map;
    }

    public String getFromAsset(String fileName) {
        String result = "";
        try {
            InputStream in = mContext.getResources().getAssets().open(fileName);
            int length = in.available();
            byte[] buffer = new byte[length];
            in.read(buffer);
            result = EncodingUtils.getString(buffer, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
