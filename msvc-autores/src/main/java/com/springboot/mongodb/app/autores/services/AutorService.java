package com.springboot.mongodb.app.autores.services;

import com.springboot.mongodb.app.autores.client.LibroClient;
import com.springboot.mongodb.app.autores.error.ErrorPersonalizado;
import com.springboot.mongodb.app.autores.repository.AutorRepository;
import com.springboot.mongodb.commons.microservicios.models.Autor;
import com.springboot.mongodb.commons.microservicios.models.Libro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AutorService implements IAutorService {

    @Autowired
    private AutorRepository autorRepository;

    @Autowired
    private LibroClient libroClient;

    @Override
    @Transactional
    public Autor guardar(Autor autor) {
        return autorRepository.save(autor);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Autor> encontrarPorId(String id) {
        return autorRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Autor> obtenerTodos() {
        return autorRepository.findAll();
    }

    @Override
    @Transactional
    public void eliminarAutor(String idAutor) {
        Optional<Autor> autorOp = autorRepository.findById(idAutor);
        if(!autorOp.isPresent()) {
            throw new ErrorPersonalizado("No existe el libro");
        }
        Autor autor = autorOp.get();
        Set<Libro> libros = autor.getLibros().stream().map(l -> {
            l.setAutor(null);
            return l;
        }).collect(Collectors.toSet());

        libroClient.crearVarios(libros);

        autorRepository.deleteById(idAutor);


    }
}
