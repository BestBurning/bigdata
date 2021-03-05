package com.di1shuai.kafka.spring.domain;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Shea
 * @date 2021-03-05
 * @description
 */
@Data
@Accessors(chain = true)
public class MessageStatus {

    private Long consumerCount;

    private Long producerCount;

    private Boolean open;

}
