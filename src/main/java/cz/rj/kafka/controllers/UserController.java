package cz.rj.kafka.controllers;

import cz.rj.kafka.domain.user.UsersCreate;
import cz.rj.kafka.domain.user.UsersDelete;
import cz.rj.kafka.services.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("${api.base-path}/users")
class UserController {
    private final UserService service;

    @GetMapping
    Object list() throws Exception {
        return service.list();
    }

    @PostMapping
    void upsert(@RequestBody @Valid UsersCreate in) {
        service.upsert(in);
    }

    @DeleteMapping
    void delete(@RequestBody @Valid UsersDelete in) {
        service.delete(in);
    }
}