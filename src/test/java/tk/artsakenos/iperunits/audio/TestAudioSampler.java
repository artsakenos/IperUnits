package tk.artsakenos.iperunits.audio;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static tk.artsakenos.iperunits.audio.MusicNote.*;

@Disabled
public class TestAudioSampler {

    @Test
    void test_JingleBells() {
        AudioSampler as = new AudioSampler();
        as.soundNotes(E, E, E, PAUSE, E, E, E, PAUSE, E, G, C, D, E, PAUSE, PAUSE);
        as.soundNotes(F, F, F, F, F, E, E, E, E, D, D, E, D, PAUSE, G);
        as.soundNotes(E, E, E, PAUSE, E, E, E, PAUSE, E, G, C, D, E, PAUSE, PAUSE);
        as.soundNotes(F, F, F, F, F, E, E, E, G, G, F, D, C, C);
    }

    @Test
    void test_Beam() {
        AudioSampler as = new AudioSampler();
        as.start();
        for (double i = 400; i < 4000; ) {
            i *= (160.0 / 159.0);
            as.putNote((int) i, 5);
        }
        as.stop();
    }

}
