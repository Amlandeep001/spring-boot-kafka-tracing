package com.org.springboot.kafkaTracing.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.org.springboot.kafkaTracing.model.ExampleMessage;

@Service
public class KafkaProducer
{
	private final KafkaTemplate<String, ExampleMessage> kafkaTemplate;
	private final String topic;

	public KafkaProducer(KafkaTemplate<String, ExampleMessage> kafkaTemplate, @Value("${kafka.tracing.example-topic}") String topic)
	{
		this.kafkaTemplate = kafkaTemplate;
		this.topic = topic;
	}

	public void sendMessage(ExampleMessage message)
	{
		kafkaTemplate.send(topic, message);
	}
}
