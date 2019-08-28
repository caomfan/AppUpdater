package com.mophan.appupdater.updater.net;

import java.io.File;

/**
 * Create by CMF on 2019/8/28.
 */
public interface INetDownloadCallBack {
    void  success(File apkFile);
    void progress(int progress);
    void failed(Throwable throwable);
}
