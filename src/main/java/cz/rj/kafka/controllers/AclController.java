package cz.rj.kafka.controllers;

import cz.rj.kafka.domain.acl.AclCreateRequest;
import cz.rj.kafka.domain.acl.AclSearchRequest;
import cz.rj.kafka.services.acl.AclService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("${api.base-path}/acls")
class AclController {
    private final AclService service;

    @PostMapping("search")
    Object list(@RequestBody(required = false) AclSearchRequest in) throws Exception {
        return service.list(in);
    }

    @PostMapping
    void create(@RequestBody @Valid AclCreateRequest in) throws Exception {
        service.create(in);
    }

    @DeleteMapping
    void delete(@RequestBody @Valid AclCreateRequest in) throws Exception {
        service.delete(in);
    }
}