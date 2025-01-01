package cz.rj.kafka.config;

import cz.rj.kafka.KafkaServerProperties;
import kafka.server.BrokerServer;
import kafka.server.KafkaRaftServer;
import org.apache.kafka.common.utils.AppInfoParser;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ActuatorConfiguration {
    @Bean
    AbstractHealthIndicator kafka(KafkaServerProperties props,
                                  ObjectProvider<kafka.server.KafkaRaftServer> serverProvider) {
        return new AbstractHealthIndicator() {
            @Override
            public void doHealthCheck(Health.Builder builder) {
                KafkaRaftServer server = serverProvider.getIfAvailable();
                if (server == null) {
                    builder.down();
                    return;
                } else {
                    builder.up();
                }
                builder.withDetail("version", AppInfoParser.getVersion());
                builder.withDetail("commitId", AppInfoParser.getCommitId());
            }
        };
    }

    @Bean
    InfoContributor kafkaInfo(ObjectProvider<BrokerServer> brokerFactory,
                              KafkaServerProperties appProps) {
        return builder -> {
            BrokerServer broker = brokerFactory.getIfAvailable();
            String clusterId = null;
            Object logDirs = null;
            Object metadataLogDirs = null;
            Object nodeId = null;
            Object listeners = null;
            Object listenerSecurityProtocolMap = null;
            Object sslEnabledProtocols = null;
            Object sslProtocol = null;
            Object logRetentionHours = null;

            if (broker != null) {
                clusterId = appProps.getServerMandatory().getClusterId();
                Map<?, ?> props = broker.config().values();
                logDirs = props.get("log.dirs");
                metadataLogDirs = props.get("metadata.log.dir");
                nodeId = props.get("node.id");
                listeners = props.get("listeners");
                listenerSecurityProtocolMap = props.get("listener.security.protocol.map");
                sslEnabledProtocols = props.get("ssl.enabled.protocols");
                sslProtocol = props.get("ssl.protocol");
                logRetentionHours = props.get("log.retention.hours");
            }

            Map<String, Object> info = new HashMap<>();
            info.put("version", AppInfoParser.getVersion());
            info.put("commitId", AppInfoParser.getCommitId());
            info.put("clusterId", clusterId);
            info.put("node.id", nodeId);
            info.put("log.dirs", logDirs);
            info.put("metadata.log.dir", metadataLogDirs);
            info.put("listeners", listeners);
            info.put("listener.security.protocol.map", listenerSecurityProtocolMap);
            info.put("ssl.enabled.protocols", sslEnabledProtocols);
            info.put("ssl.protocol", sslProtocol);
            info.put("log.retention.hours", logRetentionHours);

            builder.withDetails(Map.of("kafka", info));
        };
    }
}