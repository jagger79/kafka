package cz.rj.kafka.controllers;

import cz.rj.kafka.services.info.InfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("${api.base-path}/info")
class InfoController {
    private final InfoService service;

    @GetMapping("props")
    Object props() {
        var ret = service.props();
        return ret;
    }

    @GetMapping("metrics")
    Object showMetrics() {
        var ret = service.showMetrics();
        return ret;
    }
}