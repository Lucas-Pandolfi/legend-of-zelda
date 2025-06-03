package com.blackcatstudios.main;

import javax.sound.sampled.*;
import java.io.*;
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
	
	/*public static class Clips {
		public Clip[] clips;
		private int p;
		private int count;
		
		public Clips(byte[] buffer, int count) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
			if(buffer == null)
				return;
			
			clips = new Clip[count];
			this.count = count;
			
			for(int i = 0; i < count; i++) {
				clips[i] = AudioSystem.getClip();
				clips[i].open(AudioSystem.getAudioInputStream(new ByteArrayInputStream(buffer)));
			}
		}
		
		public void play() {
			if(clips == null)
				return;
			
			clips[p].stop();
			clips[p].setFramePosition(0);
			clips[p].start();
			
			p++;
			
			if(p >= count)
				p = 0;
		}
		
		public void loop() {
			if(clips == null)
				return;
			
			clips[p].loop(300);//Valor 300 faz com a musica toque infinitamente
		}
	}
	
	public static Clips musicBackground = load("/music.wav", 1);
	public static Clips pistolShootEffect = load("/pistolShoot.wav", 1);
	
	private static Clips load(String name, int count) {
		try {
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataInputStream dis = new DataInputStream(Sound.class.getResourceAsStream(name));
			
			byte[] buffer = new byte[1024];
			int read = 0;
			
			while((read = dis.read(buffer)) >= 0) {
				baos.write(buffer, 0, read);
			}
			
			dis.close();
			
			byte[] data = baos.toByteArray();
			
			return new Clips(data, count);
		}
		catch(Exception e) {
			try {
				e.printStackTrace();
				
				return new Clips(null, 0);
			}
			catch(Exception ee) {
				ee.printStackTrace();
				
				return null;
			}
		}
	}*/
}