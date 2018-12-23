package com.itrus.ikey.safecenter.TOPMFA.utils.algorithm;


import org.bouncycastle.util.encoders.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EasySHA1OTP extends TimeBasedOTP {

    private static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final byte[] empty_fmt = new byte[19];
    private static final byte[] empty_sha1_digest = new byte[20];
    private final String[] CHAR_ARRAY = {"0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ",
            "abcdefghijkllmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
            , "0123456789"};

    public EasySHA1OTP() {
        super("EasySHA1");
    }

    protected byte[] processTime(long To, long Tg, long Tc) {
        long T = To - Tg;
        T = T / 1000;
        T = T - T % Tc;
        T = T * 1000;

        byte[] fmt = empty_fmt;
        try {
            fmt = format.format(new Date(T)).getBytes("UTF-8");
        } catch (UnsupportedEncodingException ignore) {
        }
        byte[] r = new byte[8 + fmt.length];
        System.arraycopy(fmt, 0, r, 0, fmt.length);
        for (int i = 1; i <= 8; i++) {
            r[fmt.length + i - 1] = ((byte) ((T >> 8 * (8 - i)) & 0xff));
        }
        return r;
    }

    protected byte[] processRandom(byte[] r) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA1");
            digest.update(seed);
            digest.update(r);
            return digest.digest();
        } catch (Exception ignore) {
        }
        return empty_sha1_digest;
    }

    protected long processTruncate(byte[] s) {
        byte[] temp = Base64.encode(s);
        StringBuilder builder = new StringBuilder();
        String base = CHAR_ARRAY[2];
        for (int i = 0; i < 24; i += 4) {
            int index = temp[i] + temp[i + 1] + temp[i + 2] + temp[i + 3];
            builder.append(base.charAt(index % base.length()));
        }
        return Long.valueOf(builder.toString());
    }

}
