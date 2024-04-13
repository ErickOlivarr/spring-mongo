package com.springboot.mongodb.commons.microservicios.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.util.*;

@Document(collection = "autores")
@Setter
@Getter
@ToString
@CompoundIndex(def = "{'nombre': 1, 'apellido': 1}", unique = true)
public class Autor {

    @Id
    private String id;

    @NotNull
    @Size(max = 50)
    private String nombre;

    @NotNull
    @Size(max = 50)
    private String apellido;

    @Indexed(unique = true)
    @NotNull
    @Email
    private String email;

    @Past
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date fechaNacimiento;

    @CreatedDate
    @JsonIgnore
    private Date createdAt;

    @LastModifiedDate
    @JsonIgnore
    private Date lastModified;

    @DocumentReference(lazy = true)
    @JsonIgnoreProperties(value = {"autor", "target", "source"}, allowSetters = true)
    @Valid
    private Set<Libro> libros;

    public Autor() {
        libros = new HashSet<>();
    }

    public void addLibro(Libro libro) {
        if(libros == null) {
            libros = new HashSet<>();
        }
        libros.add(libro);
        libro.setAutor(this);
    }

}
