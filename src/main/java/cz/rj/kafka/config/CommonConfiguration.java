package cz.rj.kafka.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

import java.time.Clock;

@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@Slf4j
class CommonConfiguration {
    @Bean
    Clock clock() {
        return Clock.systemDefaultZone();
    }
}