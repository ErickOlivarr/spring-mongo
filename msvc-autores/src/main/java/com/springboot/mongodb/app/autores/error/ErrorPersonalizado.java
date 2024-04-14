package com.springboot.mongodb.app.autores.error;

public class ErrorPersonalizado extends RuntimeException {

    public ErrorPersonalizado(String mensaje) {
        super(mensaje);
    }

}
