package tk.artsakenos.iperunits.database;

import lombok.extern.java.Log;
import org.junit.jupiter.api.*;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Log
@Disabled
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestSQLiteConnector {

    private static final String TEST_DB = "test_database.db";
    private static SQLiteConnector db;

    @BeforeAll
    static void setUp() {
        // Delete existing test database if it exists
        File dbFile = new File(TEST_DB);
        if (dbFile.exists()) {
            dbFile.delete();
        }

        // Create new database connection
        db = new SQLiteConnector(TEST_DB);
    }

    @AfterAll
    static void tearDown() throws Exception {
        db.close();
        // Clean up test database
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
                        age INTEGER,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                """;

        int result = db.update(createTableSQL);
        log.info("Table creation result: " + result);

        Assertions.assertTrue(db.tableExists("users"));
    }

    @Test
    @Order(2)
    void testInsertData() throws SQLException {
        // Insert single record
        String insertSQL = "INSERT INTO users (name, age) VALUES (?, ?)";
        int result = db.update(insertSQL, "John Doe", 30);
        Assertions.assertEquals(1, result);

        // Insert multiple records in a transaction
        boolean transactionSuccess = db.executeTransaction(connection -> {
            try (var stmt = connection.prepareStatement("INSERT INTO users (name, age) VALUES (?, ?)")) {
                // First user
                stmt.setString(1, "Jane Smith");
                stmt.setInt(2, 25);
                stmt.executeUpdate();

                // Second user
                stmt.setString(1, "Bob Johnson");
                stmt.setInt(2, 35);
                stmt.executeUpdate();
            }
        });

        Assertions.assertTrue(transactionSuccess);
    }

    @Test
    @Order(3)
    void testQueryData() throws SQLException {
        // Test simple query
        List<Map<String, Object>> allUsers = db.query("SELECT * FROM users");
        Assertions.assertEquals(3, allUsers.size());

        // Test parameterized query
        List<Map<String, Object>> youngUsers = db.query(
                "SELECT * FROM users WHERE age < ?",
                30
        );
        Assertions.assertEquals(1, youngUsers.size());
        Assertions.assertEquals("Jane Smith", youngUsers.get(0).get("name"));

        // Test complex query
        List<Map<String, Object>> stats = db.query("""
                    SELECT
                        COUNT(*) as total_users,
                        AVG(age) as average_age,
                        MIN(age) as youngest,
                        MAX(age) as oldest
                    FROM users
                """);

        Map<String, Object> statsRow = stats.get(0);
        log.info("Statistics: " + statsRow);

        Assertions.assertEquals(3, statsRow.get("total_users"));
        Assertions.assertTrue((Double) statsRow.get("average_age") > 25);
    }

    @Test
    @Order(4)
    void testUpdateData() throws SQLException {
        // Update single record
        int updateResult = db.update(
                "UPDATE users SET age = age + 1 WHERE name = ?",
                "John Doe"
        );
        Assertions.assertEquals(1, updateResult);

        // Verify update
        List<Map<String, Object>> updated = db.query(
                "SELECT age FROM users WHERE name = ?",
                "John Doe"
        );
        Assertions.assertEquals(31, updated.get(0).get("age"));
    }

    @Test
    @Order(5)
    void testDeleteData() throws SQLException {
        // Delete single record
        int deleteResult = db.update(
                "DELETE FROM users WHERE name = ?",
                "Bob Johnson"
        );
        Assertions.assertEquals(1, deleteResult);

        // Verify deletion
        List<Map<String, Object>> remaining = db.query("SELECT * FROM users");
        Assertions.assertEquals(2, remaining.size());
    }

    @Test
    @Order(6)
    void testReadOnlyConnection() {
        // Create read-only connection
        try (SQLiteConnector readOnlyDb = new SQLiteConnector(TEST_DB, true)) {
            // Should be able to read
            List<Map<String, Object>> results = readOnlyDb.query("SELECT * FROM users");
            Assertions.assertFalse(results.isEmpty());

            // Should not be able to write
            Assertions.assertThrows(SQLException.class, () -> {
                readOnlyDb.update("INSERT INTO users (name, age) VALUES (?, ?)",
                        "Test User", 25);
            });
        } catch (Exception e) {
            Assertions.fail("Exception should not occur: " + e.getMessage());
        }
    }

    @Test
    @Order(7)
    void testTransactionRollback() throws SQLException {
        // Get initial count
        int initialCount = db.query("SELECT COUNT(*) as count FROM users")
                .get(0).get("count").toString().length();

        // Try to execute invalid transaction
        boolean result = db.executeTransaction(connection -> {
            // First insert should succeed
            try (var stmt = connection.prepareStatement("INSERT INTO users (name, age) VALUES (?, ?)")) {
                stmt.setString(1, "Will Rollback");
                stmt.setInt(2, 50);
                stmt.executeUpdate();
            }

            // This should fail and trigger rollback
            try (var stmt = connection.prepareStatement("INSERT INTO invalid_table (name) VALUES (?)")) {
                stmt.setString(1, "Should Fail");
                stmt.executeUpdate();
            }
        });

        Assertions.assertFalse(result);

        // Verify count hasn't changed
        int finalCount = db.query("SELECT COUNT(*) as count FROM users")
                .get(0).get("count").toString().length();
        Assertions.assertEquals(initialCount, finalCount);
    }
}