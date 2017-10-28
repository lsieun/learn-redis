package com.lsieun.redis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

@Configuration
@PropertySource("classpath:redis.properties")
public class RedisConfig {

    @Value("${redis.maxTotal}")
    private int redis_maxTotal;

    @Value("${redis.maxIdle}")
    private int redis_maxIdle;

    @Value("${redis.maxWaitMillis}")
    private int redis_maxWaitMillis;

    @Value("${redis.testOnBorrow}")
    private boolean redis_testOnBorrow;

    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig cfg = new JedisPoolConfig();

        cfg.setMaxTotal(redis_maxTotal);
        cfg.setMaxIdle(redis_maxIdle);
        cfg.setMaxWaitMillis(redis_maxWaitMillis);
        cfg.setTestOnBorrow(redis_testOnBorrow);

        return cfg;
    }

    @Bean
    public JedisCluster jedisCluster(JedisPoolConfig jedisPoolConfig) {
        Set<HostAndPort> clusterNodes = new HashSet<HostAndPort>();
        clusterNodes.add(new HostAndPort("192.168.80.30", 7001));
        clusterNodes.add(new HostAndPort("192.168.80.30", 7002));
        clusterNodes.add(new HostAndPort("192.168.80.30", 7003));
        clusterNodes.add(new HostAndPort("192.168.80.30", 7004));
        clusterNodes.add(new HostAndPort("192.168.80.30", 7005));
        clusterNodes.add(new HostAndPort("192.168.80.30", 7006));

        JedisCluster jedisCluster = new JedisCluster(clusterNodes,6000,jedisPoolConfig);

        return jedisCluster;

    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
