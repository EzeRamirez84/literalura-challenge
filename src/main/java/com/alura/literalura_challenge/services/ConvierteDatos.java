package com.alura.literalura_challenge.services;

import com.alura.literalura_challenge.models.DatosLibro;
import com.alura.literalura_challenge.models.DatosPersona;
import com.alura.literalura_challenge.models.Libro;
import com.alura.literalura_challenge.models.Persona;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.stream.Collectors;

public class ConvierteDatos implements IConvierteDatos {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public <T> T obtenerDatos(String json, Class<T> clase) {
        try {
            return objectMapper.readValue(json, clase);
        } catch (JsonProcessingException e) {
            System.out.println("Problema en obtenerDatos");
            throw new RuntimeException(e);
        }
    }

    public List<Persona> convertirListaAPersona(List<DatosPersona> datos){
        return datos.stream().map(d -> new Persona(d))
                .collect(Collectors.toList());
    }

    public List<Libro> convertirListaALibros(List<DatosLibro> datos){
        return datos.stream().map(d -> new Libro(d))
                .collect(Collectors.toList());
    }
}
