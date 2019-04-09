package com.example.android.mdx;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.example.android.client.Logger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

class MdxApiImpl {

    private static final String TAG = "MdxApiImpl";

    private volatile static MdxApiImpl instance;

    private IRemoteService mService = null;
    private Context mAppContext;
    private boolean mIsBound;

    private MdxApiImpl() {
    }

    static MdxApiImpl getInstance() {
        if (instance == null) {
            synchronized (MdxApiImpl.class) {
                if (instance == null) {
                    instance = new MdxApiImpl();
                }
            }
        }
        return instance;
    }

    void setContext(@NonNull Context context) {
        mAppContext = context.getApplicationContext();
    }

    private void bindService() {
        Logger.d(TAG, "bindService()");
        if (mIsBound) {
            return;
        }
        Intent intent = new Intent("com.example.android.service.MdxService");
        intent.setPackage("com.example.android.service");
        mAppContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindService() {
        Logger.d(TAG, "unbindService()");
        if (!mIsBound) {
            return;
        }
        mAppContext.unbindService(mConnection);
        mIsBound = false;
    }

    void request(@NonNull String json) {
        Logger.d(TAG, "request()");
        if (mService == null) {
            Logger.e(TAG, "mService is null");
            return;
        }
        try {
            mService.request(json);
        } catch (RemoteException e) {
            Logger.e(TAG, e.getMessage());
        }
    }

    @Nullable
    String get(@NonNull String json) {
        Logger.d(TAG, "get()");
        if (mService == null) {
            Logger.e(TAG, "mService is null");
            return null;
        }
        String ret = null;
        try {
            ret = mService.get(json);
        } catch (RemoteException e) {
            Logger.e(TAG, e.getMessage());
        }
        return ret;
    }

    void registerCallback(IServiceCallback callback) {
        Logger.d(TAG, "registerCallback()");
        if (mService == null) {
            Logger.e(TAG, "mService is null");
            return;
        }
        try {
            mService.registerCallback(callback);
        } catch (RemoteException e) {
            Logger.e(TAG, e.getMessage());
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Logger.d(TAG, "onServiceConnected()");
            mIsBound = true;
            mService = IRemoteService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Logger.d(TAG, "onServiceDisconnected()");
            mIsBound = false;
            mService = null;
        }
    };

}
