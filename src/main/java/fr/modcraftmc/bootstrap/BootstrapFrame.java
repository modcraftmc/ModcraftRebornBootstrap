package fr.modcraftmc.bootstrap;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class BootstrapFrame extends JFrame {

    private BootstrapPanel bootstrapPanel;

    public BootstrapFrame() throws IOException, FontFormatException {
        this.setTitle("Modcraft Launcher");
        this.setSize(700, 400);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setUndecorated(true);
        this.setContentPane(bootstrapPanel = new BootstrapPanel());
        this.setIconImage(ImageIO.read(ModcraftBootstrap.getResource("images/logo.png")));
        this.setVisible(true);
    }

    public BootstrapPanel getBootstrapPanel() {
        return bootstrapPanel;
    }
}
