package cz.rj.kafka.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@Slf4j
@EnableAsync
class AsyncConfiguration {
}