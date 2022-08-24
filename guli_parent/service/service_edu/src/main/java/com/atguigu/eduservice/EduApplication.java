package com.atguigu.eduservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableFeignClients
@SpringBootApplication
@EnableDiscoveryClient   //nacos注册
@ComponentScan(basePackages = {"com.atguigu"})//在service_edu启动类中添加注解,设置包扫描规则
public class EduApplication {
    public static void main(String[] args) {
        SpringApplication.run(EduApplication.class,args);

    }
}
