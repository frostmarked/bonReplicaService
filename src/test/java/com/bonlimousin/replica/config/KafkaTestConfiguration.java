package com.bonlimousin.replica.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.containers.KafkaContainer;

@TestConfiguration
public class KafkaTestConfiguration {
	
	public static boolean started = false;
	public static KafkaContainer kafkaContainer;
	
    @Autowired
    public void kafkaProperties(KafkaProperties kafkaProperties) {
    	if(started) {
    		kafkaProperties.setBootStrapServers(kafkaContainer.getBootstrapServers());	
    	}
    }
    
    public static void startKafka() {
    	kafkaContainer = new KafkaContainer("5.5.0").withNetwork(null);
        kafkaContainer.start();
        started = true;
    }
    
    public static void stopKafka() {
    	if(started) {
    		kafkaContainer.stop();
    	}
    }
}
