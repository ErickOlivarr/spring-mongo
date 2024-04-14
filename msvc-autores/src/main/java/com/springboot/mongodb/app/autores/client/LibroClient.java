package com.springboot.mongodb.app.autores.client;


import com.springboot.mongodb.commons.microservicios.models.Libro;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@FeignClient(name = "msvc-libros", url = "localhost:8081")
public interface LibroClient {

    @PostMapping("/guardar-libros")
    void crearVarios(@RequestBody Iterable<Libro> libros);

}
