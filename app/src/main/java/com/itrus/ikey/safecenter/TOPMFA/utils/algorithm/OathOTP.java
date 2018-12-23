package com.itrus.ikey.safecenter.TOPMFA.utils.algorithm;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.UndeclaredThrowableException;


public class OathOTP extends TimeBasedOTP {

    private long C = 0;

    public OathOTP(String algorithm) {
        super(algorithm);
    }

    protected byte[] processTime(long To, long Tg, long Tc) {
        C = (To - Tg) / 1000 / Tc;
        return long2byte(C);
    }

    protected byte[] processRandom(byte[] r) {
        try {
            Mac hmac = Mac.getInstance(getAlgorithm());
            SecretKeySpec macKey = new SecretKeySpec(seed, "RAW");
            hmac.init(macKey);
            return hmac.doFinal(r);
        } catch (Throwable e) {
            throw new UndeclaredThrowableException(e);
        }
    }

    protected long processTruncate(byte[] s) {
        int offset = s[s.length - 1] & 0x0f;
        int binary = 0;
        binary |= (s[offset] & 0x7f) << 24;
        binary |= (s[offset + 1] & 0xff) << 16;
        binary |= (s[offset + 2] & 0xff) << 8;
        binary |= (s[offset + 3] & 0xff);
        return binary;
    }

}