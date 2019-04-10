package com.example.android.mdx;

import android.content.Context;

import com.example.android.client.Logger;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

public final class MdxApi implements LifecycleObserver {

    private static final String TAG = "MdxApi";

    public static void init(Context context) {
        Logger.d(TAG, "init()");
        MdxApiImpl.getInstance().setContext(context.getApplicationContext());
    }

    private MdxApi(@NonNull OnServiceConnected onServiceConnected,
            @NonNull OnServiceDisconnected onServiceDisconnected,
            @NonNull IServiceCallback serviceCallback,
            @NonNull LifecycleOwner owner) {
        MdxApiImpl.getInstance().addServiceConnectedListener(onServiceConnected);
        MdxApiImpl.getInstance().addServiceDisconnectedListener(onServiceDisconnected);
        MdxApiImpl.getInstance().addServiceCallback(serviceCallback);
        MdxApiImpl.getInstance().addLifecycleOwner(owner);
        MdxApiImpl.getInstance().bind();
    }

    // request
    public void request(String json) {
        MdxApiImpl.getInstance().request(json);
    }

    // sync call - do not use aidl
    public String get(String json) {
        return MdxApiImpl.getInstance().get(json);
    }

    /**/
    public interface OnServiceConnected {
        void onConnected();
    }

    public interface OnServiceDisconnected {
        void onDisconnected();
    }

    public static final class Builder {

        private OnServiceConnected onServiceConnected;
        private OnServiceDisconnected onServiceDisconnected;
        private IServiceCallback serviceCallback;
        private LifecycleOwner owner;

        public Builder(LifecycleOwner owner) {
            this.owner = owner;
        }

        public Builder setOnServiceConnected(OnServiceConnected onServiceConnected) {
            this.onServiceConnected = onServiceConnected;
            return this;
        }

        public Builder setOnServiceDisconnected(OnServiceDisconnected onServiceDisconnected) {
            this.onServiceDisconnected = onServiceDisconnected;
            return this;
        }

        public Builder setCallback(IServiceCallback serviceCallback) {
            this.serviceCallback = serviceCallback;
            return this;
        }

        public MdxApi build() {
            return new MdxApi(onServiceConnected, onServiceDisconnected, serviceCallback, owner);
        }
    }

}
