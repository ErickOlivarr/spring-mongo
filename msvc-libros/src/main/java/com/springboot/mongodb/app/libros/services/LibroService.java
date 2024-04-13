package com.springboot.mongodb.app.libros.services;

import com.springboot.mongodb.app.libros.dto.LibroDto;
import com.springboot.mongodb.app.libros.enums.CamposLibro;
import com.springboot.mongodb.commons.microservicios.models.Autor;
import com.springboot.mongodb.commons.microservicios.models.Libro;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface LibroService {

    public Page<Libro> obtenerTodos(Integer pagina, CamposLibro campo, Integer ascendente);

    public Libro guardar(Libro libro);

    public Autor guardarAutor(Autor autor);

    public Libro guardarLibro(Libro libro);

    public Libro actualizarLibro(String idLibro, Libro libro);

    public Libro actualizarLibroAutorNuevo(String idLibro, Libro libro);

    public Optional<Libro> encontrarPorId(String id);

    public void eliminar(String id);

    public List<Autor> listarAutores();

    public void eliminarAutor(String idAutor);









    public List<Libro> filtrar(String termino);
    public List<Autor> filtrarAutor(String termino, Integer edad);

}
