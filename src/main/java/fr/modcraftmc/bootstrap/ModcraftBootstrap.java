package fr.modcraftmc.bootstrap;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Logger;

public class ModcraftBootstrap {

    public static Logger LOGGER = Logger.getLogger("ModcraftMC");

    private static BootstrapFrame bootstrapPanel;

    public static void main(String[] args) throws IOException, FontFormatException {
        LOGGER.info("Starting Modcraft Launcher");
        bootstrapPanel = new BootstrapFrame();
        Thread updaterThread = new Thread(() -> {
            new Updater().update();
        });
        updaterThread.start();
    }

    public static BootstrapFrame getBootstrapFrame() {
        return bootstrapPanel;
    }

    public static URL getResource(String name) {
        return ModcraftBootstrap.class.getClassLoader().getResource(name);
    }
}