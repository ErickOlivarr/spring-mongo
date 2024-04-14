package com.springboot.mongodb.app.libros.dto;

import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public interface AutorDto {

    @Value("#{target.nombre + ' ' + target.apellido}")
    public String getNombreCompleto();
    public String getEmail();
    public List<LibroDto> getLibros();
}
