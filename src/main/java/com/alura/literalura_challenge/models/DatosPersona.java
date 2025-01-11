package com.alura.literalura_challenge.models;

import com.fasterxml.jackson.annotation.JsonAlias;

public record DatosPersona(
        @JsonAlias("birth_year") Long anioNacimiento,
        @JsonAlias("death_year") Long anioMuerte,
        @JsonAlias("name") String nombre
) {
}
