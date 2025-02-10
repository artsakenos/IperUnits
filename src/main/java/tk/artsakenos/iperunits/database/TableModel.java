/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.artsakenos.iperunits.database;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;

/**
 * Build a TableModel of the selected table
 *
 * @author <p style="color:red; font-family:verdana;">
 * <a href="mailto:a.addis@gmail.com">Andrea Addis</a> - &copy;<a
 * href="http://infodev.wordpress.com">Information Devices </a> </p>
 * @version 2005.07.12
 */
public class TableModel extends AbstractTableModel {

    private Object[][] content;
    private String[] colNames;

    public TableModel(Database db, String query) {
        try {
            content = db.getTableContent(query);
            colNames = db.getTableColumnNames(query);
        } catch (SQLException ex) {
            Logger.getLogger(TableModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public int getColumnCount() {
        return colNames.length;
    }

    @Override
    public int getRowCount() {
        return content.length;
    }

    @Override
    public Object getValueAt(int arg0, int arg1) {
        return content[arg0][arg1];
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    @Override
    public void setValueAt(Object aValue, int row, int col) {
        content[row][col] = aValue;
    }

    @Override
    public String getColumnName(int col) {
        return colNames[col];
    }

}
