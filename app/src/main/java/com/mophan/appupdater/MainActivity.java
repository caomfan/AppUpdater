package com.mophan.appupdater;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mophan.appupdater.updater.AppUpdater;
import com.mophan.appupdater.updater.bean.DownloadBean;
import com.mophan.appupdater.updater.net.INetCallBack;
import com.mophan.appupdater.updater.net.INetDownloadCallBack;
import com.mophan.appupdater.updater.ui.UpdateVersionShowDialog;
import com.mophan.appupdater.updater.utils.AppUtils;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private Button mBtnUpdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnUpdater = findViewById(R.id.btn_updater);
        mBtnUpdater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUpdater.getInstance().getNetManager().get("http://59.110.162.30/app_updater_version.json", new INetCallBack() {
                    @Override
                    public void success(String response) {
                        Log.d("hyman", "response = " + response);
                        //1.解析Json
                        // {
                        //    "title":"4.5.0更新啦",
                        //    "content":"1.优化体验\n2.上线最新课程",
                        //    "url":"https://127.0.0.1/test.apk",
                        //    "md5":"a1dsa321sd31f21a3s2d1f3a21sd3",
                        //    "versionCode":"450"
                        //}
                        //2.做版本匹配
                        //如果需要更新
                        //3.弹框
                        //4.点击下载

                        DownloadBean bean = DownloadBean.parse(response);
                        if (bean == null) {
                            Toast.makeText(MainActivity.this, "版本检测接口返回数据异常", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // 检测是否需要弹框
                        try {
                            long versionCode = Long.parseLong(bean.versionCode);
                            if (versionCode <= AppUtils.getVersionCode(MainActivity.this)) {
                                Toast.makeText(MainActivity.this, "已经是最新版本，无需更新", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "版本检测接口返回版本号异常", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // 弹框
                        UpdateVersionShowDialog.show(MainActivity.this,bean);

                    }

                    @Override
                    public void failed(Throwable throwable) {
                        throwable.printStackTrace();
                        Toast.makeText(MainActivity.this, "版本更新接口请求失败", Toast.LENGTH_SHORT).show();
                    }
                },MainActivity.this);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        AppUpdater.getInstance().getNetManager().cancel(this);
    }
}
