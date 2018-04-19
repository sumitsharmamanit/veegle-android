package com.datingapp;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.datingapp.util.Constant;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.ads.MobileAds;

import java.net.URISyntaxException;


public class DatingApp extends Application{

    private Socket mSocket;
    {
        try {
            IO.Options options=new IO.Options();
            options.reconnection=true;
            mSocket = IO.socket(Constant.CHAT_SERVER_URL,options);

//            mSocket.connect();
//            mSocket.on("connect", new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//                    Log.v("User ", "connect " + args);
//                    Log.i(getClass().getName(), "Socket id is >>>>>>>>>>>" + mSocket.id());
//                }
//            });
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this, this.getResources().getString(R.string.admob_app_id));
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
