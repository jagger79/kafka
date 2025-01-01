package cz.rj.kafka.config;

import cz.rj.kafka.KafkaServerProperties;
import cz.rj.kafka.services.KafkaBeansService;
import kafka.server.BrokerServer;
import kafka.server.KafkaConfig;
import kafka.server.KafkaRaftServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.server.authorizer.Authorizer;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.ReflectionUtils;
import scala.Some;

import java.lang.reflect.Field;

import static cz.rj.kafka.services.KafkaBeansService.*;

/**
 * @see https://cwiki.apache.org/confluence/display/KAFKA/KIP-833%3A+Mark+KRaft+as+Production+Ready
 * @see https://kafka.apache.org/documentation/#configuration
 */
@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@RequiredArgsConstructor
@Slf4j
class KafkaRaftServerConfiguration {
    private final KafkaServerProperties props;
    private final KafkaBeansService kafkaBeans;

    @Async
    void createKafkaServer(ConfigurableApplicationContext appCtx) {
        try {
            KafkaConfig config = KafkaConfig.fromProps(props.getServerProperties(), false);
            log.info("kafka-server,creating,{}", props.getDescription());

            KafkaRaftServer server = new KafkaRaftServer(config, org.apache.kafka.common.utils.Time.SYSTEM);

            Field field = ReflectionUtils.findField(KafkaRaftServer.class, "broker");
            ReflectionUtils.makeAccessible(field);
            BrokerServer broker = ((Some<BrokerServer>) ReflectionUtils.getField(field, server)).get();

            log.info("kafka-server,starting");
            server.startup();
            log.info("kafka-server,started");

            ConfigurableListableBeanFactory bf = appCtx.getBeanFactory();
            bf.registerSingleton(SPRING_BEAN_KAFKA_CONFIG, config);
            bf.registerSingleton(SPRING_BEAN_KAFKA_SERVER, server);
            bf.registerSingleton(SPRING_BEAN_KAFKA_BROKER, broker);
            log.info("kafka-server,spring registered");

            Authorizer authorizer = broker.authorizer().getOrElse(null);
            if (authorizer != null) {
                log.info("kafka-server,acls,count={}", authorizer.aclCount());
            }

            log.info("kafka-server,created,{}", props.getDescription());

            kafkaBeans.initialize();
        } catch (Exception e) {
            log.error("exiting", e);
            SpringApplication.exit(appCtx, () -> 1);
        }
    }
}