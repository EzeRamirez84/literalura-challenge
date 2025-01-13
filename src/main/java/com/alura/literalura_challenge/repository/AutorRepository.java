package com.alura.literalura_challenge.repository;

import com.alura.literalura_challenge.models.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Persona, Long> {
    Optional<Persona> findById(Long id);
    Optional<Persona> findByNombre(String nombre);

    @Query("SELECT a FROM Persona a ORDER BY a.nombre")
    List<Persona> obtenerAutoresOrdenadosPorNombre();

    @Query("SELECT a FROM Persona a WHERE a.anioNacimiento <= :anio AND a.anioMuerte >= :anio")
    List<Persona> obtenerAutoresVivosEn(Long anio);
}
