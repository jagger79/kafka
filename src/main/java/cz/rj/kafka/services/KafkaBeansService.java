package cz.rj.kafka.services;

import cz.rj.kafka.KafkaServerProperties;
import jakarta.annotation.PreDestroy;
import kafka.server.BrokerServer;
import kafka.server.KafkaConfig;
import kafka.server.KafkaRaftServer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeMetadataQuorumResult;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.ScramMechanism;
import org.apache.kafka.common.security.scram.internals.ScramFormatter;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Properties;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class KafkaBeansService {
    public static final String SPRING_BEAN_KAFKA_CONFIG = "kafkaConfig";
    public static final String SPRING_BEAN_KAFKA_SERVER = "kafkaServer";
    public static final String SPRING_BEAN_KAFKA_BROKER = "kafkaBroker";

    private final KafkaServerProperties props;
    private final ObjectFactory<BrokerServer> brokerFactory;
    private final ObjectFactory<KafkaRaftServer> serverFactory;
    private final ObjectFactory<KafkaConfig> configFactory;
    private final ScramFormatter formatter;

    @Getter
    private BrokerServer broker;
    @Getter
    private KafkaRaftServer server;
    @Getter
    private KafkaConfig config;
    @Getter
    private KafkaAdminClient adminClient;

    /**
     * When spring is initialized, those beans are not yet registered, so initialize must be called lazily
     * after KRaft server is up and running
     */
    public void initialize() {
        broker = brokerFactory.getObject();
        server = serverFactory.getObject();
        config = configFactory.getObject();

        // Setting up the super-users to no need to be in kafka db
        broker.credentialProvider().updateCredential(ScramMechanism.SCRAM_SHA_512,
                props.getSuperUser().getName(),
                formatter.generateCredential(props.getSuperUser().getPassword(), 4096));

        Properties props = new Properties();
        props.putAll(this.props.getClient());
        adminClient = (KafkaAdminClient) AdminClient.create(props);

        DescribeMetadataQuorumResult metadata = getAdminClient().describeMetadataQuorum();
        try {
            metadata.quorumInfo().get();
        } catch (Exception e) {
            destroy();
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    private void destroy() {
        if (getAdminClient() != null) {
            getAdminClient().close();
            log.info("kafka-server,admin client,shutdown completed");
        }
        if (getServer() != null) {
            try {
                getServer().shutdown();
            } catch (Exception e) {
                log.error("", e);
            }
            log.info("kafka-server,shutdown completed");
        }
    }
}