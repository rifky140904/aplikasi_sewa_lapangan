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
        setSize(800, 600); // Set the same size as AdminFrame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centers the frame on the screen

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Register Akun Aplikasi Penyewaan Lapangan");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(passwordField, gbc);

        JLabel fullNameLabel = new JLabel("Full Name:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(fullNameLabel, gbc);

        fullNameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(fullNameField, gbc);

        JLabel roleLabel = new JLabel("Role:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(roleLabel, gbc);

        roleComboBox = new JComboBox<>(new String[]{"user"});
        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(roleComboBox, gbc);

        JButton registerButton = new JButton("Register");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(registerButton, gbc);

        JButton cancelButton = new JButton("Cancel");
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        panel.add(cancelButton, gbc);

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

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Menutup frame register
                showLoginForm(); // Kembali ke frame login
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
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            }
        });
    }
}
