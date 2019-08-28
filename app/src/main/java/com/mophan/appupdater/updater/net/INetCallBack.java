package com.mophan.appupdater.updater.net;

/**
 * Create by CMF on 2019/8/28.
 */
public interface INetCallBack {
    void success(String response);
    void failed(Throwable throwable);
}
