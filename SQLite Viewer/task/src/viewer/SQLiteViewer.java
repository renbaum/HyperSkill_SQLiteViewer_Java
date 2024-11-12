package viewer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;

public class SQLiteViewer extends JFrame {

    DBManager dbManager = new DBManager();

    private JPanel controlPanel;
    private JLabel dbNameLabel;
    private JButton openButton;
    private JTextField txtdbFileName;

    private JLabel tableLabel;
    private JLabel queryLabel;
    private JTextField queryTextField;
    private JButton executeQueryButton;
    private JComboBox tablesComboBox;

    private JScrollPane tableDataPanel;
    private JTable table;

    private JPanel customPanel;

    public SQLiteViewer() {
        customPanel = new JPanel();
        customPanel.setLayout(new BoxLayout(customPanel, BoxLayout.Y_AXIS));
        setContentPane(customPanel);

        controlPanel = new JPanel();
        controlPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        dbNameLabel = new JLabel("Database: ");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.1;
        controlPanel.add(dbNameLabel, c);

        txtdbFileName = new JTextField();
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.8;

        txtdbFileName.setName("FileNameTextField");
        controlPanel.add(txtdbFileName, c);

        openButton = new JButton("Open");
        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 0.1;
        openButton.setName("OpenFileButton");
        controlPanel.add(openButton, c);

        tableLabel = new JLabel("Table: ");
        c.gridx = 0;
        c.gridy = 1;
        controlPanel.add(tableLabel, c);

        tablesComboBox = new JComboBox();
        c.gridx = 1;
        c.gridwidth = 2;
        c.gridy = 1;
        tablesComboBox.setName("TablesComboBox");
        controlPanel.add(tablesComboBox, c);

        queryLabel = new JLabel("Query: ");
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        controlPanel.add(queryLabel, c);

        queryTextField = new JTextField();
        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = 2;
        c.ipady = 100;
        queryTextField.setName("QueryTextArea");
        queryTextField.setEnabled(false);
        controlPanel.add(queryTextField, c);

        executeQueryButton = new JButton("Execute");
        c.gridx = 2;
        c.gridy = 3;
        c.gridwidth = 1;
        c.ipady = 0;
        executeQueryButton.setName("ExecuteQueryButton");
        controlPanel.add(executeQueryButton, c);
        executeQueryButton.setEnabled(false);

        customPanel.add(controlPanel);

        table = new JTable();
        table.setName("Table");
        table.setPreferredSize(new Dimension(670, 600));
        //table.setModel(new DefaultTableModel(new Object[0][0], new String[0]));
        tableDataPanel = new JScrollPane(table);
        //tableDataPanel.add(table);
        //tableDataPanel.setBackground(Color.RED);
        customPanel.add(tableDataPanel);


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setTitle("SQLite Viewer");
        setSize(700, 900);
        setResizable(false);
        setVisible(true);

        openButton.addActionListener(e -> {
            String fileName = txtdbFileName.getText();
            tablesComboBox.removeAllItems();
            if(dbManager.openDB(fileName)){
                for(String table : dbManager.tables){
                    tablesComboBox.addItem(table);
                }
                createQuery();
            }
        });

        tablesComboBox.addActionListener(e -> {
            createQuery();
        });

        executeQueryButton.addActionListener(e -> {
            String query = queryTextField.getText();
            TableModel t = dbManager.getColumnNamesAndData(query);
            table.setModel(t);


        });


    }

    public void createQuery(){
        String table = (String) tablesComboBox.getSelectedItem();
        String query = "";
        if(!(table == null) &&!table.isEmpty()) {
            query = dbManager.createQuery(table);
        }
        if(query.isEmpty()){
            executeQueryButton.setEnabled(false);
            queryTextField.setEnabled(false);

        }else{
            executeQueryButton.setEnabled(true);
            queryTextField.setEnabled(true);
        }
        queryTextField.setText(query);
    }
}
