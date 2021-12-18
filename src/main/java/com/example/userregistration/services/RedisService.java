package com.example.userregistration.services;

import com.example.userregistration.entity.Customers;

public interface RedisService {
    public String setCustomer(String key, String value);

    public String getCustomer(String key);

    public String deleteCustomer(String key);
}
