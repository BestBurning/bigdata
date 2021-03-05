package com.di1shuai.kafka.spring.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author Shea
 * @date 2021-03-05
 * @description
 */
@Data
@Accessors(chain = true)
public class DemoData implements Serializable {

    private Long id;

    private String data;

}
