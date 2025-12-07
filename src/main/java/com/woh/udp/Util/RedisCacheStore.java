package com.woh.udp.Util;


import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RedisCacheStore {
    public RedisCacheStore(RedisTemplate template) {
        this.template = template;
    }

    private final RedisTemplate template;

    public void putWithExpiration(Object key, Object value, long expiration) {
        template.opsForValue().set(key, value, expiration, TimeUnit.SECONDS);
    }

    public void put(Object key, Object value) {
        template.opsForValue().set(key, value);
    }

    public void putMap(Object key, Object mapKey, Object mapValue) {
        template.opsForHash().put(key, mapKey, mapValue);
    }
    public <T> T getMapValue(Object key,String mapKey){
        return (T) template.opsForHash().get(key,mapKey);
    }

    public void putList(Object key,String value){
        template.opsForList().rightPush(key,value);
    }
    public void removeList(Object key,String value){
        template.opsForList().remove(key,0,value);
    }
    public List<String> getList(Object key){
        return template.opsForList().range(key,0,-1);
    }

    public boolean setIfAbsent(Object key, Object value, long second) {
        return template.opsForValue().setIfAbsent(key, value, Duration.ofSeconds(second));
    }

    public void delete(Object key) {
        template.delete(key);
    }

    public <T> T get(Object key) {
        return (T) template.opsForValue().get(key);
    }
}
