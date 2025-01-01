package cz.rj.kafka.config;

import cz.rj.kafka.KafkaServerProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.security.scram.internals.ScramFormatter;
import org.apache.kafka.common.security.scram.internals.ScramMechanism;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Map;

@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@RequiredArgsConstructor
@Slf4j
class KafkaConfiguration {
    private final ResourceLoader resourceLoader;

    @Bean
    ScramFormatter scramFormatter() throws Exception {
        return new ScramFormatter(ScramMechanism.SCRAM_SHA_512);
    }

    @Autowired
    void preparePropertiesForKafkaServer(KafkaServerProperties props) throws Exception {
        createDir(props.getServer().get("log.dirs"), props);
        createDir(props.getServer().get("metadata.log.dir"), props);

        for (Map.Entry<String, String> entry : props.getServer().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value.contains("classpath:") || value.contains("file:") ||
                    key.contains("ssl.keystore.location") || key.contains("ssl.truststore.location")) {
                Resource resource = resourceLoader.getResource(value);
                if (!resource.isReadable()) {
                    throw new RuntimeException("cannot read " + resource);
                }
                props.getServer().put(key, resource.getFile().getAbsolutePath());
            }
        }
        for (var entry : props.getClient().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value.contains("classpath:") || value.contains("file:") ||
                    key.contains("ssl.keystore.location") || key.contains("ssl.truststore.location")) {
                Resource resource = resourceLoader.getResource(value);
                if (!resource.isReadable()) {
                    throw new RuntimeException("cannot read " + resource);
                }
                props.getClient().put(key, resource.getFile().getAbsolutePath());
            }
        }
    }

    private void createDir(String dirOrDirs, KafkaServerProperties props) throws Exception {
        File dataDir = new File(dirOrDirs);
        if (!dataDir.exists()) {
            boolean mkdirs = dataDir.mkdirs();
            if (!mkdirs) {
                throw new RuntimeException("cannot create data dirs " + dataDir.getAbsolutePath());
            }
            log.info("kafka,data-dir,created,{}", dataDir.getAbsolutePath());
        }

        File metaFile = new File(dataDir, "meta.properties");
        if (!metaFile.exists()) {
            boolean created = metaFile.createNewFile();
            if (!created) {
                throw new RuntimeException("cannot create " + metaFile.getAbsolutePath());
            }
            log.info("kafka,meta.properties,created");
            String data = "#\n# " + new Date() +
                    "\nversion=1\n"
                    + "node.id=" + props.getServerMandatory().getNodeId().orElse(0L)
                    + "\ncluster.id=" + props.getServerMandatory().getClusterId()
                    + "\n";

            try (FileOutputStream fo = new FileOutputStream(metaFile)) {
                fo.write(data.getBytes());
            }
        }
    }
}