package com.itrus.ikey.safecenter.TOPMFA.utils.algorithm;


import java.security.NoSuchAlgorithmException;

public abstract class TimeBasedOTP {

    static final int[] DIGITS_POWER = {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000};
    static final long Tg = 0;
    static final long Tc = 60;

    protected int size;
    protected String algorithm;

    protected byte[] seed;


    public TimeBasedOTP(String algorithm) {
        this.algorithm = algorithm;

    }

    public int getSize() {
        return size;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void init(int size, byte[] seed) {
        if (size > 8 || size < 6) {
            throw new IllegalArgumentException("otp size must between 6 and 8");
        }
        this.size = size;
        this.seed = seed;
    }

    public String generate() {
        return generate(System.currentTimeMillis(), Tg, Tc);
    }

    public String generate(long To) {
        return generate(To, Tg, Tc);
    }

    public String generate(long To, long Tg, long Tc) {
        if (To < 0 || Tg < 0 || Tc < 0) {
            throw new IllegalArgumentException("To Tg Tc must great than 0");
        }
        byte[] r = processTime(To, Tg, Tc);
        byte[] s = processRandom(r);
        long odd = processTruncate(s);
        return processFormat(odd, size);
    }

    protected abstract byte[] processTime(long To, long Tg, long Tc);

    protected abstract byte[] processRandom(byte[] r);

    protected abstract long processTruncate(byte[] s);

    protected String processFormat(long r, int size) {
        String otp = Long.toString(r % DIGITS_POWER[size]);
        while (otp.length() < size) {
            otp = "0" + otp;
        }
        return otp;
    }

    protected byte[] int2byte(int a) {
        byte[] out = new byte[4];
        for (int i = 3; i >= 0; i--) {
            out[i] = (byte) (a & 0xff);
            a = a >> 8;
        }
        return out;
    }

    protected byte[] long2byte(long a) {
        byte[] out = new byte[8];
        for (int i = 7; i >= 0; i--) {
            out[i] = (byte) (a & 0xff);
            a = a >> 8;
        }
        return out;
    }


    public static TimeBasedOTP getInstance(String algorithm) throws NoSuchAlgorithmException {
        if ("HmacSHA1".equalsIgnoreCase(algorithm)) {
            return new HmacSHA1OTP();
        } else if ("SM3".equalsIgnoreCase(algorithm)) {
            return new Sm3OTP();
        } else if ("EasySHA1".equalsIgnoreCase(algorithm)) {
            return new EasySHA1OTP();
        } else if ("HmacSHA256".equalsIgnoreCase(algorithm)) {
            return new HmacSHA256OTP();
        } else if ("HmacSHA512".equalsIgnoreCase(algorithm)) {
            return new HmacSHA512OTP();
        } else {
            throw new NoSuchAlgorithmException("不支持的算法[" + algorithm + "]");
        }
    }

}
