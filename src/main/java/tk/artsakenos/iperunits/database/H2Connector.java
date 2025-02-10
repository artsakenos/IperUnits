/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.database;

/**
 * Un H2 Wrapper. Per aprire la console:
 * <p>
 * java -cp myFatJar.jar h2console org.h2.tools.GUIConsole
 * <p>
 * oppure org.h2.tools.GUIConsole.main(new String[]{});
 *
 * @author Andrea
 */
public class H2Connector extends Database {

    /**
     * The eventual path and the dbName, e.g., ./myfolder/mydbname<p>
     * It will create the necessary folder with the database files.
     *
     * @param databaseName The name of the Database, e.g., DMOZ
     */
    public H2Connector(String databaseName) {
        super(DRV_H2, "jdbc:h2:" + databaseName, "sa", "");
    }

}
