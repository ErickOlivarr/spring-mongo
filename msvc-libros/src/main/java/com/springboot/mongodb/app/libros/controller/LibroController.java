package com.springboot.mongodb.app.libros.controller;

import com.springboot.mongodb.app.libros.enums.CamposLibro;
import com.springboot.mongodb.app.libros.error.ErrorPersonalizado;
import com.springboot.mongodb.app.libros.services.LibroService;
import com.springboot.mongodb.commons.microservicios.models.Autor;
import com.springboot.mongodb.commons.microservicios.models.Libro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class LibroController {

    @Autowired
    private LibroService libroService;

    @GetMapping
    public ResponseEntity<?> todos(@RequestParam(name = "pagina", required = true) Integer pagina, @RequestParam(name = "ordenar", required = true) CamposLibro ordenar, @RequestParam(value = "ascendente", required = true) Integer ascendente) {
        Page<Libro> libros = libroService.obtenerTodos(pagina, ordenar, ascendente);
        /*libros = new PageImpl<>(
                libros.stream().map(l -> {
                    if(l.getAutor() != null && l.getAutor().getId() == null) {
                        l.setAutor(null);
                    }
                    return l;
                }).collect(Collectors.toList())
        );*/
        return ResponseEntity.status(HttpStatus.OK).body(libros);
    }

    @PostMapping("/crear-libro-y-autor") //aqui se le pone un libro sin id y un autor sin id para crear ambos
    public ResponseEntity<?> guardar(@RequestBody @Valid Libro libro, BindingResult result) {
        if(result.hasErrors()) {
            Map<String, Object> map = new HashMap<>();
            result.getFieldErrors().stream().forEach(f -> {
                map.put(f.getField(), f.getDefaultMessage());
            });
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(libroService.guardar(libro));
    }

    @PostMapping("/crear-autor") //aqui solo se crea un autor
    public ResponseEntity<?> guardarAutor(@RequestBody @Valid Autor autor, BindingResult result) {
        if(result.hasErrors()) {
            Map<String, Object> map = new HashMap<>();
            result.getFieldErrors().stream().forEach(f -> {
                map.put(f.getField(), f.getDefaultMessage());
            });
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(libroService.guardarAutor(autor));
    }

    @PostMapping("/crear-libro-con-autor-existente") //aqui se le pone un libro sin id con un autor con id para crear ese libro y asignarle ese autor existente, el id del autor que se le pone sin el ObjectId de mongodb, se le pone como string
    public ResponseEntity<?> guardarLibro(@RequestBody @Valid Libro libro, BindingResult result) {
        if(result.hasErrors()) {
            Map<String, Object> map = new HashMap<>();
            result.getFieldErrors().stream().forEach(f -> {
                map.put(f.getField(), f.getDefaultMessage());
            });
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
        }

        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(libroService.guardarLibro(libro));
        } catch (ErrorPersonalizado e) {
            Map<String, Object> map = new HashMap<>();
            map.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
        }
    }

    @PutMapping("actualizar-libro-autor-existente/{id}")
    public ResponseEntity<?> actualizarLibro(@PathVariable String id, @RequestBody @Valid Libro libro, BindingResult result) {
        if(result.hasErrors()) {
            Map<String, Object> map = new HashMap<>();
            result.getFieldErrors().stream().forEach(f -> {
                map.put(f.getField(), f.getDefaultMessage());
            });
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
        }

        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(libroService.actualizarLibro(id, libro));
        } catch(ErrorPersonalizado e) {
            Map<String, Object> map = new HashMap<>();
            map.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
        }
    }

    @PutMapping("/actualizar-libro-autor-nuevo/{id}")
    public ResponseEntity<?> actualizarLibroCrearAutor(@PathVariable String id, @RequestBody @Valid Libro libro, BindingResult result) {
        if(result.hasErrors()) {
            Map<String, Object> map = new HashMap<>();
            result.getFieldErrors().stream().forEach(f -> {
                map.put(f.getField(), f.getDefaultMessage());
            });
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
        }

        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(libroService.actualizarLibroAutorNuevo(id, libro));
        } catch(ErrorPersonalizado e) {
            Map<String, Object> map = new HashMap<>();
            map.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable String id) {
        try {
            libroService.eliminar(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch(ErrorPersonalizado e) {
            Map<String, Object> map = new HashMap<>();
            map.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
        }
    }

    @DeleteMapping("/eliminar-autor/{idAutor}")
    public ResponseEntity<?> eliminarAutor(@PathVariable String idAutor) {
        try {
            libroService.eliminarAutor(idAutor);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch(ErrorPersonalizado e) {
            Map<String, Object> map = new HashMap<>();
            map.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
        }
    }

    @GetMapping("/autores")
    public ResponseEntity<?> listarAutores() {
        return ResponseEntity.status(HttpStatus.OK).body(libroService.listarAutores());
    }









    @GetMapping("/filtrar/{termino}")
    public ResponseEntity<?> filtro(@PathVariable String termino) {
        return ResponseEntity.status(HttpStatus.OK).body(libroService.filtrar(termino));
    }

    @GetMapping("/filtrar-autor/{termino}/edad/{edad}")
    public ResponseEntity<?> filtroAutor(@PathVariable String termino, @PathVariable Integer edad) {
        return ResponseEntity.status(HttpStatus.OK).body(libroService.filtrarAutor(termino, edad));
    }

    @GetMapping("/proyeccion")
    public ResponseEntity<?> proyeccion() {
        return ResponseEntity.status(HttpStatus.OK).body(libroService.proyeccion());
    }

    @GetMapping("/proyeccion-autor")
    public ResponseEntity<?> proyeccionAutor() {
        return ResponseEntity.status(HttpStatus.OK).body(libroService.proyeccionAutor());
    }

    @GetMapping("/aggregation-autor/{fecha}")
    public ResponseEntity<?> proyeccionAutor(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date fecha) {
        return ResponseEntity.status(HttpStatus.OK).body(libroService.aggregation(fecha));
    }

    @GetMapping("/aggregation-autor-2/{edad}")
    public ResponseEntity<?> proyeccionAutor(@PathVariable Integer edad) {
        return ResponseEntity.status(HttpStatus.OK).body(libroService.aggregation2(edad));
    }

    @GetMapping("/aggregation-autor-3")
    public ResponseEntity<?> proyeccionAutor2() {
        return ResponseEntity.status(HttpStatus.OK).body(libroService.sumatoriaEdades());
    }

    @GetMapping("/aggregation-autor-4")
    public ResponseEntity<?> proyeccionAutor3() {
        return ResponseEntity.status(HttpStatus.OK).body(libroService.sumatoriaEmailConMx());
    }

    /*
    @GetMapping("/aggregation-autor-5")
    public ResponseEntity<?> proyeccionAutor4() {
        return ResponseEntity.status(HttpStatus.OK).body(libroService.autoresAg());
    }
    */

}
