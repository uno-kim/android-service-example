package com.example.android.client;

import android.util.Log;

public final class Logger {

    private static final String TAG = "mdx@";

    public static void d(String tag, String msg) {
        Log.d(TAG + tag, "[" + Thread.currentThread().getName() + "] " + msg);
    }

    public static void i(String tag, String msg) {
        Log.i(TAG + tag, msg);
    }

    public static void e(String tag, String msg) {
        Log.e(TAG + tag, msg);
    }
}
