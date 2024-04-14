package com.springboot.mongodb.app.libros.client;

import com.springboot.mongodb.commons.microservicios.models.Autor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(name = "msvc-autores", url = "localhost:8082")
public interface AutorClient {

    @PostMapping("/autor")
    Autor guardar(@RequestBody Autor autor);

    @GetMapping("/autor/{id}")
    public Autor encontrarPorId(@PathVariable String id);

}
