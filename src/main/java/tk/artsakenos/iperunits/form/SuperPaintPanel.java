/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.form;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;

/**
 * Istanziare la classe e poi chiamare l'attachTo al componente desiderato, di
 * solito un JPanel. e.g., superPaint.attachTo(pnlMain);
 * <p>
 * A questo punto fare il bello e il brutto tempo dentro all'onPaint(), e.g.,
 * <p>
 * BufferedImage image = robot.createScreenCapture(screenRect); AffineTransform
 * at = new AffineTransform(); graphics2D.drawImage(image, at, null);
 * <p>
 * Chiamare il forceRepaint() se serve forzare il refresh.
 * <pre>
 * // Esempio per il disegno di un immagine. Usare gli helper per AffineTransform, etc.
 * graphics2D.drawImage(dashboardImage, AT_DOUBLE, null);
 * // Mettere un bordo rosso
 * graphics2D.setPaint(redPaint);
 * graphics2D.drawRect(0, 0, dashboardImage.getWidth() * (int) scale, dashboardImage.getHeight() * (int) scale);
 * </pre>
 *
 * @author Andrea 2015.01.01
 * @version 2019.01.01
 */
@SuppressWarnings("unused")
public abstract class SuperPaintPanel extends JPanel {

    private JComponent component;
    private final int margin = 5;

    private Graphics2D graphics2D = null;

    public static final AffineTransform AT_DOUBLE = getAffineTransform(2.0);
    public static final Paint PAINT_RED = new Color(255, 0, 0, 100); // Translucent RED

    public static final AffineTransform getAffineTransform(double scale) {
        return AffineTransform.getScaleInstance(scale, scale);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        graphics2D = (Graphics2D) graphics;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        onPaint(graphics2D);
    }

    public void attachTo(JComponent component) {
        component.add(this);
        this.component = component;
        setBounds(margin, margin, component.getBounds().width - margin * 2, component.getBounds().height - margin * 2);
        component.addComponentListener(new ResizeListener());
    }

    public abstract void onPaint(Graphics2D graphics2D);

    public class ResizeListener extends ComponentAdapter {

        @Override
        public void componentResized(ComponentEvent e) {
            setBounds(margin, margin, component.getBounds().width - margin * 2, component.getBounds().height - margin * 2);
        }
    }

    public void forceRepaint() {
        super.repaint();
        if (graphics2D != null) {
            super.paintComponent(graphics2D);
        }
    }

}
