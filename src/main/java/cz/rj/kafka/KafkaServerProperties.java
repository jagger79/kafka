package cz.rj.kafka;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * This configuration properties are exposed as a bean to be used in SpEL injections.
 */
@Configuration
@Data
@ConfigurationProperties("kafka")
@Validated
public class KafkaServerProperties {
    /**
     * Configuration to be used for instantiate ADMIN client
     */
    @NestedConfigurationProperty
    @NotEmpty
    private final Map<String, String> client = new HashMap<>();
    /**
     * Contains information about connecting to external systems, its configurationes etc.
     */
    @NestedConfigurationProperty
    @NotEmpty
    private final Map<String, String> server = new HashMap<>();
    @NestedConfigurationProperty
    @Valid
    private final ServerMandatory serverMandatory = new ServerMandatory();
    /**
     * Configuration of super user
     */
    @NestedConfigurationProperty
    @NotNull
    private User superUser;

    @Value("${spring.application.name}")
    private String applicationName;
    /**
     * Project description
     */
    private String description;
    /**
     * Specify if running on test environment
     */
    @NotNull
    private EnvType environment = EnvType.DEV;
    private String hostName;
    private String hostAddress;

    @PostConstruct
    public void init() {
        try {
            hostName = InetAddress.getLocalHost().getHostName();
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            hostName = "localhost";
            hostAddress = "127.0.0.1";
        }
    }

    public Properties getServerProperties() {
        Properties kafkaProps = new Properties();
        server.forEach(kafkaProps::setProperty);
        return kafkaProps;
    }

    public enum EnvType {
        DEV,
        INT,
        UAT1,
        UAT2,
        UAT3,
        TEST,
        PROD;
    }

    @Data
    @Validated
    public static class User {
        private String name;
        private String password;
    }

    @Data
    @Validated
    public class ServerMandatory {
        @NestedConfigurationProperty
        @Valid
        private final InterBroker interBroker = new InterBroker();
        @NotBlank
        private String clusterId;
        @NotNull
        private Integer controllerPort;
        @NotNull
        private Integer clientBrokerPort;
        @NotEmpty
        private Map<String, Long> nodeIdMapping;

        public Optional<Long> getNodeId() {
            String hostName = KafkaServerProperties.this.getHostName();
            return nodeIdMapping.entrySet().stream()
                    .filter(entry -> entry.getKey().startsWith(hostName) || hostName.startsWith(entry.getKey()))
                    .map(Map.Entry::getValue)
                    .findFirst();
        }

        @Data
        @Validated
        public class InterBroker {
            @NestedConfigurationProperty
            @Valid
            private final InterBrokerCertificate certificate = new InterBrokerCertificate();
            @NotNull
            private Integer port;
            /**
             * Distinguish name from certificate for inter-broker communication
             */
            @NotBlank
            private String dn;

            @Data
            @Validated
            public class InterBrokerCertificate {
                @NotBlank
                private String keyStore;
                @NotBlank
                private String keyStorePassword;
            }
        }
    }
}