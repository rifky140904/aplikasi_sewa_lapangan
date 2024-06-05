package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.text.ParseException;
import com.toedter.calendar.JDateChooser;

public class PenyewaanLapanganGUI {
    private JFrame frame;
    private JPanel formPanel;
    private JPanel outputPanel;
    private JComboBox<String> lapanganComboBox;
    private JTextField namaField;
    private JComboBox<String> jamMulaiComboBox;
    private JComboBox<String> jamSelesaiComboBox;
    private JDateChooser dateChooser;
    private JButton pesanButton;
    private JTable table;
    private JButton nextButton;
    private JButton previousButton;
    private JLabel tanggalLabel;
    private Calendar currentDay;

    public PenyewaanLapanganGUI() {
        frame = new JFrame("Aplikasi Penyewaan Lapangan Olahraga");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Pilih Lapangan:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        lapanganComboBox = new JComboBox<>();
        loadLapangan();
        formPanel.add(lapanganComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Nama Penyewa:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        namaField = new JTextField(20);
        formPanel.add(namaField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Jam Mulai:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        jamMulaiComboBox = new JComboBox<>();
        for (int i = 7; i <= 21; i++) {
            jamMulaiComboBox.addItem(String.format("%02d:00", i));
        }
        jamMulaiComboBox.addActionListener(e -> updateJamSelesaiComboBox());
        formPanel.add(jamMulaiComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Jam Selesai:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        jamSelesaiComboBox = new JComboBox<>();
        updateJamSelesaiComboBox();
        formPanel.add(jamSelesaiComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Tanggal Pemesanan:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        dateChooser = new JDateChooser();
        dateChooser.setDate(new Date()); // Set tanggal default ke hari ini
        formPanel.add(dateChooser, gbc);

        pesanButton = new JButton("Pesan");
        pesanButton.setForeground(Color.WHITE);
        pesanButton.setBackground(new Color(51, 153, 255));
        pesanButton.setFont(new Font("Arial", Font.BOLD, 14));
        pesanButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pesanLapangan();
                tampilkanPenyewa();
            }
        });

        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(pesanButton, gbc);

        frame.add(formPanel, BorderLayout.NORTH);

        table = new JTable(new DefaultTableModel(new Object[]{"Jam", "Lapangan", "Nama Penyewa", "Jam Mulai", "Jam Selesai", "Total Jam", "Total Harga"}, 0));
        JScrollPane scrollPane = new JScrollPane(table);

        outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        outputPanel.setBackground(new Color(245, 245, 245));
        outputPanel.add(scrollPane, BorderLayout.CENTER);

        tanggalLabel = new JLabel();
        tanggalLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tanggalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        outputPanel.add(tanggalLabel, BorderLayout.NORTH);

        nextButton = new JButton("Next");
        nextButton.addActionListener(e -> nextDay());

        previousButton = new JButton("Previous");
        previousButton.addActionListener(e -> previousDay());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(previousButton);
        buttonPanel.add(nextButton);

        outputPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(outputPanel, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);

        // Set current day to today
        currentDay = Calendar.getInstance();

        // Menampilkan daftar penyewa saat aplikasi pertama kali dijalankan
        tampilkanPenyewa();
    }

    private void loadLapangan() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT nama FROM lapangan";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                lapanganComboBox.addItem(rs.getString("nama"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateJamSelesaiComboBox() {
        String selectedJamMulai = (String) jamMulaiComboBox.getSelectedItem();
        jamSelesaiComboBox.removeAllItems();
        int jamMulai = Integer.parseInt(selectedJamMulai.split(":")[0]);
        for (int i = jamMulai + 1; i <= 22; i++) {
            jamSelesaiComboBox.addItem(String.format("%02d:00", i));
        }
    }

