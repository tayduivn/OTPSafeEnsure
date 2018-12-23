package com.itrus.ikey.safecenter.TOPMFA.utils.algorithm;


import org.bouncycastle.util.encoders.Hex;

import java.lang.reflect.UndeclaredThrowableException;
import java.security.MessageDigest;


public class Sm3OTP extends TimeBasedOTP {

    private static final int MIN_ID_LEN = 16;

    public Sm3OTP() {
        super("SM3");
    }

    public String generate(int C) {
        byte[] r = calculateRand(-1, C, null);
        byte[] s = processRandom(r);
        long odd = processTruncate(s);
        return processFormat(odd, size);
    }

    public String generate(byte[] Q) {
        if (Q.length < 4) {
            throw new IllegalArgumentException("");
        }
        byte[] r = calculateRand(-1, -1, Q);
        byte[] s = processRandom(r);
        long odd = processTruncate(s);
        return processFormat(odd, size);
    }

    public String generate(long To, long Tg, long Tc, int C, byte[] Q) {
        if (To < 0 || Tg < 0 || Tc < 0) {
            throw new IllegalArgumentException("To Tg Tc must great than 0");
        }
        byte[] r = processTime(To, Tg, Tc, C, Q);
        byte[] s = processRandom(r);
        long odd = processTruncate(s);
        return processFormat(odd, size);
    }

    protected byte[] calculateRand(long T, int _C, byte[] _Q) {
        int size = Math.max(((T >= 0 ? 8 : 0) + (_C >= 0 ? 4 : 0) + (_Q != null ? _Q.length : 0)), MIN_ID_LEN);
        byte[] id = new byte[size];
        int offset = 0;
        if (T >= 0) {
            System.arraycopy(long2byte(T), 0, id, offset, 8);
            offset += 8;
        }
        if (_C >= 0) {
            System.arraycopy(int2byte(_C), 0, id, offset, 4);
            offset += 4;
        }
        if (_Q != null) {
            System.arraycopy(_Q, 0, id, offset, _Q.length);
        }
        return id;
    }

    protected byte[] processTime(long To, long Tg, long Tc, int C, byte[] Q) {
        long T = (To - Tg) / 1000 / Tc;
        return calculateRand(T, C, Q);
    }

    protected byte[] processTime(long To, long Tg, long Tc) {
        return processTime(To, Tg, Tc, -1, null);
    }

    protected byte[] processRandom(byte[] random) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SM3");
            digest.update(seed, 0, seed.length);
            digest.update(random, 0, random.length);
            return digest.digest();
        } catch (Throwable e) {
            throw new UndeclaredThrowableException(e);
        }
    }

    protected long processTruncate(byte[] result) {
        long od = 0, sn = 0;
        for (int i = 0; i < result.length; i++) {
            sn = sn << 8 | result[i] & 255;
            if (i % 4 == 3) {
                od += sn;
                sn = 0;
            }
        }
        return od % 4294967296L;
    }


}
