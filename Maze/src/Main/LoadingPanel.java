package Main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class LoadingPanel extends JPanel {
    private JProgressBar progressBar;
    private JLabel loadingLabel;
    private Timer timer;

    public LoadingPanel(Font customFont) {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        
        // Panel tengah untuk Label dan ProgressBar
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        
        loadingLabel = new JLabel("LOADING...");
        if (customFont != null) {
            loadingLabel.setFont(customFont.deriveFont(48f));
        } else {
            loadingLabel.setFont(new Font("Arial", Font.BOLD, 48));
        }
        loadingLabel.setForeground(Color.WHITE);
        loadingLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(100, 200, 100)); // Hijau
        progressBar.setBackground(new Color(50, 50, 50));
        if (customFont != null) {
            progressBar.setFont(customFont.deriveFont(16f));
        }
        progressBar.setMaximumSize(new Dimension(400, 30));
        progressBar.setAlignmentX(JProgressBar.CENTER_ALIGNMENT);
        
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(loadingLabel);
        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(progressBar);
        centerPanel.add(Box.createVerticalGlue());
        
        add(centerPanel, BorderLayout.CENTER);
    }

    public void startLoading(final JFrame parentFrame) {
        parentFrame.setContentPane(this);
        parentFrame.revalidate();
        parentFrame.repaint();

        // Animasi ProgressBar (Fake Loading agar UI terasa interaktif)
        timer = new Timer(30, new ActionListener() {
            int progress = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                progress += (int)(Math.random() * 5) + 1;
                if (progress > 95) {
                    progress = 95; // Tertahan di 95% sampai load benar-benar selesai
                }
                progressBar.setValue(progress);
            }
        });
        timer.start();

        // Jalankan inisialisasi GamePanel di background thread agar UI tidak freeze
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Ganti panel HUD dengan GamePanel.
                final GamePanel gamePanel = new GamePanel();
                
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (timer != null) {
                            timer.stop();
                        }
                        progressBar.setValue(100);
                        
                        // Delay kecil saat 100% agar player sempat melihat loading penuh
                        Timer delayTimer = new Timer(200, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent evt) {
                                ((Timer)evt.getSource()).stop();
                                parentFrame.setContentPane(gamePanel);
                                parentFrame.revalidate();
                                parentFrame.pack();
                                parentFrame.setLocationRelativeTo(null);
                                gamePanel.requestFocusInWindow();
                                gamePanel.startGameThread();
                            }
                        });
                        delayTimer.setRepeats(false);
                        delayTimer.start();
                    }
                });
            }
        }).start();
    }
}
