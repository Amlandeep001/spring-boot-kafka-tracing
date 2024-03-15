package com.org.springboot.kafkaTracing.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.org.springboot.kafkaTracing.model.ExampleMessage;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class KafkaConsumer
{
	@KafkaListener(topics = "${kafka.tracing.example-topic}", groupId = "${spring.kafka.consumer.group-id}")
	public void listen(ExampleMessage message)
	{
		log.info("Received message: {} ", message);
	}
}
