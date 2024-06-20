package org.unrn.framework;

import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Menu {
    private List<Accion> acciones;
    private int cantAcciones;
    private boolean salir = false;

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
        try {
            Terminal terminal = new DefaultTerminalFactory().createTerminal();
            Screen screen = new TerminalScreen(terminal);
            screen.startScreen();
            final WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);

            while (!salir) {
                var dialogo = new ActionListDialogBuilder()
                        .setTitle("Bienvenido")
                        .setDescription("Estas son sus opciones:");
                for (int i = 0; i < cantAcciones; i++) {
                    int finalI = i;
                    dialogo.addAction(String.format("%d. %s", i + 1, acciones.get(i).descripcionItemMenu())
                            , new Runnable() {
                                @Override
                                public void run() {
                                    MessageDialog.showMessageDialog(
                                            textGUI, acciones.get(finalI).nombreItemMenu(),
                                            "Se ejecuta acción");
                                    acciones.get(finalI).ejecutar();
                                }
                            });
                }
                dialogo.addAction(String.format("%d. Salir", cantAcciones + 1)
                        , new Runnable() {
                            @Override
                            public void run() {
                                MessageDialog.showMessageDialog(textGUI, "Salir", "Se cierra el programa");
                                salir = true;
                            }
                        });
                dialogo.build().showDialog(textGUI);
            }
            screen.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}