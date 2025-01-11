package com.alura.literalura_challenge.models;

public class Persona {
    private Long anioNacimiento;
    private Long anioMuerte;
    private String nombre;

    public Persona(DatosPersona datos) {
        this.anioNacimiento = datos.anioNacimiento();
        this.anioMuerte = datos.anioMuerte();
        this.nombre = datos.nombre();
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

    @Override
    public String toString() {
        return  "nombre = '" + nombre + '\'' +
                ", año de nacimiento = " + anioNacimiento +
                ", año de muerte = " + anioMuerte;
    }
}
