package com.itrus.ikey.safecenter.TOPMFA.utils;

import java.net.URLEncoder;

/**
 * @Date 2018/8/30 下午2:51
 * @Author Jalen
 * @Email:c9n9m@163.com
 * @Description
 */
public class URLEncodedUtil {


    public static String toURLEncoded(String paramString) {
        if (paramString == null || paramString.equals("")) {
            LogUtil.d("toURLEncoded error:"+paramString);
            return "";
        }

        try
        {
            String str = new String(paramString.getBytes(), "UTF-8");
            str = URLEncoder.encode(str, "UTF-8");
            return str;
        }
        catch (Exception localException)
        {
            LogUtil.e("toURLEncoded error:"+paramString, localException);
        }

        return "";
    }



}
