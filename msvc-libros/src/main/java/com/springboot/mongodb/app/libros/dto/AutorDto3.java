package com.springboot.mongodb.app.libros.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AutorDto3 {

    @JsonIgnore
    private String nombre;
    @JsonIgnore
    private String apellido;
    private Integer edadAg;

    public AutorDto3(String nombre, String apellido, Integer edadAg) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.edadAg = edadAg;
    }

    public String getNombreCompleto() {
        return nombre + ' ' + apellido;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Integer getEdadAg() {
        return edadAg;
    }

    public void setEdadAg(Integer edadAg) {
        this.edadAg = edadAg;
    }
}
