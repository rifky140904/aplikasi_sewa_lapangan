package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AdminFrame extends JFrame {
    private JTable penyewaTable;
    private JLabel totalPendapatanLabel;
    private JLabel tanggalLabel;
    private Date currentDate;

    public AdminFrame() {
        setTitle("Admin Panel");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        currentDate = new Date(); // Set tanggal saat ini

        JPanel headerPanel = new JPanel(new BorderLayout());
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton previousButton = new JButton("< Previous");
        JButton nextButton = new JButton("Next >");
        tanggalLabel = new JLabel();
        updateTanggalLabel();

        previousButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                navigateDate(-1);
            }
        });

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                navigateDate(1);
            }
        });

        datePanel.add(previousButton);
        datePanel.add(tanggalLabel);
        datePanel.add(nextButton);

        totalPendapatanLabel = new JLabel();
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.add(new JLabel("Total Pendapatan Hari Ini: "), BorderLayout.WEST);
        totalPanel.add(totalPendapatanLabel, BorderLayout.CENTER);

        headerPanel.add(datePanel, BorderLayout.NORTH);
        headerPanel.add(totalPanel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);

        penyewaTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(penyewaTable);
        add(scrollPane, BorderLayout.CENTER);

        // Menambahkan tombol Logout
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.add(logoutButton);
        add(footerPanel, BorderLayout.SOUTH);

        loadPenyewaData(); // Memuat data penyewa saat frame dimuat
        hitungTotalPendapatan(); // Menghitung total pendapatan saat frame dimuat
    }

    private void updateTanggalLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, yyyy-MM-dd HH:mm", new Locale("id"));
        String formattedDate = sdf.format(currentDate);
        tanggalLabel.setText(formattedDate);
    }

    private void navigateDate(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        currentDate = calendar.getTime();
        updateTanggalLabel();
        loadPenyewaData();
        hitungTotalPendapatan();
    }

    private void loadPenyewaData() {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Nama Lapangan", "Username", "Jam Sewa", "Total Harga", "Tanggal", "Range Jam"}, 0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT p.id, l.nama, u.username, p.jam_sewa, p.total_harga, p.tanggal, p.jam_mulai, p.jam_selesai " +
                    "FROM pemesanan p " +
                    "JOIN lapangan l ON p.id_lapangan = l.id " +
                    "JOIN user u ON p.id_user = u.id " +
                    "WHERE DATE(p.tanggal) = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setDate(1, new java.sql.Date(currentDate.getTime()));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String namaLapangan = rs.getString("nama");
                String username = rs.getString("username");
                int jamSewa = rs.getInt("jam_sewa");
                double totalHarga = rs.getDouble("total_harga");
                Date tanggal = rs.getDate("tanggal");
                Time jamMulai = rs.getTime("jam_mulai");
                Time jamSelesai = rs.getTime("jam_selesai");

                model.addRow(new Object[]{id, namaLapangan, username, jamSewa, totalHarga, new SimpleDateFormat("yyyy-MM-dd").format(tanggal), jamMulai + " - " + jamSelesai});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data penyewa!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        penyewaTable.setModel(model);
    }

    private void hitungTotalPendapatan() {
        double totalPendapatan = 0.0;
        DefaultTableModel model = (DefaultTableModel) penyewaTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            totalPendapatan += (double) model.getValueAt(i, 4); // Kolom indeks 4 adalah kolom total harga
        }
        totalPendapatanLabel.setText(String.format("Rp. %.2f", totalPendapatan));
    }

    private void logout() {
        dispose();
        new LoginFrame().setVisible(true);
    }
}
