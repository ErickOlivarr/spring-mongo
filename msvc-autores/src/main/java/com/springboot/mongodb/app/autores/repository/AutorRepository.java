package com.springboot.mongodb.app.autores.repository;

import com.springboot.mongodb.commons.microservicios.models.Autor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutorRepository extends MongoRepository<Autor, String> {



}
