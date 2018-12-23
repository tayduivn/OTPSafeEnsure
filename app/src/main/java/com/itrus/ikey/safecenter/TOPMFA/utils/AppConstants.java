package com.itrus.ikey.safecenter.TOPMFA.utils;

/**
 * @Date 2018/8/14 18:04
 * @Author Jalen
 * @Email:c9n9m@163.com
 * @Description
 */

public class AppConstants {

    public static String SP_CONFIG = "com.itrus.ikey.safecenter.TOPMFA_preferences";

    /**
     * 是否需要展示手势密码输入页面
     */
    public static boolean isGesture = false;
    /**
     * 是否设置手势密码
     */
    public static String HAS_GES_PWD = "hasGesPWD";
    public static String GES_PWD = "GesPWD";
    public static String INIT_GES_PWD = "initGesPWD";
    public static String CERT_PIN = "certPIN";
    public static String IS_FIRST = "isFirst";
    public static String USER_NAME = "username";
    public static String IS_BINDING = "isBinding";//判断用户是否有绑定
    public static String IS_FROM_WELCOME = "is_from_welcome";
    public static String IS_FROM_FRAGMENT = "is_from_fragment";
    public static String TITLE = "title";
    public static String EMPTY = "";

    /**
     * 手势密码 允许输入错误的次数
     */
    public static final int GESTURE_PWD_ERROR_COUNT = Integer.MAX_VALUE;


    public interface ACTION {
        /**
         * 关闭手势的通知
         */
        public static String CLOSE_GESTURE = "com.itrus.ikey.safecenter.TOPMFA.close_gesture";
        /**
         * 打开手势的广播
         */
        public static String OPEN_GESTURE = "com.itrus.ikey.safecenter.TOPMFA.open_gesture";
        /**
         *
         */
        public static String FRAGMENT_VERIFY_GESTURE = "com.itrus.ikey.safecenter.TOPMFA.fragment_verify_gesture";
        /**
         * 令牌短信确认的按钮
         */
        public static String TOKEN_SMS_CONFIRM = "com.itrus.ikey.safecenter.TOPMFA.token_sms_confirm";

    }


}
