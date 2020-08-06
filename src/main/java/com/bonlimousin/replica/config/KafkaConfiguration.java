package com.bonlimousin.replica.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.bonlimousin.replica.service.entitychange.EntityChangeVO;

@Configuration
public class KafkaConfiguration {

	@Autowired
	private KafkaProperties kafkaProperties;

	@Primary
	@Bean
	public ProducerFactory<String, Object> producerFactory() {
		return new DefaultKafkaProducerFactory<>(kafkaProperties.getProducerProps());
	}

	@Primary
	@Bean
	public KafkaTemplate<String, Object> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}

	@Bean
	public ProducerFactory<String, EntityChangeVO> entityChangeProducerFactory() {
		return new DefaultKafkaProducerFactory<>(kafkaProperties.getProducerProps(), null,
				new JsonSerializer<>());
	}

	@Bean
	public KafkaTemplate<String, EntityChangeVO> entityChangeKafkaTemplate() {
		return new KafkaTemplate<>(entityChangeProducerFactory());
	}
	
	@Primary
	@Bean
	public ConsumerFactory<String, Object> consumerFactory() {
		return new DefaultKafkaConsumerFactory<>(kafkaProperties.getConsumerProps());
	}

	@Primary
	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		return factory;
	}

	@Bean
	public ConsumerFactory<String, EntityChangeVO> entityChangeConsumerFactory() {
		return new DefaultKafkaConsumerFactory<>(kafkaProperties.getConsumerProps(), null,
				new JsonDeserializer<>(EntityChangeVO.class, false));
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, EntityChangeVO> entityChangeKafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, EntityChangeVO> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(entityChangeConsumerFactory());
		return factory;
	}
}
