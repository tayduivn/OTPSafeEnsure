package com.itrus.ikey.safecenter.TOPMFA.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.itrus.ikey.safecenter.TOPMFA.R;
import com.itrus.ikey.safecenter.TOPMFA.utils.DialogClickCallback;
import com.itrus.ikey.safecenter.TOPMFA.utils.ScreenUtils;

/**
 * @Date 2018/9/4 07:19
 * @Author Jalen
 * @Email:c9n9m@163.com
 * @Description
 */
@SuppressLint("ValidFragment")
public class TokenCopyDialogFragment extends AppCompatDialogFragment {

    private TextView contentTv;
    private String content;
    private DialogClickCallback dialogClickCallback;

    public TokenCopyDialogFragment() {
    }

    public TokenCopyDialogFragment(String content, DialogClickCallback dialogClickCallback) {
        this.content = content;
        this.dialogClickCallback = dialogClickCallback;
    }

    @Override
    public void onStart() {
        super.onStart();
        setTranslunteFull();
    }

    private void setTranslunteFull() {

        Window win = getDialog().getWindow();
        // 一定要设置Background，如果不设置，window属性设置无效
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //获取屏幕分辨率
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        WindowManager.LayoutParams params = win.getAttributes();
        params.gravity = Gravity.BOTTOM;
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        params.width = ScreenUtils.getScreenWith(getActivity().getApplicationContext()) * 84 / 100;
        params.height = ScreenUtils.getScreenHeight(getActivity().getApplicationContext()) * 42 / 100;
//        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.dimAmount = 0.0f;
//        params.x = ScreenUtils.getScreenWith(getActivity().getApplicationContext())*50/100;
        params.y = ScreenUtils.getScreenWith(getActivity().getApplicationContext())*58/100;
        win.setAttributes(params);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_token_sms, null);
        contentTv = (TextView) v.findViewById(R.id.dialog_content_tv);
        if (!TextUtils.isEmpty(content)) contentTv.setText(content);

        v.findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != dialogClickCallback) dialogClickCallback.confirm();
                TokenCopyDialogFragment.this.dismissAllowingStateLoss();
            }
        });
        v.findViewById(R.id.tv_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TokenCopyDialogFragment.this.dismissAllowingStateLoss();
            }
        });

        AlertDialog mAlertDialog = new AlertDialog.Builder(getContext(), R.style.TranslucentNoTitle)
                .create();
        mAlertDialog.setCanceledOnTouchOutside(true);
        mAlertDialog.setView(v);

        return mAlertDialog;
    }


    public void show(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }
}
