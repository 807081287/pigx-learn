package com.pig4cloud.pigx.common.core.util;

import java.util.Random;

/**
 * Created by Heyifan Cotter on 2018/12/28.
 */
public class RandomUtil {

    /**
     * 生成指定位数的随机数
     * @param length
     * @return
     */
    public static String getRandom(int length){
        String val = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            val += String.valueOf(random.nextInt(10));
        }
        return val;
    }
    public static void main(String[] args) {
        System.out.println(getRandom(7));
    }

}
