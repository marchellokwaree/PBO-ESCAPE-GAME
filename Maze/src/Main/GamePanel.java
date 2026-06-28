package Main;

import Obstacle.*;
import Entitiy.*;
import Item.Item;
import Item.DamageItem;
import Item.DefenseItem;

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
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.Rectangle;

public class GamePanel extends JPanel implements Runnable {

    private String map1[][] = loadMapFromFileOrDefault();
    private static final String MAP_FILE_PATH = "src/Assets/MAP/Maze1.txt";

    final int tileSize = 32;
    public final int maxScreenCol = 24;
    public final int maxScreenRow = 18;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    public final int maxWorldCol = map1[0].length;
    public final int maxWorldRow = map1.length;
    public final int worldWidth = tileSize * maxWorldCol;
    public final int worldHeight = tileSize * maxWorldRow;
    protected int Key;
    public KeyHandler keyH = new KeyHandler();
    public Thread gameThread;
    public Player player;
    public Timer timer;
    public ArrayList<Obstacle> obstacles = new ArrayList<>();
    public ArrayList<Entity> entities = new ArrayList<>();
    public Image floorTile, wallCenter, playerimg, ExitDoor, larkImage;
    public BufferedImage bufferedImage;
    public ArrayList<Entity> monsters = new ArrayList<>();

    // Fog of war settings
    public int safeVisionTiles = 100; // tiles always fully visible around player
    public int visionRangeTiles = 100; // tiles where player can still see, beyond this is black

    // Smooth camera position (world coordinates of top-left of screen)
    private double cameraX = 0.0;
    private double cameraY = 0.0;
    private final double cameraSmoothFactor = 0.15; // between 0 (no move) and 1 (instant)
    Image wallCornerTopRight, wallCornerBottomRight, wallCornerTopLeft, wallCornerBottomLeft;
    Image wallVertical, wallHorizontal;
    Image wallEndLeft, wallEndRight, wallEndTop, wallEndBottom;
    Image wallTUp, wallTDown, wallTLeft, wallTRight, wallTIntersection;

    public MonsterSpawner spawner;
    public Inventory inventory;
    private boolean gameOver = false; // Flag untuk mencegah multiple win/lose triggers

    // Lark 1A cheat console
    private ArrayList<int[]> larkPositions = new ArrayList<>(); // Posisi tile "L" di map
    private boolean cheatPopupOpen = false; // Flag popup sedang terbuka
    private boolean wasOnLarkTile = false; // Deteksi masuk tile L (edge detection)

    public GamePanel() {

        this.addKeyListener(keyH);
        this.addMouseListener(keyH);
        this.setFocusable(true);
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);

        loadAssets();

        int startX = 0, startY = 0;
        for (int i = 0; i < maxWorldRow; i++) {
            for (int j = 0; j < maxWorldCol; j++) {
                if ("S".equals(map1[i][j])) {
                    startX = j * tileSize;
                    startY = i * tileSize;
                }
            }
        }

        this.timer = new Timer(1000000000); // Timer 100 detik (100.000 ms)
        // Simpan referensi untuk linking gates dengan pressure plates
        ArrayList<PressurePlate> pressurePlates = new ArrayList<>();
        ArrayList<Gate> gates = new ArrayList<>();

        for (int i = 0; i < maxWorldRow; i++) {
            for (int j = 0; j < maxWorldCol; j++) {
                if ("F".equals(map1[i][j])) {
                    obstacles.add(new FireTrap(j * tileSize, i * tileSize, tileSize, tileSize));
                }
                if ("I".equals(map1[i][j])) {
                    obstacles.add(new IceTrap(j * tileSize, i * tileSize, tileSize, tileSize));
                }

                if ("G".equals(map1[i][j])) {
                    obstacles.add(new Finish(j * tileSize, i * tileSize, tileSize, tileSize));
                }
                if ("H".equals(map1[i][j])) {
                    obstacles.add(new HealPotion(j * tileSize, i * tileSize, tileSize, tileSize));
                }
                if (map1[i][j].length() > 1) {
                    if (map1[i][j].charAt(0) == 'P') {
                        String id = map1[i][j].substring(1); // Ambil ID setelah 'P'
                        PressurePlate plate = new PressurePlate(j * tileSize, i * tileSize, tileSize, tileSize, id);
                        obstacles.add(plate);
                        pressurePlates.add(plate);
                    } else if (map1[i][j].charAt(0) == 'D') {
                        String id = map1[i][j].substring(1); // Ambil ID setelah 'G'
                        boolean aboveIsWall = (i - 1 >= 0 && "1".equals(map1[i - 1][j]));
                        Gate gate = new Gate(j * tileSize, i * tileSize, tileSize, tileSize, aboveIsWall, id);
                        obstacles.add(gate);
                        gates.add(gate);
                    }
                }

                if ("N".equals(map1[i][j])) {
                    entities.add(new RedHood(j * tileSize, i * tileSize, 0, this));
                    Key++;
                }

                if ("L".equals(map1[i][j])) {
                    larkPositions.add(new int[] { j * tileSize, i * tileSize });
                }

                if ("C".equals(map1[i][j])) {
                    // Gunakan getRandomItem() sebagai parameter ke-5
                    obstacles.add(new Chest(j * tileSize, i * tileSize, tileSize, tileSize, getRandomItem()));
                }
            }
        }

