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
            try {
                opcion = Integer.parseInt(scanner.nextLine());
            }catch (NumberFormatException e){
                opcion = -1;
                System.out.println("Debe ingresar una opcion válida");
            }

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
//        libroRepository.save(libro);
        return libroBuscado;
    }
    private Persona crearAutorSinoExiste(Libro libro) {
        Persona autor = autorRepository.findByNombre(libro.getAutor().getNombre())
                .orElseGet(()-> {
                    Persona nuevoAutor = persistirAutor(libro.getAutor());
                    return autorRepository.save(nuevoAutor);
                });
        libro.setAutor(autor);
//        libroRepository.save(libro);
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
                .forEach(System.out::println);
    }


    private void buscarTop5Descargados() {
        List<Libro> topLibros = libroRepository.top5Libros();

        int i = 1;
        for(Libro s : topLibros){
            System.out.println(i + " - "+s.getTitulo() + " - Autor/a: "+s.getAutor().getNombre()
            + " - Descargas: " + s.getDescargas());
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
                libros.forEach(l -> System.out.println("-" + l.getTitulo() + " - Autor:" + l.getAutor().getNombre()
                        + " - Idioma:" + l.getLenguaje()));
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
                libros.forEach(l -> System.out.println("-" + l.getTitulo() + " - Autor:" + l.getAutor().getNombre()
                        + " - Idioma:" + l.getLenguaje() + " - Descargas:" + l.getDescargas()));
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
            autores.forEach(a -> System.out.println("- " + a.getNombre()
                    + " - nacido el " + a.getAnioNacimiento() + " y fallecido en " + a.getAnioMuerte()
            + " a los " + (a.getAnioMuerte() - a.getAnioNacimiento()) + " años"));
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
                autores.forEach(a -> {
                    System.out.println("- " + a.getNombre()
                        + " - nacido el " + a.getAnioNacimiento());
                });
            }else {
                System.out.println("No se encontraron autores con esas caracteristicas");
            }
        }catch (Exception e){
            System.out.println("Ingrese por favor un año válido");
            System.out.println("Volviendo al menú principal...");
        }
    }

    /*
    private void buscarSeriesPorEvaluacionYTemporadas() {
        Double evaluacion;
        Integer cantidadTemporadas;
        System.out.println("Buscar por cantidad de temporadas y evaluacion minima\n");
        System.out.print("Ingrese cantidad de temporadas maxima:");
        cantidadTemporadas = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Ingrese evaluacion minima:");

        evaluacion = Double.parseDouble(scanner.nextLine());

        List<Serie> series = repository
                .seriesPorTemporadaYEvaluacion(cantidadTemporadas, evaluacion);
        series.forEach(s->
                System.out.println("- "+ s.getTitle() + " - Temps: " + s.getTotalDeTemporadas()
                        + " - Nota: " + s.getEvaluacion()));
    }
    private void buscarEpisodiosPorTitulo() {
        System.out.println("Buscar episodio por titulo:");
        String nombreEpisodio = scanner.nextLine();

        List<Episodio> episodios = repository.episodiosPorNombre(nombreEpisodio);

        System.out.println("\nEpisodios encontrados: \n");
        episodios.forEach(
                e -> System.out.printf("%s - Episodio %d de %s\n", e.getTitulo(), e.getNumeroEpisodio(), e.getSerie().getTitle())
                        //System.out.println(e.getTitulo() + " - Episodio " + e.getNumeroEpisodio() + " de " + e.getSerie().getTitle())
        );
    }
    private void buscarTop5EpisodiosPorSerie() {
        System.out.println("Ingrese la serie para buscar su top 5 episodios:");
        String nombreSerie = scanner.nextLine();
        List<Episodio> top5Episodios = repository.top5EpisodiosPorSerie(nombreSerie);
        if(!top5Episodios.isEmpty()){
            System.out.println("Top 5 episodios de " + nombreSerie);
            top5Episodios.forEach(e -> System.out.println(" - " + e.getTitulo() + " - Nota: " + e.getEvaluacion()));
        }else{
            System.out.println("No se encuentra episodios de \"" + nombreSerie + "\" en la base de datos");
        }
    }*/


