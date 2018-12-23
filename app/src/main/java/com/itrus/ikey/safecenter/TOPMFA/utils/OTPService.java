package com.itrus.ikey.safecenter.TOPMFA.utils;

import org.bouncycastle.util.encoders.Base64;

import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OTPService {

    private static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final byte[] empty_sha1_digest = new byte[20];
    private static final String[] CHAR_ARRAY = {RandBuilder.ALL_CHAR, RandBuilder.LETTER_CHAR, RandBuilder.NUMBER_CHAR};


    public static String buildOTP(String username, long millis) {
        byte[] seed = digest(username, millis);
        return random(seed, 2);
    }

    public static boolean verify(String username, long millis, String code) {
        return buildOTP(username, millis).equalsIgnoreCase(code);
    }

    public static int getTime(long millis) {
        long time = System.currentTimeMillis();
        time += millis;
        time = (time / 100) % 600;
        return (int) time;
    }

    private static byte[] digest(String username, long millis) {
        username = username == null ? "" : username;
        long time = System.currentTimeMillis();
        time += millis;
        time = time / 1000;
        time = time - time % 60;
        time = time * 1000;

        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA1");
            digest.update(username.getBytes("UTF-8"));
            digest.update(format.format(new Date(time)).getBytes("UTF-8"));
            for (int i = 1; i <= 8; i++) {
                digest.update((byte) ((time >> 8 * (8 - i)) & 0xff));
            }
            return digest.digest();
        } catch (Exception ignore) {
        }
        return empty_sha1_digest;
    }

    private static String random(byte[] digest, int type) {
        byte[] temp = Base64.encode(digest);
        StringBuilder builder = new StringBuilder();
        String base = CHAR_ARRAY[type % 3];
        for (int i = 0; i < 24; i += 4) {
            int index = temp[i] + temp[i + 1] + temp[i + 2] + temp[i + 3];
            builder.append(base.charAt(index % base.length()));
        }
        return builder.toString();
    }
}