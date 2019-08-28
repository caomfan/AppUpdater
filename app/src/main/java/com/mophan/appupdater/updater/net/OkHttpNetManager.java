package com.mophan.appupdater.updater.net;

import android.os.Looper;
import android.os.Handler;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogRecord;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Create by CMF on 2019/8/28.
 */
public class OkHttpNetManager implements INetManager {

    private static OkHttpClient sOkHttpClient;

    private static Handler sHandler = new Handler(Looper.getMainLooper());

    static {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(15, TimeUnit.SECONDS);
        sOkHttpClient = builder.build();

        //http
        //https 自签名的，okHttp 握手的错误
        //builder.sslSocketFactory()
    }

    @Override
    public void get(String url, final INetCallBack callBack,Object tag) {
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(url).get().tag(tag).build();
        Call call = sOkHttpClient.newCall(request);
        //Response response=call.execute();
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                //非Ui线程
                sHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.failed(e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                try {
                    final String string = response.body().string();

                    sHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.success(string);
                        }
                    });
                } catch (Throwable e) {
                    e.printStackTrace();
                    callBack.failed(e);
                }
            }
        });
    }

    @Override
    public void download(String url, final File targetFile, final INetDownloadCallBack callBack,Object tag) {
        if (!targetFile.exists()) {
            targetFile.getParentFile().mkdirs();
        }
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(url).get().tag(tag).build();
        Call call = sOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                sHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.failed(e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                OutputStream os = null;
                try {
                    final long totalLen = response.body().contentLength();

                    is = response.body().byteStream();
                    os = new FileOutputStream(targetFile);

                    byte[] buffer = new byte[8 * 1024];
                    long curLen = 0;
                    int bufferLen = 0;
                    while (!call.isCanceled()&&(bufferLen = is.read(buffer)) != -1) {
                        os.write(buffer, 0, bufferLen);
                        os.flush();
                        curLen += bufferLen;


                        final long finalCurLen = curLen;
                        sHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callBack.progress((int) (finalCurLen * 1.0f / totalLen * 100));
                            }
                        });
                    }

                    if(call.isCanceled()){
                        return;
                    }

                    try {
                        targetFile.setExecutable(true, false);
                        targetFile.setReadable(true, false);
                        targetFile.setWritable(true, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    sHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.success(targetFile);
                        }
                    });
                } catch (final Throwable e) {
                    if(call.isCanceled()){
                        return;
                    }
                    e.printStackTrace();
                    sHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.failed(e);
                        }
                    });
                } finally {
                    if (is != null){
                        is.close();
                    }
                    if(os!=null){
                        os.close();
                    }
                }

            }
        });
    }

    @Override
    public void cancel(Object tag) {
        List<Call> queuedCalls = sOkHttpClient.dispatcher().queuedCalls();

        if(queuedCalls!=null){
            for (Call call:queuedCalls){
                if(tag.equals(call.request().tag())){
                    Log.d("hyman","find call = "+tag);
                    call.cancel();
                }
            }
        }

        List<Call> runningCalls = sOkHttpClient.dispatcher().runningCalls();

        if(runningCalls!=null){
            for (Call call:runningCalls){
                if(tag.equals(call.request().tag())){
                    Log.d("hyman","find call = "+tag);
                    call.cancel();
                }
            }
        }

    }
}
