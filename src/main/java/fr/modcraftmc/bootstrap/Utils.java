package fr.modcraftmc.bootstrap;

import java.net.URL;

public class Utils {

    public static URL getResource(String name) {
        return Utils.class.getClassLoader().getResource(name);
    }
}
