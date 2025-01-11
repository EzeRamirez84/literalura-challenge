package com.alura.literalura_challenge.models;

import com.alura.literalura_challenge.services.ConvierteDatos;
import java.util.List;

public class Libro {
    private Long id;
    private String titulo;
    private List<Persona> autores;
    private List<String> temas;
    private Long descargas;
    private ConvierteDatos convertidor = new ConvierteDatos();

    public Libro(DatosLibro datos) {
        this.id=datos.id();
        this.titulo = datos.titulo();
        this.autores = convertidor.convertirListaAPersona(datos.autores());
        this.temas = datos.temas();
        this.descargas = datos.descargas();
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public List<Persona> getAutores() {
        return autores;
    }

    public List<String> getTemas() {
        return temas;
    }

    public Long getDescargas() {
        return descargas;
    }


    @Override
    public String toString() {
        return
                "titulo='" + titulo + '\'' +
                        ", id=" + id +
                        ", autores=" + autores +
                ", temas=" + temas +
                ", descargas=" + descargas;
    }
}
