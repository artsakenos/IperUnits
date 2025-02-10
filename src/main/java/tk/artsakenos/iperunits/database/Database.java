/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * It is a simple, versatile Database Wrapper. Lets you to handle ResultSets and
 * Exceptions.
 * <p>
 * Connection and Query example:
 * <pre>
 * Database db = new Database(DatabaseConnector.DRV_POSTGRESQL, "jdbc:postgresql://172.16.19.199:5432/dmoz", "user", "password");
 * db.execute("DELETE FROM table");
 * ResultSet select = ds.select("SELECT * FROM table ORDER BY id");
 * while (select.next()) {
 * int idInt = select.getInt("id");
 * String id String = select.getString("pageid");
 * ...
 * </pre>
 *
 * @author <p style="color:red; font-family:verdana;">
 * <a href="mailto:a.addis@gmail.com">Andrea Addis</a> - &copy;<a
 * href="http://infodev.wordpress.com">Information Devices </a> </p>
 * @version 2005.07.12
 */
@SuppressWarnings("unused")
public class Database {

    public static final String DRV_ODBC = "sun.jdbc.odbc.JdbcOdbcDriver";
    public static final String DRV_HSQLDB = "org.hsqldb.jdbcDriver";
    public static final String DRV_POSTGRESQL = "org.postgresql.Driver";
    public static final String DRV_MYSQL = "com.mysql.jdbc.Driver";
    public static final String DRV_SQLITE = "org.sqlite.JDBC";
    public static final String DRV_H2 = "org.h2.Driver";

    // -------------------------------------------------------------------------
    private final String driver;
    private final String url;
    private final String user;
    private final String password;
    private Connection connection = null;

    public Database(String driver, String url, String user, String password) {
        this.driver = driver;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    //--------------------------------------------------------------------------
    //------------------ Connection
    //--------------------------------------------------------------------------

    /**
     * Connects to a database. DSN URL examples:
     * <ul>
     * <li> jdbc:postgresql://172.16.19.199:5432/dmoz </li>
     * <li> jdbc:sqlite:resources/test.db </li>
     * </ul>
     *
     * @param driver   the driver (See DRV_*)
     * @param url      The Dsn Url
     * @param user     user
     * @param password password
     * @throws SQLException a SQLexception
     */
    private void connect(String driver, String url, String user, String password) throws SQLException {
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            System.err.println("connect(...): Could not find PostgreSQL driver: " + e.getLocalizedMessage());
        }
    }

    /**
     * Disconnects from the database server
     *
     * @throws SQLException a SQLException
     */
    public void disconnect() throws SQLException {
        getConnection().close();
    }

    /**
     * @return the connection
     */
    public Connection getConnection() {
        if (connection == null) {
            try {
                connect(driver, url, user, password);
            } catch (SQLException ex) {
                Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return connection;
    }

    //--------------------------------------------------------------------------
    //------------------ Querues
    //--------------------------------------------------------------------------

    /**
     * Executes a DML Selection query, eg.,
     * <pre>
     * while (select.next()) {
     * String dev_number = select.getString("device_id");
     * int avg_time = (int) select.getDouble("avg_time");
     * }
     * </pre>
     *
     * @param query The query
     * @return The corresponding ResultSet
     * @throws SQLException a SQLException
     */
    public ResultSet select(String query) throws SQLException {
        Statement st = getConnection().createStatement();
        ResultSet rs = st.executeQuery(query);
        return rs;
    }

    /**
     * Executes a DML/DDL query
     *
     * @param query The query
     * @return the number of rows changed
     * @throws SQLException a SQLException
     */
    public int execute(String query) throws SQLException {
        Statement st = getConnection().createStatement();
        return st.executeUpdate(query);
    }

    /**
     * Ti dona un PreparedStatement da usare così:
     * <pre>
     * sql = "INSERT INTO " + table_name + " (test_integer, test_string) VALUES (?, ?)";
     * PreparedStatement prepStmt = database.getPreparedStatement(sql);
     * prepStmt.setInt(1, 123);
     * prepStmt.setString(2, "123");
     * prepStmt.executeUpdate();
     * </pre>
     *
     * @param sql la query
     * @return a Prepared Statement
     * @throws SQLException a SQLException
     */
    public PreparedStatement getPreparedStatement(String sql) throws SQLException {
        return getConnection().prepareStatement(sql);
    }

    //--------------------------------------------------------------------------
    //------------------ Table Information and Model
    //--------------------------------------------------------------------------

    /**
     * Returns the column names of the table corresponding to the query
     *
     * @param query The query
     * @return the column names
     * @throws SQLException a SQLException
     */
    @SuppressWarnings("ConvertToTryWithResources")
    public String[] getTableColumnNames(String query) throws SQLException {
        Statement st = getConnection().createStatement();
        ResultSet rs = st.executeQuery(query);
        ResultSetMetaData rsMeta = rs.getMetaData();
        String[] colNames = new String[rsMeta.getColumnCount()];

        for (int i = 0; i < rsMeta.getColumnCount(); i++) {
            colNames[i] = rsMeta.getColumnName(i + 1);
        }

        rs.close();
        st.close();
        return colNames;
    }

    @SuppressWarnings("ConvertToTryWithResources")
    public String[] getTableColumnClasses(String query) throws SQLException {
        Statement st = getConnection().createStatement();
        ResultSet rs = st.executeQuery(query);
        ResultSetMetaData rsMeta = rs.getMetaData();
        String[] colClasses = new String[rsMeta.getColumnCount()];

        for (int i = 0; i < rsMeta.getColumnCount(); i++) {
            colClasses[i] = rsMeta.getColumnClassName(i + 1);
        }

        rs.close();
        st.close();
        return colClasses;
    }

    @SuppressWarnings("ConvertToTryWithResources")
    public Object[][] getTableContent(String query) throws SQLException {
        String[] colNames = getTableColumnNames(query);
        String[] colClasses = getTableColumnClasses(query);

        Statement st = getConnection().createStatement();
        ResultSet rs = st.executeQuery(query);

        ArrayList<Object[]> rowList = new ArrayList<>();
        while (rs.next()) {
            ArrayList<Object> cellList = new ArrayList<>();
            for (int i = 0; i < colClasses.length; i++) {
                Object cellValue;

                if (colClasses[i].equals(String.class.getName())) {
                    cellValue = rs.getString(colNames[i]);
                } else if (colClasses[i].equals(Integer.class.getName())) {
                    cellValue = rs.getInt(colNames[i]);
                } else if (colClasses[i].equals(Double.class.getName())) {
                    cellValue = rs.getDouble(colNames[i]);
                } else if (colClasses[i].equals(Date.class.getName())) {
                    cellValue = rs.getDate(colNames[i]);
                } else if (colClasses[i].equals(Float.class.getName())) {
                    cellValue = rs.getFloat(colNames[i]);
                } else {
                    // System.out.println("PSQL Read Warning: Unknown type encountered, attempting to read as String");
                    cellValue = rs.getString(colNames[i]);
                }
                cellList.add(cellValue);
            }
            Object[] cells = cellList.toArray();
            rowList.add(cells);
        }

        rs.close();
        st.close();

        Object[][] content = new Object[rowList.size()][];
        for (int i = 0; i < content.length; i++) {
            content[i] = rowList.get(i);
        }

        return content;
    }

    /**
     * Returns the TableModel corresponding to the given query
     *
     * @param query The Query
     * @return The TableModel
     */
    public TableModel getTableModel(String query) {
        return new TableModel(this, query);
    }

}
