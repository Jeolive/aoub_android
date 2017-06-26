package com.olive.aoub_android.sdk.db.helper;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.olive.aoub_android.sdk.db.TcNote;
import com.olive.aoub_android.sdk.db.database.DataAccess;
import com.olive.aoub_android.sdk.db.database.ReadDataBaseAccess;
import com.olive.aoub_android.sdk.db.database.WriteDataBaseAccess;
import com.olive.aoub_android.sdk.model.AppAction;
import com.olive.aoub_android.sdk.model.DataBlock;
import com.olive.aoub_android.sdk.model.DataBlockInfo;
import com.olive.aoub_android.sdk.model.DataBlockList;
import com.olive.aoub_android.sdk.model.Event;
import com.olive.aoub_android.sdk.model.ExceptionInfo;
import com.olive.aoub_android.sdk.model.Page;
import com.olive.aoub_android.sdk.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tamic on 2016-03-17.
 */
public class StaticsAgent {
    private static Context mContext;
    private static TcNote note;

    /**
     * @param context
     */
    public static void init(Context context) {

        mContext = context;

        DataAccess.shareInstance(context).createAllTables();

    }

    /**
     * 存储appAction相关信息
     *
     * @param appAction
     */
    public static void storeAppAction(String appAction) {
        if (TextUtils.isEmpty(appAction))
            throw new NullPointerException("appAction is null");
        storeData(appAction, "", "");
    }

    /**
     * storePage
     *
     * @param pageString
     */
    public static void storePage(String pageString) {
        if (TextUtils.isEmpty(pageString))
            throw new NullPointerException("pageString is null");
        storeData("", pageString, "");
    }

    /**
     * storeEvent
     *
     * @param eventString
     */
    public static void storeEvent(String eventString) {
        if (TextUtils.isEmpty(eventString))
            throw new NullPointerException("eventString is null");
        storeData("", "", eventString);
    }

    /**
     * storePage
     *
     * @param exceptionInfo
     */
    public static void storeException(String exceptionInfo) {
        if (TextUtils.isEmpty(exceptionInfo))
            throw new NullPointerException("exceptionInfo is null");
        storeData("", "", "", exceptionInfo);
    }

    public static DataBlock getDataBlock() {
        DataBlock dataBlock = new DataBlock();
        List<TcNote> list = ReadDataBaseAccess.shareInstance(mContext).loadAll();
        AppAction appAction = new AppAction();
        Page page = new Page();
        Event event = new Event();
        ExceptionInfo exceptionInfo = new ExceptionInfo();
        List<AppAction> actionList = new ArrayList<AppAction>();
        List<Page> pageList = new ArrayList<Page>();
        List<Event> eventList = new ArrayList<Event>();
        List<ExceptionInfo> exceptionInfos = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (!TextUtils.isEmpty(list.get(i).getFirstCloumn())) {
                appAction = JsonUtil.parseObject(list.get(i).getFirstCloumn(), AppAction.class);
                actionList.add(appAction);
            }
            if (!TextUtils.isEmpty(list.get(i).getSecondCloumn())) {
                page = JsonUtil.parseObject(list.get(i).getSecondCloumn(), Page.class);
                pageList.add(page);
            }
            if (!TextUtils.isEmpty(list.get(i).getThirdCloumn())) {
                event = JsonUtil.parseObject(list.get(i).getThirdCloumn(), Event.class);
                eventList.add(event);
            }
            if (!TextUtils.isEmpty(list.get(i).getForthCloumn())) {
                exceptionInfo = JsonUtil.parseObject(list.get(i).getForthCloumn(), ExceptionInfo.class);
                exceptionInfos.add(exceptionInfo);
            }
        }
        dataBlock.setApp_action(actionList);
        dataBlock.setPage(pageList);
        dataBlock.setExceptionInfos(exceptionInfos);
        dataBlock.setEvent(eventList);
        return dataBlock;
    }

    public static DataBlockList getDataBlockList() {
        DataBlockList mDataBlockList = new DataBlockList();
        List<DataBlockInfo> dataBlockList = new ArrayList<>();

        List<TcNote> list = ReadDataBaseAccess.shareInstance(mContext).loadAll();


        for (int i = 0; i < list.size(); i++) {
            DataBlockInfo dataBlockInfo = new DataBlockInfo();

            AppAction appAction = new AppAction();
            Page page = new Page();
            Event event = new Event();
            ExceptionInfo exceptionInfo = new ExceptionInfo();


            appAction = JsonUtil.parseObject(list.get(i).getFirstCloumn(), AppAction.class);
            page = JsonUtil.parseObject(list.get(i).getSecondCloumn(), Page.class);
            event = JsonUtil.parseObject(list.get(i).getThirdCloumn(), Event.class);
            exceptionInfo = JsonUtil.parseObject(list.get(i).getForthCloumn(), ExceptionInfo.class);


            dataBlockInfo.setApp_action(appAction);
            dataBlockInfo.setPage(page);
            dataBlockInfo.setEvent(event);
            dataBlockInfo.setExceptionInfos(exceptionInfo);

            dataBlockList.add(dataBlockInfo);
        }

        mDataBlockList.setData_block_list(dataBlockList);

        return mDataBlockList;
    }


    public static void storeData(String firstcloumn, String secondcloumn, String thirdcloumn) {
        storeData(firstcloumn, secondcloumn, thirdcloumn, null);
    }


    public static void storeData(String firstcloumn, String secondcloumn, String thirdcloumn, String forthCloumn) {
        note = new TcNote(null, firstcloumn, secondcloumn, thirdcloumn, forthCloumn);
        WriteDataBaseAccess.shareInstance(mContext).insertData(note);
    }


    /**
     * storeObject
     *
     * @param o
     */
    public static void storeObject(Object o) {
        if (o instanceof Event) {
            storeEvent(JSONObject.toJSONString(o));
        } else if (o instanceof AppAction) {
            storeAppAction(JSONObject.toJSONString(o));
        } else if (o instanceof Page) {
            storePage(JSONObject.toJSONString(o));
        } else if (o instanceof ExceptionInfo) {
            storeException(JSONObject.toJSONString(o));
        }

    }

    public static void deleteData() {
        WriteDataBaseAccess.shareInstance(mContext).deleteAllNote();
    }

}

