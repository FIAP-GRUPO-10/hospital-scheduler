package br.com.fiap.hospital.modules.agendamento.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Kafka Configuration for Agendamento module.
 * Spring Boot auto-configuration handles the basic Kafka setup.
 */
@Configuration
@EnableKafka
public class KafkaConfig {
    // Configuration is handled by Spring Boot's auto-configuration
    // based on spring.kafka.* properties in application.properties
}

