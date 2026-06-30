package Obstacle;
import Item.Item;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import Main.GamePanel;
public class Chest extends Obstacle {
    private BufferedImage spriteSheet;
    private BufferedImage[] animationFrames = new BufferedImage[4];
    private int currentFrame = 0;
    private int animationCounter = 0;
    private final int animationDelay = 6;
    public boolean active = false;
    public boolean isOpen = false;
    public boolean showChestUI = false;
    public Item[] chestSlots = new Item[3];
    public Chest(int x, int y, int width, int height, Item item) {
        super(x, y, width, height);
        this.chestSlots[0] = item;
        try {
            this.spriteSheet = loadBufferedImage("/Assets/ASSET/coins-chests-etc-2-0.png");
            int frameWidth = 16;
            int frameHeight = 16;
            int colx = 6 * frameWidth;
            int coly = 38 * frameHeight;
            for (int i = 0; i < animationFrames.length; i++) {
                int currentX = colx + (i * frameWidth);
                animationFrames[i] = spriteSheet.getSubimage(currentX, coly, frameWidth, frameHeight);
            }
        } catch (Exception e) {
            System.out.println("Error loading chest image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void update() {
        // Cek status: Apakah peti sudah dibuka?
        if (isOpen) {
            // Jika sudah dibuka, jalankan animasi. 
            // TAPI, hentikan animasi (jangan di-reset ke 0) jika sudah sampai di frame terakhir.
            if (currentFrame < animationFrames.length - 1) {
                animationCounter++;
                if (animationCounter >= animationDelay) {
                    animationCounter = 0;
                    currentFrame++;
                }
            }
        } else {
            // Jika peti belum dibuka, paksa frame diam di gambar ke-0 (peti tertutup)
            currentFrame = 0;
        }
    }

    public BufferedImage getCurrentFrame() {
        if (animationFrames == null || animationFrames.length == 0) {
            return spriteSheet;
        }
        if (currentFrame < 0 || currentFrame >= animationFrames.length) {
            currentFrame = currentFrame % animationFrames.length;
            if (currentFrame < 0) {
                currentFrame += animationFrames.length;
            }
        }
        if (animationFrames[currentFrame] != null) {
            return animationFrames[currentFrame];
        }
        return spriteSheet;
    }

    public void draw(Graphics2D g2, GamePanel gp) {
        BufferedImage frame = getCurrentFrame();
        super.drawCamera(g2, gp , frame);
    }

    // Ubah tipe kembalian dari 'Item' menjadi 'void' karena tidak lagi mengeluarkan item
    public void openChest() {
        if (!isOpen) {
            isOpen = true; // Aktifkan animasi peti terbuka
            System.out.println("Peti berhasil dibuka!");
            
            // Kode 'storedItem' dihapus seluruhnya karena kita sekarang 
            // menggunakan UI dan Array 'chestSlots'
        }
    }

    public void close() {
        isOpen = false;
    }
}
