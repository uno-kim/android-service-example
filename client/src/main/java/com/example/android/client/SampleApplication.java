package com.example.android.client;

import android.app.Application;

import com.example.android.mdx.MdxApi;

public class SampleApplication extends Application {

    private static final String TAG = "SampleApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.d(TAG, "onCreate()");

        MdxApi.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Logger.d(TAG, "onTerminate()");
    }
}
