package com.alura.literalura_challenge.principal;


import com.alura.literalura_challenge.models.DatosAPI;
import com.alura.literalura_challenge.models.DatosLibro;
import com.alura.literalura_challenge.models.Libro;
import com.alura.literalura_challenge.models.Persona;
import com.alura.literalura_challenge.repository.AutorRepository;
import com.alura.literalura_challenge.repository.LibroRepository;
import com.alura.literalura_challenge.services.ConsumoAPI;
import com.alura.literalura_challenge.services.ConvierteDatos;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Principal {
    private final String URL = "https://gutendex.com/books/";
    private final Scanner scanner = new Scanner(System.in);
    private final ConsumoAPI api = new ConsumoAPI();
    private final ConvierteDatos conversor = new ConvierteDatos();
    private final Map<Integer, String> idiomas = new HashMap<>();
    @Autowired
    private LibroRepository libroRepository;
    @Autowired
    private AutorRepository autorRepository;
    private List<Libro> libros = new ArrayList<>();

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;

        //Seguro hay una forma mas correcta de implementarlo
        this.idiomas.put(1, "es");
        this.idiomas.put(2, "pt");
        this.idiomas.put(3, "en");
        this.idiomas.put(4, "fr");
    }


    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    
                    ***********************************************
                    
                    1 - Buscar Libro 
                    2 - Mostrar libros buscados
                    3 - Top 5 mejores libros
                    4 - Mostrar libros por lenguaje y cantidad para ese lenguaje
                    5 - Buscar por cantidad de descargas
                    6 - Mostrar todos los autores
                    7 - Mostrar autores vivos en determinado año
                                  
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = pedirOpcionAlUsuario();

            switch (opcion) {
                case 1:
                    buscarLibro();
                    break;
                case 2:
                    mostrarLibrosBuscados();
                    break;
                case 3:
                    buscarTop5Descargados();
                    break;
                case 4:
                    mostrarLibrosPorLenguaje();
                    break;
                case 5:
                    buscarLibrosPorDescargas();
                    break;
                case 6:
                    mostrarAutoresOrdenadosPorNombre();
                    break;
                case 7:
                    mostrarAutoresVivosEnDeterminadoAnio();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }


    private DatosAPI getDatosAPI() {
        System.out.println("Escribe el nombre del libro que deseas buscar");
        var nombreLibro = scanner.nextLine();
        var json = api.obtenerDatos(URL + "?search=" + URLEncoder.encode(nombreLibro, StandardCharsets.UTF_8));
        System.out.println(json);
        return conversor.obtenerDatos(json, DatosAPI.class);
    }

    private Persona persistirAutor(Persona autor){
        return new Persona(autor.getNombre(), autor.getAnioNacimiento(), autor.getAnioMuerte());
    }

    private Libro persistirLibro(Libro libro){
        return new Libro(libro.getTitulo(), libro.getDescargas(), libro.getLenguaje(), libro.getAutor());
    }
    private Libro crearLibroSinoExiste(Libro libro) {
        Libro libroBuscado = libroRepository.findByTitulo(libro.getTitulo())
                .orElseGet(()-> {
                    Libro nuevoLibro = persistirLibro(libro);
                    return libroRepository.save(nuevoLibro);
                });
        return libroBuscado;
    }
    private Persona crearAutorSinoExiste(Libro libro) {
        Persona autor = autorRepository.findByNombre(libro.getAutor().getNombre())
                .orElseGet(()-> {
                    Persona nuevoAutor = persistirAutor(libro.getAutor());
                    return autorRepository.save(nuevoAutor);
                });
        libro.setAutor(autor);
        return autor;
    }
    @Transactional
    private void buscarLibro() {
        DatosAPI datosAPI = getDatosAPI();
        DatosLibro datos;
        if(datosAPI.libros() != null && !datosAPI.libros().isEmpty()){
            datos = datosAPI.libros().get(0);
            System.out.println(datos);
            Libro libro  = new Libro(datos);
            Persona autor = crearAutorSinoExiste(libro);
            libro = crearLibroSinoExiste(libro);
            System.out.println(autor);
            System.out.println("LIBRO: "+libro);
        }else{
            System.out.println("No se encontró ningún resultado");
        }
    }

    private void mostrarLibrosBuscados() {
        libros = libroRepository.findAll();

        libros.stream()
                .sorted(Comparator.comparing(Libro::getTitulo))
                .forEach(l -> System.out.println(l.imprimir()));
    }


    private void buscarTop5Descargados() {
        List<Libro> topLibros = libroRepository.top5Libros();

        int i = 1;
        for(Libro s : topLibros){
            System.out.println(i + s.imprimir());
            i++;
        }
        System.out.println("----------------------------\n");
    }

    private void mostrarOpcionesDeLenguaje(){
        System.out.println("""
                Elija un lenguaje con las opciones
                1 - Español
                2 - Portugues
                3 - Inglés
                4 - Francés
                5 - Otro
                """);
    }

    private Integer pedirOpcionAlUsuario() {
        Integer opcion;
        try{
            opcion = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            opcion = -1;
            System.out.println("Error: Debe ingresar un numero de los mostrados en la lista de opciones");
        }
        return opcion;
    }
    private void mostrarLibrosPorLenguaje() {
        mostrarOpcionesDeLenguaje();
        Integer opcionElegida = pedirOpcionAlUsuario();
        if(opcionElegida != -1){
            String idiomaElegido = this.idiomas.get(opcionElegida);
            List<Libro> libros = libroRepository.findByLenguaje(idiomaElegido);

            if(!libros.isEmpty()){
                System.out.println("Cantidad de libros encontrados para el lenguaje "+ idiomaElegido.toUpperCase() + ": "+ libros.size());
                libros.forEach(l -> System.out.println(l.imprimir()));
            }else{
                System.out.println("No se encontraron libros con esas caracteristicas");
            }
        }

    }

    private void buscarLibrosPorDescargas() {
        System.out.println("*********************");
        System.out.println("Ingrese la cantidad de descargas minima que quiere que tengan los libros");
        try{
            Long cantidad = Long.parseLong(scanner.nextLine());
            List<Libro> libros = libroRepository.buscarPorDescargas(cantidad);
            if(!libros.isEmpty()){
                libros.forEach(l -> System.out.println(l.imprimir()));
            }else {
                System.out.println("No se encontraron libros con esas caracteristicas");
            }
        }catch (Exception e){
            System.out.println("Ingrese por favor un numero válido");
            System.out.println("Volviendo al menú principal...");
        }
    }

    private void mostrarAutoresOrdenadosPorNombre() {
        List<Persona> autores = autorRepository.obtenerAutoresOrdenadosPorNombre();
        if(autores != null && !autores.isEmpty()){
            System.out.println("Mostrando todos los autores:\n");
            autores.forEach(a -> System.out.println(a.imprimir()));
        }else{
            System.out.println("No se encontraron autores");
        }
    }

    private void mostrarAutoresVivosEnDeterminadoAnio() {
        System.out.println("Ingrese un año para buscar que autores estaban o estan vivos");
        try{
            Long anio = Long.parseLong(scanner.nextLine());
            List<Persona> autores = autorRepository.obtenerAutoresVivosEn(anio);
            if(!autores.isEmpty()){
                System.out.println("Autores vivos en " + anio + "\n");
                autores.forEach(a ->
                    System.out.println(a.imprimir())
                );
            }else {
                System.out.println("No se encontraron autores con esas caracteristicas");
            }
        }catch (Exception e){
            System.out.println("Ingrese por favor un año válido");
            System.out.println("Volviendo al menú principal...");
        }
    }

}
