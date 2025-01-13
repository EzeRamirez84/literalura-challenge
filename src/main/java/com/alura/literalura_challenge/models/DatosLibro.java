package com.alura.literalura_challenge.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosLibro(
        Long id,
        @JsonAlias("title") String titulo,
        @JsonAlias("authors") List<DatosPersona> autores,
        @JsonAlias("subjects") List<String> temas,
        @JsonAlias("download_count") Long descargas,
        @JsonAlias("languages") List<String> lenguajes
        ) {
}
