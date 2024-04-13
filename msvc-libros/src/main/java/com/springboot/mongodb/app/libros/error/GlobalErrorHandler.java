package com.springboot.mongodb.app.libros.error;

import com.mongodb.MongoCommandException;
import com.mongodb.MongoWriteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler
    public ResponseEntity<?> handler(MongoWriteException error) {
        Map<String, Object> map = new HashMap<>();
        map.put("error", error.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
    }

    @ExceptionHandler
    public ResponseEntity<?> handler(MongoCommandException error) {
        Map<String, Object> map = new HashMap<>();
        map.put("error", error.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
    }

    @ExceptionHandler
    public ResponseEntity<?> handler(MethodArgumentTypeMismatchException error) {
        Map<String, Object> map = new HashMap<>();
        map.put("error", error.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
    }

}