//    public void muestraElMenu(){
//        //buscar datos de series y pasar JSON a DatosSerie
//        System.out.println("Por favor, ingrese el nombre de la serie que quieres buscar:");
//        var nombreSerie = scanner.nextLine();
//        String datosJson = null;
//        datosJson = api.obtenerDatos(URL + URLEncoder.encode(nombreSerie, StandardCharsets.UTF_8));
//        var datos = conversor.obtenerDatos(datosJson, DatosSerie.class);
//        System.out.println(datos);
//
//        //Obtencion, conversion y creacion de lista de temporadas
//        List<DatosTemporadas> temporadas = new ArrayList<>();
//        for (int i = 1; i <= datos.totalDeTemporadas(); i++){
//            datosJson = api.obtenerDatos(this.URL+ URLEncoder.encode(nombreSerie, StandardCharsets.UTF_8) +"&Season="+i);
//            var datosTemporadas = conversor.obtenerDatos(datosJson, DatosTemporadas.class);
//            temporadas.add(datosTemporadas);
//        }
//        //temporadas.forEach(System.out::println);
//
//        //mostrar solo titulo de los episodios para las temporadas
//        /*for (int i = 1; i <= datos.totalDeTemporadas(); i++) {
//            List<DatosEpisodio> episodios = temporadas.get(i-1).episodios();
//            System.out.println("Episodios de temporada " + i + " :");
//            for (DatosEpisodio e : episodios) {
//                System.out.println("- "+e.titulo());
//            }
//        }*/
//
//
//        /*temporadas.forEach(t -> {
//            System.out.println("Episodios de temporada " + t.numero() + ":");
//            t.episodios().forEach(e -> System.out.println("- "+e.titulo()));
//        });*/
//
//        // convertir todo la info a lista de DatosEpisodios
//
//        List<DatosEpisodio> datosEpisodios = temporadas.stream()
//                .flatMap(t -> t.episodios().stream())
//                .collect(Collectors.toList()); //el resultado de la lista sera mutable y con toList() es inmutable
//
//        //top 5 episodios
//        /*System.out.println("Top 5 episodios:");
//        datosEpisodios.stream()
//                .filter(e -> !e.evaluacion().equals("N/A"))
//                .peek(e -> System.out.println("Primer filtro N/A:  "+ e))
//                .sorted(Comparator.comparing(DatosEpisodio::evaluacion).reversed())
//                .peek(e -> System.out.println("Ordenacion de M a m: "+ e))
//                .map(e -> e.titulo().toUpperCase())
//                .peek(e -> System.out.println("Mapeo Uppercase: "+ e))
//                .limit(5)
//                .forEach(System.out::println);*/
////                .forEach(e -> System.out.println("- "+e.titulo()+" "+e.evaluacion()));
//
//        System.out.println();
//
//        //convirtiendo los datos a lista de tipo Episodio
//
//        List<Episodio> episodios = temporadas.stream()
//                .flatMap(t -> t.episodios().stream()
//                        .map(e -> new Episodio(t.numero(), e)))
//                .collect(Collectors.toList());
//
//        /*episodios.forEach(System.out::println);*/
//
//        //busqueda de episodios a partir de un año
//
//        /*System.out.println("Ingrese un año:");
//
//        var fecha = scanner.nextInt();
//        scanner.nextLine();
//
//        LocalDate fechaBusqueda = LocalDate.of(fecha, 1 ,1);*/
//
//
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        /*episodios.stream()
//                .filter(e -> e.getFechaLanzamiento() != null &&
//                        e.getFechaLanzamiento().isAfter(fechaBusqueda))
//                .forEach(e -> System.out.println(
//                        "Temporada "+ e.getTemporada() +
//                                ", Episodio " + e.getTitulo() +
//                                ", Lanzamiento " + e.getFechaLanzamiento().format(dtf)
//                ));*/
//
//        //buscar episodios
//
////        System.out.println("Buscar episodios:");
////        var userSearchQuery = scanner.nextLine();
////        episodios.stream()
////                .filter(e-> e.getTitulo()
////                        .toLowerCase()
////                        .contains(userSearchQuery.toLowerCase()))
////                .findAny()
////                .ifPresentOrElse(System.out::println, () -> System.out.println("No encontrado"));
//
////        Map<Integer, Double> evaluacionesPorTemporada = episodios.stream()
////                .filter(e->e.getEvaluacion() > 0.0)
////                .collect(Collectors.groupingBy(Episodio::getTemporada,
////                        Collectors.averagingDouble(Episodio::getEvaluacion)));
////
////        System.out.println(evaluacionesPorTemporada);
//
//        DoubleSummaryStatistics est = episodios.stream()
//                .filter(e-> e.getEvaluacion() > 0.0)
//                .collect(Collectors.summarizingDouble(Episodio::getEvaluacion));
//        System.out.println("Media de evaluaciones: "+ est.getAverage());
//        System.out.println("Episodio mejor evaluado: " + est.getMax());
//        System.out.println("Episodio peor evaluado: " + est.getMin());
//
//    }
}
