package com.leo.gesturelibray.util;

import com.leo.gesturelibray.enums.LockMode;

/**
 * @Date 2018/8/13 下午10:59
 * @Author Jalen
 * @Email:c9n9m@163.com
 * @Description
 */
public interface OnCompleteListener {
    /**
     * 画完了
     */
    void onComplete(String password, int[] indexs);

    /**
     * 绘制错误
     */
    void onError(String errorTimes);

    /**
     * 密码太短
     */
    void onPasswordIsShort(int passwordMinLength);


    /**
     * 设置密码再次输入密码
     */
    void onAginInputPassword(LockMode mode, String password, int[] indexs);


    /**
     * 修改密码，输入新密码
     */
    void onInputNewPassword();

    /**
     * 两次输入密码不一致
     */
    void onEnteredPasswordsDiffer();

    /**
     * 密码输入错误次数，已达到设置次数
     */
    void onErrorNumberMany();
}
