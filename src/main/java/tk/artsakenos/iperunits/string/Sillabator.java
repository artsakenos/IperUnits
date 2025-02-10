/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.string;

/**
 * @author wayl
 */
public class Sillabator {

    private final String[] VOCALI = "a e i o u".split(" ");
    private final String[] CONSONANTI = "b c d f g l m n p r s t v z".split(" ");

    private boolean cho(double probability) {
        return Math.random() < probability;
    }

    private int rnd(int max) {
        return (int) (Math.random() * max);
    }

    private String getRandomConsonante() {
        return CONSONANTI[rnd(CONSONANTI.length)];
    }

    private String getRandomVocale() {
        return VOCALI[rnd(VOCALI.length)];
    }

    private String getRandomLett() {
        return cho(0.3) ? getRandomConsonante() : getRandomVocale();
    }

    public String creaWord() {
        StringBuilder word = new StringBuilder();

        for (int i = 1; i < rnd(5) + 2; i++) {
            String ini = getRandomConsonante();
            String fin = getRandomVocale();
            ///-{ Aggiungiamo una lettera prima della sillaba?
            String pre = cho(0.05) ? getRandomLett() : "";
            String pos = cho(0.09) ? getRandomLett() : "";
            ///-{ Aggiungiamo una lettera dopo la sillaba?
            if (pre.equals(ini)) {
                pre = "";
            }
            if (pos.equals(fin)) {
                pos = "";
            }

            word.append(pre).append(ini).append(fin).append(pos);
        }
        ///-{ Aggiungiamo una finale vocalica a, o, i, e?
        // word += (Math.random() > 0.8) ? "o" : "";
        return word.toString();
    }

    public String creaFrase() {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < rnd(5) + 3; i++) {
            output.append(creaWord()).append(" ");
        }
        return output.toString();
    }

    @Override
    public String toString() {
        return creaFrase();
    }

}
