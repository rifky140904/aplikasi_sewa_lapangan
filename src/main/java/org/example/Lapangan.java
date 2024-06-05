package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

abstract class Lapangan {
    protected String id;
    protected String nama;

    public Lapangan(String id, String nama) {
        this.id = id;
        this.nama = nama;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public abstract double getHargaPerJam();

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
    private double hargaPerJam;

    public LapanganBasket(String id, String nama, double hargaPerJam) {
        super(id, nama);
        this.hargaPerJam = hargaPerJam;
    }

    @Override
    public double getHargaPerJam() {
        return hargaPerJam;
    }

    @Override
    public void displayInfo() {
        System.out.println("Lapangan Basket - ID: " + id + ", Nama: " + nama + ", Harga per jam: Rp" + hargaPerJam);
    }
}

class LapanganFutsal extends Lapangan {
    private double hargaPerJam;

    public LapanganFutsal(String id, String nama, double hargaPerJam) {
        super(id, nama);
        this.hargaPerJam = hargaPerJam;
    }

    @Override
    public double getHargaPerJam() {
        return hargaPerJam;
    }

    @Override
    public void displayInfo() {
        System.out.println("Lapangan Futsal - ID: " + id + ", Nama: " + nama + ", Harga per jam: Rp" + hargaPerJam);
    }
}
