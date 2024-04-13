package com.springboot.mongodb.app.libros.repository;

import com.springboot.mongodb.commons.microservicios.models.Autor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AutorRepository extends MongoRepository<Autor, String> {

    //@Query(value = "{ $or: [ {nombre: /?0/}, {apellido: /?0/}, {email: /?0/} ] }")
    @Query(value = "{ $or: [ { $expr: { $regexMatch: { input: {$concat: ['$nombre', ' ', '$apellido']}, regex: ?0, options: 'i' } } }, {email: /?0/} ] }")
    public List<Autor> encontrarPorFiltro(String termino);

    @Query(value = "{ $and: [ { $or: [ { $expr: { $regexMatch: { input: {$concat: ['$nombre', ' ', '$apellido']}, regex: ?0, options: 'i' } } }, {email: /?0/} ] }, { $expr: { $lte: [ { $subtract: [ { $subtract: [ {$year: '$$NOW'}, {$year: '$fechaNacimiento'} ] }, { $cond: { if: { $lte: [ {$dayOfYear: '$$NOW'}, {$dayOfYear: '$fechaNacimiento'} ] }, then: 1, else: 0 } } ] } , ?1 ] } } ] }")
    public List<Autor> encontrarAutorConEdad(String termino, Integer edad);

}
