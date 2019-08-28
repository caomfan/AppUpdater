package com.mophan.appupdater.updater;

import com.mophan.appupdater.updater.net.INetManager;
import com.mophan.appupdater.updater.net.OkHttpNetManager;

/**
 * Create by CMF on 2019/8/28.
 */
public class AppUpdater {
    private static AppUpdater sInstance = new AppUpdater();

    //网络请求，下载的能力
    //okhttp,volley,httpclient,httpurlconn
    private INetManager mNetManager=new OkHttpNetManager();

    public void setNetManager(INetManager manager){
        mNetManager=manager;
    }

    public INetManager getNetManager(){
        return  mNetManager;
    }

    public static AppUpdater getInstance() {
        return sInstance;
    }

}
