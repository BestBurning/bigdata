package com.di1shuai.kafka.spring.controller;

import com.di1shuai.kafka.spring.consumer.MessageConsumer;
import com.di1shuai.kafka.spring.domain.DemoData;
import com.di1shuai.kafka.spring.domain.MessageStatus;
import com.di1shuai.kafka.spring.provider.MessageProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Shea
 * @date 2021-03-04
 * @description
 */
@Api(tags = {"kafka消息接口"}, value = "提供kafka消息相关的 Rest API")
@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageProvider messageProvider;

    private static boolean open = false;

    private static final AtomicLong autoId = new AtomicLong(0L);

    @ApiOperation("状态")
    @GetMapping("/status")
    public Object getStatus() {
        return new MessageStatus()
                .setConsumerCount(MessageConsumer.consumerCount.get())
                .setProducerCount(MessageProvider.producerCount.get())
                .setOpen(open);
    }

    @ApiOperation("开始发送")
    @GetMapping("/send/start")
    public String start() {
        open = true;
        new Thread(() -> {
            try {
                while (open) {
                    messageProvider.sendData(
                            new DemoData()
                                    .setData(UUID.randomUUID().toString())
                                    .setId(autoId.incrementAndGet())
                    );
                    Thread.sleep(1000L);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        return "Start OK";

    }


    @GetMapping("/send/close")
    public String close() {
        open = false;
        return "Close OK";
    }


}
