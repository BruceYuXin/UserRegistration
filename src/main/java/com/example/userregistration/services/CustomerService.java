package com.example.userregistration.services;

import com.example.userregistration.entity.Customers;

import java.util.List;

public interface CustomerService {
    List<Customers> findFirstCustomer();
    String createCustomer(Customers customers);
    String findByUsername(String username);
    List<Customers> findCustomerByUsername(String username);
    List<Customers> findCustomerByUsernameLike(String username);
    String findByEmail(String email);
    List<Customers> findCustomerByEmail(String email);
    List<Customers> findCustomerByEmailLike(String email);
    String modifyCustomer(Customers customers);
    String deleteCustomer(Customers customers);
}
