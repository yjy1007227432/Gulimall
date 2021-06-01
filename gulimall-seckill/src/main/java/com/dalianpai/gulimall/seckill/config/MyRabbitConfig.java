package com.dalianpai.gulimall.seckill.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author WGR
 * @create 2020/7/27 -- 13:15
 */
@Slf4j
@Configuration
public class MyRabbitConfig {


    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }






}
