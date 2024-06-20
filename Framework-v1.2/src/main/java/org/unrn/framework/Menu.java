package org.unrn.framework;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Menu {
    private final String path;
    private final Scanner scanner = new Scanner(System.in);
    private List<Accion> acciones;
    private int cantAcciones;
    private int respuesta = 0;

    public Menu(String path) {
        this.path = path;
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
        System.out.println("Bienvenido, estas son sus opciones:");
        leerInput();
        System.out.println("Cerrando programa");
    }

    private void leerInput() {
        while (!isSalir()) {
            System.out.println(mostrarMenu());
            try {
                respuesta = scanner.nextInt();
                acciones.get(respuesta - 1).ejecutar();
            } catch (InputMismatchException | IndexOutOfBoundsException e) {
                if (!isSalir()) System.out.println("No es una respuesta válida");
            }
        }
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