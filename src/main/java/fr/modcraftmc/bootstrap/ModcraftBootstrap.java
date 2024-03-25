package fr.modcraftmc.bootstrap;

import fr.modcraftmc.bootstrap.logger.LogManager;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Logger;

public class ModcraftBootstrap {

    public static Logger LOGGER = LogManager.createLogger("ModcraftMC");

    private static BootstrapFrame bootstrapPanel;

    public static void main(String[] args) throws IOException, FontFormatException {
        LOGGER.info("Starting Modcraft Bootstrap");
        bootstrapPanel = new BootstrapFrame();
        Thread updaterThread = new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            new Updater().update();
        });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LogManager.getFileHandler().close();
        }));
        updaterThread.start();
    }

    public static BootstrapFrame getBootstrapFrame() {
        return bootstrapPanel;
    }

    public static URL getResource(String name) {
        return ModcraftBootstrap.class.getClassLoader().getResource(name);
    }

    public static File getLaunchPath() throws URISyntaxException {
        return new File(ModcraftBootstrap.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    }
}
