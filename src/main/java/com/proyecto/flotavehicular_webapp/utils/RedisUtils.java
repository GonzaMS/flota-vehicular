package com.proyecto.flotavehicular_webapp.utils;

public class RedisUtils {
    private RedisUtils() {
    }

    public static String CacheKeyGenerator(String entity, Long id) {
        return entity + "_" + id;
    }
}
