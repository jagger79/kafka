package cz.rj.kafka.controllers.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;
import java.util.Map;

@NoArgsConstructor
@Data
public class MvcExceptionResponse {
    private ZonedDateTime timestamp;
    private HttpStatus status;
    private String error;
    private String message;
    private String path;
    /**
     * added parameters for specific results
     */
    private Map<String, String> parameters;

    @Builder
    public MvcExceptionResponse(ZonedDateTime timestamp, HttpStatus status,
                                String error, String message, String path,
                                Map<String, String> parameters) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.parameters = parameters;
    }
}