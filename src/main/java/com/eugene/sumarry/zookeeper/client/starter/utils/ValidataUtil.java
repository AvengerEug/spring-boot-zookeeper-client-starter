package com.eugene.sumarry.zookeeper.client.starter.utils;

import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

public final class ValidataUtil {

    public static <T> T validAndGet(Environment environment, String key, Class<T> clazz) {
        T retryTime = environment.getProperty(key, clazz);
        Assert.notNull(retryTime, "Please config " + key + " attribute from application.yml or application.properties");
        return retryTime;
    }
}
