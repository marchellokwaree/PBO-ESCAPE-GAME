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

    Image floorTile, wallCenter, playerimg, ExitDoor;
    BufferedImage bufferedImage;

    Image wallCornerTopRight , wallCornerBottomRight ,  wallCornerTopLeft , wallCornerBottomLeft;
    Image wallVertical , wallHorizontal;
    Image wallEndLeft, wallEndRight, wallEndTop, wallEndBottom;
    Image wallTUp, wallTDown, wallTLeft, wallTRight, wallTIntersection;
    Image BearTrap , FireTrap , Heal , Npc , IceTrap; 
    

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
                if (map1[i][j] == 'G') {
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
            this.floorTile = loadImage("/Assets/lab_tileset_LITE/seperated/tile031.png");
            this.ExitDoor = loadImage("/Assets/lab_tileset_LITE/seperated/tile067.png");
            this.wallCenter = loadImage("/Assets/lab_tileset_LITE/seperated/tile066.png");
            this.wallCornerBottomLeft = loadImage("/Assets/lab_tileset_LITE/seperated/tile067.png");
            this.wallCornerTopLeft = loadImage("/Assets/lab_tileset_LITE/seperated/tile039.png");
            this.wallCornerBottomRight = loadImage("/Assets/lab_tileset_LITE/seperated/tile071.png");
            this.wallCornerTopRight = loadImage("/Assets/lab_tileset_LITE/seperated/tile043.png");
            this.wallVertical = loadImage("/Assets/lab_tileset_LITE/seperated/tile074.png");
            this.wallHorizontal = loadImage("/Assets/lab_tileset_LITE/seperated/tile033.png");
            this.wallEndLeft = loadImage("/Assets/lab_tileset_LITE/seperated/tile052.png");
            this.wallEndRight = loadImage("/Assets/lab_tileset_LITE/seperated/tile047.png");
            this.wallEndTop = loadImage("/Assets/lab_tileset_LITE/seperated/tile058.png");
            this.wallEndBottom = loadImage("/Assets/lab_tileset_LITE/seperated/tile066.png");
            this.wallTUp = loadImage("/Assets/lab_tileset_LITE/seperated/tile042.png");
            this.wallTDown = loadImage("/Assets/lab_tileset_LITE/seperated/tile041.png");
            this.wallTLeft = loadImage("/Assets/lab_tileset_LITE/seperated/tile054.png");
            this.wallTRight = loadImage("/Assets/lab_tileset_LITE/seperated/tile055.png");

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

        // Intersection 4-arah
        if (top && bottom && left && right) {
            if (wallTIntersection != null) {
                return wallTIntersection;
            } else if (wallVertical != null) {
                return wallVertical;
            } else {
                return wallCenter;
            }
        }

        // Pertigaan terbuka ke ATAS ┴ (bawah + kiri + kanan)
        if (bottom && left && right && !top) {
            if (wallTUp != null) {
                return wallTUp;
            } else {
                return wallHorizontal;
            }
        }

        // Pertigaan terbuka ke BAWAH ┬ (atas + kiri + kanan)
        if (top && left && right && !bottom) {
            if (wallTDown != null) {
                return wallTDown;
            } else {
                return wallHorizontal;
            }
        }

        // Pertigaan terbuka ke KIRI ┤ (atas + bawah + kanan)
        if (top && bottom && right && !left) {
            if (wallTLeft != null) {
                return wallTLeft;
            } else {
                return wallVertical;
            }
        }

        // Pertigaan terbuka ke KANAN ├ (atas + bawah + kiri)
        if (top && bottom && left && !right) {
            if (wallTRight != null) {
                return wallTRight;
            } else {
                return wallVertical;
            }
        }

        // --- Straight walls --- (Tidak berubah, sudah benar)
        if (top && bottom && !left && !right) {
            if (wallVertical != null) {
                return wallVertical;
            } else {
                return wallCenter;
            }
        }
        if (left && right && !top && !bottom) {
            if (wallHorizontal != null) {
                return wallHorizontal;
            } else {
                return wallCenter;
            }
        }

        // --- Corners --- (DITUKAR untuk visual yang benar)
        // Jika ada tembok di ATAS dan KANAN, kita butuh pojokan yang visualnya
        // menghadap ke bawah-kiri.
        if (top && right && !bottom && !left) {
            if (wallCornerBottomLeft != null) {
                return wallCornerBottomLeft;
            } else {
                return wallCenter;
            }
        }
        // Jika ada tembok di KANAN dan BAWAH, kita butuh pojokan yang visualnya
        // menghadap ke atas-kiri.
        if (right && bottom && !top && !left) {
            if (wallCornerTopLeft != null) {
                return wallCornerTopLeft;
            } else {
                return wallCenter;
            }
        }
        // Jika ada tembok di BAWAH dan KIRI, kita butuh pojokan yang visualnya
        // menghadap ke atas-kanan.
        if (bottom && left && !top && !right) {
            if (wallCornerTopRight != null) {
                return wallCornerTopRight;
            } else {
                return wallCenter;
            }
        }
        // Jika ada tembok di KIRI dan ATAS, kita butuh pojokan yang visualnya menghadap
        // ke bawah-kanan.
        if (left && top && !bottom && !right) {
            if (wallCornerBottomRight != null) {
                return wallCornerBottomRight;
            } else {
                return wallCenter;
            }
        }

        // --- End pieces --- (DITUKAR untuk visual yang benar)
        // Ujung yang menyambung ke ATAS, butuh gambar ujung yang TERTUTUP (misal:
        // wallEndBottom)
        if (top && !bottom && !left && !right) {
            if (wallEndBottom != null) {
                return wallEndBottom;
            } else {
                return wallCenter;
            }
        }
        // Ujung yang menyambung ke BAWAH, butuh gambar ujung yang TERTUTUP (misal:
        // wallEndTop)
        if (bottom && !top && !left && !right) {
            if (wallEndTop != null) {
                return wallEndTop;
            } else {
                return wallCenter;
            }
        }
        // Ujung yang menyambung ke KIRI, butuh gambar ujung yang TERTUTUP (misal:
        // wallEndRight)
        if (left && !right && !top && !bottom) {
            if (wallEndRight != null) {
                return wallEndRight;
            } else {
                return wallCenter;
            }
        }
        // Ujung yang menyambung ke KANAN, butuh gambar ujung yang TERTUTUP (misal:
        // wallEndLeft)
        if (right && !left && !top && !bottom) {
            if (wallEndLeft != null) {
                return wallEndLeft;
            } else {
                return wallCenter;
            }
        }

        // --- Fallback cases --- (Juga disesuaikan agar pojokan tertutup lebih rapi)
        if ((top && left && right) || (bottom && left && right)) {
            if (wallHorizontal != null) {
                return wallHorizontal;
            } else {
                return wallCenter;
            }
        }
        if ((left && top && bottom) || (right && top && bottom)) {
            if (wallVertical != null) {
                return wallVertical;
            } else {
                return wallCenter;
            }
        }
        if (top || bottom || left || right) {
            if (wallCenter != null) {
                return wallCenter;
            } else {
                return wallHorizontal;
            }
        }

        // --- Default Fallback ---
        if (wallCenter != null) {
            return wallCenter;
        } else {
            return wallHorizontal;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        for (int i = 0; i < maxScreenRow; i++) {
            for (int j = 0; j < maxScreenCol; j++) {
                if (floorTile != null) {
                    g2.drawImage(floorTile, j * tileSize, i * tileSize, tileSize, tileSize, null);
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

        if (map1[playerTileY][playerTileX] == 'G') {
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