package br.com.fiap.grupo_10.hospitalscheduler.notificacao_service.kafka;

import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.entity.Consulta;
import br.com.fiap.grupo_10.hospitalscheduler.shared_events.ConsultaCriadaEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ConsultaConsumerConfig {

    @Bean
    ConsumerFactory<String, ConsultaCriadaEvent> consumerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "consulta-group");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        JsonDeserializer<ConsultaCriadaEvent> deserializer = new JsonDeserializer<>(ConsultaCriadaEvent.class);

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, ConsultaCriadaEvent> kafkaListenerContainerFactory(ConsumerFactory<String, ConsultaCriadaEvent> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, ConsultaCriadaEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }
}
