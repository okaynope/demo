package com.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.domain.geo.Metrics;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = com.demo.DemoApplication.class)
public class RedisTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings() {
        String redisKey = "test:counts";
        int i = 0;
        Set redisKeys = new HashSet();
        redisKeys.add(redisKey);
        redisTemplate.opsForValue().set(redisKey, "100"); //set test:counts
        redisKeys.add(redisKey + i);
        redisTemplate.opsForValue().set(redisKey + i++, "1"); //set test:counts0 1
        redisKeys.add(redisKey + i);
        redisTemplate.opsForValue().setIfAbsent(redisKey + i++, "1"); //setnx test:counts1 1
        redisKeys.add(redisKey + i);
        redisTemplate.opsForValue().setIfPresent(redisKey + i++, "1"); //setxx test:counts2 1
        redisKeys.add(redisKey + i);
        redisTemplate.opsForValue().getAndSet(redisKey + i++, "1"); //getset test:counts3 1
        redisKeys.add(redisKey + i);
        redisTemplate.opsForValue().set(redisKey + i, "1", 10, TimeUnit.SECONDS); //setex test:counts4 10 1
        System.out.println(redisTemplate.opsForValue().multiGet(redisKeys)); //mget
        System.out.println(redisTemplate.opsForValue().increment(redisKey, 12)); // incrby test:counts 12
        System.out.println(redisTemplate.opsForValue().decrement(redisKey, 11)); // decrby test:counts 11
    }

    @Test
    public void testHashes() {
        String redisKey = "test:users";
        redisTemplate.opsForHash().put(redisKey, "id", "1"); //hset test:users id 1
        redisTemplate.opsForHash().put(redisKey, "username", "张山"); //hset test:users username 张山
        System.out.println(redisTemplate.opsForHash().values(redisKey)); // hvals test:users
        System.out.println(redisTemplate.opsForHash().get(redisKey, "id")); // hget test:users id
        System.out.println(redisTemplate.opsForHash().get(redisKey, "username")); // hget test:users username
    }

    @Test
    public void testLists() {
        String redisKey = "test:ids";

        redisTemplate.opsForList().leftPush(redisKey, "101"); // lpush test:ids 101
        redisTemplate.opsForList().leftPush(redisKey, "102"); // lpush test:ids 102
        redisTemplate.opsForList().leftPush(redisKey, "103"); // lpush test:ids 103
        redisTemplate.opsForList().rightPush(redisKey, "104"); // rpush test:ids 103
        redisTemplate.opsForList().rightPush(redisKey, "105"); // rpush test:ids 103
        redisTemplate.opsForList().rightPush(redisKey, "106"); // rpush test:ids 103

        System.out.println(redisTemplate.opsForList().size(redisKey));  // llen test:ids
        System.out.println(redisTemplate.opsForList().index(redisKey, 0)); // lindex test:ids 0
        System.out.println(redisTemplate.opsForList().range(redisKey, 0, 5)); // lrange test:ids 0 5

        System.out.println(redisTemplate.opsForList().leftPop(redisKey)); // lpop test:ids
        System.out.println(redisTemplate.opsForList().leftPop(redisKey)); // lpop test:ids
        System.out.println(redisTemplate.opsForList().leftPop(redisKey)); // lpop test:ids

        System.out.println(redisTemplate.opsForList().rightPop(redisKey)); // rpop test:ids
        System.out.println(redisTemplate.opsForList().rightPop(redisKey)); // rpop test:ids
        System.out.println(redisTemplate.opsForList().rightPop(redisKey)); // rpop test:ids
    }

    @Test
    public void testSets() {
        String redisKey1 = "test:teachers1";

        redisTemplate.opsForSet().add(redisKey1, "刘备", "张飞", "关羽", "赵云", "诸葛亮"); // sadd test:teachers1 刘备 张飞 关羽 赵云 诸葛亮

        String redisKey2 = "test:teachers2";

        redisTemplate.opsForSet().add(redisKey2, "刘备", "刘彻", "刘秀");  // sadd test:teachers2 刘备 刘彻 刘秀

        System.out.println(redisTemplate.opsForSet().difference(redisKey1, redisKey2)); // sdiff test:teachers1 test:teachers2
        System.out.println(redisTemplate.opsForSet().size(redisKey1)); // scard test:teachers1
        System.out.println(redisTemplate.opsForSet().pop(redisKey1)); // spop test:teachers1
        System.out.println(redisTemplate.opsForSet().members(redisKey1)); // smembers test:teachers1
    }

    @Test
    public void testSortedSets() {
        String redisKey = "test:students";

        redisTemplate.opsForZSet().add(redisKey, "唐僧", 80);  //zadd test:students 80 唐僧
        redisTemplate.opsForZSet().add(redisKey, "悟空", 90); //zadd test:students 90 悟空
        redisTemplate.opsForZSet().add(redisKey, "猪八戒", 193); //zadd test:students 193 猪八戒
        redisTemplate.expire("test:teachers", 10, TimeUnit.SECONDS);

        System.out.println(redisTemplate.opsForZSet().zCard(redisKey)); // zcard test:students
        System.out.println(redisTemplate.opsForZSet().score(redisKey, "猪八戒"));
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey, "猪八戒"));
        System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey, 0, 2));
    }

    @Test
    public void testGeospatial() {
        // 添加地理位置信息
        redisTemplate.opsForGeo().add("locations", new Point(13.361389, 38.115556), "北京");
        // 执行结果: 成功添加 北京 的地理位置信息 (经度: 13.361389, 纬度: 38.115556)
        // 对应的 Redis 命令: GEOADD locations 13.361389 38.115556 北京

        redisTemplate.opsForGeo().add("locations", new Point(15.087269, 37.502669), "上海");
        // 执行结果: 成功添加 上海 的地理位置信息 (经度: 15.087269, 纬度: 37.502669)
        // 对应的 Redis 命令: GEOADD locations 15.087269 37.502669 上海

        // 获取地理位置信息
        List<Point> list = redisTemplate.opsForGeo().<Point>position("locations", "北京");
        Point palermoPosition = list.get(0);
        // 执行结果: 获取 北京 的地理位置信息 (经度: 13.361389, 纬度: 38.115556)
        // 对应的 Redis 命令: GEOPOS locations 北京
        System.out.println("北京: " + palermoPosition);

        // 计算两个位置之间的距离
        Distance distance = redisTemplate.opsForGeo().distance("locations", "北京", "上海", RedisGeoCommands.DistanceUnit.KILOMETERS);
        // 执行结果: 计算 北京 和 上海 之间的距离 (单位: 公里)
        // 对应的 Redis 命令: GEODIST locations 北京 上海 km
        System.out.println("北京 - 上海: " + distance.getValue() + " km");

        // 查询指定半径内的位置
        Circle circle = new Circle(new Point(15.0, 37.0), new Distance(200, Metrics.KILOMETERS));
        GeoResults<RedisGeoCommands.GeoLocation<Object>> results = redisTemplate.opsForGeo().radius("locations", circle, RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance().includeCoordinates());
        // 执行结果: 查询以 (15.0, 37.0) 为中心，半径为 200 公里的城市，坐标，距离中心距离，平均距离
        // 对应的 Redis 命令: GEORADIUS locations 15.0 37.0 200 km WITHDIST WITHCOORD
        System.out.println(results);
    }

    @Test
    public void testHyperLogLog() {
        // 添加元素到 HyperLogLog
        redisTemplate.opsForHyperLogLog().add("test:hll:00", "user1", "user2", "user3");
        // 执行结果: 成功添加用户 user1, user2, user3 到 HyperLogLog
        // 对应的 Redis 命令: PFADD test:hll:00 user1 user2 user3

        // 添加更多元素到 HyperLogLog
        redisTemplate.opsForHyperLogLog().add("test:hll:00", "user4", "user5");
        // 执行结果: 成功添加用户 user4, user5 到 HyperLogLog
        // 对应的 Redis 命令: PFADD test:hll:00 user4 user5

        // 获取 HyperLogLog 的近似唯一元素数
        long approximateCount = redisTemplate.opsForHyperLogLog().size("test:hll:00");
        // 执行结果: 获取 HyperLogLog 的近似唯一元素数
        // 对应的 Redis 命令: PFCOUNT test:hll:00
        System.out.println("获取 test:hll:00 的近似唯一元素数: " + approximateCount);
    }

    // 200万重复数据的独立总数
    @Test
    public void testHyperLogLog1() {
        String redisKey = "test:hll:01";

        for (int i = 1; i <= 100000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey, String.valueOf(i));
        }

        for (int i = 1; i <= 100000; i++) {
            int r = (int) (Math.random() * 100000 + 1);
            redisTemplate.opsForHyperLogLog().add(redisKey, String.valueOf(r));
        }
        Long size = redisTemplate.opsForHyperLogLog().size(redisKey);
        System.out.println(size);
    }

    // 将三组数据合并，在合并后的重复数据中统计
    @Test
    public void testHyperLogLog2() {
        String redisKey1 = "test:hll:02";
        for (int i = 1; i <= 10000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey1, String.valueOf(i));
        }
        String redisKey2 = "test:hll:03";
        for (int i = 5001; i <= 15000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey2, String.valueOf(i));
        }
        String redisKey3 = "test:hll:04";
        for (int i = 10001; i <= 20000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey3, String.valueOf(i));
        }

        System.out.println(redisTemplate.opsForHyperLogLog().union("unionKey", redisKey2, redisKey3, redisKey1));
    }

    // 统计一组数据布尔值
    @Test
    public void testBitMap() {
        String redisKey = "test:bm:01";

        // 记录
        redisTemplate.opsForValue().setBit(redisKey, 1, true);
        // 对应的 Redis 命令: SETBIT bitmaps 1 1
        redisTemplate.opsForValue().setBit(redisKey, 4, false);
        // 对应的 Redis 命令: SETBIT bitmaps 4 1
        redisTemplate.opsForValue().setBit(redisKey, 7, true);
        // 对应的 Redis 命令: SETBIT bitmaps 7 1

        // 查询
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 0));
        // 对应的 Redis 命令: GETBIT test:bm:01 0
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 1));
        // 对应的 Redis 命令: GETBIT test:bm:01 1
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 2));
        // 对应的 Redis 命令: GETBIT test:bm:01 2
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 4));
        // 对应的 Redis 命令: GETBIT test:bm:01 4

        // 统计
        Object obj = redisTemplate.execute((new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.bitCount(redisKey.getBytes());
            }
        }));
        System.out.println(obj);
    }

    // 统计三组数据布尔值，并作or运算
    @Test
    public void testBitMapOperation() {
        String redisKey1 = "test:bm:1";
        redisTemplate.opsForValue().setBit(redisKey1, 0, true);
        redisTemplate.opsForValue().setBit(redisKey1, 1, true);
        redisTemplate.opsForValue().setBit(redisKey1, 2, false);
        String redisKey2 = "test:bm:2";
        redisTemplate.opsForValue().setBit(redisKey2, 2, true);
        redisTemplate.opsForValue().setBit(redisKey2, 3, true);
        redisTemplate.opsForValue().setBit(redisKey2, 4, false);
        String redisKey3 = "test:bm:3";
        redisTemplate.opsForValue().setBit(redisKey3, 4, false);
        redisTemplate.opsForValue().setBit(redisKey3, 5, true);
        redisTemplate.opsForValue().setBit(redisKey3, 6, true);

        String redisKey = "test:bm:or";
        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.bitOp(RedisStringCommands.BitOperation.OR,
                        redisKey.getBytes(), redisKey1.getBytes(), redisKey2.getBytes(), redisKey3.getBytes());
                return connection.bitCount(redisKey.getBytes());
            }
        });

        System.out.println(obj);

        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 0));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 1));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 2));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 3));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 4));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 5));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 6));
    }
}
