package tk.artsakenos.iperunits.database;

import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.sqlite.SQLiteConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Un connettore thread-safe per database SQLite che semplifica l'esecuzione di query
 * e la gestione delle transazioni. Utilizza il pattern Builder per una configurazione
 * flessibile e gestisce automaticamente le risorse JDBC.
 */
@Log
@Builder(toBuilder = true) // 'toBuilder = true' è una buona pratica se vuoi clonare e modificare l'oggetto
public class SQLiteConnector implements AutoCloseable {

    @NonNull
    private final String dbFile;

    @Builder.Default
    private final boolean readOnly = false;
    @Builder.Default
    private final boolean autoCommit = true;
    @Builder.Default
    private final int cacheSize = 2000;
    @Builder.Default
    private final SQLiteConfig.JournalMode journalMode = SQLiteConfig.JournalMode.WAL;

    // --- COSTRUTTORE E CAMPI DERIVATI RIMOSSI ---
    // Lombok ora genera un costruttore "all-args" privato per il builder,
    // eliminando il conflitto.

    /**
     * Crea un'istanza del builder, impostando il parametro obbligatorio dbFile.
     * Questo è il punto di ingresso preferito per creare un SQLiteConnector.
     *
     * @param dbFile Il percorso del file del database (obbligatorio).
     * @return Un'istanza di SQLiteConnectorBuilder per la configurazione fluente.
     */
    public static SQLiteConnectorBuilder builder(@NonNull String dbFile) {
        return new SQLiteConnectorBuilder().dbFile(dbFile);
    }

    /**
     * Crea una nuova connessione al database.
     * La configurazione e l'URL vengono generati on-demand.
     * @return Una nuova istanza di Connection.
     * @throws SQLException Se si verifica un errore durante la connessione.
     */
    private Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC di SQLite non trovato.", e);
        }

        // Crea URL e Properties al momento della necessità
        String dbUrl = "jdbc:sqlite:" + this.dbFile;
        SQLiteConfig config = new SQLiteConfig();
        config.setReadOnly(this.readOnly);
        config.setJournalMode(this.journalMode);
        config.setCacheSize(this.cacheSize);
        Properties configProperties = config.toProperties();

        Connection conn = DriverManager.getConnection(dbUrl, configProperties);
        conn.setAutoCommit(this.autoCommit);
        return conn;
    }

    // --- Tutti gli altri metodi (query, update, etc.) rimangono invariati ---

    /**
     * Esegue una query e processa il risultato tramite un'interfaccia funzionale.
     */
    public <T> T query(@NonNull String sql, @NonNull ResultSetProcessor<T> processor, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                return processor.process(rs);
            }
        }
    }

    /**
     * Esegue un'operazione di aggiornamento (INSERT, UPDATE, DELETE).
     */
    public int update(@NonNull String sql, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            return stmt.executeUpdate();
        }
    }

    /**
     * Esegue una serie di operazioni all'interno di una singola transazione.
     */
    public void executeTransaction(@NonNull TransactionOperation operation) throws SQLException {
        try (Connection conn = getConnection()) {
            try {
                conn.setAutoCommit(false);
                operation.execute(conn);
                conn.commit();
            } catch (SQLException e) {
                log.severe("Transazione fallita, avvio del rollback: " + e.getMessage());
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    log.severe("Rollback fallito: " + ex.getMessage());
                    e.addSuppressed(ex);
                }
                throw e;
            }
        }
    }

    /**
     * Controlla se una tabella o una vista esiste nel database.
     */
    public boolean tableExists(@NonNull String tableName) throws SQLException {
        String sql = "SELECT name FROM sqlite_master WHERE (type='table' OR type='view') AND name=?";
        return query(sql, ResultSet::next, tableName);
    }

    @Override
    public void close() {
        // Nessuna operazione necessaria
    }

    @FunctionalInterface
    public interface ResultSetProcessor<T> {
        T process(ResultSet rs) throws SQLException;
    }

    @FunctionalInterface
    public interface TransactionOperation {
        void execute(Connection connection) throws SQLException;
    }

    public static List<Map<String, Object>> resultSetToList(ResultSet rs) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();
        while (rs.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                row.put(meta.getColumnName(i), rs.getObject(i));
            }
            results.add(row);
        }
        return results;
    }
}