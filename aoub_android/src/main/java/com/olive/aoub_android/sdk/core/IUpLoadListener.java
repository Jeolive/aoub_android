package com.olive.aoub_android.sdk.core;

/**
 * 上报状态接口
 * Created by LIUYONGKUI726 on 2016-03-25.
 */
public interface IUpLoadListener {

    void onStart();

    void onUpLoad();

    void onSuccess();

    void onFailure();

    void onCancel();
}
