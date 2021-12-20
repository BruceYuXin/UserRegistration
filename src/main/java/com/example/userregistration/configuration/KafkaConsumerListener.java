package com.example.userregistration.configuration;

import com.example.userregistration.entity.Customers;
import com.example.userregistration.services.MailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.wink.json4j.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

import com.alibaba.fastjson.JSON;

@Slf4j
@Configuration
@Component
public class KafkaConsumerListener {

    @Autowired
    private MailService mailService;

    @KafkaListener(topics = {"userregistration"}, containerFactory = "concurrenntListenerContainnerFactory")
    public void consumer(List<ConsumerRecord<String, String>> records, Acknowledgment acknowledgment) throws Exception {
        boolean sendTag = false;
        for (ConsumerRecord<String, String> record : records) {
            log.info("consume : " + record.topic() + "-" + record.partition() + "-" + record.value());
            //Customers customer = (Customers) JSON.toJSON(record.value());
            JSONObject obj = new JSONObject(record.value());
            sendTag = mailService.sendMail(obj.getString("email"));
            if (sendTag) {
                //手动提交
                acknowledgment.acknowledge();
            }
        }
    }
}
