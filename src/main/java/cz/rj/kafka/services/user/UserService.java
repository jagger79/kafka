package cz.rj.kafka.services.user;

import cz.rj.kafka.domain.user.UsersCreate;
import cz.rj.kafka.domain.user.UsersDelete;
import cz.rj.kafka.domain.user.UserDetail;
import cz.rj.kafka.services.KafkaBeansService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.errors.ApiException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@Validated
@RequiredArgsConstructor
public class UserService {
    private final KafkaBeansService kafkaBeans;

    public Collection<UserDetail> list() throws Exception {
        DescribeUserScramCredentialsResult future = kafkaBeans.getAdminClient().describeUserScramCredentials();
        Map<String, UserScramCredentialsDescription> ret = future.all().get();
        return ret.values().stream().map(UserDetail::new).toList();
    }

    //TODO return error with list of success and failure
    public void upsert(@NotNull UsersCreate in) {
        List<UserScramCredentialUpsertion> alts = in.getUsers().stream()
                .map(user -> new UserScramCredentialUpsertion(user.getUser(),
                        new ScramCredentialInfo(ScramMechanism.SCRAM_SHA_512, 4096),
                        user.getPassword()))
                .collect(toList());
        updateUser(alts);
    }

    //TODO return error with list of success and failure
    public void delete(@NotNull UsersDelete in) {
        List<UserScramCredentialDeletion> alts = in.getUsers().stream()
                .map(user -> new UserScramCredentialDeletion(user, org.apache.kafka.clients.admin.ScramMechanism.SCRAM_SHA_512))
                .collect(toList());
        updateUser(alts);
    }

    private void updateUser(List<? extends UserScramCredentialAlteration> in) {
        final List<UserScramCredentialAlteration> req = in.stream().map(UserScramCredentialAlteration.class::cast).toList();
        final AlterUserScramCredentialsOptions options = new AlterUserScramCredentialsOptions();
        options.timeoutMs(5000);

        AlterUserScramCredentialsResult ret = kafkaBeans.getAdminClient().alterUserScramCredentials(req, options);
        try {
            ret.all().get();
        } catch (ExecutionException e) {
            throw (ApiException) e.getCause();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}