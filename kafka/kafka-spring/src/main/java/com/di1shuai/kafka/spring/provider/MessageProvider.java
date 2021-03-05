package com.di1shuai.kafka.spring.provider;

import com.alibaba.fastjson.JSON;
import com.di1shuai.kafka.spring.domain.DemoData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Shea
 * @date 2021-03-04
 * @description
 */
@Component
@Slf4j
public class MessageProvider {


    @Autowired
    private KafkaTemplate kafkaTemplate;

    public static final AtomicLong producerCount = new AtomicLong(0L);

    public void sendData(DemoData data) {
        String value = JSON.toJSONString(data);
        try {
            kafkaTemplate.sendDefault(value).addCallback(
                    (result) -> {
                        producerCount.incrementAndGet();
                        log.info("数据推送成功:" + value);
                    },
                    (result) -> {
                        log.error("失败发送:" + value);
                        //Todo 存储  Kafka/RDBMS....
                    }
            );

        } catch (Exception e) {
            e.printStackTrace();
            log.error(MessageFormat.format("推送数据出错，topic:{0},data:{1}"
                    , kafkaTemplate.getDefaultTopic(), data));
        }

    }


}
