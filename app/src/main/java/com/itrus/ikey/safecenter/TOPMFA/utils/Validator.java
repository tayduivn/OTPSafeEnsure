package com.itrus.ikey.safecenter.TOPMFA.utils;


import com.itrus.raapi.implement.Des3Util;
import com.leo.gesturelibray.crypto.Base64;

import org.bouncycastle.util.encoders.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by STAR on 2016/8/17.
 */
public class Validator {


    public static boolean isBlank(String str) {
        boolean is = true;
        is = str == null || str.trim().equals("");

        return is;
    }

    public static boolean isNotEmpty(String str) {
        return !isBlank(str);
    }

    /**
     * 判断是否为浮点数或者整数
     *
     * @param str
     * @return true Or false
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("^(-?\\d+)(\\.\\d+)?$");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    /**
     * 判断是否为正确的邮件格式
     *
     * @param str
     * @return boolean
     */
    public static boolean isEmail(String str) {
        if (isBlank(str)) {
            return false;
        }
        return str.matches("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$");
    }

    /**
     * 判断字符串是否为合法手机号 11位 13 14 15 18开头
     *
     * @param str
     * @return boolean
     */
    public static boolean isMobile(String str) {
        if (isBlank(str)) {
            return false;
        }
        Pattern p = Pattern.compile("^(1[3,4,5,7,8][0-9])\\d{8}$");
        Matcher m = p.matcher(str);

        return m.matches();
    }

    public static boolean isNotMobile(String str) {

        return !isMobile(str);
    }

    /**
     * 由数字和字母组成，并且要同时含有数字和字母，且长度要在8-16位之间
     * ^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$
     * 分开来注释一下：
     * ^ 匹配一行的开头位置
     * (?![0-9]+$) 预测该位置后面不全是数字
     * (?![a-zA-Z]+$) 预测该位置后面不全是字母
     * [0-9A-Za-z] {8,16} 由8-16位数字或这字母组成
     * $ 匹配行结尾位置
     * 注：(?!xxxx) 是正则表达式的负向零宽断言一种形式，标识预该位置后不是xxxx字符。
     *
     * @param str
     * @return
     */
    public static boolean isNumAndChar8_20(String str) {
        if (isBlank(str)) {
            return false;
        }
        String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z`]{8,20}$";
        return str.matches(regex);
    }


    public static String bytes2Hex(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        String ret = "";
        for (int i = 0; i < data.length; i++) {
            String hex = Integer.toHexString(data[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    public static byte[] hexToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] data = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            data[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return data;
    }

    public static int strIndexToInt(String str) {
        return charToByte(str.toUpperCase().toCharArray()[0]) << 4 | charToByte(str.toUpperCase().toCharArray()[1]);
    }

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    // 判断是否执行成功
    public static boolean is90(byte[] data) {
        if (data == null) {
            return false;
        }
        int length = data.length;
        if ((data[length - 1] == (byte) 0x00)
                && (data[length - 2] == (byte) 0x90)) {
            return true;
        }
        return false;
    }

    public static int get2Byteslength(byte[] len) {
        int dataLen;
        dataLen = (len[0] & 0xFF) << 8;
        dataLen += len[1] & 0xFF;
        return dataLen;
    }

    public static byte[] get2Byteslength(int len) {
        byte[] dataLen = new byte[2];
        dataLen[0] = (byte) (len >> 8);
        dataLen[1] = (byte) len;
        return dataLen;
    }

    public static String getRandomString(int length) {
        String str = "ABCDEF0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(16);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }


    public static char getRandomChar() {
        String str = "ABCDEF0123456789";
        Random random = new Random();
        int number = random.nextInt(16);
        return str.charAt(number);
    }

    public static boolean checkSum(byte[] data1, byte[] data2) {
        if (getSum(data1) == getSum(data2)) {
            return true;
        }
        return false;
    }

    private static int getSum(byte[] msg) {
        int mSum = 0;
        for (int liv_Count = 0; liv_Count < msg.length; liv_Count++) {
            int mNum = msg[liv_Count];

            if (mNum < 0) {
                mNum += 256;
            }

            mSum += mNum;
        }
        return mSum;
    }

    /**
     * 数据格式转换相关 字符串格式化，每两个字符间间隔一位 补齐源字符str,fulllength为目标字节数
     */
    public static String fillStr1(String str, int fullLength) {
        StringBuilder backStr = new StringBuilder();
        int length = str.length();
        backStr.append(str);
        for (int i = 0; i < fullLength * 2 - length; i++) {
            backStr.append("0");
        }
        return backStr.toString();
    }

    public static String fillStr(String str, int fullLength) {
        StringBuilder backStr = new StringBuilder();
        int length = str.length() / 2;
        backStr.append(str);
        for (int i = 0; i < fullLength - length; i++) {
            backStr.append("00");
        }
        return backStr.toString();
    }

    public static String formatStr(String str) {
        StringBuilder backStr = new StringBuilder();
        int length = str.length();
        for (int i = 0; i < length / 2; i++) {
            String item = str.substring(i * 2, i * 2 + 2);
            backStr.append(item + " ");
        }
        return backStr.toString();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
    }

    public static boolean isCharCountOk(String str, int min, int max) {
        int count = str.length();
        int countChinest = StringsUtil.getChineseCharacterCount(str);

        int count2 = count + countChinest;//一个汉子顶二个字母
        return count2 > min && count2 < max;
    }

    public static String encryptPassword(String password, String random) {
        String temp = hexSh1Encoding(password.getBytes());
        if (temp != null) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-1");
                digest.update(temp.getBytes());
                digest.update(random.getBytes());
                temp = new String(Hex.encode(digest.digest()));
            } catch (Exception ignore) {
            }
        }
        return temp;
    }

    public static String encryptPassword(String password, String random, String iv) {
        String enc_pwd = null;
        String key = hexSh1Encoding(random.getBytes());
        if (iv.length() > 8) {
            iv = iv.substring(0, 8);
        } else {
            for (int i = iv.length(); i < 8; i++) {
                iv += "0";
            }
        }
        try {
            enc_pwd = Base64.encode(Des3Util.des3EncodeCBC(key.getBytes(), iv.getBytes(), password.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return enc_pwd;
    }

    public static String hexSh1Encoding(byte[] message) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA1");
            digest.update(message);
            return new String(Hex.encode(digest.digest()));
        } catch (NoSuchAlgorithmException ignore) {
        }
        return null;
    }

    public static String hexMD5Encoding(byte[] message) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(message);
            return new String(Hex.encode(digest.digest()));
        } catch (NoSuchAlgorithmException ignore) {
        }
        return null;
    }

    public static String getTime(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = new Date(System.currentTimeMillis());
        return sdf.format(date);
    }

}
