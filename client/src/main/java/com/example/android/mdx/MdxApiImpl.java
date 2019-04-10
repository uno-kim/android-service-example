package com.example.android.mdx;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

import com.example.android.client.Logger;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

final class MdxApiImpl implements LifecycleObserver {

    private static final String TAG = "MdxApiImpl";

    private volatile static MdxApiImpl instance;

    private IRemoteService mService = null;
    private Context mAppContext;

    // todo Set ? Map ? 으로 정리.  Handler 도 필요
    private final List<MdxApi.OnServiceConnected> serviceConnectedList = new ArrayList<>();
    private final List<MdxApi.OnServiceDisconnected> serviceDisconnectedList = new ArrayList<>();
    private final List<IServiceCallback> serviceCallbackList = new ArrayList<>();
    private final List<LifecycleOwner> lifecycleOwnerList = new ArrayList<>();
    private int refCounter;
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

    // todo sync, map? set?
    void addServiceConnectedListener(@NonNull MdxApi.OnServiceConnected onServiceConnected) {
        serviceConnectedList.add(onServiceConnected);
    }

    private void removeServiceConnectedListener() {
        serviceConnectedList.clear();
    }

    private void notifyServiceConnected() {
        serviceConnectedList.forEach(MdxApi.OnServiceConnected::onConnected);
    }

    // todo sync, map? set?
    void addServiceDisconnectedListener(MdxApi.OnServiceDisconnected onServiceDisconnected) {
        serviceDisconnectedList.add(onServiceDisconnected);
    }

    private void removeServiceDisconnectedListener() {
        serviceDisconnectedList.clear();
    }

    private void notifyServiceDisconnected() {
        serviceDisconnectedList.forEach(MdxApi.OnServiceDisconnected::onDisconnected);
    }

    // todo sync, map? set?
    void addServiceCallback(IServiceCallback serviceCallback) {
        serviceCallbackList.add(serviceCallback);
    }

    private void removeServiceCallback() {
        serviceCallbackList.clear();
    }

    private void notifyServiceCallback(final String json) {
        serviceCallbackList.forEach(iServiceCallback -> {
            try {
                iServiceCallback.onResponse(json);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    void addLifecycleOwner(LifecycleOwner owner) {
        owner.getLifecycle().addObserver(this);
        lifecycleOwnerList.add(owner);
    }

    private void removeLifecycleOwner() {
        lifecycleOwnerList.clear();
    }

    // todo sync
    void bind() {
        Logger.d(TAG, "bind(), refCount = " + refCounter);
        if (refCounter == 0) {
            Logger.d(TAG, "bind()");
            Intent intent = new Intent("com.example.android.service.MdxService");
            intent.setPackage("com.example.android.service");
            mAppContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
        refCounter++;
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_RESUME)
    private void onResume() {
        Logger.d(TAG, "Lifecycle.Event.ON_RESUME");
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_PAUSE)
    private void onPause() {
        Logger.d(TAG, "Lifecycle.Event.ON_PAUSE");
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_DESTROY)
    private void unbind() {
        refCounter--;
        Logger.d(TAG, "unbind(), refCount = " + refCounter);
        if (refCounter == 0) {
            Logger.d(TAG, "unbind()");
            mAppContext.unbindService(mConnection);
            removeServiceConnectedListener();
            removeServiceDisconnectedListener();
            removeServiceCallback();
            removeLifecycleOwner();
            Toast.makeText(mAppContext, "onServiceDisconnected", Toast.LENGTH_SHORT).show();
        }
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

    private void registerCallback(IServiceCallback callback) {
        Logger.d(TAG, "registerCallback()");
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
            if (mService == null) {
                Logger.e(TAG, "mService is null");
                mIsBound = false;
                return;
            }
            registerCallback(mServiceCallback);
            notifyServiceConnected();
            Toast.makeText(mAppContext, "onServiceConnected", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Logger.d(TAG, "onServiceDisconnected()");
            mIsBound = false;
            mService = null;
            notifyServiceDisconnected();
            Toast.makeText(mAppContext, "onServiceDisconnected", Toast.LENGTH_SHORT).show();
        }
    };

    private final IServiceCallback mServiceCallback = new IServiceCallback.Stub() {
        @Override
        public void onResponse(final String json) {
            Logger.d(TAG, "onResponse(" + json + ")");
            notifyServiceCallback(json);
        }
    };
}
