package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField fullNameField;
    private JComboBox<String> roleComboBox;

    public RegisterFrame() {
        setTitle("Register");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(6, 2));
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        JLabel fullNameLabel = new JLabel("Full Name:");
        JLabel roleLabel = new JLabel("Role:");
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        fullNameField = new JTextField();
        roleComboBox = new JComboBox<>(new String[]{"user"});
        JButton registerButton = new JButton("Register");
        JButton backButton = new JButton("Back to Login"); // Tombol kembali ke frame login

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(fullNameLabel);
        panel.add(fullNameField);
        panel.add(roleLabel);
        panel.add(roleComboBox);
        panel.add(new JLabel());
        panel.add(registerButton);
        panel.add(backButton); // Menambahkan tombol "Back" ke panel

        add(panel);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String fullName = fullNameField.getText();
                String role = (String) roleComboBox.getSelectedItem();
                if (!username.isEmpty() && !password.isEmpty() && !fullName.isEmpty()) {
                    if (registerUser(username, password, fullName, role)) {
                        JOptionPane.showMessageDialog(RegisterFrame.this, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dispose(); // Tutup frame register setelah berhasil registrasi
                        showLoginForm(); // Tampilkan kembali frame login
                    } else {
                        JOptionPane.showMessageDialog(RegisterFrame.this, "Registration failed!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(RegisterFrame.this, "Please fill in all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Tutup frame register
                showLoginForm(); // Tampilkan kembali frame login
            }
        });
    }

    private boolean registerUser(String username, String password, String fullName, String role) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO user (username, password, full_name, role) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, fullName);
            stmt.setString(4, role);
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private void showLoginForm() {
        LoginFrame loginFrame = new LoginFrame();
        loginFrame.setVisible(true);
    }
}
