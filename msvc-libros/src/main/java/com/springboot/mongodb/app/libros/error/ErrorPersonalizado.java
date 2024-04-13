package com.springboot.mongodb.app.libros.error;

public class ErrorPersonalizado extends RuntimeException {

    public ErrorPersonalizado(String mensaje) {
        super(mensaje);
    }

}
