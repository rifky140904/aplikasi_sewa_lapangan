package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class Lapangan {
    private String id;
    private String nama;
    private double hargaPerJam;

    public Lapangan(String id, String nama, double hargaPerJam) {
        this.id = id;
        this.nama = nama;
        this.hargaPerJam = hargaPerJam;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public double getHargaPerJam() {
        return hargaPerJam;
    }

    @Override
    public String toString() {
        return nama;
    }

    public abstract void displayInfo();

    public static Lapangan getLapanganById(String id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM lapangan WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String nama = rs.getString("nama");
                String jenis = rs.getString("jenis");
                double hargaPerJam = rs.getDouble("harga_per_jam");

                if ("futsal".equalsIgnoreCase(jenis)) {
                    return new LapanganFutsal(id, nama, hargaPerJam);
                } else if ("basket".equalsIgnoreCase(jenis)) {
                    return new LapanganBasket(id, nama, hargaPerJam);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

class LapanganBasket extends Lapangan {
    public LapanganBasket(String id, String nama, double hargaPerJam) {
        super(id, nama, hargaPerJam);
    }

    @Override
    public void displayInfo() {
        System.out.println("Lapangan Basket - ID: " + getId() + ", Nama: " + getNama() + ", Harga per jam: Rp" + getHargaPerJam());
    }

    @Override
    public String toString() {
        return getNama();
    }
}

class LapanganFutsal extends Lapangan {
    public LapanganFutsal(String id, String nama, double hargaPerJam) {
        super(id, nama, hargaPerJam);
    }

    @Override
    public void displayInfo() {
        System.out.println("Lapangan Futsal - ID: " + getId() + ", Nama: " + getNama() + ", Harga per jam: Rp" + getHargaPerJam());
    }

    @Override
    public String toString() {
        return getNama();
    }
}
