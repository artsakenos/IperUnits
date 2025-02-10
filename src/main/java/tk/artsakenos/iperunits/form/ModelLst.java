/*
 * ModelLst.java
 *
 * Created on 3 luglio 2007, 11.04
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.form;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Si possono creare diversi AbstractModels, ed associare dopo
 * l'initComponents() alle loro liste:
 * <p>
 * ModelLst modelLstPressReview = new ModelLst();
 *
 * <pre> [...]
 * initComponents();
 * lstPressReview.setModel(modelLstPressReview);
 * modelLstPressReview.addItem("Articolo 01 - Macedoni in birmania attaccano unione sovietica");
 * [...] </pre>
 *
 * @author <p style="color:red; font-family:verdana;">
 * <a href="mailto:a.addis@gmail.com">Andrea Addis</a> - &copy;<a
 * href="http://infodev.wordpress.com">Information Devices </a> </p>
 * @version 2005.07.12
 */
@SuppressWarnings("unused")
public class ModelLst extends AbstractListModel {

    private ArrayList<String> items = new ArrayList<>();

    public void addItem(String item) {
        items.add(item);
    }

    public void removeItem(String item) {
        items.remove(item);
    }

    public void clear() {
        items.clear();
    }

    @Override
    public int getSize() {
        return items.size();
    }

    @Override
    public Object getElementAt(int index) {
        return items.get(index);
    }

    /**
     * Mostra l'array di elementi inseriti nel modello
     */
    @Override
    public String toString() {
        return getItems().toString();
    }

    public ArrayList<String> getItems() {
        return this.items;
    }

    public void setItems(ArrayList<String> items) {
        this.items = items;
    }

    /**
     * Crea un ModelCmb a partire da una stringa di linee
     *
     * @return Un ModelCmb a partire dalla stringa
     */
    public static ModelLst fromString(String model) {
        model = model.replaceAll("\r", "");
        ModelLst mcmb = new ModelLst();
        for (String line : model.split("\n")) {
            mcmb.addItem(line.trim());
        }
        return mcmb;
    }
}
