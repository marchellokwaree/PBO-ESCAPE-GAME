package Main;

import Entitiy.Player;
import Obstacle.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import java.awt.Rectangle;

public class GamePanel extends JPanel implements Runnable {
    // Map data
    char map1[][] = {
        { '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1' },
        { '1', 'P', '0', '0', '1', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', 'I', 'I', 'S', 'I', 'I', '0', '0', '0', '0', '1', 'P', '1', '0', '0', '0', '0', '0', '1', '0', '0', '0', '1' },
        { '1', '1', '1', '0', '1', '0', '1', '0', '1', '1', '1', '0', '1', '1', '1', '0', '1', 'I', '1', '1', '1', 'I', '1', '1', '1', '0', '1', '0', '1', '0', '1', '1', '1', '0', '1', '0', '1', '0', '1' },
        { '1', '0', 'F', '0', '1', '0', '0', '0', '1', 'F', 'F', '0', '1', '0', '1', '0', '0', 'I', 'I', 'I', 'I', 'I', '1', '0', '1', '0', '1', '0', '0', '0', '0', '0', '1', '0', '0', '0', '1', '0', '1' },
        { '1', '0', 'F', '0', '1', '0', '0', '0', '1', '0', '0', '0', '1', '0', '1', '0', '0', 'I', 'I', 'I', 'I', 'I', '1', '0', '1', '0', '1', '0', '0', '0', '0', '0', '1', '0', '0', '0', '1', '0', '1' },
        { '1', '0', '1', '1', '1', '0', '1', '1', '1', '0', '1', '1', '1', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '0', '1', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '0', '1', '0', '1' },
        { '1', '0', '0', '0', '1', '0', '1', '0', '0', '0', '1', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', '0', '0', '1', '0', '0', '0', '0', '0', '1', '0', '1', '0', '1' },
        { '1', '0', '1', '0', '1', '1', '1', '0', '1', '1', '1', '0', '1', '0', '1', '0', '1', '1', '1', '1', '1', '0', '1', '0', '1', '1', '1', '0', '1', '0', '1', '1', '1', '0', '1', '1', '1', '0', '1' },
        { '1', '0', '1', '0', '0', '0', '0', '0', '1', '0', '1', '0', '1', '0', '0', '0', '1', '0', '1', '0', '0', '0', '1', '0', '1', '0', '1', '0', '0', '0', '1', '0', '1', '0', '0', '0', '0', '0', '1' },
        { '1', '0', '1', '1', '1', '1', '1', '1', '1', '0', '1', '0', '1', '1', '1', '1', '1', '0', '1', '0', '1', '1', '1', '0', '1', '0', '1', '1', '1', '1', '1', '0', '1', '1', '1', '1', '1', '0', '1' },
        { '1', '0', '0', '0', '0', '0', '1', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '1', '0', '1', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '1' },
        { '1', '0', '1', '1', '1', '1', '1', '0', '1', '0', '1', '0', '1', '1', '1', '1', '1', '0', '1', '0', '1', '1', '1', '0', '1', '0', '1', '0', '1', '1', '1', '1', '1', '0', '1', '1', '1', '1', '1' },
        { '1', '0', '0', '0', '0', '0', '0', '0', '1', '0', '0', '0', '1', '0', '0', '0', '1', '0', '1', '0', '0', '0', '1', '0', '1', '0', '1', '0', '0', '0', '1', '0', '0', '0', '1', '0', '0', '0', '1' },
        { '1', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '0', '1', '0', '1', '1', '1', '1', '1', '0', '1', '0', '1', '1', '1', '1', '1', '0', '1', '0', '1', '1', '1', '0', '1', '1', '1' },
        { '1', '0', '0', '0', '0', '0', '1', '0', '0', '0', '1', '0', '0', '0', '1', '0', '1', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '1', '0', '0', '0', '1', '0', '0', '0', '1' },
        { '1', '1', '1', '1', '1', '0', '1', '0', '1', '0', '1', '0', '1', '1', '1', '0', '1', '0', '1', '1', '1', '1', '1', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '0', '1', '1', '1', '0', '1' },
        { '1', '0', '0', '0', '1', '0', '0', '0', '1', '0', '1', '0', '1', '0', '1', '0', '0', '0', '1', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '1' },
        { '1', '0', '1', '0', '1', '1', '1', '1', '1', '0', '1', '0', '1', '0', '1', '1', '1', '1', '1', '0', '1', '1', '1', '1', '1', '0', '1', '1', '1', '0', '1', '1', '1', '1', '1', '1', '1', '0', '1' },
        { '1', '0', '1', '0', '0', '0', '0', '0', '0', '0', '1', '0', '1', '0', '0', '0', '0', '0', '1', '0', '0', '0', '1', '0', '0', '0', '0', '0', '1', '0', '1', '0', '0', '0', '0', '0', '0', '0', '1' },
        { '1', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '0', '1', '0', '1', '1', '1', '1', '1', '1', '1', '0', '1', '0', '1', '1', '1', '1', '1', '0', '1', '0', '1', '1', '1', '1', '1', '1', '1' },
        { '1', '0', '0', '0', '1', '0', '0', '0', '1', '0', '0', '0', '1', '0', '1', '0', '0', '0', '0', '0', '0', '0', '1', '0', '0', '0', '1', '0', '0', '0', '1', '0', '1', '0', '0', '0', '0', '0', '1' },
        { '1', '1', '1', '0', '1', '1', '1', '0', '1', '0', '1', '1', '1', '0', '1', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '0', '1', '0', '1', '1', '1', '0', '1', '0', '1', '1', '1', '0', '1' },
        { '1', '0', '1', '0', '0', '0', 'D', '0', '1', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', 'P', '1', '0', '1', '0', '0', '0', '0', '0', '1', '0', '0', 'P', '1', '0', '1' },
        { '1', '0', '1', '1', '1', '1', '1', '0', '1', '1', '1', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '0', '1', '0', '1', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '0', '1' },
        { '1', '0', '0', '0', '1', '0', '0', '0', '1', '0', '1', '0', '0', '0', '0', '0', '1', '0', '0', '0', '1', '0', '0', '0', '1', '0', '1', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '1' },
        { '1', '0', '1', '0', '1', '0', '1', '1', '1', '0', '1', '1', '1', '1', '1', '0', '1', '0', '1', '0', '1', '1', '1', '1', '1', '0', '1', '0', '1', '1', '1', '0', '1', '0', '1', '1', '1', '0', '1' },
        { '1', '0', '1', '0', '1', '0', '0', '0', '0', '0', '1', '0', '0', '0', 'D', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '1', '0', '1', '0', '1', '0', '0', '0', '1', '0', '1', '0', '1' },
        { '1', '0', '1', '0', '1', '1', '1', '1', '1', '0', '1', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '0', '1', '0', '1', '1', '1', '1', '1', '0', '1', '0', '1' },
        { '1', '0', '1', '0', '0', '0', '0', '0', '1', '0', '1', '0', '0', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '1', '0', '1' },
        { '1', '0', '1', '0', '1', '1', '1', '1', '1', '0', '1', '0', '1', '1', '1', '0', '1', '1', '1', '0', '1', '1', '1', '1', '1', '0', '1', '1', '1', '0', '1', '1', '1', '1', '1', '0', '1', '0', '1' },
        { '1', '0', '1', '0', '1', '0', '0', '0', '1', '0', '1', '0', '0', '0', '1', '0', '1', '0', '0', '0', '1', '0', '0', '0', '1', '0', '1', '0', '0', '0', '0', '0', '0', '0', '1', '0', '1', '0', '1' },
        { '1', '0', '1', '0', '1', '0', '1', '0', '1', '0', '1', '1', '1', '0', '1', '0', '1', '0', '1', '1', '1', '0', '1', '0', '1', '0', '1', '1', '1', '1', '1', '1', '1', '0', '1', '0', '1', '0', '1' },
        { '1', '0', '1', '0', '0', '0', '1', '0', '1', '0', '1', '0', '1', '0', '1', '0', '0', '0', '1', '0', '1', '0', '1', '0', '0', '0', '1', '0', '0', '0', '0', '0', '1', '0', '1', '0', '1', '0', '1' },
        { '1', '0', '1', '1', '1', '1', '1', '0', '1', '0', '1', '0', '1', '0', '1', '1', '1', '1', '1', '0', '1', '0', '1', '1', '1', '1', '1', '0', '1', '1', '1', '0', '1', '1', '1', '0', '1', '0', '1' },
        { '1', '0', '0', '0', '0', '0', '1', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', '1', '0', '0', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '1', '0', '1' },
        { '1', '1', '1', '1', '1', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '0', '1', '0', '1', '1', '1', '1', '1', '0', '1', '1', '1', '1', '1', '1', '1', '0', '1' },
        { '1', '0', '1', '0', '0', '0', '1', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', '0', '0', '1', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1' },
        { '1', '0', '1', '0', '1', '1', '1', '0', '1', '1', '1', '0', '1', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '0', '1', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1' },
        { '1', '0', '0', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', 'D', '0', 'F', 'F', 'F', 'F', 'F', 'F', 'F', 'F', 'F', 'F', 'F', 'P', '1' },
        { '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1' }, };

    final int tileSize = 24;
    int maxScreenCol, maxScreenRow, screenWidth, screenHeight;

    KeyHandler keyH = new KeyHandler();
    Thread gameThread;
    Player player;
    ArrayList<Obstacle> obstacles = new ArrayList<>();
    Image floorTile, wallCenter, playerimg, ExitDoor;
    BufferedImage bufferedImage;

    Image wallCornerTopRight, wallCornerBottomRight, wallCornerTopLeft, wallCornerBottomLeft;
    Image wallVertical, wallHorizontal;
    Image wallEndLeft, wallEndRight, wallEndTop, wallEndBottom;
    Image wallTUp, wallTDown, wallTLeft, wallTRight, wallTIntersection;
    Image BearTrap, FireTrap, Heal, Npc, IceTrap;

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
        
        // Simpan referensi untuk linking gates dengan pressure plates
        ArrayList<PressurePlate> pressurePlates = new ArrayList<>();
        ArrayList<Gate> gates = new ArrayList<>();
        
        for (int i = 0; i < maxScreenRow; i++) {
            for (int j = 0; j < maxScreenCol; j++) {
                if (map1[i][j] == 'F') {
                    obstacles.add(new FireTrap(j * tileSize, i * tileSize, tileSize, tileSize));
                }
                if (map1[i][j] == 'I') {
                    obstacles.add(new IceTrap(j * tileSize, i * tileSize, tileSize, tileSize));
                }
                if (map1[i][j] == 'P') {
                    PressurePlate plate = new PressurePlate(j * tileSize, i * tileSize, tileSize, tileSize);
                    obstacles.add(plate);
                    pressurePlates.add(plate);
                }
                if (map1[i][j] == 'D') {
                    Gate gate;
                    if (map1[i - 1][j] == '1') {
                        gate = new Gate(j * tileSize, i * tileSize, tileSize, tileSize, false);
                    } else {
                        gate = new Gate(j * tileSize, i * tileSize, tileSize, tileSize, true);
                    }
                    obstacles.add(gate);
                    gates.add(gate);
                }
            }
        }
        
        // CONTOH: Link gate ke pressure plate
        // Uncomment dan sesuaikan index untuk mengatur gate mana yang memerlukan trapdoor
        // Jika ada pressure plate di index 0 dan gate di index 0:
        // if (pressurePlates.size() > 0 && gates.size() > 0) {
        //     gates.get(0).setRequiredPressurePlate(pressurePlates.get(0));
        // }

        // Pastikan parameter Player sesuai dengan constructor baru di Player.java
        player = new Player(this, keyH, playerimg, startX, startY);
    }

    public Player getPlayer() {
        return player;
    }

    public void loadAssets() {
        try {
            // Loading Sprite Sheet
            this.bufferedImage = loadBufferedImage("/Assets/ASSET/ASSETKARAKTER/AnimationSheet.png");
            if (this.bufferedImage != null) {
                this.playerimg = bufferedImage.getSubimage(0, 0, 25, 25);
            }
            this.bufferedImage = loadBufferedImage("/Assets/ASSET/Traps/Fire_Trap.png");
            if (this.bufferedImage != null) {
                this.FireTrap = bufferedImage.getSubimage(0, 9, 32, 32);
            }
            this.bufferedImage = loadBufferedImage("/Assets/ASSET/Traps/Ice_Trap.png");
            if (this.bufferedImage != null) {
                this.IceTrap = bufferedImage.getSubimage(32, 150, 32, 32);
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
        try (InputStream stream = getClass().getResourceAsStream(path)) {
            if (stream != null) {
                return ImageIO.read(stream);
            }
            File file = resolveImageFile(path);
            return ImageIO.read(file);
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
            File file = resolveImageFile(path);
            return new ImageIcon(file.getAbsolutePath()).getImage();
        } catch (Exception e) {
            System.err.println("Failed to load image: " + path + " -> " + e.getMessage());
            return null;
        }
    }

    private File resolveImageFile(String path) {
        String normalizedPath = path.replace('/', File.separatorChar);
        String userDir = System.getProperty("user.dir");

        File candidate = new File(userDir + File.separator + "src" + normalizedPath);
        if (candidate.exists()) {
            return candidate;
        }

        candidate = new File(userDir + normalizedPath);
        if (candidate.exists()) {
            return candidate;
        }

        candidate = new File(userDir + File.separator + "Maze" + File.separator + "src" + normalizedPath);
        if (candidate.exists()) {
            return candidate;
        }

        return new File(userDir + File.separator + "src" + normalizedPath);
    }

    public int getTileSize() {
        return tileSize;
    }


    public boolean collidesWithWall(int nextX, int nextY, Rectangle hitbox) {
            // Hitung posisi absolut hitbox di koordinat world untuk posisi selanjutnya
        int hitboxLeftX   = nextX + hitbox.x;
        int hitboxRightX  = nextX + hitbox.x + hitbox.width - 1;
        int hitboxTopY    = nextY + hitbox.y;
        int hitboxBottomY = nextY + hitbox.y + hitbox.height - 1;
         // Konversi koordinat pixel absolut ke indeks baris/kolom matriks map
        int left   = hitboxLeftX / tileSize;
        int right  = hitboxRightX / tileSize;
        int top    = hitboxTopY / tileSize;           
        int bottom = hitboxBottomY / tileSize;
        
            // Batasan luar map (Out of Bounds)
        if (left < 0 || right >= maxScreenCol || top < 0 || bottom >= maxScreenRow) {
                return true;
        }

         // Cek apakah 4 sudut hitbox menubruk tembok ('1')
        return map1[top][left] == '1' || map1[top][right] == '1' ||   map1[bottom][left] == '1' || map1[bottom][right] == '1';
    }

    public boolean collidesWithClosedGate(int nextX, int nextY, Rectangle hitbox) {
        // 1. Buat objek Rectangle bayangan untuk posisi player selanjutnya
        Rectangle playerFutureBounds = new Rectangle(
            nextX + hitbox.x, 
            nextY + hitbox.y, 
            hitbox.width, 
            hitbox.height
        );

        for (Obstacle obstacle : obstacles) {
            if (obstacle instanceof Gate) {
                Gate gate = (Gate) obstacle;
                
                // 2. Jika gate tertutup, cek tabrakan menggunakan .intersects()
                if (!gate.open) {
                    // Asumsi: Class Gate memiliki koordinat x, y, dan ukuran sendiri.
                    // Jika Gate Anda sudah punya objek Rectangle sendiri, gunakan itu (misal: gate.hitbox).
                    Rectangle gateBounds = new Rectangle(gate.x, gate.y, tileSize, tileSize);
                    
                    if (playerFutureBounds.intersects(gateBounds)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / 60;
        double delta = 0;
        long lastTime = System.nanoTime(); // Mendapatkan waktu saat ini dalam nanodetik

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
            checkDamage();

            
        }

    }

    public void update() {
        if (player != null) {
            player.update();
        }
        for (Obstacle obstacle : obstacles) {
            if (obstacle instanceof FireTrap) {
                ((FireTrap) obstacle).update();
            }
            if (obstacle instanceof IceTrap) {
                ((IceTrap) obstacle).update();
            }
            if (obstacle instanceof Gate) {
                if (!((Gate) obstacle).alrOpen) {
                    ((Gate) obstacle).update();
                }
                ((Gate) obstacle).update();
            }
            if (obstacle instanceof PressurePlate) {
                
                ((PressurePlate) obstacle).update();
            }
        }
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

        // Gambar semua obstacles, termasuk fire trap animasi
        for (Obstacle obstacle : obstacles) {
            if (obstacle instanceof FireTrap) {
                ((FireTrap) obstacle).draw(g2);
            }
            if (obstacle instanceof IceTrap) {
                ((IceTrap) obstacle).draw(g2);
            }
            if (obstacle instanceof PressurePlate) {
                ((PressurePlate) obstacle).draw(g2);
            }
            if (obstacle instanceof Gate) {
                ((Gate) obstacle).draw(g2);
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

    protected void checkDamage() {
        for (Obstacle obstacle : obstacles) {
            if (obstacle instanceof FireTrap) {
                FireTrap fireTrap = (FireTrap) obstacle;
                if (fireTrap.active && fireTrap.collidesWith(player.x, player.y, tileSize) && player.damageCooldown == 0) {
                    player.HP -= 30;
                    if (player.HP < 0) {
                        player.HP = 0;
                    }
                    player.damageCooldown = 60; // Jangan terkena damage lagi selama sekitar 1 detik

                    System.out.println("Player hit by fire trap! HP: " + player.HP);
                    if (player.HP <= 0) {
                        System.out.println("Game Over! Player has been defeated.");
                        System.exit(0);
                    }
                }
            }
            if (obstacle instanceof PressurePlate) {
                PressurePlate pressurePlate = (PressurePlate) obstacle;
                if (pressurePlate.collidesWith(player.x, player.y, tileSize)) {
                    pressurePlate.activate();
                    // Buka gate yang tidak memiliki persyaratan trapdoor
                    // atau gate yang memiliki trapdoor terpenuhi
                    for (Obstacle other : obstacles) {
                        if (other instanceof Gate) {
                            Gate gate = (Gate) other;
                            gate.openGate();
                            gate.alrOpen = true;
                        }
                    }
                } else {
                    pressurePlate.deactivate();
                }
            }
            if (obstacle instanceof IceTrap) {
                IceTrap iceTrap = (IceTrap) obstacle;
                if (iceTrap.active && iceTrap.collidesWith(player.x, player.y, tileSize)) {
                    player.applySlow(120); // Contoh: efek es berlangsung selama 2 detik (120 frame)
                }
            }
        }
    }

}