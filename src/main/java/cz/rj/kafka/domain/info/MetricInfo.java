package cz.rj.kafka.domain.info;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MetricInfo {
    private String name;
    private String group;
    private String description;
    private String value;
}