package com.example.android.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.mdx.IRemoteService;
import com.example.android.mdx.IServiceCallback;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    IRemoteService mService = null;
    private boolean mIsBound;
    private final Handler mHandler = new Handler();
    private EditText mEditText;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Logger.d(TAG, "onCreate()");

        Button buttonBind = findViewById(R.id.button_bind);
        buttonBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindService();
            }
        });

        Button buttonUnbind = findViewById(R.id.button_unbind);
        buttonUnbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unbindService();
            }
        });

        Button buttonRequest = findViewById(R.id.button_request);
        buttonRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request(mEditText.getText().toString());
            }
        });

        Button buttonGet = findViewById(R.id.button_get);
        buttonGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = get(mEditText.getText().toString());
                mTextView.setText(result);
            }
        });

        mEditText = findViewById(R.id.editText);
        mTextView = findViewById(R.id.tv_result);
    }

    private void bindService() {
        Logger.d(TAG, "bindService()");
        if (mIsBound) {
            return;
        }
        Intent intent = new Intent("com.example.android.service.MdxService");
        intent.setPackage("com.example.android.service");
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindService() {
        Logger.d(TAG, "unbindService()");
        if (!mIsBound) {
            return;
        }
        unbindService(mConnection);
        mIsBound = false;
    }

    private void request(String json) {
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

    private String get(String json) {
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
            registerCallback(mCallback);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Logger.d(TAG, "onServiceDisconnected()");
            mIsBound = false;
            mService = null;
        }
    };

    private IServiceCallback mCallback = new IServiceCallback.Stub() {
        @Override
        public void onResponse(final String json) {
            Logger.d(TAG, "onResponse(" + json + ")");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mTextView.setText(json);
                }
            });
        }
    };
}
