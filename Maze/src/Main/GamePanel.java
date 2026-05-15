package Main;

import javax.swing.JPanel;

import Entitiy.Player;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GamePanel extends JPanel implements Runnable {

    char map1[][] = {
            { '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', },
            { '1', 'S', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '1', },
            { '1', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '0', '1', '1', },
            { '1', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', '1', '0', '1', '1', },
            { '1', '0', '1', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '0', '1', '0', '1', '0', '1', '1', },
            { '1', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '1', '0', '0', '0', '1', '0', '1', '1', },
            { '1', '1', '1', '0', '1', '0', '1', '1', '1', '1', '1', '0', '1', '1', '1', '1', '1', '0', '1', '1', },
            { '1', '0', '0', '0', '1', '0', '1', '0', '0', '0', '1', '0', '0', '0', '0', '0', '1', '0', '1', '1', },
            { '1', '0', '1', '1', '1', '0', '1', '0', '1', '0', '1', '1', '1', '1', '1', '0', '1', '0', '1', '1', },
            { '1', '0', '1', '0', '0', '0', '1', '0', '1', '0', '0', '0', '0', '0', '1', '0', '0', '0', '1', '1', },
            { '1', '0', '1', '0', '1', '1', '1', '0', '1', '1', '1', '1', '1', '0', '1', '1', '1', '1', '1', '1', },
            { '1', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '1', '1', },
            { '1', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '0', '1', '1', '1', '1', '1', '0', '1', '1', },
            { '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '1', '0', '1', '1', },
            { '1', '1', '1', '1', '1', '1', '1', '1', '1', '0', '1', '1', '1', '1', '1', '0', '1', '0', '1', '1', },
            { '1', '0', '0', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '1', },
            { '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '0', 'N', },
            { '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', } };

    final int originalTileSize = 16;
    final int scale = 2;
    final int tileSize = originalTileSize * scale;
    final int maxScreenCol = map1[0].length;
    final int maxScreenRow = map1.length;
    final int screenWidth = tileSize * maxScreenCol;
    final int screenHeight = tileSize * maxScreenRow;
    Image Tanah;
    Image Player;
    Image NPC;
    Image wallKananAtas;
    Image wallKananBawah;
    Image wallKiriAtas;
    Image wallKiriBawah;
    Image wall;
    Image wallvertical;
    Image wallhorizontal;
    Image wallKiri, wallKanan, wallAtas, wallBawah;
    Thread gameThread;
    Player player;
    int playerx, playery;
    int endx, endy;
    BufferedImage bufferedImage;

    Image playerimg;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLUE);
        this.setDoubleBuffered(true); // , sebuah teknik yang mencegah kedipan layar dengan merender grafik kompleks
                                      // dalam buffer di luar layar sebelum menampilkannya.

        for (int i = 0; i < maxScreenRow; i++) {
            for (int j = 0; j < maxScreenCol; j++) {
                if (map1[i][j] == 'S') {
                    playerx = j * tileSize;
                    playery = i * tileSize;
                }
                if (map1[i][j] == 'N') {
                    endx = j * tileSize;
                    endy = i * tileSize;
                }
            }
        }

        try {
            System.out.println(
                    "Loading bufferedImage from: " + getClass().getResource("/Assets/ASSET/AnimationSheet.png"));
            bufferedImage = ImageIO.read(getClass().getResourceAsStream("/Assets/ASSET/AnimationSheet.png"));
            System.out.println("BufferedImage loaded: " + (bufferedImage != null));
        } catch (Exception e) {
            System.out.println("Error loading AnimationSheet.png");
            e.printStackTrace();
        }

        loadAssets();
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        while (gameThread != null) {

            update();

            repaint();

        }
    }

    public void update() {

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR); // untuk
                                                                                                                    // menjaga
                                                                                                                    // kualitas
                                                                                                                    // gambar
                                                                                                                    // tetap
                                                                                                                    // tajam
                                                                                                                    // saat
                                                                                                                    // diskalakan
        for (int i = 0; i < maxScreenRow; i++) {
            for (int j = 0; j < maxScreenCol; j++) {
                g2.drawImage(Tanah, j * tileSize, i * tileSize, tileSize, tileSize, null);

                if (map1[i][j] == '1') {
                    g2.drawImage(wall, j * tileSize, i * tileSize, tileSize, tileSize, null);
                }
                if (map1[i][j] == '0') {
                    g2.drawImage(Tanah, j * tileSize, i * tileSize, tileSize, tileSize, null);
                }
                if (map1[i][j] == 'S') {
                    g2.drawImage(playerimg, j * tileSize, i * tileSize, tileSize, tileSize, null);
                }

            }
        }
    }

    public void loadAssets() {
        Tanah = loadImage("/Assets/lab_tileset_LITE/seperated/tile031.png");

        NPC = loadImage("/Assets/lab_tileset_LITE/seperated/tile033.png");
        wallKananAtas = loadImage("/Assets/lab_tileset_LITE/seperated/tile016.png");
        wallKananBawah = loadImage("/Assets/lab_tileset_LITE/seperated/tile025.png");
        wallKiriAtas = loadImage("/Assets/lab_tileset_LITE/seperated/tile015.png");
        wallKiriBawah = loadImage("/Assets/lab_tileset_LITE/seperated/tile024.png");
        wall = loadImage("/Assets/lab_tileset_LITE/seperated/tile066.png");
        wallvertical = loadImage("/Assets/lab_tileset_LITE/seperated/tile039.png");
        wallhorizontal = loadImage("/Assets/lab_tileset_LITE/seperated/tile040.png");
        wallKiri = loadImage("/Assets/lab_tileset_LITE/seperated/tile055.png");
        wallKanan = loadImage("/Assets/lab_tileset_LITE/seperated/tile054.png");
        wallAtas = loadImage("/Assets/lab_tileset_LITE/seperated/tile068.png");
        wallBawah = loadImage("/Assets/lab_tileset_LITE/seperated/tile041.png");
        playerimg = bufferedImage.getSubimage(0, 0, 25, 25);
    }

    public Image loadImage(String path) {
        Image img = null;
        try {
            img = new ImageIcon(getClass().getResource(path)).getImage();
        } catch (Exception e) {
            System.out.println("Error loading image: " + path);
            e.printStackTrace();
        }
        return img;
    }

}
