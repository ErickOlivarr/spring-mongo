package com.springboot.mongodb.app.autores.services;

import com.springboot.mongodb.commons.microservicios.models.Autor;

import java.util.List;
import java.util.Optional;

public interface IAutorService {

    public Autor guardar(Autor autor);

    public Optional<Autor> encontrarPorId(String id);

    public List<Autor> obtenerTodos();

    public void eliminarAutor(String idAutor);

}
