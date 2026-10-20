/** @author María Fortuna */

package com.rpgmaker.util;

import javax.sound.sampled.*;
import java.io.File;

/**
 * Reproductor de música de fondo (WAV).
 * Reproduce en bucle continuo hasta que se detiene.
 */
public class MusicPlayer {

    private Clip clip;
    private String currentFile;
    private float volume = 0.8f;

    /**
     * Reproduce un archivo WAV en bucle.
     * Si ya hay música sonando, la detiene primero.
     */
    public void play(String filePath) {
        if (filePath == null || filePath.isBlank()) return;

        // Si es el mismo archivo y ya suena, no hacer nada
        if (filePath.equals(currentFile) && clip != null && clip.isRunning()) {
            return;
        }

        stop();

        try {
            File audioFile = new File(filePath);
            if (!audioFile.exists()) {
                System.err.println("Archivo de audio no encontrado: " + filePath);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            setVolume(volume);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
            currentFile = filePath;
        } catch (Exception e) {
            System.err.println("Error al reproducir audio: " + e.getMessage());
        }
    }

    /** Detiene la reproducción actual. */
    public void stop() {
        if (clip != null) {
            clip.stop();
            clip.close();
            clip = null;
            currentFile = null;
        }
    }

    /**
     * Ajusta el volumen (0.0 a 1.0).
     */
    public void setVolume(float vol) {
        this.volume = Math.max(0f, Math.min(1f, vol));
        if (clip != null && clip.isOpen()) {
            try {
                FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float min = control.getMinimum();
                float max = control.getMaximum();
                float gain = min + (max - min) * this.volume;
                control.setValue(gain);
            } catch (Exception e) {
                // Algunos sistemas no soportan control de volumen
            }
        }
    }

    /** Devuelve el volumen actual (0.0 a 1.0). */
    public float getVolume() {
        return volume;
    }

    /** Indica si hay música reproduciéndose. */
    public boolean isPlaying() {
        return clip != null && clip.isRunning();
    }
}
