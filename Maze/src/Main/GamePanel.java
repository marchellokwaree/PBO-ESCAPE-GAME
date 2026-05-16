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
            { '1', 'S', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', },
            { '1', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '0', '1', },
            { '1', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', '1', },
            { '1', '0', '1', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '0', '1', '1', '1', '1', '0', '1', },
            { '1', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '1', '0', '1', },
            { '1', '1', '1', '0', '1', '0', '1', '1', '1', '1', '1', '0', '1', '1', '1', '1', '0', '1', '0', '1', },
            { '1', '0', '0', '0', '1', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', '1', },
            { '1', '0', '1', '1', '1', '0', '1', '0', '1', '0', '1', '1', '1', '1', '1', '0', '0', '1', '0', '1', },
            { '1', '0', '1', '0', '0', '0', '1', '0', '1', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '1', },
            { '1', '0', '1', '0', '1', '1', '1', '0', '1', '1', '1', '1', '1', '0', '1', '1', '1', '1', '1', '1', },
            { '1', '0', '1', '0', '0', '0', '1', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '1', },
            { '1', '0', '1', '1', '1', '1', '1', '0', '1', '1', '1', '0', '1', '1', '1', '1', '1', '1', '0', '1', },
            { '1', '0', '1', '0', '0', '0', '1', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '1', '0', '1', },
            { '1', '0', '1', '0', '1', '0', '1', '0', '1', '1', '1', '1', '1', '1', '1', '1', '0', '1', '0', '1', },
            { '1', '0', '1', '0', '1', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', '1', '0', '1', },
            { '1', '0', '0', '0', '1', '0', '1', '0', '0', '0', '0', '0', '0', '0', 'G', '1', '0', '1', '0', '1', },
            { '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', } };

    final int originalTileSize = 16;
    final int scale = 2;
    final int tileSize = originalTileSize * scale;
    final int maxScreenCol = map1[0].length;
    final int maxScreenRow = map1.length;
    final int screenWidth = tileSize * maxScreenCol;
    final int screenHeight = tileSize * maxScreenRow;
    KeyHandler keyH = new KeyHandler();
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
        this.addKeyListener(keyH);
        this.setFocusable(true);
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
        player = new Player(this, keyH, playerimg, playerx, playery);
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public int getTileSize() {
        return tileSize;
    }

    public boolean isWallTile(int col, int row) {
        if (col < 0 || col >= maxScreenCol || row < 0 || row >= maxScreenRow) {
            return true;
        }
        return map1[row][col] == '1';
    }

    public boolean collidesWithWall(int nextX, int nextY) {
        int left = nextX / tileSize;
        int right = (nextX + tileSize - 1) / tileSize;
        int top = nextY / tileSize;
        int bottom = (nextY + tileSize - 1) / tileSize;

        return isWallTile(left, top) || isWallTile(right, top) ||
                isWallTile(left, bottom) || isWallTile(right, bottom);
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / 60; // 60 FPS
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    public void update() {
        if (player != null) {
            player.update();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Gambar Map
        for (int i = 0; i < maxScreenRow; i++) {
            for (int j = 0; j < maxScreenCol; j++) {
                g2.drawImage(Tanah, j * tileSize, i * tileSize, tileSize, tileSize, null);
                if (map1[i][j] == '1') {
                    g2.drawImage(wall, j * tileSize, i * tileSize, tileSize, tileSize, null);
                }
            }
        }

        // Gambar Player menggunakan method draw dari class Player
        if (player != null) {
            player.draw(g2);
        }

        g2.dispose();
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
