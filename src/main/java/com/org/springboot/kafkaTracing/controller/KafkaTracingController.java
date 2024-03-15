package com.org.springboot.kafkaTracing.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.org.springboot.kafkaTracing.model.ExampleMessage;
import com.org.springboot.kafkaTracing.service.KafkaProducer;

@RestController
public class KafkaTracingController
{
	private final KafkaProducer producer;

	public KafkaTracingController(KafkaProducer producer)
	{
		this.producer = producer;
	}

	@PostMapping("/publish")
	public void sendEvents(@RequestBody ExampleMessage message)
	{
		producer.sendMessage(message);
	}
}
