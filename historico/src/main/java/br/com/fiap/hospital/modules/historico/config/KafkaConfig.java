package br.com.fiap.hospital.modules.historico.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Kafka Configuration for Historico module.
 * Spring Boot auto-configuration handles the basic Kafka setup.
 */
@Configuration
@EnableKafka
public class KafkaConfig {
    // Configuration is handled by Spring Boot's auto-configuration
    // based on spring.kafka.* properties in application.properties
}

