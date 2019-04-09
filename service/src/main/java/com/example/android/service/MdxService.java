package com.example.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.example.android.mdx.IRemoteService;
import com.example.android.mdx.IServiceCallback;

public class MdxService extends Service {

    private static final String TAG = "MdxService";

    private final Handler mHandler = new Handler();

    public MdxService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.d(TAG, "onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(TAG, "onStartCommand()");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Logger.d(TAG, "onDestroy()");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Logger.d(TAG, "onBind()");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Logger.d(TAG, "onUnbind()");
        return super.onUnbind(intent);
    }

    final IRemoteService.Stub mBinder = new IRemoteService.Stub() {

        @Override
        public void registerCallback(IServiceCallback cb) {
            Logger.d(TAG, "registerCallback()");
            mRemoteCallbackList.register(cb);
        }

        @Override
        public void request(final String json) {
            Logger.d(TAG, "request()");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    onResponse("request(" + json + ")");
                }
            });
        }

        @Override
        public String get(String json) {
            Logger.d(TAG, "get()");
            return "get(" + json + ")";
        }
    };

    final RemoteCallbackList<IServiceCallback> mRemoteCallbackList = new RemoteCallbackList<>();

    private void onResponse(String json) {
        Logger.d(TAG, "onResponse()");
        int count = mRemoteCallbackList.beginBroadcast();
        try {
            for (int i = 0; i < count; ++i) {
                IServiceCallback cb = mRemoteCallbackList.getBroadcastItem(i);
                cb.onResponse(json);
            }
        } catch (RemoteException e) {
            Logger.e(TAG, e.getMessage());
        }
        mRemoteCallbackList.finishBroadcast();
    }
}
