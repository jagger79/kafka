package cz.rj.kafka.domain.acl;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.resource.PatternType;
import org.apache.kafka.common.resource.ResourcePattern;
import org.apache.kafka.common.resource.ResourceType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AclAccessResource {
    @NotBlank
    private String name;
    @NotNull
    @Builder.Default
    private ResourceType resource = ResourceType.ANY;
    @NotNull
    @Builder.Default
    private PatternType pattern = PatternType.LITERAL;

    public static AclAccessResource from(ResourcePattern in) {
        return AclAccessResource.builder()
                .name(in.name())
                .resource(in.resourceType())
                .pattern(in.patternType())
                .build();
    }
}