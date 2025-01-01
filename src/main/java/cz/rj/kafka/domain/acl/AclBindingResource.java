package cz.rj.kafka.domain.acl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AclBindingResource {
    private AclAccessResource resource;
    private AclAccessSpec access;
}