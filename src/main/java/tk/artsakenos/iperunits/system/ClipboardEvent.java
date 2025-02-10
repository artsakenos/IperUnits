/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.system;

/**
 * Ogni 2 secondi controlla che il clipboard non sia cambiato e gestisce un
 * evento.
 *
 * @author Andrea
 * @version Feb 29, 2020
 */
@SuppressWarnings("unused")
public abstract class ClipboardEvent {

    private String lastClipboard;
    private boolean isRunning = false;

    private final SuperTimer timer = new SuperTimer(this) {
        @Override
        public void doTimer() {
            String newClipboard = Environment.getClipboard();
            if (!lastClipboard.equals(newClipboard)) {
                lastClipboard = newClipboard;
                onNewClipboard(newClipboard);
                Environment.Beep();
            }

            if (Mouse.isMouse00()) {
                Environment.Beep();
                stop();
                onStop();
            }
        }
    };

    public void start() {
        lastClipboard = Environment.getClipboard();
        timer.start(2000);
        isRunning = true;
    }

    public void stop() {
        lastClipboard = Environment.getClipboard();
        timer.stop();
        isRunning = false;
    }

    public abstract void onNewClipboard(String clipboard);

    public abstract void onStop();

    public boolean isRunning() {
        return isRunning;
    }
}
