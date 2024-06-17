package utilizacion;

import org.unrn.framework.Accion;

public class AccionDos implements Accion {
    @Override
    public void ejecutar() {
        System.out.println("Ejecutando AccionDos...");
    }

    @Override
    public String nombreItemMenu() {
        return "Acción 2";
    }

    @Override
    public String descripcionItemMenu() {
        return "Esto trae las primeras diez personas de la BD...";
    }
}
