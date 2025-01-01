package cz.rj.kafka.controllers;

import cz.rj.kafka.ServiceProperties;
import cz.rj.kafka.controllers.dto.MvcExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final ServiceProperties props;

    @ExceptionHandler(value = Throwable.class)
    public ResponseEntity<MvcExceptionResponse> handleException(Throwable throwable,
                                                                HttpServletRequest request) {
        HttpStatus status = getStatusFromThrowable(throwable);
        MvcExceptionResponse body = MvcExceptionResponse.builder()
                .status(status)
                .error(throwable.getClass().getSimpleName())
                .message(throwable.getMessage())
                .path(request.getRequestURI())
                .build();
//                ((ParametrizedRuntimeException) throwable).getParameters());
        ResponseEntity<MvcExceptionResponse> response = ResponseEntity
                .status(status)
                .body(body);

        if (status.is4xxClientError()) {
            if (status != HttpStatus.UNAUTHORIZED) {
                log.warn("exception,{},{}", response.getBody().getError(),
                        response.getBody().getMessage());
            }
        } else {
            log.error("", throwable);
        }

        return response;
    }

    private HttpStatus getStatusFromThrowable(Throwable throwable) {
        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(throwable.getClass(), ResponseStatus.class);
        if (responseStatus != null) {
            return responseStatus.code();
        }
        return props.getStatus(throwable)
                .orElse(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}