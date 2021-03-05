package com.di1shuai.kafka.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Shea
 * @date 2021-03-04
 * @description
 */
@SpringBootApplication
@Slf4j
public class SpringKafkaApplication {


    public static void main(String[] args) throws UnknownHostException {

        ConfigurableApplicationContext application = SpringApplication.run(SpringKafkaApplication.class,args);
        Environment env = application.getEnvironment();
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port");
        String path = env.getProperty("server.servlet.context-path");
        log.info("\n----------------------------------------------------------\n\t" +
                "Application Dtwave-Boot is running! Access URLs:\n\t" +
                "Local: \t\t\thttp://localhost:" + port + path + "/\n\t" +
                "External: \t\thttp://" + ip + ":" + port + path + "/\n\t" +
                "Swagger-UI: \thttp://" + ip + ":" + port + path + "/swagger-ui/index.html\n" +
                "----------------------------------------------------------");
    }

}
