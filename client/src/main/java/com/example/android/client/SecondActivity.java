package com.example.android.client;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.mdx.IServiceCallback;
import com.example.android.mdx.MdxApi;

import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    private static final String TAG = "SecondActivity";

    private final Handler mHandler = new Handler();
    private TextView mTextView;
    private EditText mEditText;
    private MdxApi mMdxApi;

    private IServiceCallback mCallback = new IServiceCallback.Stub() {
        @Override
        public void onResponse(final String json) {
            Logger.d(TAG, "onResponse(" + json + ")");
            mHandler.post(() -> mTextView.setText(json));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Logger.d(TAG, "onCreate()");

        mMdxApi = new MdxApi.Builder(this)
                .setOnServiceConnected(() -> {
                    Logger.d(TAG, "onServiceConnected()");
                })
                .setOnServiceDisconnected(() -> {
                    Logger.d(TAG, "setOnServiceDisconnected()");
                })
                .setCallback(mCallback)
                .build();

        mTextView = findViewById(R.id.tv_result);
        mEditText = findViewById(R.id.editText);

        Button buttonRequest = findViewById(R.id.button_request);
        buttonRequest.setOnClickListener(v -> request1(mEditText.getText().toString()));

        Button buttonGet = findViewById(R.id.button_get);
        buttonGet.setOnClickListener(v -> {
            String result = get(mEditText.getText().toString());
            mTextView.setText(result);
        });
    }

    private void request(String json) {
        mMdxApi.request(json);
    }

    private void request1(String json) {
        new Thread(() -> request(json)).start();
    }

    private String get(String json) {
        return mMdxApi.get(json);
    }
}
