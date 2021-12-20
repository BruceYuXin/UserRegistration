package com.example.userregistration.services.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.example.userregistration.dao.CustomersMapper;
import com.example.userregistration.entity.Customers;
import com.example.userregistration.entity.CustomersExample;
import com.example.userregistration.services.CustomerService;
import com.example.userregistration.services.MailService;
import com.example.userregistration.services.RedisService;
import com.example.userregistration.utils.DateUtil;
import com.mysql.cj.util.StringUtils;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.example.userregistration.configuration.Message;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
//@ComponentScan(basePackages = {"com.example.userregistration.dao"})
//@MapperScan(basePackages = {"com.example.userregistration.dao"})
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomersMapper customersMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MailService mailService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public List<Customers> findFirstCustomer() {
        CustomersExample customersExample = new CustomersExample();
        CustomersExample.Criteria criteriariteria = customersExample.createCriteria();
        criteriariteria.andIdEqualTo(Long.parseLong("1"));
        return customersMapper.selectByExample(customersExample);
    }

    @Override
    public List<Customers> findCustomerByEmail(String email) {
        CustomersExample customersExample = new CustomersExample();
        CustomersExample.Criteria criteriariteria = customersExample.createCriteria();
        criteriariteria.andEmailEqualTo(email);
        return customersMapper.selectByExample(customersExample);
    }

    @HystrixCommand(fallbackMethod = "buildFallbackCreate",
            threadPoolKey = "createCustomerThreadPool",
            threadPoolProperties =
                    {@HystrixProperty(name = "coreSize",value="100"),
                            @HystrixProperty(name="maxQueueSize", value="100")},
            commandProperties={
                    @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="10000"),
                    @HystrixProperty(name="execution.isolation.strategy", value="SEMAPHORE"),
                    @HystrixProperty(name="execution.isolation.thread.interruptOnTimeout", value="true"),
                    @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="10000")}
    )
    @Override
    public String createCustomer(Customers customers) {
        List<Customers> customersByEmailList = findCustomerByEmail(customers.getEmail());
        List<Customers> customersByUsernameList = findCustomerByUsername(customers.getUsername());
        if (CollectionUtils.isEmpty(customersByEmailList) && CollectionUtils.isEmpty(customersByUsernameList)) {
            Date createDate = DateUtil.toDate(DateUtil.formatDateTime(new Date()));
            customers.setCreate_date(createDate);
            customers.setUpdate_date(createDate);
            int num = customersMapper.insert(customers);
            if (num == 1) {

                Message message = new Message();
                message.setId(System.currentTimeMillis());
                message.setMsg(JSON.toJSON(customers).toString());
                message.setSendTime(new Date());

                ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send("userregistration", JSON.toJSON(customers).toString());
                future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
                    @Override
                    public void onFailure(@NonNull Throwable throwable) {
                        //错误结果
                        log.info("+++++++++++++++++++++  Errors happened when sending message : " + throwable.getMessage());
                    }
                    @Override
                    public void onSuccess(SendResult<String, String> result) {
                        //正确结果
                        log.info("+++++++++++++++++++++  Success in sending message : " + result.getProducerRecord());
                    }
                });

                /*String customerJSONStr = JSON.toJSON(customers).toString();
                redisService.setCustomer(customers.getUsername(), customerJSONStr);
                redisService.setCustomer(customers.getEmail(), customerJSONStr);
                mailService.sendMail(customers.getEmail());*/
                return customers.getUsername() + " is successfully registered.";
            } else {
                return customers.getUsername() + " fail to be registered.";
            }
        } else {
            StringBuffer resultInfoSB = new StringBuffer("Fail to register. ");
            if (!CollectionUtils.isEmpty(customersByEmailList)) {
                resultInfoSB.append(customers.getEmail());
                resultInfoSB.append(" has already been registered.");
            } else if (!CollectionUtils.isEmpty(customersByUsernameList)) {
                resultInfoSB.append(customers.getUsername());
                resultInfoSB.append(" has already been registered.");

            }
            return resultInfoSB.toString();
        }
    }

    @HystrixCommand(fallbackMethod = "buildFallbackSearchByUsernameList",
            threadPoolKey = "findCustomerThreadPool",
            threadPoolProperties =
                    {@HystrixProperty(name = "coreSize",value="200"),
                            @HystrixProperty(name="maxQueueSize", value="200")},
            commandProperties={
                    @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="10000"),
                    @HystrixProperty(name="execution.isolation.strategy", value="SEMAPHORE"),
                    @HystrixProperty(name="execution.isolation.thread.interruptOnTimeout", value="true"),
                    @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="10000")}
    )
    @Override
    public String findByUsername(String username) {
        JSONArray jsonArray = new JSONArray();
        String customerJSONStr = redisService.getCustomer(username);
        if (StringUtils.isNullOrEmpty(customerJSONStr)) {
            List<Customers> resultList = findCustomerByUsernameLike(username);
            if (CollectionUtils.isEmpty(resultList)) {
                return "no result is found.";
            } else {
                for (Customers customers : resultList) {
                    customerJSONStr = JSON.toJSON(customers).toString();
                    jsonArray.add(customerJSONStr);
                    redisService.setCustomer(customers.getUsername(), customerJSONStr);
                    redisService.setCustomer(customers.getEmail(), customerJSONStr);
                }
                return jsonArray.toString();
            }
        } else {
            return customerJSONStr;
        }
    }

    @Override
    public List<Customers> findCustomerByUsername(String username) {
        CustomersExample customersExample = new CustomersExample();
        CustomersExample.Criteria criteriariteria = customersExample.createCriteria();
        criteriariteria.andUsernameEqualTo(username);
        return customersMapper.selectByExample(customersExample);
    }

    @Override
    public List<Customers> findCustomerByEmailLike(String email) {
        CustomersExample customersExample = new CustomersExample();
        CustomersExample.Criteria criteriariteria = customersExample.createCriteria();
        criteriariteria.andEmailLike(email + "%");
        return customersMapper.selectByExample(customersExample);
    }

    @HystrixCommand(fallbackMethod = "buildFallbackSearchByEmailList",
            threadPoolKey = "findCustomerThreadPool",
            threadPoolProperties =
                    {@HystrixProperty(name = "coreSize",value="200"),
                            @HystrixProperty(name="maxQueueSize", value="200")},
            commandProperties={
                    @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="10000"),
                    @HystrixProperty(name="execution.isolation.strategy", value="SEMAPHORE"),
                    @HystrixProperty(name="execution.isolation.thread.interruptOnTimeout", value="true"),
                    @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="10000")}
    )
    @Override
    public String findByEmail(String email) {
        JSONArray jsonArray = new JSONArray();
        String customerJSONStr = redisService.getCustomer(email);
        if (StringUtils.isNullOrEmpty(customerJSONStr)) {
            List<Customers> resultList = findCustomerByEmail(email);
            if (CollectionUtils.isEmpty(resultList)) {
                log.info("Do not find {} in Database", email);
                return "no result is found.";
            } else {
                for (Customers customers : resultList) {
                    customerJSONStr = JSON.toJSON(customers).toString();
                    jsonArray.add(customerJSONStr);
                    redisService.setCustomer(customers.getUsername(), customerJSONStr);
                    redisService.setCustomer(customers.getEmail(), customerJSONStr);
                }
                return jsonArray.toString();
            }
        } else {
            log.info("Do not find {} in Redis", email);
            return customerJSONStr;
        }
    }

    @Override
    public List<Customers> findCustomerByUsernameLike(String username) {
        CustomersExample customersExample = new CustomersExample();
        CustomersExample.Criteria criteriariteria = customersExample.createCriteria();
        criteriariteria.andUsernameLike(username + "%");
        return customersMapper.selectByExample(customersExample);
    }

    @HystrixCommand(fallbackMethod = "buildFallbackModify",
            threadPoolKey = "modifyCustomerThreadPool",
            threadPoolProperties =
                    {@HystrixProperty(name = "coreSize",value="100"),
                            @HystrixProperty(name="maxQueueSize", value="100")},
            commandProperties={
                    @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="10000"),
                    @HystrixProperty(name="execution.isolation.strategy", value="SEMAPHORE"),
                    @HystrixProperty(name="execution.isolation.thread.interruptOnTimeout", value="true"),
                    @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="10000")}
    )
    @Override
    public String modifyCustomer(Customers customers) {
        List<Customers> customersByEmailList = findCustomerByEmail(customers.getEmail());
        List<Customers> customersByUsernameList = findCustomerByUsername(customers.getUsername());
        if (CollectionUtils.isEmpty(customersByEmailList) && CollectionUtils.isEmpty(customersByUsernameList)) {
            return "Do not find related customers";
        } else {
            int num = 0;
            Customers updateCus = null;
            Date updateDate = DateUtil.toDate(DateUtil.formatDateTime(new Date()));
            String oldEmail = null;
            if (!CollectionUtils.isEmpty(customersByEmailList)) {
                updateCus = customersByEmailList.get(0);
                updateCus.setAddress(customers.getAddress());
                oldEmail = updateCus.getEmail();
                updateCus.setEmail(customers.getEmail());
                updateCus.setPassword(customers.getPassword());
                updateCus.setUpdate_date(updateDate);
                num = customersMapper.updateCustomer(updateCus);
            } else if (!CollectionUtils.isEmpty(customersByUsernameList)) {
                updateCus = customersByUsernameList.get(0);
                updateCus.setAddress(customers.getAddress());
                oldEmail = updateCus.getEmail();
                updateCus.setEmail(customers.getEmail());
                updateCus.setPassword(customers.getPassword());
                updateCus.setUpdate_date(updateDate);
                num = customersMapper.updateCustomer(updateCus);
            }
            if (num > 1 && updateCus != null) {
                // 延迟双删
                redisService.deleteCustomer(customers.getUsername());
                redisService.deleteCustomer(oldEmail);
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    log.error(e.toString());
                }
                redisService.deleteCustomer(customers.getUsername());
                redisService.deleteCustomer(oldEmail);
            }
            return "Successfully modify";
        }
    }

    @HystrixCommand(fallbackMethod = "buildFallbackDelete",
            threadPoolKey = "deleteCustomerThreadPool",
            threadPoolProperties =
                    {@HystrixProperty(name = "coreSize",value="100"),
                            @HystrixProperty(name="maxQueueSize", value="100")},
            commandProperties={
                    @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="10000"),
                    @HystrixProperty(name="execution.isolation.strategy", value="SEMAPHORE"),
                    @HystrixProperty(name="execution.isolation.thread.interruptOnTimeout", value="true"),
                    @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="10000")}
    )
    @Override
    public String deleteCustomer(Customers customers) {
        List<Customers> customersByUsernameList = null;
        if (customers.getUsername() != null && !customers.getUsername().isEmpty()) {
            customersByUsernameList = findCustomerByUsername(customers.getUsername());
        }
        List<Customers> customersByEmailList = null;
        Customers deletedCustomers = null;
        if (customers.getEmail() != null && !customers.getEmail().isEmpty()) {
            customersByEmailList = findCustomerByEmail(customers.getEmail());
        }
        if (CollectionUtils.isEmpty(customersByUsernameList) && CollectionUtils.isEmpty(customersByEmailList)) {
            return "Do not find related customers";
        } else {
            if (!CollectionUtils.isEmpty(customersByUsernameList)) {
                deletedCustomers = customersByUsernameList.get(0);
            } else if (CollectionUtils.isEmpty(customersByEmailList)) {
                deletedCustomers = customersByEmailList.get(0);
            }
        }
        int recordNum = customersMapper.deleteCustomer(deletedCustomers);
        if (recordNum > 0) {
            redisService.deleteCustomer(deletedCustomers.getUsername());
            redisService.deleteCustomer(deletedCustomers.getEmail());
            return customers.getUsername() + " has been successfully deleted.";
        } else {
            return customers.getUsername() + " fail to be deleted";
        }
    }

    private String buildFallbackCreate(Customers customers) {
        log.info("Fallbakck for creating customer: {}", customers.getUsername());
        return "Sorry to fail to register. Please try again.";
    }

    private String buildFallbackModify(Customers customers) {
        log.info("Fallbakck for customer: {}", customers.getUsername());
        return "Due to network issue. Please try again.";
    }

    private String buildFallbackSearchByEmailList(String email) {
        log.info("Fallbakck for querying email: {}", email);
        return "Due to network issue. Please try again.";
    }

    private String buildFallbackSearchByUsernameList(String username) {
        log.info("Fallbakck for querying username: {}", username);
        return "Due to network issue. Please try again.";
    }

    private String buildFallbackDelete(Customers customers) {
        log.info("Fallbakck for delete customer: {}", customers.getUsername() != null ? customers.getUsername() : customers.getEmail());
        return "Sorry to fail to delete. Please try again.";
    }
}
