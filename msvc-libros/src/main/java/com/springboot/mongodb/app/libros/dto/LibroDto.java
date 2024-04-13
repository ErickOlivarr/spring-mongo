package com.springboot.mongodb.app.libros.dto;

import com.springboot.mongodb.commons.microservicios.models.Autor;
import com.springboot.mongodb.commons.microservicios.models.Libro;

import java.util.List;

public interface LibroDto {

    public String getNombre();
    public String getIsbn();
    public List<Autor> getAutor();

}
