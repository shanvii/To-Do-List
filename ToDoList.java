import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ToDoList extends JFrame implements ActionListener {
    private JTextField taskTextField;
    private JButton addButton;
    private JButton deleteButton;
    private JList<String> tasksJList;
    private DefaultListModel<String> listModel;
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;

    public ToDoList() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/todolist", "root", "");
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        listModel = new DefaultListModel<>();
        tasksJList = new JList<>(listModel);

        addButton = new JButton("Add Task");
        addButton.addActionListener(this);

        deleteButton = new JButton("Delete Task");
        deleteButton.addActionListener(this);

        try {
            resultSet = statement.executeQuery("SELECT * FROM tasks");
            while (resultSet.next()) {
                listModel.addElement(resultSet.getString("task"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JScrollPane tasksScrollPane = new JScrollPane(tasksJList);

        taskTextField = new JTextField();

        JPanel controlPanel = new JPanel();
        controlPanel.add(taskTextField);
        controlPanel.add(addButton);
        controlPanel.add(deleteButton);

        Container container = getContentPane();
        container.add(tasksScrollPane, BorderLayout.CENTER);
        container.add(controlPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            String task = taskTextField.getText().trim();
            if (!task.equals("")) {
                listModel.addElement(task);
                taskTextField.setText("");

                try {
                    statement.executeUpdate("INSERT INTO tasks(task) VALUES ('" + task + "')");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } else if (e.getSource() == deleteButton) {
            int selectedIndex = tasksJList.getSelectedIndex();
            if (selectedIndex != -1) {
                listModel.remove(selectedIndex);
                tasksJList.clearSelection();

                try {
                    statement.executeUpdate(
                            "DELETE FROM tasks WHERE task = '" + listModel.getElementAt(selectedIndex) + "'");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            new ToDoList();
        });
    }
}
