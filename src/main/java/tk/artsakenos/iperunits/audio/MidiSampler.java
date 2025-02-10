/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tk.artsakenos.iperunits.audio;

import tk.artsakenos.iperunits.system.SuperTimer;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import java.util.Arrays;
import java.util.List;

/**
 * @author andre
 */
@SuppressWarnings("unused")
public class MidiSampler {

    private static MidiChannel[] channels;
    private static final int INSTRUMENT = 0; // 0 is a piano, 9 is percussion, other channels are for other instruments
    private static final int VOLUME = 120; // between 0 et 127
    private static final List<String> notes = Arrays.asList("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B");

    static {
        try {
            Synthesizer synth = MidiSystem.getSynthesizer();
            synth.open();
            channels = synth.getChannels();
        } catch (MidiUnavailableException ex) {
            System.err.println("Errore nell'inizializzazione del IperUnits::MidiSample");
        }

    }

    /**
     * Plays the given note for the given duration
     *
     * @param notes    The Note or notes separated by space
     * @param duration The Duration
     */
    public static void play(String notes, int duration) {
        for (String note : notes.split(" ")) {
            // * start playing a note
            channels[INSTRUMENT].noteOn(id(note), VOLUME);
            // * wait
            SuperTimer.sleep(duration);
            // * stop playing a note
            channels[INSTRUMENT].noteOff(id(note));
        }
    }

    /**
     * Returns the MIDI id for a given note: eg. 4C -> 60
     *
     * @return The id
     */
    private static int id(String note) {
        int octave = Integer.parseInt(note.substring(0, 1));
        return notes.indexOf(note.substring(1)) + 12 * octave + 12;
    }
}
