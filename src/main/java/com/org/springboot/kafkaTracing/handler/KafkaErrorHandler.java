package com.org.springboot.kafkaTracing.handler;

import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class KafkaErrorHandler implements KafkaListenerErrorHandler
{
	@Override
	public Object handleError(Message<?> message, ListenerExecutionFailedException exception)
	{
		throw new ListenerExecutionFailedException("Kafka listener error", exception);
	}
}
