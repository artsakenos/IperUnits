/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.form;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * After initComponents(); put your code: <br>
 * JTextEnterAction action = new JTextEnterAction(edtMessage) { ...
 *
 * @author Andrea, 2015.04.23
 * @version 2019-02.28
 */
@SuppressWarnings("unused")
public abstract class JTextEnterAction {

    private final JTextField jText;

    public JTextEnterAction(JTextField jText) {
        this.jText = jText;
        jText.addActionListener(action);
    }

    private final Action action = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            on_enter(jText.getText());
        }
    };

    public abstract void on_enter(String text);

}
