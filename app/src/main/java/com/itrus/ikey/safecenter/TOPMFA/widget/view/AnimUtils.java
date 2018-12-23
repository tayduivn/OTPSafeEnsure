package com.itrus.ikey.safecenter.TOPMFA.widget.view;


import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.itrus.ikey.safecenter.TOPMFA.R;

public class AnimUtils {


    /**
     * 获取顶部的动画
     *
     * @param context
     * @param from
     * @param to
     * @return
     */
    public static AnimationDrawable getTopDrawable(Context context, int from, int to) {
        Log.d("TEST", "FROM:" + from + "--TO:" + to);
        AnimationDrawable anim = new AnimationDrawable();
        anim.addFrame(getResourceDrawable(context, "top_" + from + "_0"), context.getResources().getInteger(R.integer.animationSpeed));
        anim.addFrame(getResourceDrawable(context, "top_" + from + "_1"), context.getResources().getInteger(R.integer.animationSpeed));
        anim.addFrame(getResourceDrawable(context, "top_" + to + "_0"), context.getResources().getInteger(R.integer.animationSpeed));
        anim.setOneShot(true);
        return anim;
    }


    //     <item android:drawable="@drawable/bottom_1_0" android:duration="@integer/animationWait" />
//  <item android:drawable="@drawable/bottom_1_1" android:duration="@integer/animationSpeed" />
//  <item android:drawable="@drawable/bottom_3_0" android:duration="@integer/animationSpeed" />

    /**
     * 获取底部的动画
     *
     * @param context
     * @param from
     * @param to
     * @return
     */
    public static AnimationDrawable getBottomDrawable(Context context, int from, int to) {
        AnimationDrawable anim = new AnimationDrawable();
        anim.addFrame(getResourceDrawable(context, "bottom_" + from + "_0"), context.getResources().getInteger(R.integer.animationWait));
        anim.addFrame(getResourceDrawable(context, "bottom_" + from + "_1"), context.getResources().getInteger(R.integer.animationSpeed));
        anim.addFrame(getResourceDrawable(context, "bottom_" + to + "_0"), context.getResources().getInteger(R.integer.animationSpeed));
        anim.setOneShot(true);
        return anim;
    }

    public static Drawable getResourceDrawable(Context context, String name) {
        int id = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        Drawable drawable = context.getResources().getDrawable(id);
        return drawable;
    }


    /**
     * 获取顶部的图片
     * R.drawable.top_2_0
     *
     * @param context
     * @param index
     * @return
     */
    public static int getTopDrawable(Context context, int index) {
        return context.getResources().getIdentifier("top_" + index + "_0", "drawable", context.getPackageName());
    }

    /**
     * 获取底部的图片
     * R.drawable.bottom_2_0
     *
     * @param context
     * @param index
     * @return
     */
    public static int getBottomDrawable(Context context, int index) {
        return context.getResources().getIdentifier("bottom_" + index + "_0", "drawable", context.getPackageName());
    }


}
