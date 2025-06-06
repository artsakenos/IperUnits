/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.form;

import tk.artsakenos.iperunits.system.Mouse;
import tk.artsakenos.iperunits.system.SuperTimer;

import javax.swing.*;

/**
 * Questa classe consente una volta nascosto un frame, di farlo riapparire una
 * volta che si verifica una data condizione. Ad esempio: Il mouse al punto 0,0.
 * <p>
 * Per utilizzarlo è sufficiente istanziare la classe: FormDisappearer fd = new
 * FormDisappearer(this); E quando si vuole nascondere fare fd.hide(); Lui farà
 * tutto il resto.
 *
 * @author Andrea Addis
 */
public class FormDisappearer {

    private final JFrame form;
    private final SuperTimer st = new SuperTimer(this) {

        @Override
        public void doTimer() {
            if (Mouse.isMouse00()) {
                form.setVisible(true);
                stop();
            }
        }
    };

    public FormDisappearer(JFrame form) {
        this.form = form;
    }

    public void hide() {
        form.setVisible(false);
        st.start(1000);
    }
}
