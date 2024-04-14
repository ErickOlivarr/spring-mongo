package com.springboot.mongodb.app.libros.repository;

import com.springboot.mongodb.app.libros.dto.AutorDto;
import com.springboot.mongodb.app.libros.dto.AutorDto2;
import com.springboot.mongodb.app.libros.dto.AutorDto3;
import com.springboot.mongodb.commons.microservicios.models.Autor;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface AutorRepository extends MongoRepository<Autor, String> {

    //@Query(value = "{ $or: [ {nombre: /?0/}, {apellido: /?0/}, {email: /?0/} ] }")
    @Query(value = "{ $or: [ { $expr: { $regexMatch: { input: {$concat: ['$nombre', ' ', '$apellido']}, regex: ?0, options: 'i' } } }, {email: /?0/} ] }")
    public List<Autor> encontrarPorFiltro(String termino);

    @Query(value = "{ $and: [ { $or: [ { $expr: { $regexMatch: { input: {$concat: ['$nombre', ' ', '$apellido']}, regex: ?0, options: 'i' } } }, {email: /?0/} ] }, { $expr: { $lte: [ { $subtract: [ { $subtract: [ {$year: '$$NOW'}, {$year: '$fechaNacimiento'} ] }, { $cond: { if: { $lte: [ {$dayOfYear: '$$NOW'}, {$dayOfYear: '$fechaNacimiento'} ] }, then: 1, else: 0 } } ] } , ?1 ] } } ] }")
    public List<Autor> encontrarAutorConEdad(String termino, Integer edad);

    @Query(value = "{}") //en el metodo proyeccion del LibroRepository se vio cómo podemos hacer proyecciones, checarlo, y aqui igual se hace una proyeccion con la interfaz AutorDto en la cual se usa el @Value como lo visto en las proyecciones vistas en la guia de spring boot 2023
    public List<AutorDto> proyeccion();

    //@Query(value = "{ fechaNacimiento: { $lte: ?0 } }")
    @Aggregation(pipeline = { //asi se puede hacer un aggregation igual haciendo un filtro con el $match y la proyeccion con el $project que en este caso estamos retornando todos los atributos de LibroDto, y esto resultaría en lo mismo que la consulta de arriba que se comentó
            "{ $match: { fechaNacimiento: {$lte: ?0} } }",
            "{ $project: { nombre: true, apellido: true, email: true, libros: true } }"
    })
    public List<AutorDto> aggregation(Date fecha);

    //Lo siguiente se comentó porque en el @Aggregation, en su $project sí podemos retornar un atributo que no existe en la clase Autor, eso sí se puede, pero daría error porque ahi estamos haciendo una proyeccion con interfaz y cuando hacemos eso debemos poner en esa proyeccion con interfaz solo los atributos que están en la clase Autor en este caso porque es el repository del Autor, si ponemos algun metodo getter en esa interfaz que no corresponda a un atributo de la clase Autor (y que no le pongamos el @Value ahi) entonces dará error incluso aunque ese atributo lo hayamos retornado en el $project del @Aggregate, y en la interfaz AutorDto2 pusimos el metodo getter getEdadAg() , y por eso daría error
    /*
    @Aggregation(pipeline = {
            "{ $addFields: { edadAg: { $subtract: [ { $subtract: [ {$year: '$$NOW'}, {$year: '$fechaNacimiento'} ] }, { $cond: { if: { $lte: [ {$dayOfYear: '$$NOW'}, {$dayOfYear: '$fechaNacimiento'} ] }, then: 1, else: 0 } } ] } } }",
            "{ $match: { edadAg: {$lte: ?0} } }",
            "{ $project: { nombre: true, apellido: true, edadAg: true } }"
    })
    public List<AutorDto2> aggregation2(Integer edad);
    */
    //Lo siguiente sí está bien, funcionaría correctamente pero ahi solo se tienen en cuenta atributos de la clase Autor, aunque mas abajo hicimos una proyeccion donde sí se puede tener atributos incluso aunque no estén dentro de la clase Autor, por eso al final comentamos lo siguiente, checar mas abajo
    /*
    @Aggregation(pipeline = {
            "{ $addFields: { edadAg: { $subtract: [ { $subtract: [ {$year: '$$NOW'}, {$year: '$fechaNacimiento'} ] }, { $cond: { if: { $lte: [ {$dayOfYear: '$$NOW'}, {$dayOfYear: '$fechaNacimiento'} ] }, then: 1, else: 0 } } ] } } }",
            "{ $match: { edadAg: {$lte: ?0} } }",
            "{ $project: { nombre: true, apellido: true, email: true, libros: true } }"
    })
    public List<AutorDto> aggregation2(Integer edad);
    */
    //Abajo se ve cómo podríamos tener una proyeccion poniendo atributos incluso aunque no estén dentro de la clase Autor, para eso se hace una proyeccion pero no usando una interfaz sino usando una clase con los atributos y un constructor con esos atributos de lo que retornamos en el $project del @Aggregate, como se hizo con la clase LibroDto3
    @Aggregation(pipeline = {
            "{ $addFields: { edadAg: { $subtract: [ { $subtract: [ {$year: '$$NOW'}, {$year: '$fechaNacimiento'} ] }, { $cond: { if: { $lte: [ {$dayOfYear: '$$NOW'}, {$dayOfYear: '$fechaNacimiento'} ] }, then: 1, else: 0 } } ] } } }",
            "{ $match: { edadAg: {$lte: ?0} } }",
            "{ $project: { nombre: true, apellido: true, edadAg: true } }"
    })
    public List<AutorDto3> aggregation2(Integer edad);

    @Aggregation(pipeline = { //asi hacemos un aggregate de group by, esto del group by con el aggregate se vio en el curso 1 de nodejs, pero aqui ene este caso al _id del group le ponemos null y asi en realidad no agrupará por ningun campo, pero nos sirve para asi retornar el total de los valores de una columna en todos los documentos por ejemplo, como en este caso el total de la edad de todos los documentos del autor, aunque la edad no está como atributo en los documentos del autor pero pues lo agregamos en este aggregate con el $addFields y asi ya lo podemos usar en el group y asi vamos sumando con el $sum la edad de todos los documentos del autor
            "{ $addFields: { edad: { $subtract: [ { $subtract: [ {$year: '$$NOW'}, {$year: '$fechaNacimiento'} ] }, { $cond: { if: { $lte: [ {$dayOfYear: '$$NOW'}, {$dayOfYear: '$fechaNacimiento'} ] }, then: 1, else: 0 } } ] } } }",
            "{ $group: { _id: null, total: { $sum: '$edad' } } }",
            "{ $project: { total: true } }"
    })
    public Integer aggregation3();

    @Aggregation(pipeline = {
            "{ $match: { email: /.mx$/ } }",
            "{ $group: { _id: null, total: { $sum: 1 } } }",
            "{ $project: { total: true } }"
    })
    public Integer aggregation4();

    //Intenté hacer la siguiente consulta con el $lookup del aggregation visto en el curso de nodejs, pero no daba error pero no traía los libros de los autores que tenían libros, osea no funcionaba el $lookup, y intenté tambien poniendole en el localField 'id' y en el foreignField 'autor.id' o 'autor._id' o 'autor.$id' o 'autor.$_id' e igual seguía sin funcionar el $lookup, asi que mejor lo comenté al final, igual podría probar despues de otra manera
    /*
    @Aggregation(pipeline = {
            "{ $lookup: { from: 'libros', localField: '_id', foreignField: 'autor', as: 'libros', pipeline: [ { $project: { nombre: true, isbn: true } } ] } }",
            "{ $project: { nombre: true, apellido: true, email: true, fechaNacimiento: true, libros: true } }"
    })
    public List<Autor> aggregatetion5();
    */

}
