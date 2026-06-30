package Main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.InputStream;
import java.io.BufferedInputStream;

/**
 * Custom JButton dengan rounded corners untuk tampilan yang lebih modern.
 */
class RoundedButton extends JButton {
	private static final long serialVersionUID = 1L;
	private int arcWidth = 20;
	private int arcHeight = 20;
	private Color hoverColor;
	private Color defaultColor;
	private boolean isHovered = false;

	public RoundedButton(String text, Color bgColor, Color borderColor) {
		super(text);
		this.defaultColor = bgColor;
		this.hoverColor = new Color(
				Math.min(bgColor.getRed() + 30, 255),
				Math.min(bgColor.getGreen() + 30, 255),
				Math.min(bgColor.getBlue() + 30, 255));

		setContentAreaFilled(false);
		setOpaque(false);
		setFocusPainted(false);
		setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		setBackground(bgColor);
		setForeground(Color.WHITE);

		addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				isHovered = true;
				repaint();
			}

			public void mouseExited(java.awt.event.MouseEvent e) {
				isHovered = false;
				repaint();
			}
		});

	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		Color color = isHovered ? hoverColor : defaultColor;
		g2.setColor(color);
		g2.fillRoundRect(0, 0, getWidth(), getHeight(), arcWidth, arcHeight);

		super.paintComponent(g);
	}

	@Override
	protected void paintBorder(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(getForeground());
		g2.setStroke(new java.awt.BasicStroke(2));
		g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arcWidth, arcHeight);
	}
}

/**
 * HUDPanel adalah panel menu utama sebelum masuk ke GamePanel.
 * Panel ini memungkinkan pemain memilih difficulty, memulai permainan, atau
 * keluar.
 */
