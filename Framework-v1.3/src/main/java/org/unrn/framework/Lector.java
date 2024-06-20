package org.unrn.framework;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class Lector {
    private final String path;
    private List<Accion> acciones;

    public Lector(String path) {
        this.path = path;
    }

    private static Accion intentarInstanciarClase(String nombreAccion) {
        Accion accion;
        try {
            accion = (Accion) Class.forName(nombreAccion).getDeclaredConstructor().newInstance();
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
        return accion;
    }

    public List<Accion> leerArchivo() {
        acciones = new ArrayList<>();
        try {
            if (path.contains(".json")) {
                leerJSON();
                return acciones;
            } else if (path.contains(".properties")) {
                leerProperties();
                return acciones;
            }
        } catch (FileNotFoundException e) {
            System.out.println("Problemas al leer el archivo de propiedades...");
        } catch (IOException e) {
            System.out.println("Problemas al leer el archivo... IOException");
        } catch (ParseException e) {
            System.out.println("Problemas al parsear el archivo JSON...");
        }
        return null;
    }

    private List<Accion> leerJSON() throws IOException, ParseException {
        Object obj = new JSONParser().parse(new FileReader(path));
        JSONObject jsonObject = (JSONObject) obj;
        JSONArray array = (JSONArray) jsonObject.get("acciones");
        Iterator iterator = array.iterator();
        while (iterator.hasNext()) {
            acciones.add(intentarInstanciarClase((String) iterator.next()));
        }
        return acciones;
    }

    private List<Accion> leerProperties() throws IOException {
        Properties prop = new Properties();
        prop.load(getClass().getResourceAsStream(path));
        for (int i = 0; i < prop.size(); i++) {
            String clase = prop.getProperty(String.valueOf(i + 1));
            acciones.add(intentarInstanciarClase(clase));
        }
        return acciones;
    }
}
