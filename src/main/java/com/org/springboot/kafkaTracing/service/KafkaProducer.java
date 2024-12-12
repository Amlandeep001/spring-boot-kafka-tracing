package com.org.springboot.kafkaTracing.service;

import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.org.springboot.kafkaTracing.model.ExampleMessage;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaProducer
{
	private final KafkaTemplate<String, ExampleMessage> kafkaTemplate;
	private final String topic;
	private final Tracer tracer;

	public KafkaProducer(KafkaTemplate<String, ExampleMessage> kafkaTemplate, @Value("${kafka.tracing.example-topic}") String topic, Tracer tracer)
	{
		this.kafkaTemplate = kafkaTemplate;
		this.topic = topic;
		this.tracer = tracer;
	}

	// @Observed
	/*public void sendMessage(ExampleMessage message)
	{
		kafkaTemplate.send(topic, message);
		log.info("Message sent to consumer {}", message);
	}*/

	public void sendMessage(ExampleMessage message)
	{
		// Get the current span to propagate tracing context
		Span currentSpan = tracer.currentSpan();
		Headers headers = new RecordHeaders();

		if(currentSpan != null)
		{
			// Add the current traceId to Kafka headers
			String traceId = currentSpan.context().traceId();
			headers.add("traceId", traceId.getBytes(StandardCharsets.UTF_8));
		}
		else
		{
			log.warn("No current span found. TraceId will not be propagated.");
		}

		// Create the ProducerRecord with headers
		ProducerRecord<String, ExampleMessage> record = new ProducerRecord<>(topic, null, null, null, message, headers);

		kafkaTemplate.send(record);
		log.info("Produced message: {}", record.value());
	}
}
