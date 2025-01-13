package com.alura.literalura_challenge.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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


    @Override
    public String toString() {
        return  "nombre = '" + nombre + '\'' +
                ", año de nacimiento = " + anioNacimiento +
                ", año de muerte = " + anioMuerte +
                ", ID: " + id;
    }

    public Long getEdad(){
        if(this.getAnioMuerte() != null){
            return this.getAnioMuerte() - this.getAnioNacimiento();
        }else {
            LocalDate fechaActual = LocalDate.now();
            return fechaActual.getYear() - this.getAnioNacimiento();
        }
    }
    public String imprimir() {
        String str = " - " + this.nombre + " - Nacido en " + this.getAnioNacimiento() + " - ";
        if(this.getAnioMuerte() != null){
            str += "Fallecido en "+ this.getAnioMuerte() + " a los " + getEdad() + " años";
        }else {
            str += "Edad actual: " + getEdad();
        }
        return str;
    }

}
