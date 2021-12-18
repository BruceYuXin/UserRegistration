package com.example.userregistration.services.impl;

import com.example.userregistration.entity.Customers;
import com.example.userregistration.services.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    @Qualifier("stringRedisTemplate")
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public String setCustomer(String key, String value) {
        ValueOperations ops = stringRedisTemplate.opsForValue();
        ops.set(key, value, 2, TimeUnit.HOURS);
        return "success";
    }

    @Override
    public String getCustomer(String key) {
        ValueOperations ops = stringRedisTemplate.opsForValue();
        return (String) ops.get(key);
    }

    @Override
    public String deleteCustomer(String key) {
        stringRedisTemplate.delete(key);
        return "success";
    }
}
