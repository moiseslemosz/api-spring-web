package dio.web.api.handler;

import org.springframework.http.HttpHeaders;

import java.lang.reflect.UndeclaredThrowableException;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import dio.web.api.exception.BusinessException;
import jakarta.annotation.Resource;

@RestControllerAdvice // Isso diz ao Spring: "Escute todos os erros da API aqui"
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Resource
    private MessageSource messageSource;
    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
    private ResponseError responseError(String message, HttpStatus statusCode) {
        ResponseError responseError = new ResponseError(message, statusCode.value());
        responseError.setStatus("ERROR");
        responseError.setError(message);
        responseError.setStatusCode(statusCode.value());
        return responseError;
    }
    // Exemplo: Captura erros genéricos (Exception.class)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception e, WebRequest request) {
        if(e.getClass().isAssignableFrom(UndeclaredThrowableException.class)) {
            UndeclaredThrowableException exception = (UndeclaredThrowableException) e;
            return handleBusinessException((BusinessException) exception.getUndeclaredThrowable(), request);
        } else {
            String message = messageSource.getMessage("internal.server.error", new Object[]{e.getMessage()}, request.getLocale());
            ResponseError error = responseError(message, HttpStatus.INTERNAL_SERVER_ERROR);
            return handleExceptionInternal(e, error, headers(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }
    // Você pode adicionar métodos específicos para BusinessException, NullPointerException, etc.
    
    @ExceptionHandler({BusinessException.class})
    public ResponseEntity<Object> handleBusinessException(BusinessException e, WebRequest request) {
        ResponseError error = responseError(e.getMessage(), HttpStatus.CONFLICT);
        return handleExceptionInternal(e, error, headers(), HttpStatus.CONFLICT,request);
    }
}