package Main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * GameOverPanel menampilkan layar akhir game (WIN/LOSE) dengan style mirip
 * landing page.
 * Memiliki background TampilanAwal.png, teks "YOU WIN" atau "YOU LOSE",
 * serta tombol untuk kembali ke Main Menu atau Exit.
 */
public class GameOverPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JFrame parentFrame;
    private final boolean isWin;
    private BufferedImage backgroundImage;
    private Font customFont;

    /**
     * Konstruktor GameOverPanel.
     *
     * @param frame JFrame induk.
     * @param isWin true jika pemain menang, false jika kalah.
     */
    public GameOverPanel(JFrame frame, boolean isWin) {
        this.parentFrame = frame;
        this.isWin = isWin;

        setPreferredSize(new Dimension(768, 576));
        setOpaque(false);
        setLayout(new BorderLayout(0, 0));

        // Load custom font
        loadCustomFont();

        // Load background image (sama dengan landing page)
        loadBackgroundImage();

        // Teks judul: "YOU WIN" atau "YOU LOSE"
        JLabel titleLabel = createTitleLabel();
        add(titleLabel, BorderLayout.NORTH);

        // Panel tengah berisi tombol-tombol
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);

        // Footer
        JLabel footerLabel = createFooterLabel();
        add(footerLabel, BorderLayout.SOUTH);

        // Play defeat sound if the player lost
        if (!isWin) {
            SoundManager.play("/Assets/Sound/SoundKalah.wav");
        } else {
            SoundManager.play("/Assets/Sound/SoundMenang.wav");
        }
    }

    /**
     * Load custom font Pixuf.ttf
     */
    private void loadCustomFont() {
        try {
            File fontFile = resolveFile("/Assets/Pixuf.ttf");
            if (fontFile.exists()) {
                customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(16f);
            } else {
                throw new Exception("Font file tidak ditemukan: " + fontFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("Gagal load font di GameOverPanel: " + e.getMessage());
            customFont = new Font("Arial", Font.BOLD, 16);
        }
    }

    /**
     * Load background image TampilanAwal.png (sama dengan landing page)
     */
    private void loadBackgroundImage() {
        try {
            URL url = getClass().getResource("/Assets/TampilanAwal.png");
            if (url != null) {
                backgroundImage = ImageIO.read(url);
            } else {
                File file = resolveFile("/Assets/TampilanAwal.png");
                if (file.exists()) {
                    backgroundImage = ImageIO.read(file);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load game over background: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw background image (sama dengan landing page)
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);

            // Overlay semi-transparan untuk visibility teks (lebih gelap dari landing page)
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, getWidth(), getHeight());
        } else {
            // Fallback gradient
            g2.setColor(new Color(10, 18, 30));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    /**
     * Membuat label judul "YOU WIN" atau "YOU LOSE"
     */
    private JLabel createTitleLabel() {
        String text = isWin ? "YOU WIN" : "YOU LOSE";
        Color textColor = isWin ? new Color(100, 255, 100) : new Color(255, 80, 80);

        JLabel titleLabel = new JLabel(text);
        titleLabel.setFont(customFont.deriveFont(64f));
        titleLabel.setForeground(textColor);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(80, 0, 10, 0));
        return titleLabel;
    }

    /**
     * Membuat panel tengah berisi sub-teks dan tombol Main Menu / Exit.
     */
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // Sub-teks keterangan
        String subText = isWin
                ? "Selamat! Kamu berhasil menyelesaikan maze!"
                : "Jangan menyerah! Coba lagi!";
        JLabel subLabel = new JLabel(subText);
        subLabel.setFont(customFont.deriveFont(18f));
        subLabel.setForeground(new Color(200, 200, 220));
        subLabel.setAlignmentX(CENTER_ALIGNMENT);
        subLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));

        // Tombol MAIN MENU — kembali ke HUDPanel (landing page)
        RoundedButton mainMenuButton = new RoundedButton("MAIN MENU", new Color(70, 130, 220),
                new Color(120, 180, 255));
        mainMenuButton.setFont(customFont.deriveFont(22f));
        mainMenuButton.setAlignmentX(CENTER_ALIGNMENT);
        mainMenuButton.setMaximumSize(new Dimension(300, 50));
        mainMenuButton.setForeground(Color.WHITE);
        mainMenuButton.addActionListener(e -> onMainMenuPressed());

        // Tombol EXIT — keluar dari game
        RoundedButton exitButton = new RoundedButton("EXIT", new Color(220, 80, 80), new Color(255, 150, 150));
        exitButton.setFont(customFont.deriveFont(22f));
        exitButton.setAlignmentX(CENTER_ALIGNMENT);
        exitButton.setMaximumSize(new Dimension(300, 50));
        exitButton.setForeground(Color.WHITE);
        exitButton.addActionListener(e -> onExitPressed());

        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(subLabel);
        centerPanel.add(mainMenuButton);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(exitButton);
        centerPanel.add(Box.createVerticalGlue());

        return centerPanel;
    }

    /**
     * Membuat label footer.
     */
    private JLabel createFooterLabel() {
        JLabel footerLabel = new JLabel("Gunakan WASD untuk bergerak - Hindari jebakan - Selamatkan semua Red Hood!");
        footerLabel.setFont(customFont.deriveFont(13f));
        footerLabel.setForeground(new Color(180, 180, 200));
        footerLabel.setHorizontalAlignment(JLabel.CENTER);
        footerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        return footerLabel;
    }

    /**
     * Kembali ke landing page (HUDPanel)
     */
    private void onMainMenuPressed() {
        HUDPanel hudPanel = new HUDPanel(parentFrame);
        parentFrame.setContentPane(hudPanel);
        parentFrame.revalidate();
        parentFrame.repaint();
        parentFrame.pack();
        parentFrame.setLocationRelativeTo(null);
    }

    /**
     * Keluar dari game
     */
    private void onExitPressed() {
        System.exit(0);
    }

    /**
     * Resolve file path (sama dengan HUDPanel)
     */
    private File resolveFile(String path) {
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
}
