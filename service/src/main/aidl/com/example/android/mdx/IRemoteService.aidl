// IRemoteService.aidl
package com.example.android.mdx;
import com.example.android.mdx.IServiceCallback;

// Declare any non-default types here with import statements

interface IRemoteService {

    void registerCallback(in IServiceCallback cb);

    void request(String json);

    String get(String json);
}
