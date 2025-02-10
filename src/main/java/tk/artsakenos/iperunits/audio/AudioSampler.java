/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.audio;

import tk.artsakenos.iperunits.system.SuperTimer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.util.logging.Level;
import java.util.logging.Logger;

import static tk.artsakenos.iperunits.audio.MusicNote.*;

/**
 * @author Andrea
 * @version Sep 12, 2020
 */
@SuppressWarnings("unused")
public class AudioSampler {

    /**
     * Samples per seconds
     */
    private static final float SAMPLE_RATE = 44100;
    private static final int VOLUME = 20;

    public static final MusicNote[] JINGLEBELLS = {
            E, E, E, PAUSE, E, E, E, PAUSE, E, G, C, D, E, PAUSE, PAUSE, PAUSE,
            F, F, F, F, F, E, E, E, E, D, D, E, D, PAUSE, G, PAUSE,
            E, E, E, PAUSE, E, E, E, PAUSE, E, G, C, D, E, PAUSE, PAUSE, PAUSE,
            F, F, F, F, F, E, E, E, G, G, F, D, C, PAUSE};

    public static final MusicNote[] SUCCESS = {G, C, E, G, G, E, G, G, G, G};
    public static final MusicNote[] BEEP_OK = {C, G};
    public static final MusicNote[] BEEP_NO = {G, C};

    private static SourceDataLine sdl;

    public void start() {
        AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
        try {
            sdl = AudioSystem.getSourceDataLine(af);
            sdl.open(af);
            sdl.start();
        } catch (LineUnavailableException ex) {
            Logger.getLogger(AudioSampler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void stop() {
        sdl.stop();
        sdl.close();
    }

    /**
     * Suona le note desiderate da MusicNote.*
     *
     * @param notes The notes
     */
    public void soundNotes(MusicNote... notes) {
        start();
        for (MusicNote note : notes) {
            if (PAUSE.equals(note)) {
                SuperTimer.sleep(note.duration);
            } else {
                putNote(note.analogFrequency(note.octave), note.duration);
                drain();
            }
        }
        stop();
    }

    /**
     * Appende una frequenza al source dataline. Utilizzare drain() per separare
     * le note, e sotto i 50 millisecondi senza drain le note diventano
     * inintellegibili, si creano altri effetti di aliasing.
     *
     * @param analogFrequency La frequenza in Hz
     * @param milliseconds    La durata in millisecondi
     */
    public void putNote(int analogFrequency, long milliseconds) {

        byte[] buf = new byte[1];
        double normalizedFrequency = (analogFrequency / SAMPLE_RATE) * 2.0 * Math.PI;

        /*
         * samples per second / 1000 = samples per milliseconds
         * numberOfSamples = samples per milliseconds * milliseconds
         */
        float numberOfSamples = (SAMPLE_RATE / 1000) * milliseconds;

        for (int i = 0; i < numberOfSamples; i++) {
            double angleForThisSample = i * normalizedFrequency;
            buf[0] = (byte) (Math.sin(angleForThisSample) * VOLUME);
            sdl.write(buf, 0, 1);
        }
        // sdl.drain();
    }

    public void drain() {
        sdl.drain();
    }


}
