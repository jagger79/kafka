package cz.rj.kafka.domain.acl;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class AclCreate {
    @NotNull
    @Valid
    private AclAccessResource resource;
    @NotNull
    @Valid
    private AclAccessSpec access;
}