package com.olive.aoub_android.sdk.core;

import android.content.Context;


/**
 * StatSdk
 * Created by Tamic on 2016-04-05.
 */
public class TcStatSdk {

    /** context */
    private Context mContext;
    /** Instance */
    private static TcStatSdk sInstance;

    private static final String TAG = "TcStaInterface::StatSdk";

    private TcStatManager staticsManager;

    /**
     * getInstance
     *
     * @param aContext
     *            context
     * @return 返回 TcStatManager
     */
    protected static synchronized TcStatSdk getInstance(Context aContext) {
        if (sInstance == null) {
            sInstance = new TcStatSdk(aContext,  new TcStatManagerImpl(aContext));
        }
        return sInstance;
    }

    /**
     * constructor
     *
     * @param aContext
     *            context
     */
    private TcStatSdk(Context aContext, TcStatManager aStaticsManager) {
        mContext = aContext;
        staticsManager = aStaticsManager;

    }

    protected void init(int appId, String channel, String fileName) {

        staticsManager.onInit(appId, channel, fileName);

    }

    protected void send() {

        staticsManager.onSend();
    }

    protected void store() {

        staticsManager.onStore();

    }

    protected void upLoad() {

        staticsManager.onSend();
    }

    /**
     * release
     */
    protected void release() {

        staticsManager.onRelease();

    }

    protected void recordPageEnd() {

        staticsManager.onRecordPageEnd();

    }

    protected void recordAppStart() {

        staticsManager.onRecordAppStart();

    }

    protected void recordAppEnd() {

        staticsManager.onRecordAppEnd();

    }

    protected void recordPageStart(Context context) {

        staticsManager.onRecordPageStart(context);

    }

    protected void setPageParameter(String k, String v) {

        staticsManager.onPageParameter(k, v);

    }

    protected void initEvent(String envntName) {

        staticsManager.onInitEvent(envntName);

    }

    protected void setEventParameter(String k, String v) {

        staticsManager.onEventParameter(k, v);

    }

    protected void initPage(String pageId, String referPageId) {

        staticsManager.onInitPage(pageId, referPageId);

    }

}
