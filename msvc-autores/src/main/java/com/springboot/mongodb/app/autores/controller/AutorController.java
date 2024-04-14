package com.springboot.mongodb.app.autores.controller;

import com.springboot.mongodb.app.autores.error.ErrorPersonalizado;
import com.springboot.mongodb.app.autores.services.IAutorService;
import com.springboot.mongodb.commons.microservicios.models.Autor;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/autor")
public class AutorController {

    @Autowired
    private IAutorService autorService;

    @GetMapping("/{id}")
    public ResponseEntity<?> encontrarPorId(@PathVariable String id) {
        Autor autor = autorService.encontrarPorId(id).orElse(null);
        if(autor == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(autor);
    }

    @GetMapping
    public ResponseEntity<?> encontrarTodos() {
        return ResponseEntity.status(HttpStatus.OK).body(autorService.obtenerTodos());
    }

    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody @Valid Autor autor, BindingResult result) {
        if(result.hasErrors()) {
            Map<String, Object> map = new HashMap<>();
            result.getFieldErrors().stream().forEach(f -> {
                map.put(f.getField(), f.getDefaultMessage());
            });
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(autorService.guardar(autor));
    }

    @DeleteMapping("/{idAutor}")
    public ResponseEntity<?> eliminarAutor(@PathVariable String idAutor) {
        try {
            autorService.eliminarAutor(idAutor);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch(ErrorPersonalizado | FeignException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
        }
    }

}
