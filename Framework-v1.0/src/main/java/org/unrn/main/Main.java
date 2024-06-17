package org.unrn.main;

import org.unrn.framework.Menu;

public class Main {
    public static void main(String[] args) {
        Menu menu = new Menu("/config.properties");
        menu.iniciar();
    }
}
