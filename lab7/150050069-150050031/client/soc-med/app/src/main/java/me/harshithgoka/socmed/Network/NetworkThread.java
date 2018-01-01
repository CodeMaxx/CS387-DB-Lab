package me.harshithgoka.socmed.Network;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Created by harshithgoka on 03/10/17.
 */

public class NetworkThread extends HandlerThread {


    public static final String TAG = NetworkService.class.getName();
    Handler serviceHandler;

    public NetworkThread(String name, WeakReference<Handler> sHandler) {
        super(name);
        serviceHandler = sHandler.get();
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
    }
}
