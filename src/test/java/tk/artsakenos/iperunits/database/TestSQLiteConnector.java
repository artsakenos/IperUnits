package tk.artsakenos.iperunits.database;

import lombok.extern.java.Log;
import org.junit.jupiter.api.*;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({"ResultOfMethodCallIgnored", "SqlSourceToSinkFlow"})
@Log
@Disabled
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestSQLiteConnector {

    private static final String TEST_DB = "test_database.db";
    private static SQLiteConnector db;

    @BeforeAll
    static void setUp() {
        // Rimuove il database di test se esiste per partire da uno stato pulito
        File dbFile = new File(TEST_DB);
        if (dbFile.exists()) {
            dbFile.delete();
        }

        // Ora questa sintassi è corretta grazie alla modifica in SQLiteConnector
        db = SQLiteConnector.builder(TEST_DB).build();
    }

    @AfterAll
    static void tearDown() {
        if (db != null) {
            db.close();
        }
        // Pulisce il file del database dopo i test
        File dbFile = new File(TEST_DB);
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    @Test
    @Order(1)
    void testCreateTable() throws SQLException {
        String createTableSQL = """
                    CREATE TABLE IF NOT EXISTS users (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        age INTEGER
                    )
                """;

        db.update(createTableSQL);
        log.info("Tabella 'users' creata.");
        assertTrue(db.tableExists("users"), "La tabella 'users' dovrebbe esistere dopo la creazione.");
    }

    @Test
    @Order(2)
    void testInsertData() {
        assertDoesNotThrow(() -> {
            String insertSQL = "INSERT INTO users (name, age) VALUES (?, ?)";
            int result = db.update(insertSQL, "John Doe", 30);
            assertEquals(1, result, "Dovrebbe essere stata inserita una riga.");
        });

        assertDoesNotThrow(() -> db.executeTransaction(connection -> {
            try (var stmt = connection.prepareStatement("INSERT INTO users (name, age) VALUES (?, ?)")) {
                stmt.setString(1, "Jane Smith");
                stmt.setInt(2, 25);
                stmt.addBatch();

                stmt.setString(1, "Bob Johnson");
                stmt.setInt(2, 35);
                stmt.addBatch();

                stmt.executeBatch();
            }
        }), "La transazione di inserimento non dovrebbe lanciare eccezioni.");
    }

    @Test
    @Order(3)
    void testQueryData() throws SQLException {
        List<Map<String, Object>> allUsers = db.query("SELECT * FROM users", SQLiteConnector::resultSetToList);
        assertEquals(3, allUsers.size(), "Dovrebbero esserci 3 utenti nel database.");

        List<Map<String, Object>> youngUsers = db.query(
                "SELECT * FROM users WHERE age < ?", SQLiteConnector::resultSetToList, 30);
        assertEquals(1, youngUsers.size(), "Dovrebbe esserci un solo utente con meno di 30 anni.");
        assertEquals("Jane Smith", youngUsers.get(0).get("name"));

        List<Map<String, Object>> stats = db.query(
                "SELECT COUNT(*) as total_users, AVG(age) as average_age FROM users", SQLiteConnector::resultSetToList);
        Map<String, Object> statsRow = stats.get(0);
        log.info("Statistiche: " + statsRow);

        assertEquals(3, statsRow.get("total_users"));
        assertEquals(30.0, (Double) statsRow.get("average_age"));
    }

    @Test
    @Order(4)
    void testUpdateData() throws SQLException {
        int updateResult = db.update("UPDATE users SET age = age + 1 WHERE name = ?", "John Doe");
        assertEquals(1, updateResult, "Dovrebbe essere stata aggiornata una riga.");

        List<Map<String, Object>> updated = db.query(
                "SELECT age FROM users WHERE name = ?", SQLiteConnector::resultSetToList, "John Doe");
        assertEquals(31, updated.get(0).get("age"));
    }

    @Test
    @Order(5)
    void testDeleteData() throws SQLException {
        int deleteResult = db.update("DELETE FROM users WHERE name = ?", "Bob Johnson");
        assertEquals(1, deleteResult, "Dovrebbe essere stata cancellata una riga.");

        List<Map<String, Object>> remaining = db.query("SELECT * FROM users", SQLiteConnector::resultSetToList);
        assertEquals(2, remaining.size(), "Dovrebbero rimanere due utenti.");
    }

    @Test
    @Order(6)
    void testReadOnlyConnection() {
        // Usa il builder per creare una connessione in sola lettura
        try (SQLiteConnector readOnlyDb = SQLiteConnector.builder(TEST_DB).readOnly(true).build()) {

            List<Map<String, Object>> results = assertDoesNotThrow(
                    () -> readOnlyDb.query("SELECT * FROM users", SQLiteConnector::resultSetToList)
            );
            assertFalse(results.isEmpty(), "I risultati non dovrebbero essere vuoti in modalità sola lettura.");

            assertThrows(SQLException.class, () -> readOnlyDb.update("INSERT INTO users (name, age) VALUES (?, ?)", "Test User", 25), "Un'operazione di scrittura su una connessione read-only dovrebbe lanciare una SQLException.");
        }
    }

    @Test
    @Order(7)
    void testTransactionRollback() throws SQLException {
        Number initialCount = (Number) db.query(
                "SELECT COUNT(*) as count FROM users", SQLiteConnector::resultSetToList).get(0).get("count");

        assertThrows(SQLException.class, () -> db.executeTransaction(connection -> {
            try (var stmt = connection.prepareStatement("INSERT INTO users (name, age) VALUES (?, ?)")) {
                stmt.setString(1, "Will Rollback");
                stmt.setInt(2, 50);
                stmt.executeUpdate();
            }
            try (var stmt = connection.prepareStatement("INSERT INTO invalid_table (name) VALUES (?)")) {
                stmt.setString(1, "Should Fail");
                stmt.executeUpdate();
            }
        }), "Una transazione con un'istruzione SQL non valida dovrebbe lanciare una SQLException.");

        Number finalCount = (Number) db.query(
                "SELECT COUNT(*) as count FROM users", SQLiteConnector::resultSetToList).get(0).get("count");

        assertEquals(initialCount.intValue(), finalCount.intValue(), "Il conteggio degli utenti dovrebbe essere invariato dopo un rollback.");
    }
}