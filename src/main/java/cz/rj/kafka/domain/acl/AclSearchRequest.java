package cz.rj.kafka.domain.acl;

import lombok.Data;


@Data
public class AclSearchRequest {
    private AclAccessResource resource;
    private AclAccessSpec access;
}