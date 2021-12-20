package com.example.userregistration.configuration;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Data
@Slf4j
@Getter
@Setter
public class Message {
    private Long id;    //id
    private String msg; //消息
    private Date sendTime;  //时间戳
}
