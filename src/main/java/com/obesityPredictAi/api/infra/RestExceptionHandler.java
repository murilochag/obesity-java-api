package com.obesityPredictAi.api.infra;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    // Este método será chamado sempre que uma IllegalArgumentException for lançada em qualquer controller
    @ExceptionHandler(IllegalArgumentException.class )
    private ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
        // Cria um corpo de resposta JSON simples com a mensagem da exceção
        Map<String, String> errorBody = Map.of("message", ex.getMessage());
        
        // Retorna uma resposta HTTP 400 (Bad Request) com o corpo do erro
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
    }
}
