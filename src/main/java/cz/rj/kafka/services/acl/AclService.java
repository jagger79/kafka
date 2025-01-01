package cz.rj.kafka.services.acl;

import cz.rj.kafka.domain.acl.Acl;
import cz.rj.kafka.domain.acl.AclCreate;
import cz.rj.kafka.domain.acl.AclCreateRequest;
import cz.rj.kafka.domain.acl.AclSearchRequest;
import cz.rj.kafka.services.KafkaBeansService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.CreateAclsResult;
import org.apache.kafka.clients.admin.DeleteAclsResult;
import org.apache.kafka.clients.admin.DescribeAclsResult;
import org.apache.kafka.common.acl.AccessControlEntry;
import org.apache.kafka.common.acl.AccessControlEntryFilter;
import org.apache.kafka.common.acl.AclBinding;
import org.apache.kafka.common.acl.AclBindingFilter;
import org.apache.kafka.common.resource.ResourcePattern;
import org.apache.kafka.common.resource.ResourcePatternFilter;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class AclService {
    private final KafkaBeansService kafkaBeans;

    public Object list(AclSearchRequest in) throws Exception {
        final var binding = createBinding(in);
        DescribeAclsResult acls = kafkaBeans.getAdminClient().describeAcls(binding);

        Collection<AclBinding> vals = acls.values().get();
        return Map.of("acls", vals.stream().map(Acl::from).collect(toList()));
    }

    public void create(AclCreateRequest in) throws Exception {
        final var bindings = createBindings(in);
        final CreateAclsResult acls = kafkaBeans.getAdminClient().createAcls(bindings);

        acls.all().get();
    }

    public void delete(AclCreateRequest in) throws Exception {
        final var bindings = createBindings(in).stream().map(AclBinding::toFilter).collect(toList());
        final DeleteAclsResult acls = kafkaBeans.getAdminClient().deleteAcls(bindings);

        acls.all().get();
    }

    private List<AclBinding> createBindings(AclCreateRequest in) {
        return in.getAcls().stream().map(this::createBinding).collect(toList());
    }

    private AclBinding createBinding(AclCreate acl) {
        if (acl == null) return null;
        ResourcePattern pattern = new ResourcePattern(acl.getResource().getResource(),
                acl.getResource().getName(),
                acl.getResource().getPattern());
        AccessControlEntry aclEntry = new AccessControlEntry(acl.getAccess().getPrincipal(),
                acl.getAccess().getHost(),
                acl.getAccess().getOperation(),
                acl.getAccess().getPermission());
        return new AclBinding(pattern, aclEntry);
    }

    private AclBindingFilter createBinding(AclSearchRequest acl) {
        if (acl == null) return null;

        ResourcePatternFilter pattern = acl.getResource() == null ? ResourcePatternFilter.ANY :
                new ResourcePatternFilter(acl.getResource().getResource(),
                        acl.getResource().getName(),
                        acl.getResource().getPattern());
        AccessControlEntryFilter aclEntry = acl.getResource() == null ? AccessControlEntryFilter.ANY :
                new AccessControlEntryFilter(acl.getAccess().getPrincipal(),
                        acl.getAccess().getHost(),
                        acl.getAccess().getOperation(),
                        acl.getAccess().getPermission());
        return new AclBindingFilter(pattern, aclEntry);
    }
}