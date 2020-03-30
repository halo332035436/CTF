package com.bullb.ctf;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.bullb.ctf.Utils.KeyTools;
import com.bullb.ctf.Utils.LanguageUtils;
import com.crashlytics.android.Crashlytics;
import com.karumi.dexter.Dexter;
import com.liulishuo.filedownloader.FileDownloader;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import io.fabric.sdk.android.Fabric;


public class MainApplication extends Application {
    private boolean notificationStartInSplash = true;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageUtils.changeLocale(base));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Dexter.initialize(this);
        LanguageUtils.setDefaultLanguage(this);
        FileDownloader.init(this);
        Log.d("Main", "onCreate: "+BuildConfig.APPLICATION_ID);
        if(BuildConfig.DEBUG) {
            JPushInterface.setDebugMode(true);
        }
        JPushInterface.init(this);
        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(MainApplication.this);
        builder.statusBarDrawable = R.mipmap.noti_icon;
//        JPushInterface.setPushNotificationBuilder(1, builder);
        JPushInterface.setDefaultPushNotificationBuilder(builder);

        KeyTools.attendanceDesEncryptId("j01");
    }


    public boolean isNotificationStartInSplash() {
        return notificationStartInSplash;
    }

    public void setNotificationStartInSplash(boolean notificationStartInSplash) {
        this.notificationStartInSplash = notificationStartInSplash;
    }
}
