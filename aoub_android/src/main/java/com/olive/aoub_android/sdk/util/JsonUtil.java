package com.olive.aoub_android.sdk.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.lang.reflect.Type;
import java.util.List;

/**
 * JsonUtil
 * Created by Tamic
 */
public class JsonUtil {

    /**
     * parseObject
     * update by jeyOlive
     *
     * @param jsonStr
     * @param entityClass
     * @param <T>
     * @return
     */
    public static <T> T parseObject(String jsonStr, Class<T> entityClass) {
        T ret = null;

        if (jsonStr == null || jsonStr.equals("")) {

            try {
                ret = JSON.parseObject(null, entityClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                ret = JSON.parseObject(jsonStr, entityClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * parseObject
     * update by jeyOlive
     *
     * @param jsonStr
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T parseObject(String jsonStr, Type type) {
        T obj = null;
        try {
            obj = JSON.parseObject(jsonStr, type, Feature.AutoCloseSource);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * parseObject
     * update by jeyOlive
     *
     * @param jsonStr
     * @param tf
     * @param <T>
     * @return
     */
    public static <T> T parseObject(String jsonStr, TypeReference<T> tf) {
        T obj = null;
        try {
            obj = JSON.parseObject(jsonStr, tf, Feature.AutoCloseSource);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * parseList
     *
     * @param jsonStr
     * @param entityClass
     * @param <T>
     * @return
     */
    public static <T> List<T> parseList(String jsonStr, Class<T> entityClass) {
        List<T> ret = null;

        try {
            ret = JSON.parseArray(jsonStr, entityClass);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * object to string
     * update by jeyOlive
     *
     * @param obj
     * @return
     */
    public static String toJSONString(Object obj) {
        String ret = null;

        try {
            ret = JSON.toJSONString(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * create by jeyOlive
     *
     * @param obj
     * @return
     */
    public static String toJSONStringIncludeNull(Object obj) {
        String ret = null;

        try {
            ret = JSONObject.toJSONString(obj, SerializerFeature.WriteMapNullValue);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }
}
