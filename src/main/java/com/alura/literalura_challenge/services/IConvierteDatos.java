package com.alura.literalura_challenge.services;

public interface IConvierteDatos {
    <T> T obtenerDatos(String json, Class<T> clase);
}
