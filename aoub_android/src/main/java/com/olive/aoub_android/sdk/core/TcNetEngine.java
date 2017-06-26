package com.olive.aoub_android.sdk.core;

import android.content.Context;
import android.os.Looper;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.olive.aoub_android.sdk.constans.NetConfig;
import com.olive.aoub_android.sdk.constans.StaticConfig;
import com.olive.aoub_android.sdk.http.TcHttpClient;
import com.olive.aoub_android.sdk.util.JsonUtil;
import com.olive.aoub_android.sdk.util.StatLog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HeaderElement;
import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.ParseException;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by LIUYONGKUI726 on 2016-04-18.
 */
public class TcNetEngine {


    private Context context;

    private TcHttpClient mHttpClient;

    private String mKey;

    /**
     * 重试次数
     */
    protected int mRetryTimes = NetConfig.RETRY_TIMES;

    public static final String TAG = "TamicStat::TaNetEngine";

    /**
     * 是否支持断点
     */
    protected boolean mCanContinue;

    private String mHostUrl = NetConfig.ONLINE_URL;

    private PaJsonHttpResponseHandler mTaskHandler;

    private IUpLoadListener mUpLoadListener;

    private HashMap<String, String> headers;

    private RequestParams requestParams;

    Header[] reqHeaders;

    Header header;

    public TcNetEngine(Context context, IUpLoadListener upLoadlistener) {

        this(context, null, upLoadlistener);

    }

    public TcNetEngine(Context context, TcHttpClient httpClient, IUpLoadListener upLoadlistener) {
        this.context = context;
        mHttpClient = httpClient;
        mCanContinue = true;
        mTaskHandler = new PaJsonHttpResponseHandler(true);
        mUpLoadListener = upLoadlistener;
        init();


    }

    private void init() {

        if (StaticConfig.DEBUG) {
            mHostUrl = NetConfig.URL;
        }
        headers = new HashMap<String, String>();
        requestParams = new RequestParams();
    }

    public TcHttpClient getHttpClient() {
        return mHttpClient;
    }

    public void setHttpClient(TcHttpClient mHttpClient) {
        this.mHttpClient = mHttpClient;
    }

    public String startByHeaderParams(final String... strings) {


        String str = JsonUtil.toJSONString(TcHeadrHandle.getHeader(context));

        StatLog.d(TAG, "head:" + str);
        if (headers.size() >= 0) {
            headers.clear();
        }
        headers.put(NetConfig.HEADERS_KEY, URLEncoder.encode(str));
        //headers.put("Accept", "application/json");

        requestParams.remove(NetConfig.PARAMS_KEY);

        requestParams.put(NetConfig.PARAMS_KEY, strings[0]);

        StatLog.d(TAG, "body:" + strings[0]);

        if (headers != null && headers.size() > 0) {
            reqHeaders = new Header[headers.size()];
            Set<String> keys = headers.keySet();
            int index = 0;
            for (final String myKey : keys) {
                header = new Header() {
                    @Override
                    public String getName() {
                        return myKey;
                    }

                    @Override
                    public String getValue() {
                        return headers.get(myKey);
                    }

                    @Override
                    public HeaderElement[] getElements() throws ParseException {
                        return new HeaderElement[0];
                    }
                };
                reqHeaders[index++] = header;
            }

        }


        TcHttpClient.post(context, mHostUrl, reqHeaders, requestParams, "application/json", mTaskHandler);
        return null;
    }

    public String startByEntity(final String... strings) {


        String strHeader = JsonUtil.toJSONString(TcHeadrHandle.getHeader(context));

        StatLog.d(TAG, "head:" + strHeader);

        String strBody = strings[0];

        StatLog.d(TAG, "body:" + strings[0]);

        String strEntity = strHeader + "," + strBody;
        StringEntity stringEntity = null;

        try {
            stringEntity = new StringEntity(strEntity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        TcHttpClient.post(context, mHostUrl, stringEntity, "application/json", mTaskHandler);
        return null;
    }

    public String startByRequestParm(final String... strings) {


        requestParams.remove(NetConfig.PARAMS_KEY);

        requestParams.put(NetConfig.PARAMS_KEY, strings[0]);

        StatLog.d(TAG, "body:" + strings[0]);


        TcHttpClient.post(mHostUrl, requestParams, mTaskHandler);
        return null;
    }

    void cancel() {

        TcHttpClient.cancle(mKey, true);
    }

    private class PaJsonHttpResponseHandler extends AsyncHttpResponseHandler {

        public PaJsonHttpResponseHandler() {
        }

        public PaJsonHttpResponseHandler(Looper looper) {
            super(looper);
        }

        public PaJsonHttpResponseHandler(boolean usePoolThread) {
            super(usePoolThread);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            if (mUpLoadListener != null) {
                mUpLoadListener.onSuccess();
            }

            for (Header tmp : headers) {
                StatLog.d(TAG, tmp.getName() + ":" + tmp.getValue());
            }

            StatLog.d(TAG, "response code: " + statusCode);
            if (statusCode == HttpStatus.SC_OK) {
                StatLog.d(TAG, "onSuccess");
                mCanContinue = false;
            } else if (statusCode == HttpStatus.SC_PARTIAL_CONTENT) {
                mCanContinue = true;
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            if (mUpLoadListener != null) {
                mUpLoadListener.onFailure();
            }
            cancel();
        }

    }

}
