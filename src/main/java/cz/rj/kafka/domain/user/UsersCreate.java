package cz.rj.kafka.domain.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UsersCreate {
    @NotEmpty
    private Collection<@Valid UserCreateRequest> users;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class UserCreateRequest {
        @NotBlank
        private String user;
        @NotBlank
        private String password;
    }
}