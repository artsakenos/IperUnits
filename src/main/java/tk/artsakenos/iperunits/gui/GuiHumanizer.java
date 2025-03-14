package tk.artsakenos.iperunits.gui;

import tk.artsakenos.iperunits.form.FormGraphics;
import tk.artsakenos.iperunits.string.SuperString;
import tk.artsakenos.iperunits.system.Human;
import tk.artsakenos.iperunits.system.Mouse;
import tk.artsakenos.iperunits.system.SuperTimer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

@SuppressWarnings("FieldCanBeLocal")
public class GuiHumanizer extends JFrame {
    // Componenti del pannello 1
    private JPanel pnlCommand;
    private JComboBox<String> cmbCommands;
    private JButton btnAdd;
    private JToggleButton btnStartStop;

    // Componenti del pannello 2
    private JPanel pnlScript;
    private JTextArea edtScript;
    private JScrollPane scrollPane;

    // Componenti del pannello 3
    private JPanel pnlStatus;
    private JTextField txtStatus1;
    private JTextField txtStatus2;
    private JTextField txtStatus3;

    private final Human human = new Human() {
        @Override
        public void onCommandParsed(
                String command, String commandResult,
                int cLoopsProcessed, int cLineProcessing, int cLoopTotalLines) {
            command = SuperString.fixLength(command, 50);
            String status = "Loop: %d, Line: %d/%d: %s".formatted(
                    cLoopsProcessed, cLineProcessing, cLoopTotalLines, command
            );
            txtStatus2.setText(status);
        }
    };

    private final SuperTimer timer = new SuperTimer() {
        @Override
        public void doTimer() {
            Point mp = Mouse.getPoint();
            txtStatus1.setText("M: %04d-%04d".formatted(mp.x, mp.y));
        }
    };

    public GuiHumanizer() {
        // Configurazione della finestra principale
        setTitle("IperUnits Humanizer Revenge Deluxe 2000");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Inizializza i pannelli
        creaPannelloCommand();
        creaPannelloScript();
        creaPannelloStatus();

        // Aggiungi i pannelli alla finestra principale
        add(pnlCommand, BorderLayout.NORTH);
        add(pnlScript, BorderLayout.CENTER);
        add(pnlStatus, BorderLayout.SOUTH);

        // Configura l'azione del pulsante Add
        configuraPulsanti();

        FormGraphics.setFramePosition(this, 1, 20, 20);
        FormGraphics.setLookAndFeel("");
        timer.start(999);
    }

    private void creaPannelloCommand() {
        // Crea il pannello dei comandi
        pnlCommand = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlCommand.setBorder(BorderFactory.createTitledBorder("Comandi"));

        // Crea e configura la combobox
        String[] comandi = {
                "KEYBOARD TYPE TextToType",
                "MOUSE MOVETO x,y",
                "MOUSE MOVETOR x,y (Movimento Rapido)",
                "MOUSE MOVEBY x,y",
                "MOUSE CLICK 1L | DL 120,50 | 2C (Operazione 12D+Tasto LR)",
                "MOUSE WHEEL 5", "SYSTEM WAIT millisec",
                "SYSTEM WAITCHANGE x,y",
                "SYSTEM WAITCOLOR x,y,r,g,b",
                "SYSTEM EXECUTE c:\\command.com",
                "SYSTEM START callto:+39070123456",
                "SYSTEM BEEP freq",
                "SYSTEM OPENFOLDER c:\\Programmi\\",
                "SYSTEM OPENURL http://www.google.com",
                "# Puoi commentare iniziando la frase con #",
                "#STOP# Se il commento comincia così si ferma il ciclo.",
                "#STOP ONMOUSEMOVE# Si ferma quando si sposta il mouse.",
                "#STOP ONMOUSE00# Si ferma quando il mouse è a 00.",
                "#INCLUDE#batch.txt"};
        cmbCommands = new JComboBox<>(comandi);

        // Crea e configura il pulsante Add
        btnAdd = new JButton("Aggiungi");
        btnStartStop = new JToggleButton("Start");

        // Aggiungi i componenti al pannello
        pnlCommand.add(cmbCommands);
        pnlCommand.add(btnAdd);
        pnlCommand.add(btnStartStop);
    }

    private void creaPannelloScript() {
        // Crea il pannello degli script
        pnlScript = new JPanel(new BorderLayout());
        pnlScript.setBorder(BorderFactory.createTitledBorder("Script"));

        // Crea e configura la text area
        edtScript = new JTextArea();
        edtScript.setLineWrap(true);
        edtScript.setWrapStyleWord(true);
        edtScript.setText("""
                SYSTEM WAIT 4000
                MOUSE MOVETO 100, 100
                SYSTEM WAIT 4000
                MOUSE MOVETO 200, 200
                """);


        // Aggiungi scrollbar alla text area
        scrollPane = new JScrollPane(edtScript);

        // Aggiungi i componenti al pannello
        pnlScript.add(scrollPane, BorderLayout.CENTER);
    }

    private void creaPannelloStatus() {
        // Crea il pannello di stato
        pnlStatus = new JPanel(new GridLayout(1, 3, 5, 0));
        pnlStatus.setBorder(BorderFactory.createTitledBorder("Status"));

        // Crea e configura i campi di testo di stato
        txtStatus1 = new JTextField("Log 1");
        txtStatus1.setEditable(false);

        txtStatus2 = new JTextField("Log 2");
        txtStatus2.setEditable(false);

        txtStatus3 = new JTextField("Log 3");
        txtStatus3.setEditable(false);

        // Aggiungi i componenti al pannello
        pnlStatus.add(txtStatus1);
        pnlStatus.add(txtStatus2);
        pnlStatus.add(txtStatus3);
    }

    private void configuraPulsanti() {
        btnAdd.addActionListener(e -> {
            // Ottieni il comando selezionato
            String comandoSelezionato = (String) cmbCommands.getSelectedItem();

            // Aggiungi il comando alla text area
            edtScript.append(comandoSelezionato + "\n");

            // Aggiorna i log di stato
            txtStatus1.setText("Comando aggiunto: " + comandoSelezionato);
            txtStatus2.setText("Timestamp: " + System.currentTimeMillis());
            txtStatus3.setText("Totale comandi: " + contaLinee());
        });

        btnStartStop.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                btnStartStop.setText("Stop");
                // Qui puoi aggiungere il codice per l'azione di Start
                human.parseScript(edtScript.getText(), 1000);
                txtStatus1.setText("Status: Running");
            } else {
                btnStartStop.setText("Start");
                // Qui puoi aggiungere il codice per l'azione di Stop
                txtStatus3.setText("Status: Stopped");
                human.stopLoop();
            }
        });
    }

    private int contaLinee() {
        // Conta il numero di linee nella text area
        String testo = edtScript.getText();
        if (testo.isEmpty()) {
            return 0;
        }
        return testo.split("\n").length;
    }

    public static void start(boolean exitOnClose) {
        // Crea e visualizza l'interfaccia GUI
        SwingUtilities.invokeLater(() -> {
            GuiHumanizer gui = new GuiHumanizer();
            gui.setVisible(true);
            if (!exitOnClose) {
                gui.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            }
        });
    }

    public static void main(String[] args) {
        start(true);
    }
}