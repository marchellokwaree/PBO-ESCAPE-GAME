package Main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;

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

	// Combo box untuk memilih tingkat kesulitan.
	private JComboBox<String> difficultyCombo;

	/**
	 * Konstruktor HUDPanel.
	 * 
	 * @param frame JFrame induk tempat panel ini akan ditambahkan.
	 */
	public HUDPanel(JFrame frame) {
		this.parentFrame = frame;
		setPreferredSize(new Dimension(768, 576));
		setOpaque(false);
		setLayout(new BorderLayout());

		// Membuat judul menu.
		JLabel titleLabel = createTitleLabel();
		add(titleLabel, BorderLayout.NORTH);

		// Membuat area tengah berisi pilihan difficulty dan tombol.
		JPanel centerPanel = createCenterPanel();
		add(centerPanel, BorderLayout.CENTER);

		// Menambahkan teks footer sebagai instruksi tambahan.
		JLabel footerLabel = createFooterLabel();
		add(footerLabel, BorderLayout.SOUTH);
	}

	/**
	 * Membuat label judul untuk HUD panel.
	 */
	private JLabel createTitleLabel() {
		JLabel titleLabel = new JLabel("MAZE PAK HENDRAWAN");
		titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setHorizontalAlignment(JLabel.CENTER);
		titleLabel.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
		return titleLabel;
	}

	/**
	 * Membuat panel tengah berisi pilihan difficulty dan tombol Start/Exit.
	 */
	private JPanel createCenterPanel() {
		JPanel centerPanel = new JPanel();
		centerPanel.setOpaque(false);
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

		JLabel instructionLabel = new JLabel("Pilih Jenis Backtracking dan tekan Start untuk mulai permainan");
		instructionLabel.setFont(new Font("Arial", Font.PLAIN, 18));
		instructionLabel.setForeground(Color.WHITE);
		instructionLabel.setAlignmentX(CENTER_ALIGNMENT);
		instructionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

		difficultyCombo = new JComboBox<>(new String[] { "Easy", "Medium", "Hard" });
		difficultyCombo.setFont(new Font("Arial", Font.PLAIN, 18));
		difficultyCombo.setMaximumSize(new Dimension(260, 40));
		difficultyCombo.setAlignmentX(CENTER_ALIGNMENT);

		JButton startButton = new JButton("Start Game");
		startButton.setFont(new Font("Arial", Font.BOLD, 20));
		startButton.setAlignmentX(CENTER_ALIGNMENT);
		startButton.setMaximumSize(new Dimension(260, 45));
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onStartPressed();
			}
		});

		JButton exitButton = new JButton("Exit");
		exitButton.setFont(new Font("Arial", Font.BOLD, 20));
		exitButton.setAlignmentX(CENTER_ALIGNMENT);
		exitButton.setMaximumSize(new Dimension(260, 45));
		exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onExitPressed();
			}
		});

		centerPanel.add(instructionLabel);
		centerPanel.add(difficultyCombo);
		centerPanel.add(Box.createVerticalStrut(30));
		centerPanel.add(startButton);
		centerPanel.add(Box.createVerticalStrut(12));
		centerPanel.add(exitButton);

		return centerPanel;
	}

	/**
	 * Membuat label footer untuk HUD panel.
	 */
	private JLabel createFooterLabel() {
		JLabel footerLabel = new JLabel("Gunakan WASD untuk bergerak setelah permainan dimulai");
		footerLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		footerLabel.setForeground(Color.LIGHT_GRAY);
		footerLabel.setHorizontalAlignment(JLabel.CENTER);
		footerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
		return footerLabel;
	}

	/**
	 * Menangani aksi ketika tombol Start ditekan.
	 * Panel kemudian diganti menjadi GamePanel dan game dimulai.
	 */
	private void onStartPressed() {

		// Ganti panel HUD dengan GamePanel.
		GamePanel gamePanel = new GamePanel();
		parentFrame.setContentPane(gamePanel);
		parentFrame.revalidate();
		parentFrame.pack();
		parentFrame.setLocationRelativeTo(null);
		gamePanel.requestFocusInWindow();
		gamePanel.startGameThread();
	}

	/**
	 * Menangani aksi ketika tombol Exit ditekan.
	 */
	private void onExitPressed() {
		System.exit(0);
	}

}
