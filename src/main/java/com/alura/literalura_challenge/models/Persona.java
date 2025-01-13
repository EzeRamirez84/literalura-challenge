package com.alura.literalura_challenge.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "autores")
public class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long anioNacimiento;
    private Long anioMuerte;
    @Column(unique = true)
    private String nombre;

    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Libro> libros;


    public Persona() {}

    public Persona(DatosPersona datos) {
        this.anioNacimiento = datos.anioNacimiento();
        this.anioMuerte = datos.anioMuerte();
        this.nombre = datos.nombre();
    }

    public Persona(String nombre, Long anioNacimiento, Long anioMuerte) {
        this.anioNacimiento = anioNacimiento;
        this.anioMuerte = anioMuerte;
        this.nombre = nombre;
    }

    public Long getAnioNacimiento() {
        return anioNacimiento;
    }


    public Long getAnioMuerte() {
        return anioMuerte;
    }

    public String getNombre() {
        return nombre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAnioNacimiento(Long anioNacimiento) {
        this.anioNacimiento = anioNacimiento;
    }

    public void setAnioMuerte(Long anioMuerte) {
        this.anioMuerte = anioMuerte;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Libro> getLibros() {
        return libros;
    }

    public void setLibros(List<Libro> libros) {
        this.libros = libros;
    }

    @Override
    public String toString() {
        return  "nombre = '" + nombre + '\'' +
                ", año de nacimiento = " + anioNacimiento +
                ", año de muerte = " + anioMuerte +
                ", ID: " + id;
    }

    public void addLibro(Libro libro) {
        if(this.libros != null){
            if(!this.libros.stream().anyMatch(l -> l.getId() == libro.getId())){
                this.libros.add(libro);
            }
        }else{
            this.libros = new ArrayList<>();
            this.libros.add(libro);
        }
    }
}
