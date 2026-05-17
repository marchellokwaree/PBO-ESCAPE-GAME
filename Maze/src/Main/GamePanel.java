package Main;

import Entitiy.Player;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {
    // Map data
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

    final int tileSize = 32;
    int maxScreenCol, maxScreenRow, screenWidth, screenHeight;

    KeyHandler keyH = new KeyHandler();
    Thread gameThread;
    Player player;

    Image Tanah, wall, playerimg, ExitDoor;
    BufferedImage bufferedImage;

    Image wallKananAtas;
    Image wallKananBawah;
    Image wallKiriAtas;
    Image wallKiriBawah;
    Image wallvertical;
    Image wallhorizontal;
    Image wallKiri, wallKanan, wallAtas, wallBawah;

    public GamePanel() {
        this.maxScreenCol = map1[0].length;
        this.maxScreenRow = map1.length;
        this.screenWidth = tileSize * maxScreenCol;
        this.screenHeight = tileSize * maxScreenRow;

        this.addKeyListener(keyH);
        this.setFocusable(true);
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);

        loadAssets();

        int startX = 0, startY = 0;
        for (int i = 0; i < maxScreenRow; i++) {
            for (int j = 0; j < maxScreenCol; j++) {
                if (map1[i][j] == 'S') {
                    startX = j * tileSize;
                    startY = i * tileSize;
                }
            }
        }

        int endX = 0, endY = 0;
        for (int i = 0; i < maxScreenRow; i++) {
            for (int j = 0; j < maxScreenCol; j++) {
                if (map1[i][j] == 'N') {
                    endX = j * tileSize;
                    endY = i * tileSize;
                }
            }
        }

        // Pastikan parameter Player sesuai dengan constructor baru di Player.java
        player = new Player(this, keyH, playerimg, startX, startY);
    }

    public void loadAssets() {
        try {
            // Loading Sprite Sheet
            this.bufferedImage = loadBufferedImage("/Assets/ASSET/ASSETKARAKTER/AnimationSheet.png");
            if (this.bufferedImage != null) {
                this.playerimg = bufferedImage.getSubimage(0, 0, 25, 25);
            }

            // Loading Tiles
            this.Tanah = loadImage("/Assets/lab_tileset_LITE/seperated/tile031.png");
            this.wall = loadImage("/Assets/lab_tileset_LITE/seperated/tile066.png");
            this.ExitDoor = loadImage("/Assets/lab_tileset_LITE/seperated/tile067.png");
            this.wallKananAtas = loadImage("/Assets/lab_tileset_LITE/seperated/tile016.png");
            this.wallKananBawah = loadImage("/Assets/lab_tileset_LITE/seperated/tile025.png");
            this.wallKiriAtas = loadImage("/Assets/lab_tileset_LITE/seperated/tile015.png");
            this.wallKiriBawah = loadImage("/Assets/lab_tileset_LITE/seperated/tile024.png");
            this.wallvertical = loadImage("/Assets/lab_tileset_LITE/seperated/tile039.png");
            this.wallhorizontal = loadImage("/Assets/lab_tileset_LITE/seperated/tile040.png");
            this.wallKiri = loadImage("/Assets/lab_tileset_LITE/seperated/tile055.png");
            this.wallKanan = loadImage("/Assets/lab_tileset_LITE/seperated/tile054.png");
            this.wallAtas = loadImage("/Assets/lab_tileset_LITE/seperated/tile068.png");
            this.wallBawah = loadImage("/Assets/lab_tileset_LITE/seperated/tile041.png");

        } catch (Exception e) {
            System.err.println("Error loading assets!");
            e.printStackTrace();
        }
    }

    private BufferedImage loadBufferedImage(String path) {
        try {
            URL url = getClass().getResource(path);
            if (url != null) {
                return ImageIO.read(url);
            }
            return ImageIO.read(new File("src" + path));
        } catch (Exception e) {
            System.err.println("Failed to load buffered image: " + path + " -> " + e.getMessage());
            return null;
        }
    }

    public Image loadImage(String path) {
        try {
            URL url = getClass().getResource(path);
            if (url != null) {
                return new ImageIcon(url).getImage();
            }
            return new ImageIcon(new File("src" + path).getAbsolutePath()).getImage();
        } catch (Exception e) {
            System.err.println("Failed to load image: " + path + " -> " + e.getMessage());
            return null;
        }
    }

    public int getTileSize() {
        return tileSize;
    }

    public boolean collidesWithWall(int nextX, int nextY) {
        int left = nextX / tileSize;
        int right = (nextX + tileSize - 1) / tileSize;
        int top = nextY / tileSize;
        int bottom = (nextY + tileSize - 1) / tileSize;

        if (left < 0 || right >= maxScreenCol || top < 0 || bottom >= maxScreenRow)
            return true;
        return map1[top][left] == '1' || map1[top][right] == '1' ||
                map1[bottom][left] == '1' || map1[bottom][right] == '1';
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / 60;
        double delta = 0;
        long lastTime = System.nanoTime();

        while (gameThread != null) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
            checkWinCondition();
        }

    }

    public void update() {
        if (player != null)
            player.update();
    }

    private boolean hasWallAt(int row, int col) {
        if (row < 0 || row >= maxScreenRow || col < 0 || col >= maxScreenCol) {
            return false;
        }
        return map1[row][col] == '1';
    }

    private Image getWallImageForTile(int row, int col) {
        boolean top = hasWallAt(row - 1, col);
        boolean bottom = hasWallAt(row + 1, col);
        boolean left = hasWallAt(row, col - 1);
        boolean right = hasWallAt(row, col + 1);

        if (top && right && !bottom && !left)
            return wallKananAtas;
        if (top && left && !bottom && !right)
            return wallKiriAtas;
        if (bottom && right && !top && !left)
            return wallKananBawah;
        if (bottom && left && !top && !right)
            return wallKiriBawah;
        if (top && bottom && !left && !right)
            return wallvertical;
        if (left && right && !top && !bottom)
            return wallhorizontal;
        if (top && !bottom && !left && !right)
            return wallAtas;
        if (bottom && !top && !left && !right)
            return wallBawah;
        if (left && !right && !top && !bottom)
            return wallKiri;
        if (right && !left && !top && !bottom)
            return wallKanan;
        if ((top && left && right) || (bottom && left && right))
            return wallhorizontal;
        if ((left && top && bottom) || (right && top && bottom))
            return wallvertical;

        return wall != null ? wall : wallhorizontal;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        for (int i = 0; i < maxScreenRow; i++) {
            for (int j = 0; j < maxScreenCol; j++) {
                if (Tanah != null) {
                    g2.drawImage(Tanah, j * tileSize, i * tileSize, tileSize, tileSize, null);
                }

                if (map1[i][j] == 'G' && ExitDoor != null) {
                    g2.drawImage(ExitDoor, j * tileSize, i * tileSize, tileSize, tileSize, null);
                } else if (map1[i][j] == '1') {
                    Image wallImage = getWallImageForTile(i, j);
                    if (wallImage != null) {
                        g2.drawImage(wallImage, j * tileSize, i * tileSize, tileSize, tileSize, null);
                    }
                }
            }
        }

        if (player != null)
            player.draw(g2);
        g2.dispose();
    }

    protected void checkWinCondition() {
        int playerTileX = player.x / tileSize;
        int playerTileY = player.y / tileSize;

        if (map1[playerTileY][playerTileX] == 'N') {
            // Tampilkan pesan kemenangan
            System.out.println("Congratulations! You've reached the exit!");
            WinGame();
        }
    }

    protected void WinGame() {
        // Implementasi logika kemenangan, matikan game loop, dan tampilkan pesan
        // kemenangan
        System.out.println("Congratulations! You've reached the exit!");
        System.exit(0); // Keluar dari game
    }
}