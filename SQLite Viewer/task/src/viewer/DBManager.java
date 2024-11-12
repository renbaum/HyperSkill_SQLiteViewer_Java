package viewer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBManager {

    Connection connection = null;
    Statement statement = null;
    List<String> tables;

    public DBManager() {
        tables = new java.util.ArrayList<>();
    }

    public boolean openDB(String fileName) {
        if (fileName.isEmpty()) {
            JOptionPane.showMessageDialog(new Frame(), "No such file!");
            return false;
        }
        Path path = Paths.get(fileName);
        if(!Files.exists(path)){
            JOptionPane.showMessageDialog(new Frame(), "No such file!");
            System.err.println("File does not exist");
            return false;
        }

        tables.clear();

        closeDB();
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s", fileName));
            //System.out.println("Connection to SQLite has been established.");
            // create table

            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT name FROM sqlite_master WHERE type ='table' AND name NOT LIKE 'sqlite_%'");
            while(resultSet.next()) {
                String str = resultSet.getString("name");
                tables.add(str);
            }
            resultSet.close();

        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load SQLite JDBC driver.");
            return false;
        } catch (
                SQLException e) {
            System.err.println("SQLite connection error.");
            return false;
        }
        return true;
    }

    public void closeDB() {
        try {
            if (connection != null) {
                if (statement != null) {
                    statement.close();
                    statement = null;
                }
                connection.close();
                connection = null;
            }
        }catch (SQLException e){
            System.err.println("Error while closing DB");
        }
    }

    String createQuery(String tableName) {
        return "SELECT * FROM " + tableName + ";";
    }

    public TableModel getColumnNamesAndData(String query) {
        TableModel tableModel = null;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Get metadata from the result set
            ResultSetMetaData metaData = rs.getMetaData();

            // Get the number of columns
            int columnCount = metaData.getColumnCount();
            Object[] columnNames = new Object[columnCount];
            //List<Object> columnNames = new ArrayList<>();
            Object[][] rows = new Object[columnCount][];
            //List<Object[]> rows = new ArrayList<>();


            // Retrieve column names
            System.out.println("Column names:");
            for (int i = 1; i <= columnCount; i++) {
                columnNames[i-1] = metaData.getColumnName(i);
            }


            // Retrieve data
            int index = 0;
            List<Object[]> rowsList = new ArrayList<>();
            while (rs.next()) {
                Object[] row = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i-1] = rs.getString(i);
                }
                rowsList.add(row);
            }
            Object[][] rowsArray = rowsList.toArray(new Object[rowsList.size()][]);
            //Object[] data = rows.toArray();
            //Object[] str = columnNames.toArray();
            //Object[][] data2 = Object[][] str;
            tableModel = new DefaultTableModel(rowsArray, columnNames);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(new Frame(), "Wrong query!");

            System.err.println(e.getMessage());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return tableModel;
    }
}
