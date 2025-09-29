# 🛍️ Project Raksa

**Project Raksa** adalah aplikasi e-commerce berbasis **Spring Boot** dengan sistem role-based access menggunakan **Spring Security**.  
Aplikasi ini mendukung dua role utama: **Admin** dan **User**, serta menyediakan fitur marketplace sederhana mulai dari registrasi akun, manajemen toko, produk, transaksi, hingga dashboard admin.

---

## 🚀 Tech Stack
- **Java 17+**
- **Spring Boot 3**
- **Spring Security** (Role-based Authorization)
- **Thymeleaf** (Template Engine)
- **Spring Data JPA (Hibernate)**
- **MySQL / PostgreSQL**
- **Maven** (Build tools)

---

## 🔑 Role & Konstanta
Aplikasi ini menggunakan konstanta role untuk membedakan akses:

public class RoleConstant {
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";
}

src/main/java/com/guitartune/project_raksa/
 ├── configurations/   # Spring Security, Auth config
 ├── constant/         # Konstanta role & konfigurasi global
 ├── controllers/      # MVC Controller (User, Store, Product, Transaction, Admin)
 ├── dto/              # Data Transfer Object untuk request/response
 ├── init/             # Inisialisasi data awal (misalnya default admin/user)
 ├── models/           # Entity utama (User, Store, Product, Transaction, Category, Role)
 ├── repositorys/      # Spring Data JPA Repository
 └── services/         # Business logic (UserService, StoreService, ProductService, dll.)

 ## ✨ Fitur Utama

### 👤 User
- Register & Login
- Update profile
- Top-up saldo
- Lihat & hapus riwayat transaksi
- Membeli produk dari store

### 🏪 Store
- Registrasi toko
- Upload foto toko
- CRUD produk (tambah, edit, hapus)
- Melihat produk di toko
- Withdraw saldo hasil penjualan

### 📦 Produk
- CRUD produk dengan upload gambar (Base64)
- Filter produk berdasarkan nama, kategori, dan rentang harga
- Sorting produk berdasarkan harga (ASC/DESC)

### 💳 Transaksi
- Membeli produk dengan validasi saldo & stok
- Melihat history transaksi
- Filter transaksi berdasarkan waktu (1 menit, 5 menit, 10 menit terakhir)
- Hapus transaksi dari riwayat

### ⚙️ Admin
- Dashboard admin
- Manajemen pengguna (lihat, cari, sort, hapus user)
- Manajemen produk & kategori
- Monitoring seluruh transaksi

---

## ⚙️ Cara Menjalankan

1. Clone repository:
   ```bash
   git clone https://github.com/username/project-raksa.git
   cd project-raksa
2. Clone repository:
Buat database baru (MySQL/PostgreSQL).
3. Sesuaikan konfigurasi DB di application.properties.
4. Jalankan aplikasi:
mvn spring-boot:run
5. Akses di browser:
- User: http://localhost:8080/home
- Admin: http://localhost:8080/admin/dashboard

## 👥 Role Default

- **Admin**
  - Username: `admin`
  - Password: `admin123`

- **User**
  - Registrasi manual melalui endpoint `/register`

---

## 📌 Catatan

- Folder **DTO** digunakan sebagai *Data Transfer Object* agar komunikasi data antara controller dan service lebih efisien & aman.  
- Folder **init** berfungsi untuk inisialisasi data default saat aplikasi pertama kali dijalankan (contoh: membuat akun admin default).  
- Konstanta role didefinisikan di **RoleConstant** untuk menjaga konsistensi penggunaan role.  

---
