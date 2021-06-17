package com.atguigu.gulimall.product.controller;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("kafka")
@SpringBootTest
public class TestKafkaProducerController {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate; //这里会报错，但不影响


   @GetMapping("/normal/{message}")
   public void sendMessage1(@PathVariable("message") String normalMessage) {
       kafkaTemplate.send("topic1", normalMessage);
   }
    // 消费监听
    @Test
    @KafkaListener(topics = {"topic1"})
    public void onMessage1(ConsumerRecord<?, ?> record){
        // 消费的哪个topic、partition的消息,打印出消息内容
        System.out.println("简单消费："+record.topic()+"-"+record.partition()+"-"+record.value());
    }
}
