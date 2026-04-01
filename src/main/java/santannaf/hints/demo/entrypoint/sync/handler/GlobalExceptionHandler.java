package santannaf.hints.demo.entrypoint.sync.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(HttpClientErrorException.class)
    public ProblemDetail handleHttpClientError(HttpClientErrorException ex) {
        LOGGER.error("[GlobalExceptionHandler] - HTTP client error: {}", ex.getMessage());
        var problem = ProblemDetail.forStatus(ex.getStatusCode());
        problem.setTitle("External API Client Error");
        problem.setDetail(ex.getMessage());
        return problem;
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ProblemDetail handleHttpServerError(HttpServerErrorException ex) {
        LOGGER.error("[GlobalExceptionHandler] - HTTP server error: {}", ex.getMessage());
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_GATEWAY);
        problem.setTitle("External API Server Error");
        problem.setDetail("The external service returned an error");
        return problem;
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ProblemDetail handleResourceAccessError(ResourceAccessException ex) {
        LOGGER.error("[GlobalExceptionHandler] - Resource access error: {}", ex.getMessage());
        var problem = ProblemDetail.forStatus(HttpStatus.SERVICE_UNAVAILABLE);
        problem.setTitle("External API Unavailable");
        problem.setDetail("Could not connect to the external service");
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericError(Exception ex) {
        LOGGER.error("[GlobalExceptionHandler] - Unexpected error: {}", ex.getMessage(), ex);
        var problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle("Internal Server Error");
        problem.setDetail("An unexpected error occurred");
        return problem;
    }
}
