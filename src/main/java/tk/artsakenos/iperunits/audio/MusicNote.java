/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.audio;

/**
 * @author Andrea
 * @version Sep 12, 2020
 */
@SuppressWarnings("unused")
public enum MusicNote {

    C(new int[]{16, 32, 65, 130, 261, 523, 1046, 2093, 4186, 8372}),
    D(new int[]{18, 36, 73, 146, 293, 587, 1174, 2349, 4698, 9397}),
    E(new int[]{20, 41, 82, 164, 329, 659, 1318, 2637, 5274, 10548}),
    F(new int[]{21, 43, 87, 174, 349, 698, 1396, 2793, 5587, 11175}),
    G(new int[]{24, 48, 97, 195, 391, 783, 1567, 3135, 6271, 12543}),
    A(new int[]{27, 55, 110, 220, 440, 880, 1760, 3520, 7040, 14080}),
    B(new int[]{30, 61, 123, 246, 493, 987, 1975, 3951, 7902, 15804}),
    PAUSE(new int[]{});

    private final int[] analogFrequencies;
    public long duration = 200; // Note time unit
    public int octave = 5;  // Octave di riferimento

    MusicNote(int[] frequencies) {
        this.analogFrequencies = frequencies;
    }

    MusicNote(int[] frequencies, int octave, long duration) {
        this.analogFrequencies = frequencies;
        this.duration = duration;
        this.octave = octave;
    }

    public int analogFrequency(int octave) {
        return analogFrequencies[octave];
    }

}
