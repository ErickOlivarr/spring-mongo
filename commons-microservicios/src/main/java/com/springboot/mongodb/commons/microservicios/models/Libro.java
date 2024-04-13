package com.springboot.mongodb.commons.microservicios.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Date;

@Document(collection = "libros")
@Setter
@Getter
@ToString
public class Libro {

    @Id
    private String id;

    @NotNull
    @Size(max = 50)
    private String nombre;

    @Indexed(unique = true)
    @Field(name = "codigo")
    @NotNull
    @Size(max = 50)
    private String isbn;

    @CreatedDate
    @JsonIgnore
    private Date createdAt;

    @LastModifiedDate
    @JsonIgnore
    private Date lastModified;

    @DocumentReference(lazy = true)
    @JsonIgnoreProperties(value = {"libros", "target", "source"}, allowSetters = true)
    @Valid
    private Autor autor;

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        if(!(obj instanceof Libro)) {
            return false;
        }
        if( ((Libro)obj).getId() != null && ((Libro)obj).getId().equals(this.getId()) ) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
