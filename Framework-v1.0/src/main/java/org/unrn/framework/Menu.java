package org.unrn.framework;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Menu {
    private final Scanner scanner = new Scanner(System.in);
    private List<Accion> acciones;
    private int cantAcciones;
    private int respuesta = 0;

    public Menu(String pathConfig) {
        cargarAcciones(pathConfig);
    }

    private void cargarAcciones(String pathConfig) {
        acciones = new ArrayList<>();
        Properties prop = new Properties();
        try {
            prop.load(getClass().getResourceAsStream(pathConfig));
            cantAcciones = prop.size();
            agregarAccion(prop);
        } catch (IOException e) {
            System.out.println("Problemas al leer el archivo de propiedades...");
        }
    }

    private void agregarAccion(Properties prop) {
        for (int i = 0; i < cantAcciones; i++) {
            String clase = prop.getProperty(String.valueOf(i + 1));
            Accion accion = null;
            try {
                accion = (Accion) Class.forName(clase).getDeclaredConstructor().newInstance();
            } catch (InstantiationException e) {
                System.out.println("Algo salió mal... InstantiationException");
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                System.out.println("Algo salió mal... IllegalAccessException");
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                System.out.println("Algo salió mal... InvocationTargetException");
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                System.out.println("Algo salió mal... NoSuchMethodException");
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                System.out.println("Algo salió mal... ClassNotFoundException");
                System.out.println("Revise la ruta {package}.Accion en config.properties");
                throw new RuntimeException(e);
            }
            acciones.add(accion);
        }
    }

    public void iniciar() {
        System.out.println("Bienvenido, estas son sus opciones:");
        leerOpcionIngresada();
        System.out.println("Cerrando programa");
    }

    private void leerOpcionIngresada() {

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