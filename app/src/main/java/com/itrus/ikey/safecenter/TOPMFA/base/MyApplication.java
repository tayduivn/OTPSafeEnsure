package com.itrus.ikey.safecenter.TOPMFA.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.util.Log;


import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.https.HttpsUtils;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;



import okhttp3.OkHttpClient;

public class MyApplication extends Application {

    public static MyApplication instance;
    private List<Activity> activityList = new ArrayList<>();
    private static Activity topActivity = null;
    private int appCount = 0;

    //这里采用全局变量的形式,为了方便使用
    private String USERNAME;
    private String DEVICESINFO;
    //-------------------------------

    public String getUSERNAME() {
        return USERNAME;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }

    public String getDEVICESINFO() {
        return DEVICESINFO;
    }

    public void setDEVICESINFO(String DEVICESINFO) {
        this.DEVICESINFO = DEVICESINFO;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        ZXingLibrary.initDisplayOpinion(this);

        //logger的相关配置
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(true)  //（可选）是否显示线程信息。 默认值为true
                .methodCount(3)         // （可选）要显示的方法行数。 默认2
                .methodOffset(5)        // （可选）隐藏内部方法调用到偏移量。 默认5
                .tag("-----logger-----")//（可选）每个日志的全局标记。 默认PRETTY_LOGGER
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));//根据上面的格式设置logger相应的适配器



        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
                appCount++;
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
                appCount--;
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(6000L, TimeUnit.MILLISECONDS)
                .readTimeout(6000L, TimeUnit.MILLISECONDS)
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient);

        try {
            //CrashHandler.Initialize(getApplicationContext());
        } catch (Exception Ex) {
            Log.i("TopVPNSdkDemo", Ex.toString());
        }

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    @Override
    public void onLowMemory() {
        Log.i("TopVPNSdkDemo", "TopVPNSdkDemo onLowMemory");

        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        Log.i("TopVPNSdkDemo", "TopVPNSdkDemo onTerminate");

        super.onTerminate();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onTrimMemory(int level) {
        Log.i("TopVPNSdkDemo", "TopVPNSdkDemo onTrimMemory");

        super.onTrimMemory(level);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }



    public static MyApplication getInstance() {
        return instance;
    }

    public void addActivity(Activity activity) {
        if (activity != null) {
            activityList.add(activity);
            topActivity = activity;
        }
    }

    public void removeActivity(Activity activity) {
        if (activity != null) {
            if (activityList.contains(activity)) {
                activityList.remove(activity);
                if (activityList.size() != 0)
                    topActivity = activityList.get(activityList.size() - 1);
            }
        }
    }

    public void clearActivity() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        activityList.clear();
    }

    public void exit() {
        clearActivity();
        System.exit(0);
    }

    public int getAppCount() {
        return appCount;
    }

    public void setAppCount(int appCount) {
        this.appCount = appCount;
    }

    public static Activity getTopActivity() {
        return topActivity;
    }



}
