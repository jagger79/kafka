package cz.rj.kafka.services.info;

import cz.rj.kafka.domain.info.MetricInfo;
import cz.rj.kafka.services.KafkaBeansService;
import kafka.server.KafkaConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class InfoService {
    private final KafkaBeansService kafkaBeans;

    public Object props() {
        final KafkaConfig config = kafkaBeans.getBroker().config();
        return Map.of("props", config.props(),"values", config.values());
    }

    public Object showMetrics() {
        return kafkaBeans.getBroker().metrics().metrics().values().stream()
                .map(metric -> MetricInfo.builder()
                        .name(metric.metricName().name())
                        .group(metric.metricName().group())
                        .description(metric.metricName().description())
                        .value(metric.metricValue().toString())
                        .build())
                .collect(Collectors.toList());
    }
}