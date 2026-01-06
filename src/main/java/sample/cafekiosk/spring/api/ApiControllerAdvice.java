package sample.cafekiosk.spring.api;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST) // 오류가 났을때 예외를 지정할 수 있다.
    @ExceptionHandler(BindException.class)
    public ApiResponse<Object> bindException(BindException ex) {
        return ApiResponse.of(
                HttpStatus.BAD_REQUEST,
                ex.getBindingResult().getAllErrors().get(0).getDefaultMessage(),
                null
        );
    }
}
