package com.example.userregistration.controllers;

import com.example.userregistration.entity.Customers;
import com.example.userregistration.services.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mysql")
@Slf4j
public class MySQLTestController {

    @Autowired
    private CustomerService customerService;

    // http://localhost:8080/mysql/findfirstuser
    @RequestMapping(value = "/findfirstuser", method = RequestMethod.GET)
    public String findfirstuser() {
        List<Customers> userList = customerService.findFirstCustomer();
        for (Customers customers : userList) {
            log.info("*********** Username: {}", customers.getUsername());
        }
        return userList.toString();
    }
}
