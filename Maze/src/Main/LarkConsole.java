package Main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.InputStream;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import Item.Lantern;

/**
 * LarkConsole class handles the Lark 1A cheat console system.
 * It manages cheat tile positions, collision detection, retro dialog UI, and
 * cheat processing.
 */
public class LarkConsole {
    private final GamePanel gp;
    private final ArrayList<int[]> larkPositions = new ArrayList<>();
    private boolean cheatPopupOpen = false;
    private boolean wasOnLarkTile = false;
    private Image larkImage;

    public LarkConsole(GamePanel gp) {
        this.gp = gp;
        loadAsset();
    }

    private void loadAsset() {
        this.larkImage = gp.loadImage("/Assets/ASSET/Lark 1A.png");
    }

    public void addPosition(int x, int y) {
        larkPositions.add(new int[] { x, y });
    }

    public void clearPositions() {
        larkPositions.clear();
    }

    public Image getLarkImage() {
        return larkImage;
    }

    public boolean isCheatPopupOpen() {
        return cheatPopupOpen;
    }

    /**
     * Checks if the player is standing on any Lark ("L") tiles and triggers the
     * cheat console.
     */
    public void checkCollision() {
        if (gp.player == null)
            return;

        // Deteksi apakah player berdiri di atas tile "L"
        boolean isOnLarkTile = false;
        Rectangle playerHitbox = gp.player.getHitbox();
        for (int[] larkPos : larkPositions) {
            Rectangle larkHitbox = new Rectangle(larkPos[0], larkPos[1], gp.tileSize, gp.tileSize);
            if (larkHitbox.intersects(playerHitbox)) {
                isOnLarkTile = true;
                break;
            }
        }

        // Edge detection: popup hanya muncul saat PERTAMA KALI masuk tile L
        if (isOnLarkTile && !wasOnLarkTile && !cheatPopupOpen) {
            // Reset keys immediately so player stops moving
            if (gp.keyH != null) {
                gp.keyH.upPressed = false;
                gp.keyH.downPressed = false;
                gp.keyH.leftPressed = false;
                gp.keyH.rightPressed = false;
            }

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
    }

    /**
     * Memperlihatkan dialog cheat console yang didesain retro sesuai tema game.
     */
    private String showStyledCheatDialog() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(gp);
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
                System.out.println("CHEAT ACTIVATED: Instant Win / Lantern test prompt!");
                int choice = JOptionPane.showOptionDialog(
                        gp,
                        "Pilih aksi cheat:\n\nYes = End Game\nNo = Dapatkan Lantern untuk tes.",
                        "LARK 1A - Cheat Mode",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new String[] { "End Game", "Dapatkan Lantern" },
                        "Dapatkan Lantern");
                if (choice == JOptionPane.YES_OPTION) {
                    gp.WinGame();
                } else if (choice == JOptionPane.NO_OPTION) {
                    grantLantern();
                }
                break;
            case "heal":
                if (gp.player != null && gp.player.darah != null) {
                    gp.player.darah.heal(100);
                    System.out.println("CHEAT ACTIVATED: Full Heal! HP: " + gp.player.darah.getCurrentHP());
                }
                break;
            case "soeharto":
                if (gp.player != null && gp.player.darah != null) {
                    gp.player.darah.heal(-99);
                    System.out.println("CHEAT ACTIVATED: Ditembak petrus! HP: " + gp.player.darah.getCurrentHP());
                }
                break;
            case "speed":
                if (gp.player != null) {
                    gp.player.speed += 2;
                    gp.player.normalSpeed += 2;
                    System.out.println("CHEAT ACTIVATED: Speed increased! Speed: " + gp.player.speed
                            + ", Normal Speed: " + gp.player.normalSpeed);
                }
                break;
            default:
                System.out.println("Cheat code tidak dikenali: " + code);
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(
                            gp,
                            "Cheat code \"" + code + "\" tidak dikenali!",
                            "LARK 1A",
                            JOptionPane.WARNING_MESSAGE);
                });
                break;
        }
    }

    private void grantLantern() {
        if (gp == null || gp.inventory == null || gp.player == null) {
            return;
        }

        Lantern lantern = new Lantern("Lampu Cheat", 2, 2);
        boolean added = gp.inventory.addItem(lantern);

        if (gp.inventory.equippedLantern == null) {
            gp.inventory.equippedLantern = lantern;
            System.out.println("Lantern cheat di-equip otomatis.");
        }

        gp.player.attackDamage = gp.player.baseDamage;
        gp.player.defense = gp.player.baseDefense;
        gp.safeVisionTiles = gp.baseSafeVisionTiles;
        gp.visionRangeTiles = gp.baseVisionRangeTiles;

        if (gp.inventory.equippedWeapon != null) {
            gp.inventory.equippedWeapon.use(gp.player);
        }
        if (gp.inventory.equippedArmor != null) {
            gp.inventory.equippedArmor.use(gp.player);
        }
        if (gp.inventory.equippedLantern != null) {
            gp.inventory.equippedLantern.use(gp.player);
        }

        if (added) {
            JOptionPane.showMessageDialog(
                    gp,
                    "Lantern cheat telah diberikan dan di-equip untuk pengujian.",
                    "LARK 1A",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(
                    gp,
                    "Inventory penuh, tetapi lantern cheat tetap di-equip.",
                    "LARK 1A",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
}