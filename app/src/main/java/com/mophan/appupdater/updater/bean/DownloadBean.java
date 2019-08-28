package com.mophan.appupdater.updater.bean;

import androidx.fragment.app.FragmentManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Create by CMF on 2019/8/29.
 */
public class DownloadBean implements Serializable {
    public String title;
    public String content;
    public String url;
    public String md5;
    public String versionCode;

    public static DownloadBean parse(String response) {

        try {
            JSONObject repJson=new JSONObject(response);
            String title=repJson.optString("title");
            String content=repJson.optString("content");
            String url=repJson.optString("url");
            String md5=repJson.optString("md5");
            String versionCode=repJson.optString("versionCode");

            DownloadBean bean=new DownloadBean();
            bean.title=title;
            bean.content=content;
            bean.url=url;
            bean.md5=md5;
            bean.versionCode=versionCode;
            return bean;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
