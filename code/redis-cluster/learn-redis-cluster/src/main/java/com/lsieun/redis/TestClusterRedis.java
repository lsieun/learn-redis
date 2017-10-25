package com.lsieun.redis;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

public class TestClusterRedis {

    public static void main(String[] args) {
        Set<HostAndPort> clusterNodes = new HashSet<HostAndPort>();
        clusterNodes.add(new HostAndPort("192.168.80.30", 7001));
        clusterNodes.add(new HostAndPort("192.168.80.30", 7002));
        clusterNodes.add(new HostAndPort("192.168.80.30", 7003));
        clusterNodes.add(new HostAndPort("192.168.80.30", 7004));
        clusterNodes.add(new HostAndPort("192.168.80.30", 7005));
        clusterNodes.add(new HostAndPort("192.168.80.30", 7006));

        JedisPoolConfig cfg = new JedisPoolConfig();
        cfg.setMaxTotal(100);
        cfg.setMaxIdle(20);
        cfg.setMaxWaitMillis(-1);
        cfg.setTestOnBorrow(true);

        JedisCluster cluster = new JedisCluster(clusterNodes,6000,100,cfg);

        String res1 = cluster.set("name", "RK");
        String res2 = cluster.set("age", "30");
        System.out.println("res1 = " + res1);
        System.out.println("res2 = " + res2);
        String res3 = cluster.get("name");
        String res4 = cluster.get("age");
        System.out.println("res3 = " + res3);
        System.out.println("res4 = " + res4);
    }
}