        // CONTOH: Link gate ke pressure plate
        // Uncomment dan sesuaikan index untuk mengatur gate mana yang memerlukan
        // trapdoor
        // Jika ada pressure plate di index 0 dan gate di index 0:
        // if (pressurePlates.size() > 0 && gates.size() > 0) {
        // gates.get(0).setRequiredPressurePlate(pressurePlates.get(0));
        // }

        // Pastikan parameter Player sesuai dengan constructor baru di Player.java
        player = new Player(this, keyH, playerimg, startX, startY);

        // Initialize camera at player-centered position (clamped)
        cameraX = player.x - player.screenX;
        cameraY = player.y - player.screenY;
        clampCamera();

        spawner = new MonsterSpawner(this);
        inventory = new Inventory(this);
    }

    public String[][] getMap() {
        return map1;
    }

    public Player getPlayer() {
        return player; // agar player bisa di akses di obstacle
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

            // Loading Lark 1A Sprite
            this.larkImage = loadImage("/Assets/ASSET/Lark 1A.png");

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
        String normalizedPath = path.replace("/", File.separator);
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

    private String resolveMapFilePath(String path) {
        String normalizedPath = path.replace("/", File.separator);
        String userDir = System.getProperty("user.dir");

        File candidate = new File(userDir + File.separator + normalizedPath);
        if (candidate.exists()) {
            return candidate.getAbsolutePath();
        }

        candidate = new File(userDir + File.separator + "Maze" + File.separator + normalizedPath);
        if (candidate.exists()) {
            return candidate.getAbsolutePath();
        }

        return userDir + File.separator + normalizedPath;
    }

    private String[][] loadMapFromFileOrDefault() {
        String filePath = resolveMapFilePath(MAP_FILE_PATH);
        String[][] loaded = new MapLoader().loadMapFromFile(filePath);
        return loaded;
    }

    public int getTileSize() {
        return tileSize;
    }

    public int getMaxWorldCol() {
        return maxWorldCol;
    }

    public int getMaxWorldRow() {
        return maxWorldRow;
    }

    public boolean collidesWithWall(int nextX, int nextY, Rectangle hitbox) {
        // Hitung posisi absolut hitbox di koordinat world untuk posisi selanjutnya
        int hitboxLeftX = nextX + hitbox.x;
        int hitboxRightX = nextX + hitbox.x + hitbox.width - 1;
        int hitboxTopY = nextY + hitbox.y;
        int hitboxBottomY = nextY + hitbox.y + hitbox.height - 1;
        // Konversi koordinat pixel absolut ke indeks baris/kolom matriks map
        int left = hitboxLeftX / tileSize;
        int right = hitboxRightX / tileSize;
        int top = hitboxTopY / tileSize;
        int bottom = hitboxBottomY / tileSize;

        // Batasan luar map (Out of Bounds)
        if (left < 0 || right >= maxWorldCol || top < 0 || bottom >= maxWorldRow) {
            return true;
        }

        // Cek apakah 4 sudut hitbox menubruk tembok ("1")
        return "1".equals(map1[top][left]) || "1".equals(map1[top][right]) || "1".equals(map1[bottom][left])
                || "1".equals(map1[bottom][right]);
    }

    public boolean collidesWithClosedGate(int nextX, int nextY, Rectangle hitbox) {
        // 1. Buat objek Rectangle bayangan untuk posisi player selanjutnya
        Rectangle playerFutureBounds = new Rectangle(
                nextX + hitbox.x,
                nextY + hitbox.y,
                hitbox.width,
                hitbox.height);

        for (Obstacle obstacle : obstacles) {
            if (obstacle instanceof Gate) {
                Gate gate = (Gate) obstacle;

                // 2. Jika gate tertutup, cek tabrakan menggunakan .intersects()
                if (!gate.open) {
                    // Asumsi: Class Gate memiliki koordinat x, y, dan ukuran sendiri.
                    // Jika Gate Anda sudah punya objek Rectangle sendiri, gunakan itu (misal:
                    // gate.hitbox).
                    Rectangle gateBounds = new Rectangle(gate.x, gate.y, tileSize, tileSize);

                    if (playerFutureBounds.intersects(gateBounds)) {
                        return true;
                    }
                }
            }

            if (obstacle instanceof Chest) {
                Chest chest = (Chest) obstacle;

                // Buat area padat (hitbox) sebesar 1 kotak ubin/tile di posisi peti
                Rectangle chestBounds = new Rectangle(chest.x, chest.y, tileSize, tileSize);

                // Jika posisi pemain selanjutnya menabrak area peti, kembalikan nilai true
                // (terblokir)
                if (playerFutureBounds.intersects(chestBounds)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
        timer.start(); // Mulai timer saat game thread dimulai
        if (spawner != null) {
            spawner.SpawnMonsters(5);
        }
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

            checkDamage();
            if (timer.isTimeUp()) {
                // Waktu habis, tampilkan layar Game Over
                System.out.println("Time's Up! Game Over!");
                LoseGame();
            }
            try {
                Thread.sleep(1000 / 60); // Tidur sebentar untuk mengurangi beban CPU, target sekitar 60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void update() {
        if (cheatPopupOpen) {
            return;
        }
        if (player != null) {
            player.update();
        }
        for (Entity entity : entities) {
            if (entity instanceof RedHood) {
                ((RedHood) entity).update();
            }
        }

        if (timer != null) {
            timer.update();
        }
        for (Obstacle obstacle : obstacles) {
            if (obstacle instanceof FireTrap) {
                ((FireTrap) obstacle).update();
            }
            if (obstacle instanceof IceTrap) {
                ((IceTrap) obstacle).update();
            }
            if (obstacle instanceof Gate) {
                ((Gate) obstacle).update();
            }
            if (obstacle instanceof PressurePlate) {

                ((PressurePlate) obstacle).update();
            }
            if (obstacle instanceof Finish) {
                ((Finish) obstacle).update();
            }
            if (obstacle instanceof HealPotion) {
                ((HealPotion) obstacle).update();
            }
            if (obstacle instanceof Chest) {
                ((Chest) obstacle).update();
            }
        }
        // Update smooth camera after updating entities
        updateCamera();

        if (spawner != null) {
            spawner.update();
        }

        // Gunakan Iterator untuk iterasi dan menghapus dengan aman
        Iterator<Entity> iterator = monsters.iterator();
        while (iterator.hasNext()) {
            Entity monster = iterator.next();

            if (monster != null) {
                monster.update();

                if (monster instanceof FireSlime) {
                    FireSlime slime = (FireSlime) monster;
                    slime.checkPlayerCollision(player);

                    // Jika slime sudah mati dan animasi matinya sudah selesai
                    if (slime.readyToRemove) {
                        iterator.remove(); // Menghapus slime dari ArrayList dengan aman
                        System.out.println("FireSlime berhasil dihapus dari Map!");
                    }
                }
                if (monster instanceof Slime2) {
                    Slime2 slime = (Slime2) monster;
                    slime.checkPlayerCollision(player);

                    if (slime.readyToRemove) {
                        iterator.remove();

                    }
                }
                if (monster instanceof Slime3) {
                    Slime3 slime = (Slime3) monster;
                    slime.checkPlayerCollision(player);

                    if (slime.readyToRemove) {
                        iterator.remove();

                    }
                }
            }
        }

        // Contoh di dalam GamePanel.java
        // Di dalam method update() GamePanel
        boolean sedangBukaPeti = false;
        for (Obstacle obs : obstacles) {
            if (obs instanceof Chest && ((Chest) obs).showChestUI) {
                sedangBukaPeti = true;
                break; // Ketemu 1 peti yang terbuka, langsung keluar dari pencarian
            }
        }

        // 2. Jika klik kiri ditekan, DAN pemain TIDAK sedang membuka peti, baru serang
        if (keyH.leftMousePressed && !sedangBukaPeti) {
            player.attackEnemies(monsters);

            // Reset status klik agar tidak menyerang berkali-kali dalam 1 detik
            keyH.leftMousePressed = false;
        }

        // ===== LOGIKA TOMBOL ITEM DI DALAM METHOD update() =====
        if (keyH.key1Pressed) {
            handleSlotKey(0); // GANTI INI
            keyH.key1Pressed = false;
        }
        if (keyH.key2Pressed) {
            handleSlotKey(1); // GANTI INI
            keyH.key2Pressed = false;
        }
        if (keyH.key3Pressed) {
            handleSlotKey(2); // GANTI INI
            keyH.key3Pressed = false;
        }
        if (keyH.key4Pressed) {
            handleSlotKey(3); // GANTI INI
            keyH.key4Pressed = false;
        }
        // =====================================================

    }

    private void updateCamera() {
        if (player == null)
            return;

        double targetX = player.x - player.screenX;
        double targetY = player.y - player.screenY;

        cameraX += (targetX - cameraX) * cameraSmoothFactor;
        cameraY += (targetY - cameraY) * cameraSmoothFactor;

        clampCamera();
    }

    private void clampCamera() {
        if (cameraX < 0)
            cameraX = 0;
        double maxCamX = Math.max(0, worldWidth - screenWidth);
        if (cameraX > maxCamX)
            cameraX = maxCamX;

        if (cameraY < 0)
            cameraY = 0;
        double maxCamY = Math.max(0, worldHeight - screenHeight);
        if (cameraY > maxCamY)
            cameraY = maxCamY;
    }

    public int getCameraXInt() {
        return (int) Math.round(cameraX);
    }

    public int getCameraYInt() {
        return (int) Math.round(cameraY);
    }

    private boolean hasWallAt(int row, int col) {
        if (row < 0 || row >= maxWorldRow || col < 0 || col >= maxWorldCol) {
            return false;
        }
        return "1".equals(map1[row][col]);
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

    private int getFogAlphaForDistance(double distanceTiles) {
        if (distanceTiles <= safeVisionTiles) {
            return 0; // fully visible
        }
        if (distanceTiles >= visionRangeTiles) {
            return 255; // outside visible range
        }

        double range = visionRangeTiles - safeVisionTiles;
        if (range <= 0) {
            return 255;
        }

        double normalized = (distanceTiles - safeVisionTiles) / range;
        int alpha = (int) Math.round(normalized * 255);
        return Math.min(255, Math.max(0, alpha));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // clear background
        Graphics2D g2 = (Graphics2D) g; // use 2D graphics for drawing
        int camX = getCameraXInt(); // camera world x position
        int camY = getCameraYInt(); // camera world y position

        // Draw every visible map tile
        for (int i = 0; i < maxWorldRow; i++) {
            for (int j = 0; j < maxWorldCol; j++) {
                int worldX = j * tileSize; // tile x in world coordinates
                int worldY = i * tileSize; // tile y in world coordinates
                int screenX = worldX - camX; // convert to screen x
                int screenY = worldY - camY; // convert to screen y

                if (worldX + tileSize > camX &&
                        worldX - tileSize < camX + screenWidth &&
                        worldY + tileSize > camY &&
                        worldY - tileSize < camY + screenHeight) {
                    if (floorTile != null) {
                        g2.drawImage(floorTile, screenX, screenY, tileSize, tileSize, null); // floor tile
                    }
                    if ("1".equals(map1[i][j])) {
                        Image wallImage = getWallImageForTile(i, j); // choose correct wall sprite
                        if (wallImage != null) {
                            g2.drawImage(wallImage, screenX, screenY, tileSize, tileSize, null); // wall tile
                        }
                    }

                    // Draw Lark 1A marker tile
                    if ("L".equals(map1[i][j])) {
                        if (larkImage != null) {
                            g2.drawImage(larkImage, screenX, screenY, tileSize, tileSize, null);
                        } else {
                            // Glowing cyan marker (fallback)
                            g2.setColor(new Color(0, 200, 255, 80));
                            g2.fillRect(screenX, screenY, tileSize, tileSize);
                            g2.setColor(new Color(0, 200, 255, 180));
                            g2.drawRect(screenX + 1, screenY + 1, tileSize - 2, tileSize - 2);
                            // Label "L"
                            g2.setColor(new Color(0, 255, 255));
                            g2.setFont(new Font("Arial", Font.BOLD, 12));
                            g2.drawString("L", screenX + 10, screenY + 22);
                        }
                    }
                }
            }
        }

        // Draw all obstacles
        for (Obstacle obstacle : obstacles) {
            if (obstacle instanceof FireTrap) {
                ((FireTrap) obstacle).draw(g2, this);
            }
            if (obstacle instanceof IceTrap) {
                ((IceTrap) obstacle).draw(g2, this);
            }
            if (obstacle instanceof PressurePlate) {
                ((PressurePlate) obstacle).draw(g2, this);
            }
            if (obstacle instanceof Gate) {
                ((Gate) obstacle).draw(g2, this);
            }
            if (obstacle instanceof Finish) {
                ((Finish) obstacle).draw(g2, this);
            }
            if (obstacle instanceof HealPotion) {
                ((HealPotion) obstacle).draw(g2, this);
            }
            if (obstacle instanceof Chest) {
                ((Chest) obstacle).draw(g2, this);
            }
        }

        // Draw red hood enemies
        for (Entity entity : entities) {
            if (entity instanceof RedHood) {
                ((RedHood) entity).draw(g2, this);
            }
        }

        // Draw monsters
        for (Entity monster : monsters) {
            if (monster instanceof FireSlime) {
                ((FireSlime) monster).draw(g2);
            }
            if (monster instanceof Slime2) {
                ((Slime2) monster).draw(g2);
            }
            if (monster instanceof Slime3) {
                ((Slime3) monster).draw(g2);
            }
        }

        // Fog of war overlay: darken tiles by distance
        if (player != null) {
            double playerCenterX = player.x + tileSize * 0.5; // player center x
            double playerCenterY = player.y + tileSize * 0.5; // player center y
            double playerTileX = playerCenterX / tileSize; // player tile x
            double playerTileY = playerCenterY / tileSize; // player tile y

            g2.setComposite(java.awt.AlphaComposite.SrcOver); // normal drawing mode
            for (int i = 0; i < maxWorldRow; i++) {
                for (int j = 0; j < maxWorldCol; j++) {
                    int worldX = j * tileSize;
                    int worldY = i * tileSize;
                    int screenX = worldX - camX;
                    int screenY = worldY - camY;

                    if (screenX + tileSize < 0 || screenX > screenWidth || screenY + tileSize < 0
                            || screenY > screenHeight) {
                        continue; // skip off-screen tiles
                    }

                    double dx = (j + 0.5) - playerTileX; // tile delta x from player
                    double dy = (i + 0.5) - playerTileY; // tile delta y from player
                    double distanceTiles = Math.sqrt(dx * dx + dy * dy); // Euclidean distance in tiles
                    int alpha = getFogAlphaForDistance(distanceTiles); // opacity based on distance
                    if (alpha > 0) {
                        g2.setColor(new java.awt.Color(0, 0, 0, alpha));
                        g2.fillRect(screenX, screenY, tileSize, tileSize); // draw fog tile
                    }
                }
            }
        }

        // Draw player and UI on top of fog
        if (player != null) {
            player.draw(g2);
            player.darah.draw(g2);
        }

        // Draw chest UI if open
        for (Obstacle obstacle : obstacles) {
            if (obstacle instanceof Chest) {
                Chest chest = (Chest) obstacle;
                if (chest.showChestUI) {
                    int uiWidth = screenWidth - 200;
                    int uiHeight = screenHeight - 200;
                    int uiX = 100;
                    int uiY = 100;

                    // Background hitam semi-transparan
                    g2.setColor(new java.awt.Color(0, 0, 0, 200));
                    g2.fillRoundRect(uiX, uiY, uiWidth, uiHeight, 35, 35);

                    // Garis pinggiran putih
                    g2.setColor(java.awt.Color.WHITE);
                    g2.setStroke(new java.awt.BasicStroke(5));
                    g2.drawRoundRect(uiX + 5, uiY + 5, uiWidth - 10, uiHeight - 10, 25, 25);

                    // Teks judul
                    g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 30));
                    g2.drawString("Isi Peti (Maks 3 Item)", uiX + 30, uiY + 50);

                    // ===== MENGGAMBAR 3 SLOT PETI =====
                    int itemSize = 64;
                    int startX = uiX + 50;
                    int startY = uiY + 100;
                    int jarakAntarItem = 140;

                    for (int k = 0; k < chest.chestSlots.length; k++) {
                        int slotX = startX + (k * jarakAntarItem);
                        g2.setColor(new java.awt.Color(255, 255, 255, 50));
                        g2.fillRoundRect(slotX, startY, itemSize, itemSize, 15, 15);
                        if (chest.chestSlots[k] != null && chest.chestSlots[k].image != null) {
                            Item itemDiPeti = chest.chestSlots[k];
                            g2.drawImage(itemDiPeti.image, slotX, startY, itemSize, itemSize, null);
                            g2.setColor(java.awt.Color.WHITE);
                            g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
                            g2.drawString(itemDiPeti.name, slotX, startY + itemSize + 20);
                            g2.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 13));
                            if (itemDiPeti instanceof DamageItem) {
                                DamageItem weapon = (DamageItem) itemDiPeti;
                                g2.setColor(java.awt.Color.YELLOW);
                                g2.drawString("+ " + weapon.bonusDamage + " DMG", slotX, startY + itemSize + 38);
                            } else if (itemDiPeti instanceof DefenseItem) {
                                DefenseItem armor = (DefenseItem) itemDiPeti;
                                g2.setColor(new java.awt.Color(255, 165, 0));
                                g2.drawString("+ " + armor.bonusDef + "% DEF", slotX, startY + itemSize + 38);
                            }
                        }
                    }
                }
            }
        }

        // Draw inventory UI last so it stays visible above the fog and map
        if (inventory != null) {
            inventory.draw(g2);
        }

        g2.dispose();
    }

    protected void WinGame() {
        if (gameOver)
            return; // Cegah multiple trigger
        gameOver = true;
        System.out.println("Congratulations! You've reached the exit!");
        stopGame();
        SwingUtilities.invokeLater(() -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (frame != null) {
                GameOverPanel gameOverPanel = new GameOverPanel(frame, true);
                frame.setContentPane(gameOverPanel);
                frame.revalidate();
                frame.repaint();
                frame.pack();
                frame.setLocationRelativeTo(null);
            }
        });
    }

    public void LoseGame() {
        if (gameOver)
            return; // Cegah multiple trigger
        gameOver = true;
        System.out.println("Game Over! You lost.");
        stopGame();
        SwingUtilities.invokeLater(() -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (frame != null) {
                GameOverPanel gameOverPanel = new GameOverPanel(frame, false);
                frame.setContentPane(gameOverPanel);
                frame.revalidate();
                frame.repaint();
                frame.pack();
                frame.setLocationRelativeTo(null);
            }
        });
    }

    /**
     * Menghentikan game thread dan semua proses game dengan aman.
     */
    private void stopGame() {
        gameThread = null;
    }

    protected void checkDamage() {
        java.util.Iterator<Obstacle> it = obstacles.iterator();
        while (it.hasNext()) {
            Obstacle obstacle = it.next();
            if (obstacle instanceof Finish) {
                Finish finish = (Finish) obstacle;
                if (finish.collidesWith(player.x, player.y, tileSize)) {
                    if (Key == 0) {
                        WinGame();
                    } else {
                        System.out.println("You need to find all Red Hood to unlock the exit!");
                    }
                }
            }
            if (obstacle instanceof FireTrap) {
                FireTrap fireTrap = (FireTrap) obstacle;
                Rectangle fireHitbox = new Rectangle(fireTrap.x, fireTrap.y, 30, 30);

                if (fireTrap.active && fireHitbox.intersects(player.getHitbox()) && player.damageCooldown == 0) {

                    // ===== CUKUP PANGGIL METHOD BARU INI =====
                    int damageTrap = 30; // Tentukan damage murni trap
                    player.terimaDamage(damageTrap);
                    // =========================================

                    if (player.darah.getCurrentHP() < 0) {
                        player.darah.update(0);
                    }
                    player.damageCooldown = 60;
                }
            }
            if (obstacle instanceof PressurePlate) {
                PressurePlate pressurePlate = (PressurePlate) obstacle;
                Rectangle pressureHitbox = new Rectangle(pressurePlate.x, pressurePlate.y, tileSize, tileSize);
                if (pressureHitbox.intersects(player.getHitbox())) {
                    pressurePlate.activate();
                    for (Obstacle o : obstacles) {
                        if (o instanceof Gate) {
                            Gate gate = (Gate) o;
                            if (gate.ID.equals(pressurePlate.ID) && pressurePlate.activated) { // Cek apakah ID gate
                                                                                               // cocok dengan ID
                                                                                               // pressure plate
                                gate.alrOpen = true;
                                gate.open = true;
                                gate.openGate();
                            }
                        }
                    }
                } else {
                    pressurePlate.deactivate();
                }
            }
            if (obstacle instanceof IceTrap) {
                IceTrap iceTrap = (IceTrap) obstacle;
                Rectangle iceHitbox = new Rectangle(iceTrap.x, iceTrap.y, tileSize, tileSize);
                if (iceTrap.active && iceHitbox.intersects(player.getHitbox())) {
                    player.applySlow(5); // Contoh: efek es berlangsung selama 2 detik (120 frame)
                }
            }
            if (obstacle instanceof HealPotion) {
                HealPotion healPotion = (HealPotion) obstacle;
                Rectangle healHitbox = new Rectangle(healPotion.x, healPotion.y, tileSize, tileSize);
                if (healHitbox.intersects(player.getHitbox())) {
                    player.darah.heal(50); // Contoh: menyembuhkan 50 HP
                    if (player.darah.getCurrentHP() > 100) {
                        player.darah.update(100); // Batas maksimal HP
                    }
                    it.remove(); // Hapus potion setelah digunakan dengan aman
                    System.out.println("Player consumed a heal potion! HP: " + player.darah.getCurrentHP());
                }
            }
            if (obstacle instanceof Chest) {
                Chest chest = (Chest) obstacle;
                Rectangle interactArea = new Rectangle(chest.x - 10, chest.y - 10, 84, 84);

                if (interactArea.intersects(player.getHitbox())) {

                    // 1. KLIK KANAN: Hanya untuk memunculkan animasi & buka/tutup UI
                    if (keyH.rightMousePressed) {
                        if (!chest.isOpen) {
                            chest.isOpen = true;
                        }
                        chest.showChestUI = !chest.showChestUI;
                        keyH.rightMousePressed = false;
                    }

                    // 2. KLIK KIRI: Untuk mengambil item DARI DALAM UI
                    if (chest.showChestUI && keyH.leftMousePressed) {

                        int startX = 150;
                        int startY = 200;
                        int itemSize = 64;

                        // UBAH JUGA ANGKA INI: Samakan dengan yang ada di paintComponent
                        int jarakAntarItem = 140;

                        // Cek kotak mana yang diklik oleh mouse
                        for (int i = 0; i < chest.chestSlots.length; i++) {
                            if (chest.chestSlots[i] != null) {
                                int slotX = startX + (i * jarakAntarItem);
                                Rectangle itemClickArea = new Rectangle(slotX, startY, itemSize, itemSize);

                                if (itemClickArea.contains(keyH.mouseX, keyH.mouseY)) {
                                    // Pindahkan ke tas
                                    boolean masuk = inventory.addItem(chest.chestSlots[i]);
                                    if (masuk) {
                                        System.out.println(chest.chestSlots[i].name + " diambil dari peti!");
                                        chest.chestSlots[i] = null; // Kosongkan slot peti tersebut
                                    }
                                    break; // Cukup ambil 1 item per klik
                                }
                            }
                        }
                        keyH.leftMousePressed = false;
                    }

                } else {
                    chest.showChestUI = false;
                }
            }
        }

        java.util.Iterator<Entity> entityIt = entities.iterator();
        while (entityIt.hasNext()) {
            Entity entity = entityIt.next();
            if (entity instanceof RedHood) {
                RedHood redHood = (RedHood) entity;
                if (redHood.shouldRemove()) {
                    entityIt.remove();
                    continue;
                }

                Rectangle enemyHitbox = new Rectangle(redHood.x, redHood.y, redHood.hitbox.width,
                        redHood.hitbox.height);
                if (enemyHitbox.intersects(player.getHitbox()) && !redHood.active) {
                    redHood.startDisappear();
                    System.out.println("you got a key ");
                    Key--;

                }
            }
        }

        // ===== LARK 1A CHEAT CONSOLE =====
        // Deteksi apakah player berdiri di atas tile "L"
        boolean isOnLarkTile = false;
        for (int[] larkPos : larkPositions) {
            Rectangle larkHitbox = new Rectangle(larkPos[0], larkPos[1], tileSize, tileSize);
            if (larkHitbox.intersects(player.getHitbox())) {
                isOnLarkTile = true;
                break;
            }
        }

        // Edge detection: popup hanya muncul saat PERTAMA KALI masuk tile L
        if (isOnLarkTile && !wasOnLarkTile && !cheatPopupOpen) {
            // Reset keys immediately so player stops moving
            keyH.upPressed = false;
            keyH.downPressed = false;
            keyH.leftPressed = false;
            keyH.rightPressed = false;

            cheatPopupOpen = true;
            SwingUtilities.invokeLater(() -> {
                String input = showStyledCheatDialog();
                if (input != null && !input.trim().isEmpty()) {
                    processCheatCode(input.trim());
                }
                cheatPopupOpen = false;
            });
        }
        wasOnLarkTile = isOnLarkTile;
        // ===== END LARK 1A =====
    }

    /**
     * Memperlihatkan dialog cheat console yang didesain retro sesuai tema game.
     */
    private String showStyledCheatDialog() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(frame, "LARK 1A - Cheat Console", true);
        dialog.setUndecorated(true); // Custom title bar
        dialog.setSize(380, 220);
        dialog.setLocationRelativeTo(frame);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(new Color(15, 15, 18));
        mainPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0, 200, 255), 2));

        Font pixelFont = null;
        try {
            InputStream is = getClass().getResourceAsStream("/Assets/Pixuf.ttf");
            if (is != null) {
                pixelFont = Font.createFont(Font.TRUETYPE_FONT, is);
            }
        } catch (Exception e) {
            // Abaikan jika gagal
        }

        if (pixelFont == null) {
            pixelFont = new Font("Consolas", Font.BOLD, 14);
        }

        JLabel titleLabel = new JLabel("LARK 1A - SYSTEM TERMINAL");
        titleLabel.setBounds(20, 15, 340, 25);
        titleLabel.setFont(pixelFont.deriveFont(Font.BOLD, 16f));
        titleLabel.setForeground(new Color(0, 200, 255));
        mainPanel.add(titleLabel);

        JLabel promptLabel = new JLabel("ENTER ACCESS KEY / CHEAT CODE:");
        promptLabel.setBounds(20, 45, 340, 20);
        promptLabel.setFont(pixelFont.deriveFont(12f));
        promptLabel.setForeground(new Color(200, 200, 200));
        mainPanel.add(promptLabel);

        JTextField textField = new JTextField();
        textField.setBounds(20, 75, 340, 35);
        textField.setBackground(new Color(25, 25, 30));
        textField.setForeground(new Color(0, 255, 150)); // Hijau neon
        textField.setCaretColor(new Color(0, 255, 150));
        textField.setFont(pixelFont.deriveFont(14f));
        textField.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(new Color(0, 200, 255, 100), 1),
                javax.swing.BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        mainPanel.add(textField);
        final String[] result = { null };

        JButton btnOk = new JButton("EXECUTE") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                // Clear background with parent's color first to avoid alpha stacking artifact
                g2.setColor(new Color(15, 15, 18));
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Draw our custom background
                if (getModel().isPressed()) {
                    g2.setColor(new Color(0, 120, 220));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(0, 150, 255, 60));
                    setForeground(Color.WHITE);
                } else {
                    g2.setColor(new Color(0, 150, 255, 20));
                    setForeground(new Color(0, 200, 255));
                }
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Draw border
                g2.setColor(new Color(0, 200, 255));
                g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();

                super.paintComponent(g);
            }
        };
        btnOk.setBounds(20, 155, 160, 40);
        btnOk.setFont(pixelFont.deriveFont(Font.BOLD, 12f));
        btnOk.setFocusPainted(false);
        btnOk.setContentAreaFilled(false);
        btnOk.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        btnOk.addActionListener(e -> {
            result[0] = textField.getText();
            dialog.dispose();
        });
        mainPanel.add(btnOk);

        JButton btnCancel = new JButton("ABORT") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                // Clear background with parent's color first to avoid alpha stacking artifact
                g2.setColor(new Color(15, 15, 18));
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Draw our custom background
                if (getModel().isPressed()) {
                    g2.setColor(new Color(200, 30, 30));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 50, 50, 50));
                    setForeground(Color.WHITE);
                } else {
                    g2.setColor(new Color(255, 50, 50, 15));
                    setForeground(new Color(255, 100, 100));
                }
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Draw border
                g2.setColor(new Color(255, 100, 100));
                g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();

                super.paintComponent(g);
            }
        };
        btnCancel.setBounds(200, 155, 160, 40);
        btnCancel.setFont(pixelFont.deriveFont(Font.BOLD, 12f));
        btnCancel.setFocusPainted(false);
        btnCancel.setContentAreaFilled(false);
        btnCancel.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        btnCancel.addActionListener(e -> {
            dialog.dispose();
        });
        mainPanel.add(btnCancel);

        textField.addActionListener(e -> {
            result[0] = textField.getText();
            dialog.dispose();
        });

        dialog.setContentPane(mainPanel);
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent e) {
                textField.requestFocusInWindow();
            }
        });

        dialog.setVisible(true);
        return result[0];
    }

    /**
     * Memproses cheat code yang dimasukkan melalui Lark 1A console.
     */
    private void processCheatCode(String code) {
        switch (code.toLowerCase()) {
            case "99":
                System.out.println("CHEAT ACTIVATED: Instant Win!");
                WinGame();
                break;
            case "heal":
                if (player != null && player.darah != null) {
                    player.darah.heal(100);
                    System.out.println("CHEAT ACTIVATED: Full Heal! HP: " + player.darah.getCurrentHP());
                }
                break;
            case "soeharto":
                if (player != null && player.darah != null) {
                    player.darah.heal(-99);
                    System.out.println("CHEAT ACTIVATED: Ditembak petrus! HP: " + player.darah.getCurrentHP());
                }
                break;
            case "speed":
                if (player != null) {
                    player.speed += 2;
                    player.normalSpeed += 2;
                    System.out.println("CHEAT ACTIVATED: Speed increased! Speed: " + player.speed + ", Normal Speed: " + player.normalSpeed);
                }
                break;
            default:
                System.out.println("Cheat code tidak dikenali: " + code);
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(
                            this,
                            "Cheat code \"" + code + "\" tidak dikenali!",
                            "LARK 1A",
                            JOptionPane.WARNING_MESSAGE);
                });
                break;
        }
    }

    private Item getRandomItem() {
        int randomNum = (int) (Math.random() * 8) + 1;

        switch (randomNum) {
            // 4 Item Damage
            case 1:
                return new DamageItem("Pedang Kayu", 10);
            case 2:
                return new DamageItem("Pedang Besi", 15);
            case 3:
                return new DamageItem("Kapak Ganda", 20);
            case 4:
                return new DamageItem("Pedang Petir", 25);

            // 4 Item Defense
            case 5:
                return new DefenseItem("Baju Besi", 15);
            case 6:
                return new DefenseItem("Baju Perunggu", 30);
            case 7:
                return new DefenseItem("Armor Emas", 45);
            case 8:
                return new DefenseItem("Armor Dewa", 60);

            default:
                return new DamageItem("Pedang Kayu", 5);
        }
    }

    // Method baru untuk menangani logika tombol 1-4 dengan cerdas
    private void handleSlotKey(int slotIndex) {
        Chest openChest = null;
        for (Obstacle obs : obstacles) {
            if (obs instanceof Chest && ((Chest) obs).showChestUI) {
                openChest = (Chest) obs;
                break;
            }
        }

        // JIKA PETI TERBUKA -> Taruh barang ke dalam peti
        if (openChest != null) {
            Item itemDiTas = inventory.slots[slotIndex];

            if (itemDiTas != null) {
                boolean berhasilDitaruh = false;

                // Cari slot kosong di dalam peti
                for (int i = 0; i < openChest.chestSlots.length; i++) {
                    if (openChest.chestSlots[i] == null) { // Jika ketemu kotak kosong

                        // 1. Lepas equip jika sedang dipakai
                        if (itemDiTas == inventory.equippedWeapon)
                            inventory.equippedWeapon = null;
                        if (itemDiTas == inventory.equippedArmor)
                            inventory.equippedArmor = null;

                        player.attackDamage = player.baseDamage;
                        player.defense = player.baseDefense;
                        if (inventory.equippedWeapon != null)
                            inventory.equippedWeapon.use(player);
                        if (inventory.equippedArmor != null)
                            inventory.equippedArmor.use(player);

                        // 2. Pindahkan barang dari Tas ke Peti
                        openChest.chestSlots[i] = itemDiTas;
                        inventory.slots[slotIndex] = null;

                        System.out.println(itemDiTas.name + " ditaruh ke dalam peti!");
                        berhasilDitaruh = true;
                        break; // Keluar dari loop agar tidak ter-copy ke kotak lain
                    }
                }

                if (!berhasilDitaruh) {
                    System.out.println("Peti sudah penuh! Maksimal 3 item.");
                }
            }
        }
        // JIKA TIDAK ADA PETI TERBUKA -> Fitur Equip Normal
        else {
            equipItem(slotIndex);
        }
    }

    // Letakkan di dalam GamePanel.java (misalnya di bagian paling bawah class)
    private void equipItem(int slotIndex) {
        // Pastikan slot inventory yang ditekan ada isinya (tidak kosong)
        if (inventory.slots[slotIndex] != null) {

            Item itemToEquip = inventory.slots[slotIndex];

            // 1A. LOGIKA UNEQUIP (LEPAS ITEM)
            // Jika item yang ditekan sama persis dengan yang sedang dipakai, maka lepas!
            if (itemToEquip == inventory.equippedWeapon) {
                inventory.equippedWeapon = null;
                System.out.println("Senjata dilepas!");
            } else if (itemToEquip == inventory.equippedArmor) {
                inventory.equippedArmor = null;
                System.out.println("Armor dilepas!");
            }
            // 1B. LOGIKA EQUIP (PASANG ITEM BARU)
            // Jika belum dipakai, maka cek tipe class-nya lalu pasang
            else {
                if (itemToEquip instanceof DamageItem) {
                    inventory.equippedWeapon = itemToEquip;
                    System.out.println("Senjata baru di-equip!");
                } else if (itemToEquip instanceof DefenseItem) {
                    inventory.equippedArmor = itemToEquip;
                    System.out.println("Armor baru di-equip!");
                }
            }

            // 2. RESET STATUS KE NILAI AWAL (Pencegah Bug Infinite Stats)
            player.attackDamage = player.baseDamage;
            player.defense = player.baseDefense;

            // 3. TERAPKAN EFEK DARI ITEM YANG MASIH TERPASANG (JIKA ADA)
            if (inventory.equippedWeapon != null) {
                inventory.equippedWeapon.use(player);
            }
            if (inventory.equippedArmor != null) {
                inventory.equippedArmor.use(player);
            }

            System.out.println("Status Sekarang -> ATK: " + player.attackDamage + " | DEF: " + player.defense + "%");

        } else {
            System.out.println("Slot " + (slotIndex + 1) + " kosong!");
        }
    }

}