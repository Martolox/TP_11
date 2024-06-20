package org.unrn.framework;

import de.codeshelf.consoleui.prompt.ConsolePrompt;
import de.codeshelf.consoleui.prompt.PromtResultItemIF;
import de.codeshelf.consoleui.prompt.builder.PromptBuilder;
import org.fusesource.jansi.AnsiConsole;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static org.fusesource.jansi.Ansi.ansi;

public class Menu {
    private final Scanner scanner = new Scanner(System.in);
    private List<Accion> acciones;
    private int cantAcciones;

    public Menu(String pathConfig) {
        AnsiConsole.systemInstall();
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
        System.out.println(ansi().eraseScreen().render("@|red,italic Bienvenido," +
                " estas son su opciones:|@\n@|reset |@"));
        boolean salir = false;
        while (!salir) {
            try {
                ConsolePrompt prompt = new ConsolePrompt();
                PromptBuilder promptBuilder = prompt.getPromptBuilder();
                var listPrompt = promptBuilder.createListPrompt();
                listPrompt.name("menu")
                        .message("");
                for (int i = 0; i < cantAcciones; i++) {
                    listPrompt.newItem(acciones.get(i).nombreItemMenu())
                            .text(String.format("%d. %s", i + 1, acciones.get(i).descripcionItemMenu()))
                            .add();
                }
                listPrompt.newItem("Salir").text(String.format("%d. Salir", cantAcciones + 1)).add()
                        .message("").addPrompt();

                HashMap<String, ? extends PromtResultItemIF> result = null;
                result = prompt.prompt(promptBuilder.build());
                for (int i = 0; i < cantAcciones; i++) {
                    if (result.toString().contains(acciones.get(i).nombreItemMenu()))
                        acciones.get(i).ejecutar();
                }
                if (result.toString().contains("Salir")) {
                    System.out.println("Cerrando el programa");
                    salir = true;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}