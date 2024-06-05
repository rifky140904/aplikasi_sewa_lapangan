-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 05, 2024 at 02:49 PM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `penyewaan_lapangan`
--

-- --------------------------------------------------------

--
-- Table structure for table `lapangan`
--

CREATE TABLE `lapangan` (
  `id` varchar(10) NOT NULL,
  `nama` varchar(50) NOT NULL,
  `jenis` varchar(10) NOT NULL,
  `harga_per_jam` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `lapangan`
--

INSERT INTO `lapangan` (`id`, `nama`, `jenis`, `harga_per_jam`) VALUES
('1', 'Lapangan Futsal A', 'futsal', 80000),
('2', 'Lapangan Basket A', 'basket', 70000);

-- --------------------------------------------------------

--
-- Table structure for table `pemesanan`
--

CREATE TABLE `pemesanan` (
  `id` int(11) NOT NULL,
  `id_lapangan` varchar(10) DEFAULT NULL,
  `nama_penyewa` varchar(100) DEFAULT NULL,
  `jam_sewa` int(11) DEFAULT NULL,
  `total_harga` double DEFAULT NULL,
  `tanggal` date DEFAULT NULL,
  `jam_mulai` time DEFAULT NULL,
  `jam_selesai` time DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `pemesanan`
--

INSERT INTO `pemesanan` (`id`, `id_lapangan`, `nama_penyewa`, `jam_sewa`, `total_harga`, `tanggal`, `jam_mulai`, `jam_selesai`) VALUES
(30, '1', 'Rifky', 3, 240000, '2024-06-05', '07:00:00', '10:00:00'),
(31, '2', 'Rifky', 3, 210000, '2024-06-05', '07:00:00', '10:00:00');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `lapangan`
--
ALTER TABLE `lapangan`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `pemesanan`
--
ALTER TABLE `pemesanan`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_lapangan` (`id_lapangan`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `pemesanan`
--
ALTER TABLE `pemesanan`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `pemesanan`
--
ALTER TABLE `pemesanan`
  ADD CONSTRAINT `pemesanan_ibfk_1` FOREIGN KEY (`id_lapangan`) REFERENCES `lapangan` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
