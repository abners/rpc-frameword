package com.abners.nettyrpc.util;

import java.util.UUID;

/**
 * 类MD5Util.java的实现描述：TODO 类实现描述
 *
 * @author baoxing.peng 2021年02月28日 23:52:54
 */
public class UUIDUtil {

    public static String uuid() {

        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
