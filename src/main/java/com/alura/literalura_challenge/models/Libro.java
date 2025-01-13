package com.alura.literalura_challenge.models;


import jakarta.persistence.*;

import java.util.Locale;


@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private Long descargas;
    private String lenguaje;

    @ManyToOne
    private Persona autor;


    public Libro() {}

    public Libro(DatosLibro datos) {
        this.id=datos.id();
        this.titulo = datos.titulo();
        this.autor = new Persona(datos.autores().get(0));
        this.descargas = datos.descargas();
        this.lenguaje = datos.lenguajes().getFirst();
    }

    public Libro(String titulo, Long descargas, String lenguaje, Persona autor) {
        this.titulo = titulo;
        this.descargas = descargas;
        this.lenguaje = lenguaje;
        this.autor = autor;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }


    public void setDescargas(Long descargas) {
        this.descargas = descargas;
    }

    public void setLenguaje(String lenguaje) {
        this.lenguaje = lenguaje;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public Persona getAutor() {
        return autor;
    }


    public Long getDescargas() {
        return descargas;
    }

    public String getLenguaje() {
        return lenguaje;
    }

    public void setAutor(Persona autor) {
        this.autor = autor;
    }

    @Override
    public String toString() {
        return
                "titulo='" + titulo + '\'' +
                        ", id=" + id +
                        ", autor=" + autor +
                ", descargas=" + descargas +
                ", lenguaje= " +lenguaje;
    }
    public String imprimir() {
        String idioma = new Locale(this.lenguaje).getDisplayLanguage(new Locale("es"));
        String str = " - " + this.titulo + " - Autor/a: " + this.autor.getNombre()
                + " - Idioma: " + idioma + " - Descargas: " + this.descargas;
        return str;
    }

}
