package cz.rj.kafka.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.context.event.EventListener;

@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@RequiredArgsConstructor
@Slf4j
class KafkaEventConfiguration {
    private final KafkaRaftServerConfiguration kafkaStart;

    @EventListener(ApplicationStartedEvent.class)
    public void applicationStarted(ApplicationStartedEvent ctx) {
        // start KAFKA server after spring-boot is initialized
        kafkaStart.createKafkaServer(ctx.getApplicationContext());
    }
}