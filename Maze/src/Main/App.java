package Main;

import javax.swing.JFrame;


public class App {
    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame("Maze Pak Hendrawan");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // frame.setResizable(false);
        GamePanel gamePanel = new GamePanel();
        frame.add(gamePanel);
        frame.pack(); 
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        gamePanel.startGameThread();
    }
}
