/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.types;

import java.util.LinkedList;

/**
 * Implementa una Queue, una Coda, ovvero una struttura FIFO. Immagina la coda
 * alla posta, un push entra alla fine e poi scorre per arrivare allo sportello.
 * Un pop prende quello allo sportello.
 * <p>
 * Consente i duplicati.
 * <p>
 * Utilizzare solo i metodi push e pop, altrimenti diventa una LinkedList e.g.,
 * Fifo{String}(3); push(1, ..2, ..3, ..4) -diventa- 2, 3, 4. pop == 2;
 *
 * @param <E>
 * @author Andrea
 */
@SuppressWarnings("unused")
public class Fifo<E> extends LinkedList<E> {

    // private LinkedList<E> heap = new LinkedList<E>();
    private int size = 0;

    public Fifo(int size) {
        this.size = size;
    }

    /**
     * Aggiunge un elemento in coda all'heap
     *
     * @param item The Item
     */
    @Override
    public void push(E item) {
        this.add(item);
        if (this.size() > size) {
            this.removeFirst();
        }
    }

    /**
     * @param index L'indice: 0 il piu vecchio, N il piu recente
     * @return l'elemento di indice "index". null se qualcosa è andato storto
     * (index > size o < 0).
     */
    public E pop(int index) {
        if (index > size || index < 0) {
            return null;
        }
        return this.get(index);
    }

    /**
     * Restituisce l'elemento in testa, ovvero il più vecchio.
     *
     * @return l'elemento in testa
     */
    @Override
    public E pop() {
        return this.getFirst();
    }

    public int getSize() {
        return this.size();
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (E e : this) {
            output.append(e).append("#");
        }
        return "#" + output;
    }
}
