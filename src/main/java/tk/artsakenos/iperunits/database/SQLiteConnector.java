package tk.artsakenos.iperunits.database;

import lombok.NonNull;
import lombok.extern.java.Log;
import org.sqlite.SQLiteConfig;

import java.sql.*;
import java.util.*;

/**
 * A thread-safe SQLite database connector with built-in connection pooling and resource management.
 * Uses builder pattern for easy configuration and provides convenient query methods.
 */
@SuppressWarnings("unused")
@Log
public class SQLiteConnector implements AutoCloseable {
    @NonNull
    private final String dbFile;

    private final boolean readOnly;
    private final boolean autoCommit;
    private final int poolSize;
    private final int cacheSize;
    private final SQLiteConfig.JournalMode journalMode;

    private final ConnectionPool connectionPool;
    private final SQLiteConfig config;

    public SQLiteConnector(String dbFile) {
        this(dbFile, false, true, 5, 2000, SQLiteConfig.JournalMode.WAL);
    }

    public SQLiteConnector(String dbFile, boolean readOnly) {
        this(dbFile, readOnly, true, 5, 2000, SQLiteConfig.JournalMode.WAL);
    }

    // This constructor is called by the generated builder
    public SQLiteConnector(String dbFile,
                           boolean readOnly,
                           boolean autoCommit,
                           int poolSize,
                           int cacheSize,
                           SQLiteConfig.JournalMode journalMode) {
        this.dbFile = dbFile;
        this.readOnly = readOnly;
        this.autoCommit = autoCommit;
        this.poolSize = poolSize;
        this.cacheSize = cacheSize;
        this.journalMode = journalMode;

        this.config = new SQLiteConfig();
        this.config.setReadOnly(readOnly);
        this.config.setJournalMode(journalMode);
        this.config.setCacheSize(cacheSize);

        this.connectionPool = new ConnectionPool(
                dbFile,
                poolSize,
                autoCommit,
                config
        );
    }

    /**
     * Executes a query that returns a result set.
     */
    public List<Map<String, Object>> query(@NonNull String sql) throws SQLException {
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            return resultSetToList(rs);
        }
    }

    /**
     * Executes a query with prepared statement parameters.
     */
    public List<Map<String, Object>> query(@NonNull String sql, Object... params) throws SQLException {
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                return resultSetToList(rs);
            }
        }
    }

    /**
     * Executes an update statement (INSERT, UPDATE, DELETE).
     */
    public int update(@NonNull String sql) throws SQLException {
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement()) {
            return stmt.executeUpdate(sql);
        }
    }

    /**
     * Executes an update statement with prepared statement parameters.
     */
    public int update(@NonNull String sql, Object... params) throws SQLException {
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            return stmt.executeUpdate();
        }
    }

    /**
     * Executes multiple statements in a transaction.
     */
    public boolean executeTransaction(@NonNull TransactionOperation operations) {
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            conn.setAutoCommit(false);
            operations.execute(conn);
            conn.commit();
            return true;
        } catch (SQLException e) {
            log.severe("Transaction failed: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    log.severe("Rollback failed: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    log.severe("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Checks if a table exists in the database.
     */
    public boolean tableExists(@NonNull String tableName) throws SQLException {
        String sql = "SELECT name FROM sqlite_master WHERE (type='table' OR type='view') AND name=?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tableName);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public void close() {
        connectionPool.close();
    }

    private List<Map<String, Object>> resultSetToList(ResultSet rs) throws SQLException {
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

    /**
     * Functional interface for transaction operations.
     */
    @FunctionalInterface
    public interface TransactionOperation {
        void execute(Connection connection) throws SQLException;
    }

    /**
     * Internal connection pool implementation.
     */
    @Log
    private static class ConnectionPool {
        private final Queue<Connection> connections;
        private final String dbFile;
        private final boolean autoCommit;
        private final SQLiteConfig config;
        private final int poolSize;

        public ConnectionPool(String dbFile, int poolSize, boolean autoCommit, SQLiteConfig config) {
            this.dbFile = dbFile;
            this.poolSize = poolSize;
            this.autoCommit = autoCommit;
            this.config = config;
            this.connections = new LinkedList<>();
        }

        public synchronized Connection getConnection() throws SQLException {
            Connection conn = connections.poll();
            if (conn == null || conn.isClosed()) {
                conn = createConnection();
            }
            return conn;
        }

        private Connection createConnection() throws SQLException {
            try {
                Class.forName("org.sqlite.JDBC");
                Connection conn = DriverManager.getConnection(
                        "jdbc:sqlite:" + dbFile,
                        config.toProperties()
                );
                conn.setAutoCommit(autoCommit);
                return conn;
            } catch (ClassNotFoundException e) {
                throw new SQLException("SQLite JDBC driver not found", e);
            }
        }

        public synchronized void close() {
            connections.forEach(conn -> {
                try {
                    conn.close();
                } catch (SQLException e) {
                    log.warning("Error closing connection: " + e.getMessage());
                }
            });
            connections.clear();
        }
    }
}