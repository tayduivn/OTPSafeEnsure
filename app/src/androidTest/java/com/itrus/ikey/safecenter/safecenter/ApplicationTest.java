package com.itrus.ikey.safecenter.safecenter;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.itrus.ikey.safecenter.TOPMFA.utils.DensityUtil;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
        test1();
    }

    public void test1() {
        int dp = DensityUtil.px2dip(getContext(), 200);
        System.out.println(dp);
    }
}