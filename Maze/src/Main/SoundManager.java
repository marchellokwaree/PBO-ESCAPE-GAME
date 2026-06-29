package Main;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private static Map<String, Clip> clips = new HashMap<>();

    private static File resolveFile(String path) {
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

    public static Clip loadSound(String path) {
        if (clips.containsKey(path)) {
            return clips.get(path);
        }
        try {
            AudioInputStream audioStream = null;
            InputStream is = SoundManager.class.getResourceAsStream(path);
            if (is != null) {
                InputStream bufferedIn = new BufferedInputStream(is);
                audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            } else {
                File soundFile = resolveFile(path);
                if (soundFile.exists()) {
                    audioStream = AudioSystem.getAudioInputStream(soundFile);
                }
            }

            if (audioStream != null) {
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clips.put(path, clip);
                return clip;
            } else {
                System.err.println("Sound file not found: " + path);
            }
        } catch (Exception e) {
            System.err.println("Error loading sound (" + path + "): " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static void play(String path) {
        Clip clip = loadSound(path);
        if (clip != null) {
            try {
                if (clip.isRunning()) {
                    clip.stop();
                }
                clip.setFramePosition(0);
                clip.start();
            } catch (Exception e) {
                System.err.println("Error playing sound (" + path + "): " + e.getMessage());
            }
        }
    }

    public static void stop(String path) {
        Clip clip = clips.get(path);
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public static void loop(String path) {
        Clip clip = loadSound(path);
        if (clip != null) {
            try {
                if (!clip.isRunning()) {
                    clip.setFramePosition(0);
                    clip.loop(Clip.LOOP_CONTINUOUSLY);
                    clip.start();
                }
            } catch (Exception e) {
                System.err.println("Error looping sound (" + path + "): " + e.getMessage());
            }
        }
    }
}
