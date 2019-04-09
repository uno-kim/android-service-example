package com.example.android.mdx;

import android.content.Context;

import com.example.android.client.Logger;

public class MdxApi {

    private static final String TAG = "MdxApi";

    public static void init(Context context) {
        Logger.d(TAG, "init()");
        MdxApiImpl.getInstance().setContext(context);
    }

    public static void request(String json) {
        Logger.d(TAG, "request()");
        MdxApiImpl.getInstance().request(json);
    }

    public static String get(String json) {
        Logger.d(TAG, "get()");
        return MdxApiImpl.getInstance().get(json);
    }

    public static void registerCallback(IServiceCallback callback) {
        Logger.d(TAG, "registerCallback()");
        MdxApiImpl.getInstance().registerCallback(callback);
    }

    public static class Builder {

        public Builder() {

        }
    }
}
