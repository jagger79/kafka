package cz.rj.kafka.domain.acl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.acl.AclBinding;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Acl {
    private AclAccessResource resource;
    private AclAccessSpec access;

    public static Acl from(AclBinding aclBinding) {
        return Acl.builder()
                .resource(AclAccessResource.from(aclBinding.pattern()))
                .access(AclAccessSpec.from(aclBinding.entry()))
                .build();
    }
}