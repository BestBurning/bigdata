package com.di1shuai.kafka.spring.consumer;

import com.alibaba.fastjson.JSON;
import com.di1shuai.kafka.spring.domain.DemoData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Shea
 * @date 2021-03-04
 * @description
 */
@Component
@Slf4j
public class MessageConsumer {

    public static final AtomicLong consumerCount = new AtomicLong(0L);

    @KafkaListener(topics = "${spring.kafka.consumer.topics}")
    public void processMessage(String data) {
        log.info("消费到数据 -> "+ data);
        DemoData demoData = JSON.parseObject(data, DemoData.class);
        log.debug("反序列化 -> " + demoData.toString());
        long nowCount = consumerCount.incrementAndGet();
    }


}
