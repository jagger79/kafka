package cz.rj.kafka.domain.acl;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.acl.AccessControlEntry;
import org.apache.kafka.common.acl.AclOperation;
import org.apache.kafka.common.acl.AclPermissionType;
import org.apache.kafka.common.resource.ResourcePattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AclAccessSpec {
    /**
     * May be user login or DN certificate
     */
    @NotBlank
    private String principal;
    @NotNull
    @Builder.Default
    private AclOperation operation = AclOperation.ANY;
    @NotNull
    @Builder.Default
    private AclPermissionType permission = AclPermissionType.DENY;
    @Builder.Default
    private String host = ResourcePattern.WILDCARD_RESOURCE;

    public static AclAccessSpec from(AccessControlEntry in) {
        return AclAccessSpec.builder()
                .principal(in.principal())
                .operation(in.operation())
                .permission(in.permissionType())
                .host(in.host())
                .build();
    }
}