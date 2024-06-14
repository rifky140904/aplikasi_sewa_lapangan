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
import java.util.Locale;
import com.toedter.calendar.JDateChooser;

public class PenyewaanLapanganGUI {
    private JFrame frame;
    private JPanel formPanel;
    private JPanel outputPanel;
    private JComboBox<Lapangan> lapanganComboBox;
    private JComboBox<String> jamMulaiComboBox;
    private JComboBox<String> jamSelesaiComboBox;
    private JDateChooser dateChooser;
    private JButton pesanButton;
    private JButton logoutButton;
    private JTable table;
    private JButton nextButton;
    private JButton previousButton;
    private JLabel tanggalLabel;
    private Calendar currentDay;
    private int idUser;

    public PenyewaanLapanganGUI(int userId) {
        this.idUser = userId;

        frame = new JFrame("Aplikasi Penyewaan Lapangan Olahraga");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String fullName = getFullNameForUserId(userId);
        JLabel welcomeLabel = new JLabel("Selamat Datang " + fullName + " di Aplikasi Sewa Lapangan");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(welcomeLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Pilih Lapangan:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        lapanganComboBox = new JComboBox<>();
        loadLapangan();
        formPanel.add(lapanganComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Nama Penyewa:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        JLabel fullNameLabel = new JLabel(fullName);
        formPanel.add(fullNameLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Jam Mulai:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        jamMulaiComboBox = new JComboBox<>();
        for (int i = 7; i <= 21; i++) {
            jamMulaiComboBox.addItem(String.format("%02d:00", i));
        }
        jamMulaiComboBox.addActionListener(e -> updateJamSelesaiComboBox());
        formPanel.add(jamMulaiComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Jam Selesai:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        jamSelesaiComboBox = new JComboBox<>();
        updateJamSelesaiComboBox();
        formPanel.add(jamSelesaiComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Tanggal Pemesanan:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        dateChooser = new JDateChooser();
        dateChooser.setDate(new Date()); // Set tanggal default ke hari ini
        formPanel.add(dateChooser, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(new JLabel("Harga Futsal (per jam):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 7;
        JLabel hargaFutsalLabel = new JLabel("Rp " + getHargaLapangan("Lapangan Futsal A"));
        formPanel.add(hargaFutsalLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        formPanel.add(new JLabel("Harga Basket (per jam):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 8;
        JLabel hargaBasketLabel = new JLabel("Rp " + getHargaLapangan("Lapangan Basket A"));
        formPanel.add(hargaBasketLabel, gbc);

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
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(pesanButton, gbc);

        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        gbc.gridx = 2;
        gbc.gridy = 2;
        formPanel.add(logoutButton, gbc);

        frame.add(formPanel, BorderLayout.NORTH);

        table = new JTable(new DefaultTableModel(new Object[]{"Jam", "Lapangan", "Nama Penyewa", "Jam Mulai", "Jam Selesai", "Total Jam", "Total Harga", "Status Pembayaran"}, 0));
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
        frame.add(formPanel, BorderLayout.NORTH);

        frame.pack();
        frame.setVisible(true);

        // Set current day to today
        currentDay = Calendar.getInstance();

        // Menampilkan daftar penyewa saat aplikasi pertama kali dijalankan
        tampilkanPenyewa();
    }

    private double getHargaLapangan(String namaLapangan) {
        double harga = 0.0;
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT harga_per_jam FROM lapangan WHERE nama = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, namaLapangan);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                harga = rs.getDouble("harga_per_jam");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal mengambil harga lapangan!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return harga;
    }

    private String getFullNameForUserId(int userId) {
        String fullName = "";
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT full_name FROM user WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                fullName = rs.getString("full_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fullName;
    }

    private void loadLapangan() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT id FROM lapangan";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Lapangan lapangan = Lapangan.getLapanganById(rs.getString("id"));
                if (lapangan != null) {
                    lapanganComboBox.addItem(lapangan);
                }
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
        Lapangan lapangan = (Lapangan) lapanganComboBox.getSelectedItem();
        int idUser = this.idUser;
        String jamMulaiStr = (String) jamMulaiComboBox.getSelectedItem();
        String jamSelesaiStr = (String) jamSelesaiComboBox.getSelectedItem();
        Date tanggal = dateChooser.getDate();

        if (lapangan == null || jamMulaiStr.isEmpty() || jamSelesaiStr.isEmpty() || tanggal == null) {
            JOptionPane.showMessageDialog(frame, "Mohon lengkapi semua kolom pemesanan.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            java.sql.Time jamMulai = new java.sql.Time(sdf.parse(jamMulaiStr).getTime());
            java.sql.Time jamSelesai = new java.sql.Time(sdf.parse(jamSelesaiStr).getTime());

            int jamSewa = jamSelesai.getHours() - jamMulai.getHours();
            double hargaPerJam = lapangan.getHargaPerJam();
            double totalHarga = jamSewa * hargaPerJam;

            String idLapangan = lapangan.getId();

            if (isPenyewaAda(new java.sql.Date(tanggal.getTime()), jamMulai, jamSelesai, lapangan.getNama())) {
                JOptionPane.showMessageDialog(frame, "Lapangan sudah dipesan pada waktu tersebut.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "INSERT INTO pemesanan (id_lapangan, id_user, jam_sewa, total_harga, tanggal, jam_mulai, jam_selesai, status_pembayaran) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, idLapangan);
                stmt.setInt(2, idUser);
                stmt.setInt(3, jamSewa);
                stmt.setDouble(4, totalHarga);
                stmt.setDate(5, new java.sql.Date(tanggal.getTime()));
                stmt.setTime(6, jamMulai);
                stmt.setTime(7, jamSelesai);
                stmt.setString(8, "belum lunas");
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", new Locale("id"));
        tanggalLabel.setText(dateFormat.format(currentDate));

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT l.nama AS lapangan, u.full_name AS nama_penyewa, p.jam_mulai, p.jam_selesai, p.jam_sewa, p.total_harga, p.status_pembayaran " +
                    "FROM pemesanan p " +
                    "JOIN lapangan l ON p.id_lapangan = l.id " +
                    "JOIN user u ON p.id_user = u.id " +
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
                String statusPembayaran = rs.getString("status_pembayaran");

                for (int hour = jamMulai.getHours(); hour < jamSelesai.getHours(); hour++) {
                    isBooked[hour] = true;
                }

                model.addRow(new Object[]{jamMulai + " - " + jamSelesai, lapangan, namaPenyewa, jamMulai, jamSelesai, jamSewa, totalHarga, statusPembayaran});
            }

            for (int hour = 7; hour <= 21; hour++) {
                if (!isBooked[hour]) {
                    model.addRow(new Object[]{hour + ":00 - " + (hour + 1) + ":00", "Tersedia", "", "", "", "", "", ""});
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

    private void logout() {
        frame.dispose();
        LoginFrame loginFrame = new LoginFrame();
        loginFrame.setVisible(true);
    }
}
