package com.alura.literalura_challenge.repository;

import com.alura.literalura_challenge.models.Libro;
import com.alura.literalura_challenge.models.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro, Long> {

    List<Libro> findByLenguaje(String lenguaje);

    @Query("SELECT l FROM Libro l ORDER BY l.descargas DESC LIMIT 5")
    List<Libro> top5Libros();

    Optional<Libro> findByTitulo(String nombre);

    @Query("SELECT l FROM Libro l WHERE l.descargas >= :cantidad ORDER BY l.descargas DESC LIMIT 10")
    List<Libro> buscarPorDescargas(Long cantidad);
}
