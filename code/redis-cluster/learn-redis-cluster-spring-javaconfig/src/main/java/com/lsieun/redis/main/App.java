package com.lsieun.redis.main;

import com.lsieun.redis.config.RedisConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import redis.clients.jedis.JedisCluster;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(RedisConfig.class);
        JedisCluster cluster = ctx.getBean(JedisCluster.class);

        for (int i=0; i<100; i++) {
            cluster.set("name" + i, "value" + i);
        }
        System.out.println(cluster.get("name4"));

        cluster.close();
    }
}
