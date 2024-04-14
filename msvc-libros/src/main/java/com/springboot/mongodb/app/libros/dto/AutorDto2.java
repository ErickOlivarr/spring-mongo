package com.springboot.mongodb.app.libros.dto;

import org.springframework.beans.factory.annotation.Value;

public interface AutorDto2 {

    @Value("#{target.nombre + ' ' + target.apellido}")
    public String getNombreCompleto();

    public Integer getEdadAg();

}
