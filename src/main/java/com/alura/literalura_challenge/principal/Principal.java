package com.alura.literalura_challenge.principal;


import com.alura.literalura_challenge.models.DatosAPI;
import com.alura.literalura_challenge.models.DatosLibro;
import com.alura.literalura_challenge.models.Libro;
import com.alura.literalura_challenge.services.ConsumoAPI;
import com.alura.literalura_challenge.services.ConvierteDatos;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private final String URL = "https://gutendex.com/books/";
    private final Scanner scanner = new Scanner(System.in);
    private final ConsumoAPI api = new ConsumoAPI();
    private final ConvierteDatos conversor = new ConvierteDatos();
    private List<DatosLibro> datosLibro = new ArrayList<>();
    //private SerieRepository repository;
    private List<Libro> libros = new ArrayList<>();

    //public Principal(SerieRepository repository) {
//        this.repository = repository;
//    }


    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    
                    ***********************************************
                    
                    1 - Buscar Libro 
                    2 - Mostrar libros buscados
                    3 - Top 5 mejores libros en español
                    4 - Buscar libros por Categoria (Genero)
                    5 - Buscar por cantidad de descargas
                                  
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
                case 6:
//                    buscarSeriesPorCategoria();
                    break;
                case 7:
//                    buscarSeriesPorEvaluacionYTemporadas();
                    break;
                case 8:
//                    buscarEpisodiosPorTitulo();
                    break;
                case 9:
//                    buscarTop5EpisodiosPorSerie();
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

    private List<DatosLibro> getTop5DatosLibros() {
        var json = api.obtenerDatos(URL + "?sort=popular&languages=es");
        System.out.println(json);
        DatosAPI datos = conversor.obtenerDatos(json, DatosAPI.class);
        return datos.libros().stream().limit(5).toList();
    }


    private void buscarLibro() {
        DatosAPI datosAPI = getDatosAPI();
        DatosLibro datos = datosAPI.libros().get(0);
        Libro libro  = new Libro(datos);
        //repository.save(serie);
        libros.add(libro);
        datosLibro.add(datos);
        System.out.println(datos);
    }
    private void mostrarLibrosBuscados() {
        //libros = repository.findAll();

        libros.stream()
                .sorted(Comparator.comparing(Libro::getTitulo))
                .forEach(System.out::println);
    }


    private void buscarTop5Descargados() {
        List<Libro> topLibros = conversor.convertirListaALibros(getTop5DatosLibros());

        int i = 1;
        for(Libro s : topLibros){
            System.out.println(i + " - "+s.getTitulo() + " - Autor/es: "+s.getAutores());
            i++;
        }
        System.out.println("----------------------------\n");
    }
    /*private void buscarSeriesPorCategoria() {
        System.out.println("Ingrese una categoria, por ejemplo Accion:");
        String nombreCategoria = scanner.nextLine();
        Categoria categoria = null;
        try{
            categoria = Categoria.fromEspanol(nombreCategoria);
        }catch(IllegalArgumentException e){
            System.out.println(e.getMessage());
        }
        if(categoria == null){
            try{
                categoria = Categoria.fromString(nombreCategoria);
            }catch (IllegalArgumentException e){
                throw new IllegalArgumentException(e.getMessage());
            }
        }
        List<Serie> series = repository.findByGenero(categoria);
        if(!series.isEmpty()){
            series.forEach(s -> System.out.println(s.getTitle() + " - Genero: " + s.getGenero()));
        }else{
            System.out.println("No se ha encontrado una serie con la categoria "+ categoria);
        }
    }
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
