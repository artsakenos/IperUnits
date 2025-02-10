package tk.artsakenos.iperunits.initors;

import lombok.extern.java.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

@SuppressWarnings("unused")
@Log
public class SuperProperties {

    private static final String DEFAULT_FILENAME = "SuperProperties.properties";
    private final String path;
    private final Properties properties;

    public SuperProperties(String path) {
        this.path = path != null ? path : DEFAULT_FILENAME;
        this.properties = new Properties();
        loadProperties();
    }

    /**
     * Carica le proprietà dal file. Se il file non esiste, ne crea uno vuoto.
     */
    private void loadProperties() {
        File file = new File(path);
        if (file.exists()) {
            try (FileInputStream inStream = new FileInputStream(path)) {
                properties.load(inStream);
            } catch (IOException e) {
                log.warning("Errore nel caricamento del file di proprietà: " + e.getMessage());
            }
            // log.info("Properties Loaded: " + properties.keySet());
        } else {
            save();
        }

        // Carica le proprietà da parametri di avvio Java (es. -Dpropertykey=propertyvalue)
        // properties.putAll(System.getProperties());
        // log.info("Properties Added from System: " + properties.keySet());

        // Carica le proprietà dalle variabili di ambiente
        // System.getenv().forEach(properties::putIfAbsent);
        // log.info("Properties Added from Environment: " + properties.keySet());
    }

    /**
     * Salva le proprietà attuali nel file.
     *
     * @return true se il salvataggio è avvenuto con successo, false altrimenti
     */
    public boolean save() {
        try (FileOutputStream outStream = new FileOutputStream(path)) {
            properties.store(outStream, "Handled by SuperProperties");
            return true;
        } catch (IOException e) {
            log.severe("Errore nel salvataggio del file di proprietà: " + e.getMessage());
            return false;
        }
    }

    /**
     * Imposta una proprietà e la salva immediatamente.
     *
     * @param key   la chiave della proprietà
     * @param value il valore da impostare
     * @return true se l'operazione è avvenuta con successo, false altrimenti
     */
    public boolean set(String key, String value) {
        properties.setProperty(key, value);
        return save();
    }

    /**
     * Restituisce il valore di una proprietà.
     *
     * @param key la chiave della proprietà
     * @return il valore della proprietà o null se non presente
     */
    public String get(String key) {
        return properties.getProperty(key);
    }

    /**
     * Restituisce il valore di una proprietà o imposta e restituisce il valore di default se non presente.
     *
     * @param key          la chiave della proprietà
     * @param defaultValue il valore di default da utilizzare se la chiave non è presente
     * @return il valore della proprietà o il valore di default
     */
    public String get(String key, String defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            set(key, defaultValue);
            return defaultValue;
        }
        return value;
    }

    public static String get(String path, String key, String defaultValue) {
        SuperProperties superProperties = new SuperProperties(path);
        return superProperties.get(key);
    }

    /**
     * Rimuove una proprietà.
     *
     * @param key la chiave della proprietà da rimuovere
     * @return true se la proprietà è stata rimossa con successo, false altrimenti
     */
    public boolean remove(String key) {
        if (properties.remove(key) != null) {
            return save();
        }
        return false;
    }

    /**
     * Verifica se una proprietà esiste.
     *
     * @param key la chiave da verificare
     * @return true se la proprietà esiste, false altrimenti
     */
    public boolean contains(String key) {
        return properties.containsKey(key);
    }
}