package com.blackcatstudios.main;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Sound {
	private byte[] audioData;
    private AudioFormat format;
    private final List<Clip> activeClips = new CopyOnWriteArrayList<>();
    private static final ExecutorService soundExecutor = Executors.newCachedThreadPool();
    
    public static final Sound musicBackground = new Sound("/music.wav");
    public static final Sound playerReceivingDamageEffect = new Sound("/hurt.wav");
    
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
            	activeClips.removeIf(clip -> {
                    if (clip == null) return true;
                    try {
                        return !clip.isActive();
                    } catch (Exception e) {
                        return true;
                    }
                });
                
                DataLine.Info info = new DataLine.Info(Clip.class, format);
                Clip newClip = (Clip) AudioSystem.getLine(info);
                newClip.open(format, audioData, 0, audioData.length);
                
                newClip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        newClip.close();
                    }
                });
                
                newClip.start();
                activeClips.add(newClip);
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
    
    private void stopAll() {
        soundExecutor.submit(() -> {
            List<Clip> clipsToStop = new ArrayList<>(activeClips);
            activeClips.clear();
            
            clipsToStop.forEach(clip -> {
                try {
                    if (clip.isRunning()) {
                        clip.stop();
                    }
                    clip.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }
    
    public static void stopAllSounds() {
        musicBackground.stopAll();
    }
    
    public static void shutdown() {
        soundExecutor.shutdown();
    }
}