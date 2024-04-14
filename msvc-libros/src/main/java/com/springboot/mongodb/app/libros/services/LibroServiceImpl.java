package com.springboot.mongodb.app.libros.services;

import com.mongodb.MongoCommandException;
import com.mongodb.MongoWriteException;
import com.springboot.mongodb.app.libros.dto.AutorDto;
import com.springboot.mongodb.app.libros.dto.AutorDto2;
import com.springboot.mongodb.app.libros.dto.AutorDto3;
import com.springboot.mongodb.app.libros.dto.LibroDto;
import com.springboot.mongodb.app.libros.enums.CamposLibro;
import com.springboot.mongodb.app.libros.error.ErrorPersonalizado;
import com.springboot.mongodb.app.libros.repository.AutorRepository;
import com.springboot.mongodb.app.libros.repository.LibroRepository;
import com.springboot.mongodb.commons.microservicios.models.Autor;
import com.springboot.mongodb.commons.microservicios.models.Libro;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LibroServiceImpl implements LibroService {

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private AutorRepository autorRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Libro> obtenerTodos(Integer pagina, CamposLibro campo, Integer ascendente) {
        //Sort sort = Sort.by((ascendente > 0) ? Sort.Direction.ASC : Sort.Direction.DESC, campo.toString().equals("autorNombre") ? "autor.nombre" : campo.toString().equals("autorFechaNacimiento") ? "autor.fechaNacimiento" : campo.toString() ); //OJO que esto de ordenar por atributos de objetos anidados sí se puede hacer en JPA con SQL, pero no se puede hacer con mongodb, asi que si intentamos ordenar por autor.nombre o autor.fechaNacimiento dará error, solo se puede hacer el Sort por los atributos de Libro en este caso cuando usamos mongodb
        Sort sort = Sort.by((ascendente > 0) ? Sort.Direction.ASC : Sort.Direction.DESC, campo.toString());
        Pageable pageable = PageRequest.of(pagina, 2, sort);
        return libroRepository.findAll(pageable);
    }

    @Override
    @Transactional //NOTA: Las transacciones en mongodb solo funcionan si ponemos la clase de MongoConfig que tenemos en este proyecto, se explica de eso ahi, checarlo
    public Libro guardar(Libro libro) {
        //En mongodb no existen los cascades, aunque sí hay una forma de hacerlos pero no es tan facil como con JPA porque debemos crear unas clases manualmente y to-do eso, asi que mejor con mongodb se hacen asi sin cascades las cosas, y abajo se hizo de manera que se agregue un libro y que al mismo tiempo tambien se agregue un autor como si se tuviera el cascade de persist de JPA en el atributo autor del libro, de modo que en el JSON aqui pasaríamos un libro sin su id junto con su atributo autor sin su id para que se inserten los 2
        Libro libroDb = libroRepository.save(libro);
        Autor autor = autorRepository.save(libro.getAutor());
        libroDb.setAutor(autor);
        libroRepository.save(libroDb);
        autor.setLibros(new HashSet<>(Arrays.asList(libroDb)));
        autorRepository.save(autor);
        return libroDb;
    }

    @Override
    @Transactional
    public Autor guardarAutor(Autor autor) {
        return autorRepository.save(autor);
    }

    @Override
    @Transactional
    public Libro guardarLibro(Libro libro) {
        Libro libroDb = libroRepository.save(libro);
        Optional<Autor> autorOptional = autorRepository.findById(libroDb.getAutor().getId());
        if(!autorOptional.isPresent()) {
            throw new ErrorPersonalizado("No se encuentra el autor");
        }
        Autor autor = autorOptional.get();
        autor.getLibros().add(libroDb);
        autorRepository.save(autor);
        return libroDb;
    }

    @Override
    @Transactional
    public Libro actualizarLibro(String idLibro, Libro libro) {
        Optional<Libro> libroOptional = encontrarPorId(idLibro);
        if(!libroOptional.isPresent()) {
            throw new ErrorPersonalizado("No existe libro");
        }

        Libro libroDb = libroOptional.get();

        Optional<Autor> autorAntiguoOp = autorRepository.findById(libroDb.getAutor().getId());
        if(!autorAntiguoOp.isPresent()) {
            throw new ErrorPersonalizado("No existe autor");
        }
        Autor autorAntiguo = autorAntiguoOp.get();
        autorAntiguo.getLibros().remove(libroDb);
        autorRepository.save(autorAntiguo);

        Optional<Autor> autorNuevoOp = autorRepository.findById(libro.getAutor().getId());
        if(!autorNuevoOp.isPresent()) {
            throw new ErrorPersonalizado("No existe autor");
        }
        Autor autorNuevo = autorNuevoOp.get();
        autorNuevo.getLibros().add(libroDb);
        autorRepository.save(autorNuevo);

        libroDb.setNombre(libro.getNombre());
        libroDb.setIsbn(libro.getIsbn());
        libroDb.setAutor(libro.getAutor());

        return libroRepository.save(libroDb);

    }

    @Override
    @Transactional
    public Libro actualizarLibroAutorNuevo(String idLibro, Libro libro) {
        Optional<Libro> libroOptional = encontrarPorId(idLibro);
        if(!libroOptional.isPresent()) {
            throw new ErrorPersonalizado("No existe libro");
        }

        Libro libroDb = libroOptional.get();

        //Autor autorAntiguo = libroDb.getAutor(); //si lo hacía de esta forma daba error, por eso se hizo como está en la siguiente linea para obtener el autor antiguo
        Optional<Autor> autorAntiguoOp = autorRepository.findById(libroDb.getAutor().getId());
        if(!autorAntiguoOp.isPresent()) {
            throw new ErrorPersonalizado("No existe libro");
        }
        Autor autorAntiguo = autorAntiguoOp.get();
        autorAntiguo.getLibros().remove(libroDb);
        autorRepository.save(autorAntiguo);

        libro.getAutor().setLibros(new HashSet<>(Arrays.asList(libroDb)));
        Autor autor = autorRepository.save(libro.getAutor());

        libroDb.setNombre(libro.getNombre());
        libroDb.setIsbn(libro.getIsbn());
        libroDb.setAutor(autor);
        return libroRepository.save(libroDb);


    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Libro> encontrarPorId(String id) {
        return libroRepository.findById(id);
    }

    @Override
    @Transactional
    public void eliminar(String id) {
        Optional<Libro> libroOp = encontrarPorId(id);
        if(!libroOp.isPresent()) {
            throw new ErrorPersonalizado("No existe el libro");
        }

        Libro libro = libroOp.get();

        Optional<Autor> autorOp = autorRepository.findById(libro.getAutor().getId());
        if(!autorOp.isPresent()) {
            throw new ErrorPersonalizado("No existe el autor");
        }
        Autor autor = autorOp.get();
        autor.getLibros().remove(libro);
        autorRepository.save(autor);

        libroRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Autor> listarAutores() {
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
        libroRepository.saveAll(libros);

        autorRepository.deleteById(idAutor);


    }














    @Override
    @Transactional(readOnly = true)
    public List<Libro> filtrar(String termino) {
        List<Libro> libros = libroRepository.encontrarPorFiltro(termino);
        if(!libros.isEmpty()) {
            return libros;
        }
        else {
            List<Autor> autores = autorRepository.encontrarPorFiltro(termino);
            List<ObjectId> idsAutores = autores.stream().map(a -> new ObjectId(a.getId())).collect(Collectors.toList());
            //NOTA: Cuando buscamos por id en el repository como en el metodo encontrarPorIdsAutores del LibroRepository debemos mandarle objetos de ObjectId con el new ObjectId visto en la anterior linea, ya que en base de datos se guardan los ids como ObjectId y no como string
            List<Libro> librosFinal = libroRepository.encontrarPorIdsAutores(idsAutores);
            return librosFinal;
        }

    }

    @Override
    @Transactional(readOnly = true)
    public List<Autor> filtrarAutor(String termino, Integer edad) {
        //Aqui se hace una consulta al autor
        return autorRepository.encontrarAutorConEdad(termino, edad);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibroDto> proyeccion() {
        return libroRepository.proyeccion();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AutorDto> proyeccionAutor() {
        return autorRepository.proyeccion();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AutorDto> aggregation(Date fecha) {
        return autorRepository.aggregation(fecha);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AutorDto3> aggregation2(Integer edad) {
        List<AutorDto3> lista = autorRepository.aggregation2(edad);
        return lista;
    }

    @Override
    @Transactional(readOnly = true)
    public Integer sumatoriaEdades() {
        return autorRepository.aggregation3();
    }

    @Override
    @Transactional(readOnly = true)
    public Integer sumatoriaEmailConMx() {
        return autorRepository.aggregation4();
    }

    /*
    @Override
    public List<Autor> autoresAg() {
        return autorRepository.aggregatetion5();
    }
    */
}
