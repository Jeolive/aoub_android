package com.olive.aoub_android.sdk.core;

import android.content.Context;

/**
 * Created by Tamic on 2016-04-05.
 * StaticsManager
 */
public interface TcStatManager {

    boolean onInit(int appId, String channel, String fileName);

    void onSend();

    void onStore();

    void onRelease();

    void onRecordAppStart();

    void onRecordPageEnd();

    void onRecordPageStart(Context context);

    void onRecordAppEnd();

    void onInitPage(String... strings);

    void onPageParameter(String... strings);

    void onInitEvent(String eventName);

    void onEventParameter(String... strings);

    void onDeleteAll();
}
