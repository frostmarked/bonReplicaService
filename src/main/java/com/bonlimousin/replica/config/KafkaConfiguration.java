package com.bonlimousin.replica.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
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
}
