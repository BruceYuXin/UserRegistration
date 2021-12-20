package com.example.userregistration.controllers;

import com.example.userregistration.entity.Customers;
import com.example.userregistration.services.CustomerService;
import com.example.userregistration.services.MailService;
import com.example.userregistration.services.RedisService;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 *
 * Swagger address :
 * http://localhost:8080/swagger-ui.html
 *
 */
@Api(tags = "注册用户相关接口")
@RestController
@RequestMapping("/customer")
@Slf4j
public class CustomerController {
    private static final String SUCCESS_RESULT = "success";

    @Autowired
    private CustomerService customerService;

    @Autowired
    private RedisService redisService;

    /**
     * {
     * "username": "test2",
     * "password": "123456",
     * "email": "test1@test2.com",
     * "address": "test1"
     * }
     */
    // http://localhost:8080/customer/create
    @ApiOperation(value = "注册用户" ,  notes="新增注册")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String createCustomer(@RequestBody Customers customers) {
        String createResult = StringUtil.EMPTY_STRING;
        if (StringUtil.isNullOrEmpty(customers.getUsername())) {
            return "Username can not be null";
        }
        if (StringUtil.isNullOrEmpty(customers.getAddress())) {
            return "Address can not be null";
        }
        if (StringUtil.isNullOrEmpty(customers.getEmail())) {
            return "Email can not be null";
        }
        createResult = customerService.createCustomer(customers);
        return createResult;
    }

    @ApiOperation(value = "根据 email 查询用户" ,  notes="根据 email 查询用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", value = "用户 email", required = true, paramType = "query", dataType = "String")
    })
    // http://localhost:8080/customer/findbyemail?email=test
    @RequestMapping(value = "/findbyemail", method = RequestMethod.GET)
    public String findByEmail(@RequestParam(value = "email", required = true) String email) {
        return customerService.findByEmail(email);
    }

    @ApiOperation(value = "根据 username 查询用户" ,  notes="根据 username 查询用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, paramType = "query", dataType = "String")
    })
    // http://localhost:8080/customer/findbyusername?username=test
    @RequestMapping(value = "/findbyusername", method = RequestMethod.GET)
    public String findbyusername(@RequestParam(value = "username", required = true) String username) {
        return customerService.findByUsername(username);
    }

    /**
     * {
     * "username": "test2",
     * "password": "123456",
     * "email": "test2@test2.com",
     * "address": "test1"
     * }
     */
    // http://localhost:8080/customer/modify
    @ApiOperation(value = "修改用户" ,  notes="修改用户")
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public String modifyCustomer(@RequestBody Customers customers) {
        if (StringUtil.isNullOrEmpty(customers.getUsername())) {
            return "Username can not be null";
        }
        if (StringUtil.isNullOrEmpty(customers.getAddress())) {
            return "Address can not be null";
        }
        if (StringUtil.isNullOrEmpty(customers.getEmail())) {
            return "Email can not be null";
        }
        return customerService.modifyCustomer(customers);
    }

    /**
     *
     * {
     * "username": "test3",
     * "email": "test3@test3.com"
     * }
     *
     * OR
     *
     * {
     * "username": "test3"
     * }
     *
     */
    // http://localhost:8080/customer/delete
    @ApiOperation(value = "删除用户" ,  notes="删除用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, paramType = "query", dataType = "String")
    })
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String deleteCustomer(@RequestBody Customers customers) {
        if (StringUtil.isNullOrEmpty(customers.getUsername()) && StringUtil.isNullOrEmpty(customers.getEmail())) {
            return "Username or email can not be null";
        }
        return customerService.deleteCustomer(customers);
    }
}
