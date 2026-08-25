package br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.kafka.producer;

import br.com.fiap.grupo_10.hospitalscheduler.shared_events.ConsultaCriadaEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ConsultaProducerConfig {

    @Bean
    public ProducerFactory<String, ConsultaCriadaEvent> producerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, ConsultaCriadaEvent> kafkaTemplate(ProducerFactory<String, ConsultaCriadaEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