public class HUDPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	// Referensi ke frame utama agar HUDPanel bisa diganti dengan GamePanel ketika
	// tombol Start ditekan.
	private final JFrame parentFrame;

	// Background image
	private BufferedImage backgroundImage;

	private Font customFont; // custom font untuk tombol dan teks lainnya

	private Clip musicClip;

	/**
	 * Konstruktor HUDPanel.
	 * 
	 * @param frame JFrame induk tempat panel ini akan ditambahkan.
	 */
	public HUDPanel(JFrame frame) {
		this.parentFrame = frame;
		setPreferredSize(new Dimension(900, 700));
		setOpaque(false);
		setLayout(new BorderLayout(0, 0));

		try {
			// Gunakan method bantuanmu! (Gunakan slash di awal agar sesuai dengan logic
			// method)
			File fontFile = resolveFile("/Assets/Pixuf.ttf");

			if (fontFile.exists()) {
				// Buat font dari file, lalu atur ukurannya
				customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(16f);
				setFont(customFont);
			} else {
				System.out.println("Path yang dicoba: " + fontFile.getAbsolutePath());
				throw new Exception("Font file benar-benar tidak ditemukan oleh resolveFile.");
			}

		} catch (Exception e) {
			System.out.println("Gagal load font: " + e.getMessage());

			// Fallback WAJIB agar tidak NullPointerException saat createTitleLabel
			customFont = new Font("Arial", Font.BOLD, 16);
			setFont(customFont);
		}

		// Load background image
		loadBackgroundImage();

		// Membuat judul menu.
		JLabel titleLabel = createTitleLabel();
		add(titleLabel, BorderLayout.NORTH);

		// Membuat area tengah berisi pilihan difficulty dan tombol.
		JPanel centerPanel = createCenterPanel();
		add(centerPanel, BorderLayout.CENTER);

		// Menambahkan teks footer sebagai instruksi tambahan.
		JLabel footerLabel = createFooterLabel();
		add(footerLabel, BorderLayout.SOUTH);

		// Mainkan background musik
		playBackgroundMusic();
	}

	private void playBackgroundMusic() {
		try {
			AudioInputStream audioStream = null;
			// 1. Coba load dari Classpath (Resource)
			InputStream is = getClass().getResourceAsStream("/Assets/Sound/menu_soundtrack_by_chiphead64.wav");
			if (is != null) {
				InputStream bufferedIn = new BufferedInputStream(is);
				audioStream = AudioSystem.getAudioInputStream(bufferedIn);
			} else {
				// 2. Coba load dari File System
				File soundFile = resolveFile("/Assets/Sound/menu_soundtrack_by_chiphead64.wav");
				if (soundFile.exists()) {
					audioStream = AudioSystem.getAudioInputStream(soundFile);
				}
			}

			if (audioStream != null) {
				musicClip = AudioSystem.getClip();
				musicClip.open(audioStream);
				musicClip.loop(Clip.LOOP_CONTINUOUSLY);
				musicClip.start();
			} else {
				System.err.println("Soundtrack file not found.");
			}
		} catch (Exception e) {
			System.err.println("Error playing background music: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void stopBackgroundMusic() {
		if (musicClip != null) {
			try {
				if (musicClip.isRunning()) {
					musicClip.stop();
				}
				musicClip.close();
			} catch (Exception e) {
				System.err.println("Error stopping music: " + e.getMessage());
			}
		}
	}

	/**
	 * Load background image dari folder assets
	 */
	private void loadBackgroundImage() {
		try {
			// Try loading from classpath first
			URL url = getClass().getResource("/Assets/TampilanAwal.png");
			if (url != null) {
				backgroundImage = ImageIO.read(url);
			} else {
				// Try loading from file system
				File file = new File("src/Assets/TampilanAwal.png");
				if (file.exists()) {
					backgroundImage = ImageIO.read(file);
				}
			}
		} catch (Exception e) {
			System.err.println("Failed to load menu background: " + e.getMessage());
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Draw background image if available
		if (backgroundImage != null) {
			g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);

			// Add semi-transparent overlay for better text visibility
			g2.setColor(new Color(0, 0, 0, 100));
			g2.fillRect(0, 0, getWidth(), getHeight());
		} else {
			// Fallback gradient if no image
			g2.setColor(new Color(10, 18, 30));
			g2.fillRect(0, 0, getWidth(), getHeight());
		}
	}

	/**
	 * Membuat label judul untuk HUD panel.
	 */
	private JLabel createTitleLabel() {
		JLabel titleLabel = new JLabel("");
		titleLabel.setFont(customFont.deriveFont(48f)); // Ukuran font lebih besar untuk judul
		titleLabel.setForeground(new Color(255, 100, 100));
		titleLabel.setHorizontalAlignment(JLabel.CENTER);
		titleLabel.setBorder(BorderFactory.createEmptyBorder(40, 0, 30, 0));
		return titleLabel;
	}

	/**
	 * Membuat panel tengah berisi pilihan difficulty dan tombol Start/Exit.
	 */
	private JPanel createCenterPanel() {
		JPanel centerPanel = new JPanel();
		centerPanel.setOpaque(false);
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

		RoundedButton startButton = new RoundedButton("PLAY", new Color(100, 200, 100), new Color(150, 255, 150));
		startButton.setFont(customFont.deriveFont(22f));
		startButton.setAlignmentX(CENTER_ALIGNMENT);
		startButton.setMaximumSize(new Dimension(300, 50));
		startButton.setForeground(Color.WHITE);
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onStartPressed();
			}
		});

		RoundedButton exitButton = new RoundedButton("EXIT", new Color(220, 80, 80), new Color(255, 150, 150));
		exitButton.setFont(customFont.deriveFont(22f));
		exitButton.setAlignmentX(CENTER_ALIGNMENT);
		exitButton.setMaximumSize(new Dimension(300, 50));
		exitButton.setForeground(Color.WHITE);
		exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onExitPressed();
			}
		});

		centerPanel.add(Box.createVerticalStrut(140)); // tengahin untuk play exit
		centerPanel.add(startButton);
		centerPanel.add(Box.createVerticalStrut(20));
		centerPanel.add(exitButton);
		centerPanel.add(Box.createVerticalStrut(140));

		return centerPanel;
	}

	/**
	 * Membuat label footer untuk HUD panel.
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
	 * Menangani aksi ketika tombol Start ditekan.
	 * Panel kemudian diganti menjadi GamePanel dan game dimulai.
	 */
	private void onStartPressed() {
		stopBackgroundMusic();

		LoadingPanel loadingPanel = new LoadingPanel(customFont);
		loadingPanel.startLoading(parentFrame);
	}

	/**
	 * Menangani aksi ketika tombol Exit ditekan.
	 */
	private void onExitPressed() {
		stopBackgroundMusic();
		System.exit(0);
	}

	private File resolveFile(String path) {
		String normalizedPath = path.replace('/', File.separatorChar);
		String userDir = System.getProperty("user.dir");

		// Coba path 1: di dalam src
		File candidate = new File(userDir + File.separator + "src" + normalizedPath);
		if (candidate.exists()) {
			return candidate;
		}

		// Coba path 2: langsung di root
		candidate = new File(userDir + normalizedPath);
		if (candidate.exists()) {
			return candidate;
		}

		// Coba path 3: kalau project ada di dalam folder Maze
		candidate = new File(userDir + File.separator + "Maze" + File.separator + "src" + normalizedPath);
		if (candidate.exists()) {
			return candidate;
		}

		// Default fallback
		return new File(userDir + File.separator + "src" + normalizedPath);
	}

}
