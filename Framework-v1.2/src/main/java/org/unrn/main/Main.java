package org.unrn.main;

import org.unrn.framework.Menu;

public class Main {
    public static void main(String[] args) {
        // src/main/resources/config.json
        // /config.properties
        Menu menu = new Menu("/config.properties");
        menu.iniciar();
    }
}
