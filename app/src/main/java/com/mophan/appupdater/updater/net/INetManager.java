package com.mophan.appupdater.updater.net;

import java.io.File;

/**
 * Create by CMF on 2019/8/28.
 */
public interface INetManager {

    void get(String url, INetCallBack callBack,Object tag);

    void download(String url, File targetFile, INetDownloadCallBack callBack,Object tag);

    void cancel(Object tag);
}
