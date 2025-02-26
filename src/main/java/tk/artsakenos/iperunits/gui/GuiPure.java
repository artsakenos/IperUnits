package tk.artsakenos.iperunits.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings({"FieldCanBeLocal", "CallToPrintStackTrace", "Convert2Lambda"})
public class GuiPure extends JFrame {

    private JPanel pnlTest01;
    private JPanel pnlTest02;
    private JPanel pnlTest03;
    private JButton btnPanel1;
    private JTextField txtPanel1;
    private JTextArea txtAreaPanel2;
    private JButton btnPanel2_1;
    private JButton btnPanel2_2;
    private JButton btnPanel2_3;
    private JButton btnPanel3_1;
    private JButton btnPanel3_2;
    private JTextField txtPanel3_1;
    private JTextField txtPanel3_2;

    public GuiPure() {
        // Configurazione della finestra principale
        setTitle("GUI con pannelli verticali");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Applicazione di un look and feel gradevole
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Configurazione del layout principale
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Creazione dei tre pannelli
        createPanel1();
        createPanel2();
        createPanel3();

        // Aggiunta dei pannelli al pannello principale
        mainPanel.add(pnlTest01);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spaziatura
        mainPanel.add(pnlTest02);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spaziatura
        mainPanel.add(pnlTest03);

        // Aggiunta del pannello principale alla finestra
        add(mainPanel, BorderLayout.CENTER);
    }

    private void createPanel1() {
        pnlTest01 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTest01.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Panel 1"));

        btnPanel1 = new JButton("Button 1");
        txtPanel1 = new JTextField(20);
        txtPanel1.setToolTipText("Inserisci il testo qui");

        // Aggiunta componenti al pannello
        pnlTest01.add(btnPanel1);
        pnlTest01.add(txtPanel1);

        // Gestione evento click
        btnPanel1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleButtonClick("Button 1 in Panel 1 clicked");
            }
        });
    }

    private void createPanel2() {
        pnlTest02 = new JPanel();
        pnlTest02.setLayout(new BorderLayout(0, 5));
        pnlTest02.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Panel 2"));

        // TextArea
        txtAreaPanel2 = new JTextArea(3, 20);
        txtAreaPanel2.setLineWrap(true);
        txtAreaPanel2.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(txtAreaPanel2);

        // Pannello per i 3 bottoni
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel2_1 = new JButton("Button 2-1");
        btnPanel2_2 = new JButton("Button 2-2");
        btnPanel2_3 = new JButton("Button 2-3");

        // Aggiunta bottoni al pannello dei bottoni
        buttonPanel.add(btnPanel2_1);
        buttonPanel.add(btnPanel2_2);
        buttonPanel.add(btnPanel2_3);

        // Aggiunta componenti al pannello principale
        pnlTest02.add(scrollPane, BorderLayout.CENTER);
        pnlTest02.add(buttonPanel, BorderLayout.SOUTH);

        // Gestione eventi click
        btnPanel2_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleButtonClick("Button 2-1 in Panel 2 clicked");
            }
        });

        btnPanel2_2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleButtonClick("Button 2-2 in Panel 2 clicked");
            }
        });

        btnPanel2_3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleButtonClick("Button 2-3 in Panel 2 clicked");
            }
        });
    }

    private void createPanel3() {
        pnlTest03 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTest03.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Panel 3"));

        btnPanel3_1 = new JButton("Button 3-1");
        btnPanel3_2 = new JButton("Button 3-2");
        txtPanel3_1 = new JTextField(10);
        txtPanel3_2 = new JTextField(10);

        // Aggiunta componenti al pannello
        pnlTest03.add(btnPanel3_1);
        pnlTest03.add(txtPanel3_1);
        pnlTest03.add(btnPanel3_2);
        pnlTest03.add(txtPanel3_2);

        // Gestione eventi click
        btnPanel3_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleButtonClick("Button 3-1 in Panel 3 clicked");
            }
        });

        btnPanel3_2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleButtonClick("Button 3-2 in Panel 3 clicked");
            }
        });
    }

    // Metodo per gestire i click dei bottoni
    private void handleButtonClick(String message) {
        JOptionPane.showMessageDialog(this, message, "Button Click", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        // Avvio dell'applicazione nel thread di eventi Swing
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GuiPure gui = new GuiPure();
                gui.setVisible(true);
            }
        });
    }
}