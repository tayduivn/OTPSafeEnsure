/**
 * SoundAliveUtils.java [V 1.0.0]
 * classes : com.bh_face_alive.utile.SoundAliveUtils
 * 姚述智  Create at 2016-3-2 下午3:49:55
 */
package com.itrus.ikey.safecenter.TOPMFA.utils;

import java.util.ArrayList;
import java.util.Random;

/**
 * 用作语音识别出现的数字和活体的随机数
 * <p/>
 * Created by STAR on 2016/8/17.
 */
public class SoundAliveUtils {

    private static final int[] NUMBERS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

    // private static final String[] ALIVE_TYPES = {"张嘴","眨眼"};

    private static final int[] ALIVE_INDEXS = {0, 1, 2, 3, 4, 5, 6};

    private static final int NUMBER_LENGTH = 6;

    private static String getRandomNumber() {
        return NUMBERS[(int) (Math.random() * NUMBERS.length)] + "";
    }

    public static String getRandomNumbers() {
        StringBuffer buff = new StringBuffer();
        String random;
        String temp = "";
        int count = 0;
        while (count < NUMBER_LENGTH) {
            random = getRandomNumber();
            if (!random.equals(temp) && !"2".equals(random) && !"8".equals(random)) {
                buff.append(random);
                temp = random;
                count++;
            }
        }
        return buff.toString();

    }

    public static int getAliveIndex() {
        return -1;
    }

    public static int[] getAliveTypes(ArrayList<Integer> list) {
        int count = 0;
        int typeSize = list.size();
        int[] aliveTypeItem = new int[typeSize];
        boolean isBol = true;
        Random mRandom = new Random();
        while (count < typeSize) {
            int round = mRandom.nextInt(typeSize);
            isBol = true;
//			Log.e(SoundAliveUtils.class.getSimpleName(), "--------------"+round);
            for (int i = 0; i < count; i++) {
                if (aliveTypeItem[i] == round) {
                    isBol = false;
                    break;
                }
            }
            if (isBol) {
                aliveTypeItem[count] = round;
                count++;
//				Log.e(SoundAliveUtils.class.getSimpleName(), "-------4-------");
            }
        }
        int[] aliveType = new int[typeSize];
        for (int i = 0; i < typeSize; i++) {
            aliveType[i] = list.get(aliveTypeItem[i]);
        }
        return aliveType;

    }


}
