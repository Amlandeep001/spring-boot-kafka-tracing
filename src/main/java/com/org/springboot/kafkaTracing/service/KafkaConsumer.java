package com.org.springboot.kafkaTracing.service;

import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.org.springboot.kafkaTracing.model.ExampleMessage;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaConsumer
{
	private final Tracer tracer;

	public KafkaConsumer(Tracer tracer)
	{
		this.tracer = tracer;
	}

	// @Observed
	/*@KafkaListener(topics = "${kafka.tracing.example-topic}", groupId = "${spring.kafka.consumer.group-id}")
	public void listen(ExampleMessage message)
	{
		log.info("Received message: {} ", message);
	}*/

	@KafkaListener(topics = "${kafka.tracing.example-topic}", groupId = "${spring.kafka.consumer.group-id}")
	// @Observed(name = "kafka-consume", contextualName = "kafka-consume")
	public void listen(ConsumerRecord<String, ExampleMessage> record)
	{
		Headers headers = record.headers();
		String traceId = null;

		// Extract traceId from headers
		if(headers.lastHeader("traceId") != null)
		{
			traceId = new String(headers.lastHeader("traceId").value(), StandardCharsets.UTF_8);
		}
		final Span newSpan;
		if(traceId != null)
		{
			// Continue the trace
			newSpan = tracer.nextSpan()
					.name("kafka-consume") // Set the span name
					.tag("kafka.consumer", "true") // Add custom tag
					.tag("traceId", traceId) // Add traceId as a tag
					.start(); // Starts the new span
		}
		else
		{
			log.warn("No traceId found in Kafka headers. Creating a new trace.");
			// Optional: Handle missing traceId (e.g., start a new trace)
			// Handle the case where no trace context is provided, for example, creating a new trace
			newSpan = tracer.nextSpan()
					.name("kafka-consume") // Set the span name
					.tag("kafka.consumer", "true") // Add custom tag
					.start();
		}

		try(Tracer.SpanInScope spanInScope = tracer.withSpan(newSpan))
		{
			// Log trace information along with the consumed message
			MDC.put("traceId", traceId); // Add traceId to MDC for logging context
			// Process the message
			ExampleMessage message = record.value();
			log.info("Consumed message: {} with traceId: {}", message, traceId);
		}
		finally
		{
			// Clear MDC after processing
			MDC.clear();

			// Finish the span after processing the message
			newSpan.end();
		}
	}

}
