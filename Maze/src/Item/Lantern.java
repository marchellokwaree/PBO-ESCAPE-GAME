package Item;

import Entitiy.Player;
import Main.GamePanel;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class Lantern extends Item {
    public int bonusSafeVision;
    public int bonusRange;
    private BufferedImage[] animationFrames;
    private int currentFrame = 0;
    private int animationCounter = 0;
    private final int animationDelay = 6;

    public Lantern(String name, int safeVisionBonus, int rangeBonus) {
        this.name = name;
        this.bonusSafeVision = safeVisionBonus;
        this.bonusRange = rangeBonus;
        loadIcon();
    }

    private void loadIcon() {
        String[] paths = {
                "/Assets/ASSET/Lantern/Lantern 5- Silver and Orange.png",
                "Assets/ASSET/Lantern/Lantern 5- Silver and Orange.png"
        };

        BufferedImage spriteSheet = null;
        for (String path : paths) {
            try (InputStream stream = getClass().getResourceAsStream(path)) {
                if (stream != null) {
                    spriteSheet = ImageIO.read(stream);
                    break;
                }
            } catch (Exception e) {
                System.out.println("Error memuat ikon lantern dari resource: " + e.getMessage());
            }
        }

        if (spriteSheet == null) {
            try {
                String userDir = System.getProperty("user.dir");
                for (String path : paths) {
                    File file = new File(userDir, path.replace('/', File.separatorChar));
                    if (file.exists()) {
                        spriteSheet = ImageIO.read(file);
                        break;
                    }
                }

                if (spriteSheet == null) {
                    for (String path : paths) {
                        File file = new File(userDir + File.separator + "Maze" + File.separator + "src",
                                path.replace('/', File.separatorChar));
                        if (file.exists()) {
                            spriteSheet = ImageIO.read(file);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Error memuat ikon lantern dari file system: " + e.getMessage());
            }
        }

        if (spriteSheet != null) {
            int frameWidth = 32;
            int frameHeight = 32;
            int frameCount = Math.max(1, spriteSheet.getWidth() / frameWidth);
            animationFrames = new BufferedImage[frameCount];
            for (int i = 0; i < frameCount; i++) {
                int x = i * frameWidth;
                if (x + frameWidth <= spriteSheet.getWidth() && frameHeight <= spriteSheet.getHeight()) {
                    animationFrames[i] = spriteSheet.getSubimage(x, 0, frameWidth, frameHeight);
                }
            }
            if (animationFrames.length > 0) {
                this.image = animationFrames[0];
            }
            return;
        }

        System.out.println("Gambar lantern tidak ditemukan di resource maupun filesystem.");
    }

    private void updateAnimation() {
        if (animationFrames == null || animationFrames.length == 0) {
            return;
        }
        animationCounter++;
        if (animationCounter >= animationDelay) {
            animationCounter = 0;
            currentFrame++;
            if (currentFrame >= animationFrames.length) {
                currentFrame = 0;
            }
        }
    }

    public BufferedImage getAnimatedFrame() {
        if (animationFrames != null && animationFrames.length > 0) {
            updateAnimation();
            return animationFrames[currentFrame];
        }
        return this.image;
    }

    @Override
    public void use(Player p) {
        GamePanel gp = p.getGamePanel();
        if (gp != null) {
            gp.safeVisionTiles += bonusSafeVision;
            gp.visionRangeTiles += bonusRange;
            System.out.println(
                    this.name + " di-equip! Safe Vision +" + bonusSafeVision + ", Vision Range +" + bonusRange);
        }
    }
}
