package com.blackcatstudios.main;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Sound {
    
    private byte[] audioData;
    private AudioFormat format;
    private final Queue<Clip> activeClips = new ConcurrentLinkedQueue<>();
    private static final ExecutorService soundExecutor = Executors.newCachedThreadPool();
    
    public static final Sound musicBackground = new Sound("/music.wav");
    public static final Sound playerReceivingDamageEffect = new Sound("/hurt.wav");
    public static final Sound pistolShootEffect = new Sound("/pistolShoot.wav");
    
    public Sound(String name) {
        try (InputStream audioSrc = Sound.class.getResourceAsStream(name);
             InputStream bufferedIn = new BufferedInputStream(audioSrc);
             AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn)) {
            
            format = audioStream.getFormat();
            audioData = audioStream.readAllBytes();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void play() {
        soundExecutor.submit(() -> {
            try {
                cleanInactiveClips();
                
                DataLine.Info info = new DataLine.Info(Clip.class, format);
                Clip newClip = (Clip) AudioSystem.getLine(info);
                newClip.open(format, audioData, 0, audioData.length);
                
                newClip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        newClip.close();
                        activeClips.remove(newClip);
                    }
                });
                
                activeClips.add(newClip);
                newClip.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    public void loop() {
        soundExecutor.submit(() -> {
            try {
                DataLine.Info info = new DataLine.Info(Clip.class, format);
                Clip loopClip = (Clip) AudioSystem.getLine(info);
                loopClip.open(format, audioData, 0, audioData.length);
                loopClip.loop(Clip.LOOP_CONTINUOUSLY);
                activeClips.add(loopClip);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    private void cleanInactiveClips() {
        for (Clip clip : activeClips) {
            if (clip == null || !clip.isActive()) {
                activeClips.remove(clip);
            }
        }
    }
    
    private void stopAll() {
        soundExecutor.submit(() -> {
            for (Clip clip : activeClips) {
                try {
                    if (clip != null && clip.isRunning()) {
                        clip.stop();
                    }
                    clip.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            activeClips.clear();
        });
    }
    
    public static void stopAllSounds() {
        musicBackground.stopAll();
    }
    
    public static void shutdown() {
        soundExecutor.shutdown();
    }
}
