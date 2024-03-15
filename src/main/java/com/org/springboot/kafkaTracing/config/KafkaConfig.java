package com.org.springboot.kafkaTracing.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.org.springboot.kafkaTracing.model.ExampleMessage;

@Configuration
@EnableKafka
public class KafkaConfig
{

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	/*@Value("${spring.kafka.consumer.group-id}")
	private String groupId;*/

	@Bean
	ProducerFactory<String, ExampleMessage> producerFactory()
	{
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		return new DefaultKafkaProducerFactory<>(configProps);
	}

	@Bean
	KafkaTemplate<String, ExampleMessage> kafkaTemplate(ProducerFactory<String, ExampleMessage> producerFactory)
	{
		return new KafkaTemplate<>(producerFactory);
	}

	@Bean
	ConsumerFactory<String, ExampleMessage> consumerFactory()
	{
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		// props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
				StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
				JsonDeserializer.class);
		// props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.org.springboot.kafkaTracing.model");
		return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(ExampleMessage.class));
	}

	@Bean
	ConcurrentKafkaListenerContainerFactory<String, ExampleMessage> kafkaListenerContainerFactory(ConsumerFactory<String, ExampleMessage> consumerFactory)
	{
		ConcurrentKafkaListenerContainerFactory<String, ExampleMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory);
		return factory;
	}
}
