package com.springboot.mongodb.app.libros.repository;

import com.springboot.mongodb.app.libros.dto.LibroDto;
import com.springboot.mongodb.commons.microservicios.models.Libro;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface LibroRepository extends MongoRepository<Libro, String> {

    @Query(value = "{ $or: [ {nombre: /?0/}, {isbn: /?0/} ] }") //OJO QUE AQUI INTENTÉ PONERLE EL REGEX CON LAS 2 DIAGONALES USANDO UN CONCAT PERO ME DIO ERROR, INCLUSO CON EL $EXPR ANTES DEL $CONCAT ME DIO ERROR, ASI QUE CUANDO USAMOS EL LIKE CON EL REGEX EN MONGODB ASI SE DEBE HACER COMO SE VE AQUI, Y OJO QUE SI QUEREMOS FILTRAR POR ATRIBUTOS DE OBJETOS ANIDADOS, COMO POR EJEMPLO EL NOMBRE DEL AUTOR QUE TIENE ASOCIADO ESE LIBRO PONIENDO DENTRO DE ESTE $or: {'autor.nombre': /?0/} ENTONCES DARÁ ERROR PORQUE ASI CON UNA CONSULTA DE FIND SOLO SE PUEDE FILTRAR POR LOS ATRIBUTOS DE LA CLASE LIBRO QUE NO SEAN OBJETOS ANIDADOS, YA QUE ESTAS CONSULTAS DE MONGODB EN REALIDAD SE HACEN DE FORMA NATIVA A LA BASE DE DATOS, Y EN LA BASE DE DATOS PARA EL ATRIBUTO AUTOR DEL LIBRO NO SE GUARDA TO-DO EL OBJETO DEL AUTOR, SINO QUE SOLO SE GUARDA EL ID DE ESE AUTOR, POR ESO NO SE PUEDE FILTRAR ASI POR ATRIBUTOS DE OBJETOS ANIDADOS PORQUE EN BASE DE DATOS SOLO TENEMOS EL PURO ID DE ESE OBJETO Y ESTAS CONSULTAS SE HACEN EN BASE A LA BASE DE DATOS, TAMBIEN INTENTÉ PONERLE A ESTE OBJETO QUE PUSE UN SEGUNDO OBJETO SEPARADO POR UNA COMA, OSEA DESPUES DE LAS LLAVES DE ESTA CONSULTA, LAS LLAVES PRINCIPALES, LE PUSE UNA COMA Y PUSE ESTO: { nombre: 1, isbn: 0 } Y TAMBIEN PROBÉ CON TRUE Y FALSE EN LUGAR DE 1 O 0, PERO NO DABA ERROR PERO NO FUNCIONABA, ASI QUE ASI NO SE PUEDE HACER UNA PROYECCION, Y SI PONEMOS EL ATRIBUTO FIELDS DEL @QUERY ENTONCES HARÍAMOS ALGO ASI COMO UNA PROYECCION PERO AL FINAL DE CUENTAS SE RETORNARÍA TO-DO EL OBJETO DE LIBRO, SOLO QUE ALGUNOS ATRIBUTOS ESTARÁN CON VALOR NULL SI LE PONEMOS FALSE A ESOS ATRIBUTOS EN EL ATRIBUTO FIELDS DEL @QUERY, ASI QUE HACER UNA PROYECCION DE ESA FORMA CON ESE ATRIBUTO FIELDS DEL @QUERY NO SIRVE DE MUCHO, PERO SI QUISIERAMOS PONER EL ATRIBUTO FIELDS LO PONDRÍAMOS COMO SEGUNDO PARAMETRO DEL @QUERY Y LO PONDRÍAMOS POR EJEMPLO DE ESTA FORMA: fields = "{ isbn: false }" , ASI HARÍAMOS QUE TODOS LOS ATRIBUTOS DEL LIBRO TENGAN SU VALOR NORMAL EXCEPTO EL ATRIBUTO ISBN DEL LIBRO QUE TENDRÍA EL VALOR DE NULL, PERO PUES AUN ASI SE MOSTRARÍA EN EL JSON RETORNADO, ASI QUE COMO DIGO, HACER UNA PROYECCION ASI NO SIRVE DE MUCHO
    public List<Libro> encontrarPorFiltro(String termino);

    @Query(value = "{ autor: { $in: ?0 } }")
    public List<Libro> encontrarPorIdsAutores(Iterable<ObjectId> idsAutores);

    /*@Aggregation(pipeline = {
            "{ $lookup: {from: 'autores', localField: 'autor', foreignField: 'id', as: 'autor'} }",
            "{ $project: {nombre: true, isbn: true, autor: true} }"
    })*/
    @Query(value = "{ $and: [ { $or: [ {nombre:/?0/}, {isbn: /?0/} ] }, { $expr: { $lte: [ { $subtract: [ { $subtract: [ {$year: '$$NOW'}, {$year: } ] }, {} ] } , ?1 ] } } ] }")
    public List<Libro> encontrarConEdad(String termino, Integer edad);

    @Query(value = "{}") //asi se puede hacer una proyeccion, poniendo una interfaz como proyeccion con los metodos getter de los campos que queremos retornar como lo visto en la guia de spring boot 2023, aunque ahi se hace con mysql pero pues asi como aqui se haría con mongodb, por eso en la siguiente linea pusimos LibroDto porque esa es una interfaz para hacer nuestra proyeccion en la cual solo se mostrarán el atributo nombre y el atributo isbn del Libro, e igual aqui pudimos haberle puesto un filtro al @Query pero en este caso no le pusimos ningun filtro, asi que asi traería todos los libros, e igual aqui podemos poner el atributo fields del @Query que se explicó arriba y ponerle ahi solo los atributos de nombre e isbn en true y hubiera sido lo mismo, resultaría en lo mismo, asi que asi se pueden hacer las proyecciones
    public List<LibroDto> proyeccion();

}
