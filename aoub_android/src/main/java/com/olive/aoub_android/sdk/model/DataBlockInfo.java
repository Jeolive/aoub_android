package com.olive.aoub_android.sdk.model;

/**
 * 上传数据对象bean
 * Created by jeyOlive on 2017-06-5.
 */
public class DataBlockInfo {
    private AppAction app_action ;
    private Page page ;
    private Event event ;
    private ExceptionInfo exceptionInfos;

    public AppAction getApp_action() {
        return app_action;
    }

    public void setApp_action(AppAction app_action) {
        this.app_action = app_action;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public ExceptionInfo getExceptionInfos() {
        return exceptionInfos;
    }

    public void setExceptionInfos(ExceptionInfo exceptionInfos) {
        this.exceptionInfos = exceptionInfos;
    }
}
