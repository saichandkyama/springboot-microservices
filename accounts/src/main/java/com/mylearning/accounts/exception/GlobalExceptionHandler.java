package com.mylearning.accounts.exception;

import com.mylearning.accounts.dto.ErrorResponseDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    /**
     * Handles the {@link MethodArgumentNotValidException} by returning a response entity
     * with a map of validation errors.
     *
     * @param ex the exception thrown when a method argument is not valid
     * @param headers the HTTP headers of the request
     * @param status the HTTP status of the request
     * @param request the web request during which the exception occurred
     * @return a {@link ResponseEntity} containing the map of validation errors
     *         with HTTP status BAD_REQUEST
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> validationErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError -> FieldError.getField(), FieldError -> FieldError.getDefaultMessage()));
        return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
    }
    /**
     * Handles the {@link CustomerAlreadyExistsException} by returning a response entity
     * with an error response DTO containing the exception details.
     *
     * @param exception the exception thrown when a customer already exists
     * @param webRequest the web request during which the exception occurred
     * @return a {@link ResponseEntity} containing the {@link ErrorResponseDto} with
     *         HTTP status BAD_REQUEST and details of the exception
     */
    @ExceptionHandler(CustomerAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleCustomerAlreadyExistsException(CustomerAlreadyExistsException exception, WebRequest webRequest) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                webRequest.getDescription(false),
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles the {@link ResourceNotFoundException} by returning a response entity
     * with an error response DTO containing the exception details.
     *
     * @param exception the exception thrown when a resource is not found
     * @param webRequest the web request during which the exception occurred
     * @return a {@link ResponseEntity} containing the {@link ErrorResponseDto} with
     *         HTTP status NOT_FOUND and details of the exception
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(ResourceNotFoundException exception, WebRequest webRequest) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                webRequest.getDescription(false),
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponseDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGlobalException(Exception exception, WebRequest webRequest) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                webRequest.getDescription(false),
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