    private void pesanLapangan() {
        String lapangan = (String) lapanganComboBox.getSelectedItem();
        String namaPenyewa = namaField.getText();
        String jamMulaiStr = (String) jamMulaiComboBox.getSelectedItem();
        String jamSelesaiStr = (String) jamSelesaiComboBox.getSelectedItem();
        Date tanggal = dateChooser.getDate();

        if (lapangan.isEmpty() || namaPenyewa.isEmpty() || jamMulaiStr.isEmpty() || jamSelesaiStr.isEmpty() || tanggal == null) {
            JOptionPane.showMessageDialog(frame, "Mohon lengkapi semua kolom pemesanan.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            java.sql.Time jamMulai = new java.sql.Time(sdf.parse(jamMulaiStr).getTime());
            java.sql.Time jamSelesai = new java.sql.Time(sdf.parse(jamSelesaiStr).getTime());

            if (isPenyewaAda(new java.sql.Date(tanggal.getTime()), jamMulai, jamSelesai, lapangan)) {
                JOptionPane.showMessageDialog(frame, "Lapangan sudah dipesan pada waktu tersebut.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int jamSewa = jamSelesai.getHours() - jamMulai.getHours();
            double hargaPerJam = getHargaPerJam(lapangan);
            double totalHarga = jamSewa * hargaPerJam;

            String idLapangan = getLapanganId(lapangan);

            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "INSERT INTO pemesanan (id_lapangan, nama_penyewa, tanggal, jam_mulai, jam_selesai, jam_sewa, total_harga) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, idLapangan);
                stmt.setString(2, namaPenyewa);
                stmt.setDate(3, new java.sql.Date(tanggal.getTime()));
                stmt.setTime(4, jamMulai);
                stmt.setTime(5, jamSelesai);
                stmt.setInt(6, jamSewa);
                stmt.setDouble(7, totalHarga);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Pemesanan berhasil disimpan.\nTotal Sewa: " + jamSewa + " jam\nTotal Harga: " + totalHarga, "Informasi", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Gagal menyimpan pemesanan: " + e.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Format waktu tidak valid.", "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void tampilkanPenyewa() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        Date currentDate = currentDay.getTime();
        java.sql.Date sqlDate = new java.sql.Date(currentDate.getTime());
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        tanggalLabel.setText(dateFormat.format(currentDate));

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT l.nama AS lapangan, p.nama_penyewa, p.jam_mulai, p.jam_selesai, p.jam_sewa, p.total_harga " +
                    "FROM pemesanan p " +
                    "JOIN lapangan l ON p.id_lapangan = l.id " +
                    "WHERE p.tanggal = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setDate(1, sqlDate);
            ResultSet rs = stmt.executeQuery();

            boolean[] isBooked = new boolean[24];

            while (rs.next()) {
                String lapangan = rs.getString("lapangan");
                String namaPenyewa = rs.getString("nama_penyewa");
                Time jamMulai = rs.getTime("jam_mulai");
                Time jamSelesai = rs.getTime("jam_selesai");
                int jamSewa = rs.getInt("jam_sewa");
                double totalHarga = rs.getDouble("total_harga");

                for (int hour = jamMulai.getHours(); hour < jamSelesai.getHours(); hour++) {
                    isBooked[hour] = true;
                }

                model.addRow(new Object[]{jamMulai + " - " + jamSelesai, lapangan, namaPenyewa, jamMulai, jamSelesai, jamSewa, totalHarga});
            }

            for (int hour = 7; hour <= 21; hour++) {
                if (!isBooked[hour]) {
                    model.addRow(new Object[]{hour + ":00 - " + (hour + 1) + ":00", "Tersedia", "", "", "", "", ""});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void nextDay() {
        currentDay.add(Calendar.DAY_OF_MONTH, 1);
        tampilkanPenyewa();
    }

    private void previousDay() {
        currentDay.add(Calendar.DAY_OF_MONTH, -1);
        tampilkanPenyewa();
    }

    private boolean isPenyewaAda(java.sql.Date tanggal, Time jamMulai, Time jamSelesai, String lapangan) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) FROM pemesanan p JOIN lapangan l ON p.id_lapangan = l.id WHERE p.tanggal = ? AND l.nama = ? AND ((p.jam_mulai <= ? AND p.jam_selesai > ?) OR (p.jam_mulai < ? AND p.jam_selesai >= ?))";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setDate(1, tanggal);
            stmt.setString(2, lapangan);
            stmt.setTime(3, jamMulai);
            stmt.setTime(4, jamMulai);
            stmt.setTime(5, jamSelesai);
            stmt.setTime(6, jamSelesai);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private double getHargaPerJam(String lapangan) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT harga_per_jam FROM lapangan WHERE nama = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, lapangan);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("harga_per_jam");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private String getLapanganId(String lapangan) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT id FROM lapangan WHERE nama = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, lapangan);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
