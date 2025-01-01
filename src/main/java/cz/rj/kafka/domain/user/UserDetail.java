package cz.rj.kafka.domain.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.kafka.clients.admin.ScramMechanism;
import org.apache.kafka.clients.admin.UserScramCredentialsDescription;

import java.util.List;

public class UserDetail {
    private final UserScramCredentialsDescription delegate;

    public UserDetail(UserScramCredentialsDescription delegate) {
        this.delegate = delegate;
    }

    public String getName() {
        return delegate.name();
    }

    public List<ScramCredentialInfo> getCredentialInfos() {
        return delegate.credentialInfos().stream()
                .map(v -> new ScramCredentialInfo(v.mechanism(), v.iterations()))
                .toList();
    }

    @Data
    @AllArgsConstructor
    public static class ScramCredentialInfo {
        private ScramMechanism mechanism;
        private int iterations;
    }
}