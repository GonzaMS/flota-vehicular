package com.proyecto.flotavehicular_webapp.utils;

public class RedisUtils {
    private RedisUtils() {
    }

    public static String CacheKeyGenerator(String entity, Object id) {
        return entity + "_" + id.toString();
    }

    public static String CacheKeyGenerator(String entity, Object... params) {
        StringBuilder keyBuilder = new StringBuilder(entity);
        for (Object param : params) {
            keyBuilder.append("_").append(param.toString());
        }
        return keyBuilder.toString();
    }
}