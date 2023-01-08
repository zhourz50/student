package com.mashang.util;

public class IdGenUtil {

    private static int num = 0;

    public synchronized static long gen() {
        Long timeTemp = System.currentTimeMillis();

        long id = Long.valueOf(timeTemp + "" + num++);

        return id;
    }
}
