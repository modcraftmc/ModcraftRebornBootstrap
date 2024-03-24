package fr.modcraftmc.bootstrap;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class BootstrapPanel extends JPanel {

    private final Image background = ImageIO.read(ModcraftBootstrap.getResource("images/background.png"));

    //private JProgressBar progressBar = new JProgressBar();
    private JLabel topLabel = new JLabel("Démarrage du launcher", SwingConstants.CENTER);
    private JLabel bottomLabel = new JLabel("", SwingConstants.CENTER);

    public BootstrapPanel() throws IOException, FontFormatException {
        this.setLayout(null);
        //progressBar.setBounds(150, 320, 400, 20);

        topLabel.setBounds(150, 340, 400, 50);

        InputStream is = ModcraftBootstrap.class.getClassLoader().getResourceAsStream("LilitaOne-Regular.ttf");
        Font font = Font.createFont(Font.TRUETYPE_FONT, is);
        Font sizedFont = font.deriveFont(20f);
        topLabel.setFont(sizedFont);
        topLabel.setForeground(Color.WHITE);

        bottomLabel.setBounds(150, 360, 400, 50);
        bottomLabel.setFont(sizedFont.deriveFont(15f));
        bottomLabel.setForeground(Color.WHITE);

        //this.add(progressBar);
        this.add(topLabel);
        this.add(bottomLabel);
    }

    public void paintComponent(Graphics g) {
        super.paintComponents(g);
        g.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), this);

    }

    public void updateTopText(String text) {
        this.topLabel.setText(text);
    }

    public void updateBottomText(String text) {
        this.bottomLabel.setText(text);
    }
}
