package org.unrn.framework;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Menu {
    private final static int MAX_THREADS = 4;
    private final String path;
    private final Scanner scanner = new Scanner(System.in);
    private List<Accion> acciones;
    private int cantAcciones;
    private int respuesta = 0;
    private ExecutorService executor;

    public Menu(String path) {
        this.path = path;
    }

    private static void mostrarTexto(String texto) {
        System.out.println(texto);
    }

    public void iniciar() {
        cargarAcciones();
        correrEjecucion();
    }

    private void cargarAcciones() {
        Lector lector = new Lector(path);
        acciones = lector.leerArchivo();
        cantAcciones = acciones.size();
    }

    private void correrEjecucion() {
        //-------------------//

        executor = Executors.newFixedThreadPool(MAX_THREADS);

        //-------------------//
        mostrarTexto("Bienvenido, estas son sus opciones:");
        leerInput();
        mostrarTexto("Cerrando programa");
    }

    private void leerInput() {
        while (!isSalir()) {
            mostrarTexto(mostrarMenu());
            try {
                respuesta = scanner.nextInt();
//                acciones.get(respuesta - 1).ejecutar();
                //-------------------//

                Future<String> future = executor.submit(() -> ejecutarAccion(acciones.get(respuesta - 1)));
                System.out.println(future.get());

                //------------------//
            } catch (InputMismatchException | IndexOutOfBoundsException e) {
                if (!isSalir()) mostrarTexto("No es una respuesta válida");
            } catch (ExecutionException | InterruptedException e) {
                mostrarTexto("Problema al abrir un hilo o ejecución interrumpida...");
            }
        }
    }

    private String ejecutarAccion(Accion accion) throws InterruptedException {
        accion.ejecutar();
        Thread.sleep(5000l);
        return "Finalizada la acción";
    }

    private boolean isSalir() {
        return (respuesta == (cantAcciones + 1));
    }

    private String mostrarMenu() {
        String texto = "\n";
        for (int i = 0; i < cantAcciones; i++)
            texto += String.format("%d. %s (%s) \n", i + 1,
                    acciones.get(i).nombreItemMenu(),
                    acciones.get(i).descripcionItemMenu());
        texto += String.format("%d. Salir\n\nIngrese su opción: ", cantAcciones + 1);
        return texto;
    }
}