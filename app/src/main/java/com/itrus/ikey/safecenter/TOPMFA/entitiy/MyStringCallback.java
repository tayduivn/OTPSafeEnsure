package com.itrus.ikey.safecenter.TOPMFA.entitiy;

import android.content.Intent;
import android.util.Log;


import com.itrus.ikey.safecenter.TOPMFA.activity.MainActivity;
import com.itrus.ikey.safecenter.TOPMFA.utils.DialogUtil;
import com.itrus.ikey.safecenter.TOPMFA.utils.SpUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * Created by star on 2017/3/31/031.
 */

public abstract class MyStringCallback extends StringCallback {
    @Override
    public void onError(Call call, Exception e, int id) {
        if (e.getMessage().contains("401")) {
            DialogUtil.dismissProgress();
            Log.d("TEST11", "401:" + e.getMessage());

        }

    }
}
