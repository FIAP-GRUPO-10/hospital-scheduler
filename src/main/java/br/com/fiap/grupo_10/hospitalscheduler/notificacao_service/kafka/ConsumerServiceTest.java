package br.com.fiap.grupo_10.hospitalscheduler.notificacao_service.kafka;

import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.entity.Consulta;
import br.com.fiap.grupo_10.hospitalscheduler.shared_events.ConsultaCriadaEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ConsumerServiceTest {

    private final Logger log = LoggerFactory.getLogger(ConsumerServiceTest.class);

    @KafkaListener(topics = "consulta-topic", groupId = "consulta-group")
    public void consumerTest(ConsultaCriadaEvent consulta) {
        log.info("Consulta criada {}", consulta);
    }
}
