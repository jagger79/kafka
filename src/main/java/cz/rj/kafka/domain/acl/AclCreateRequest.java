package cz.rj.kafka.domain.acl;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Collection;

@Data
public class AclCreateRequest {
    @NotEmpty
    private Collection<@Valid @NotNull AclCreate> acls;
}