# LAPORAN PROYEK

## SAGA ORCHESTRATION PATTERN — SIMULASI DISTRIBUTED TRANSACTION PADA PLATFORM E-COMMERCE

---

**Mata Kuliah:** Web Services

**Kelompok:** 6

---

<br>

# DAFTAR ISI

- [BAB I PENDAHULUAN](#bab-i-pendahuluan)
  - [1.1 Latar Belakang](#11-latar-belakang)
  - [1.2 Rumusan Masalah](#12-rumusan-masalah)
  - [1.3 Tujuan](#13-tujuan)
  - [1.4 Manfaat](#14-manfaat)
  - [1.5 Batasan Masalah](#15-batasan-masalah)
- [BAB II LANDASAN TEORI](#bab-ii-landasan-teori)
  - [2.1 Distributed Transaction](#21-distributed-transaction)
  - [2.2 Saga Pattern](#22-saga-pattern)
  - [2.3 Orchestration vs Choreography](#23-orchestration-vs-choreography)
  - [2.4 JWT (JSON Web Token)](#24-jwt-json-web-token)
  - [2.5 REST API](#25-rest-api)
  - [2.6 Spring Boot](#26-spring-boot)
  - [2.7 PostgreSQL](#27-postgresql)
- [BAB III PERANCANGAN SISTEM](#bab-iii-perancangan-sistem)
  - [3.1 Arsitektur Sistem](#31-arsitektur-sistem)
  - [3.2 Tech Stack](#32-tech-stack)
  - [3.3 Use Case Diagram](#33-use-case-diagram)
  - [3.4 Struktur Database](#34-struktur-database)
  - [3.5 Desain API](#35-desain-api)
- [BAB IV IMPLEMENTASI](#bab-iv-implementasi)
  - [4.1 Struktur Proyek](#41-struktur-proyek)
  - [4.2 Konfigurasi Aplikasi](#42-konfigurasi-aplikasi)
  - [4.3 Implementasi Autentikasi](#43-implementasi-autentikasi)
  - [4.4 Implementasi Saga Orchestrator](#44-implementasi-saga-orchestrator)
  - [4.5 Implementasi Mock Services](#45-implementasi-mock-services)
  - [4.6 Implementasi Frontend](#46-implementasi-frontend)
  - [4.7 Alur Saga Lengkap](#47-alur-saga-lengkap)
- [BAB V CARA PENGGUNAAN](#bab-v-cara-penggunaan)
  - [5.1 Persiapan Environment](#51-persiapan-environment)
  - [5.2 Menjalankan Aplikasi](#52-menjalankan-aplikasi)
  - [5.3 Skenario Testing](#53-skenario-testing)
- [BAB VI PENUTUP](#bab-vi-penutup)
  - [6.1 Kesimpulan](#61-kesimpulan)
  - [6.2 Saran](#62-saran)

---

<br>

# BAB I PENDAHULUAN

## 1.1 Latar Belakang

Perkembangan teknologi informasi mendorong transformasi sistem monolitik menjadi arsitektur microservices. Dalam arsitektur microservices, setiap layanan berjalan secara independen dan memiliki basis data sendiri. Tantangan utama yang muncul adalah bagaimana menjaga konsistensi data di seluruh layanan ketika terjadi transaksi yang melibatkan banyak microservices.

Pendekatan tradisional menggunakan ACID transaction tidak dapat diterapkan secara langsung karena setiap microservices memiliki database yang terpisah. Oleh karena itu, diperlukan pola arsitektural yang dapat menangani transaksi terdistribusi, salah satunya adalah **Saga Pattern**.

Proyek ini dibuat untuk mensimulasikan penerapan **Saga Orchestration Pattern** pada studi kasus pemesanan e-commerce yang melibatkan tiga layanan: Payment, Inventory, dan Shipping. Sistem dilengkapi dengan antarmuka pengguna berbasis web, autentikasi JWT, dan visualisasi real-time dari alur transaksi.

## 1.2 Rumusan Masalah

1. Bagaimana mengimplementasikan Saga Orchestration Pattern untuk menangani transaksi terdistribusi pada platform e-commerce?
2. Bagaimana merancang mekanisme rollback yang tepat ketika terjadi kegagalan di salah satu step transaksi?
3. Bagaimana membangun antarmuka pengguna yang dapat memvisualisasikan alur saga secara real-time?

## 1.3 Tujuan

1. Mengimplementasikan Saga Orchestration Pattern menggunakan Java Spring Boot.
2. Merancang mekanisme kompensasi (rollback) untuk setiap step transaksi.
3. Membangun Single Page Application (SPA) yang menampilkan visualisasi alur saga secara real-time.
4. Menyediakan fitur simulasi error untuk menguji ketahanan sistem terhadap kegagalan.

## 1.4 Manfaat

1. Memberikan pemahaman praktis tentang implementasi Saga Pattern dalam arsitektur microservices.
2. Menyediakan platform simulasi untuk mempelajari mekanisme distributed transaction.
3. Menjadi referensi pengembangan sistem e-commerce yang membutuhkan konsistensi data antar layanan.

## 1.5 Batasan Masalah

1. Microservices yang disimulasikan hanya berupa mock service dalam satu aplikasi (tidak deployment terpisah).
2. Data transaksi saga tidak dipersist ke database (hanya dikelola in-memory).
3. Tidak menggunakan message broker untuk komunikasi antar service.
4. Sistem hanya mencakup 3 step transaksi: Payment, Inventory, Shipping.

---

<br>

# BAB II LANDASAN TEORI

## 2.1 Distributed Transaction

Distributed transaction adalah transaksi yang melibatkan dua atau lebih resource (database, message queue, service) yang berbeda. Dalam arsitektur microservices, setiap service memiliki database sendiri (database-per-service pattern), sehingga transaksi yang melintasi batas service tidak dapat menggunakan mekanisme ACID tradisional.

Tantangan utama distributed transaction:
- **Atomicity**: Menjamin semua operasi selesai atau tidak sama sekali
- **Consistency**: Menjaga konsistensi data di seluruh service
- **Isolation**: Menangani konkurensi antar transaksi
- **Durability**: Menjamin data tersimpan setelah transaksi berhasil

## 2.2 Saga Pattern

Saga Pattern adalah pola arsitektural untuk mengelola distributed transaction dengan memecah transaksi besar menjadi serangkaian transaksi lokal yang lebih kecil. Setiap transaksi lokal memiliki **compensating transaction** (rollback) yang dapat membatalkan efek transaksi jika terjadi kegagalan.

Karakteristik Saga Pattern:
- Setiap step adalah transaksi lokal dengan database sendiri
- Jika sebuah step gagal, saga menjalankan compensating transaction untuk step-step sebelumnya
- Tidak ada locking lintas service (mendukung eventual consistency)
- Cocok untuk long-lived transaction

### Contoh Saga pada E-Commerce:

| Step | Forward Transaction | Compensating Transaction |
|------|--------------------|-------------------------|
| 1 | Payment Service — Proses pembayaran | Refund dana ke pelanggan |
| 2 | Inventory Service — Reserve stok | Release stok kembali |
| 3 | Shipping Service — Jadwalkan kirim | Batalkan pengiriman |

## 2.3 Orchestration vs Choreography

Terdapat dua pendekatan dalam implementasi Saga Pattern:

### Orchestration (Digunakan dalam Proyek Ini)

Seorang **orchestrator** sentral bertanggung jawab mengarahkan setiap step transaksi. Orchestrator memerintahkan service untuk menjalankan transaksi lokal, mendeteksi kegagalan, dan memicu compensating transaction.

**Kelebihan:**
- Alur transaksi mudah dipahami dan dilacak
- Orchestrator mengelola kompleksitas penuh
- Mudah menambahkan step baru

**Kekurangan:**
- Orchestrator menjadi single point of failure
- Orchestrator bisa menjadi bottleneck

### Choreography

Setiap service mendengarkan event dari service lain dan bereaksi secara mandiri. Tidak ada koordinator sentral; setiap service tahu apa yang harus dilakukan ketika event tertentu terjadi.

**Kelebihan:**
- Desentralisasi, tidak ada single point of failure
- Service lebih independen dan loosely coupled

**Kekurangan:**
- Alur transaksi tersebar dan sulit dilacak
- Kompleksitas tinggi pada sistem besar

## 2.4 JWT (JSON Web Token)

JWT adalah standar terbuka (RFC 7519) untuk mengirimkan informasi terverifikasi antara dua pihak dalam format JSON. Token terdiri dari tiga bagian: **Header**, **Payload**, dan **Signature**.

**Cara Kerja dalam Aplikasi:**
1. User login → server memvalidasi kredensial
2. Server membuat JWT dengan data user (email) dan signature
3. Client menyimpan token dan mengirimkannya di header Authorization untuk setiap request
4. Server memverifikasi token sebelum memproses request

## 2.5 REST API

REST (Representational State Transfer) adalah gaya arsitektur untuk merancang API web. Karakteristik RESTful API:
- **Stateless**: Setiap request berdiri sendiri
- **Resource-based**: Menggunakan URL untuk merepresentasikan resource
- **HTTP Methods**: GET (membaca), POST (membuat), PUT (mengupdate), DELETE (menghapus)
- **JSON Format**: Format pertukaran data yang ringan dan mudah dibaca

## 2.6 Spring Boot

Spring Boot adalah framework Java untuk membangun aplikasi production-grade dengan konfigurasi minimal. Fitur utama yang digunakan dalam proyek ini:
- **Spring MVC**: Pembuatan REST API
- **Spring Security**: Autentikasi dan otorisasi
- **Spring Data JPA**: Akses database dengan ORM
- **Spring WebFlux**: WebClient untuk HTTP calls asynchronous
- **Embedded Tomcat**: Server aplikasi terintegrasi

## 2.7 PostgreSQL

PostgreSQL adalah sistem manajemen basis data relasional open-source yang terkenal dengan keandalan, performa, dan fitur lengkapnya. Dalam proyek ini, PostgreSQL digunakan untuk menyimpan data pengguna (users table) dengan JPA/Hibernate sebagai ORM.

---

<br>

# BAB III PERANCANGAN SISTEM

## 3.1 Arsitektur Sistem

```
┌─────────────────────────────────────────────────────────────────────┐
│                        APLIKASI SPRING BOOT                         │
│                                                                     │
│  ┌──────────┐   ┌────────────────┐   ┌──────────────────────────┐  │
│  │  CLIENT  │──▶│  CONTROLLER    │──▶│  ORCHESTRATION SERVICE   │  │
│  │ (Browser)│   │  (REST API)    │   │  (Saga Coordinator)      │  │
│  └──────────┘   └────────────────┘   └────────────┬─────────────┘  │
│                                                    │                │
│                          ┌─────────────────────────┼──────────┐     │
│                          │                         │          │     │
│                          ▼                         ▼          ▼     │
│                    ┌──────────┐            ┌──────────┐ ┌────────┐ │
│                    │  Step 1  │            │  Step 2  │ │ Step 3 │ │
│                    │ Payment  │            │Inventory │ │Shipping│ │
│                    └────┬─────┘            └────┬─────┘ └───┬────┘ │
│                         │                       │           │      │
│                         ▼                       ▼           ▼      │
│                    ┌───────────────────────────────────────────┐    │
│                    │           MOCK CONTROLLER                 │    │
│                    │   (Simulasi 3 Microservices)              │    │
│                    └───────────────────────────────────────────┘    │
│                                                                     │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │                    DATABASE POSTGRESQL                       │  │
│  │                    (Tabel: users)                            │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                     │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │               SECURITY (Spring Security + JWT)              │  │
│  └──────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────┘
```

### Alur Komunikasi:

1. **Client → Controller**: Browser mengirim request HTTP (REST API)
2. **Controller → Orchestrator**: Controller memanggil OrchestrationService
3. **Orchestrator → Step Service**: Orchestrator mengeksekusi step berurutan
4. **Step Service → Mock Controller**: Setiap step memanggil mock endpoint via WebClient (HTTP internal)
5. **Orchestrator → Response**: Hasil dikembalikan ke client dalam format JSON

## 3.2 Tech Stack

| Layer | Teknologi | Keterangan |
|-------|-----------|------------|
| **Bahasa Pemrograman** | Java 17 | Versi LTS terbaru |
| **Framework** | Spring Boot 4.1.0 | Konfigurasi minimal, production-grade |
| **Build Tool** | Apache Maven | Manajemen dependensi |
| **Database** | PostgreSQL | Database relasional |
| **ORM** | Hibernate / JPA | Object-Relational Mapping |
| **Security** | Spring Security + JWT | Autentikasi stateless |
| **HTTP Client** | WebClient (Reactive) | Panggilan HTTP internal ke mock service |
| **Frontend** | HTML + CSS + JavaScript Vanilla | Single Page Application |
| **Version Control** | Git + GitHub | Manajemen kode sumber |

## 3.3 Use Case Diagram

```
                    ┌─────────────────────────────────────┐
                    │         SISTEM E-COMMERCE           │
                    │         (ShopSaga)                  │
                    └─────────────────────────────────────┘
                                    │
         ┌──────────────────────────┼──────────────────────────┐
         │                          │                          │
         ▼                          ▼                          ▼
   ┌─────────────┐          ┌──────────────┐          ┌──────────────┐
   │   Register  │          │    Login     │          │  Lihat       │
   │   Akun      │          │              │          │  Produk      │
   └─────────────┘          └──────────────┘          └──────────────┘
         │                          │                         │
         └────────────┬─────────────┘                         │
                      ▼                                       │
               ┌──────────────┐                               │
               │  Kelola      │                               │
               │  Keranjang   │                               │
               └──────┬───────┘                               │
                      │                                       │
                      ▼                                       │
               ┌──────────────┐                               │
               │  Checkout    │                               │
               │  & Order     │                               │
               └──────┬───────┘                               │
                      │                                       │
                      ▼                                       │
               ┌──────────────┐                               │
               │  Lihat Hasil │                               │
               │  Transaksi   │                               │
               └──────────────┘                               │
                                                              │
        ┌─────────────────────────────────────────────────────┘
        │
        ▼
  ┌──────────────────────────────────────────────────────────┐
  │                    SAGA SYSTEM                           │
  │  ┌──────────┐   ┌──────────┐   ┌──────────┐             │
  │  │ Payment  │──▶│Inventory │──▶│ Shipping │             │
  │  │  Step 1  │   │  Step 2  │   │  Step 3  │             │
  │  └────┬─────┘   └────┬─────┘   └────┬─────┘             │
  │       │              │              │                   │
  │       ▼              ▼              ▼                   │
  │  ┌──────────┐   ┌──────────┐   ┌──────────┐            │
  │  │ Rollback │   │ Rollback │   │ Rollback │            │
  │  │  Step 1  │   │  Step 2  │   │  Step 3  │            │
  │  └──────────┘   └──────────┘   └──────────┘            │
  └──────────────────────────────────────────────────────────┘
```

### Aktor:

| Aktor | Deskripsi |
|-------|-----------|
| **User (Pengguna)** | Pengguna sistem yang dapat mendaftar, login, melihat produk, berbelanja, dan melihat hasil transaksi |
| **Saga System** | Sistem backend yang menjalankan orchestration transaksi secara otomatis |

## 3.4 Struktur Database

Sistem hanya menggunakan satu tabel untuk menyimpan data pengguna:

### Tabel: `users`

| Kolom | Tipe Data | Constraint | Deskripsi |
|-------|-----------|------------|-----------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | ID unik pengguna |
| `email` | VARCHAR | UNIQUE, NOT NULL | Email pengguna (username) |
| `password` | VARCHAR | NOT NULL | Password terenkripsi (BCrypt) |
| `name` | VARCHAR | NOT NULL | Nama lengkap pengguna |
| `role` | VARCHAR | DEFAULT 'USER' | Role pengguna (USER / ADMIN) |

### Entity Relationship Diagram:

```
┌──────────────────────┐
│        users         │
├──────────────────────┤
│ PK │ id: BIGINT      │
│    │ email: VARCHAR  │
│    │ password: VARCHAR│
│    │ name: VARCHAR   │
│    │ role: VARCHAR   │
└──────────────────────┘
```

## 3.5 Desain API

### Base URL: `http://localhost:8080`

### Format Request/Response: JSON

### Autentikasi: Bearer Token (JWT)

### Endpoint:

#### Autentikasi (PUBLIC)

**Register**
- `POST /api/auth/register`
- Body: `{ "name": "...", "email": "...", "password": "..." }`
- Response: `{ "token": "...", "email": "...", "name": "..." }`

**Login**
- `POST /api/auth/login`
- Body: `{ "email": "...", "password": "..." }`
- Response: `{ "token": "...", "email": "...", "name": "..." }`

#### Produk (PUBLIC)

- `GET /api/products` → Daftar produk
- `GET /api/products/{id}` → Detail produk

#### Saga Orchestration (AUTH REQUIRED)

**Eksekusi Saga**
- `POST /api/orchestration/saga`
- Header: `Authorization: Bearer <token>`
- Query Param (opsional): `?failAt=step1|step2|step3`
- Body: `OrderRequest`
- Response: `SagaResponseDto`

**Test Step (AUTH REQUIRED)**
- `GET /api/orchestration/step1` → Execute step 1 saja
- `GET /api/orchestration/step2` → Execute step 2 saja
- `GET /api/orchestration/step3` → Execute step 3 saja

---

<br>

# BAB IV IMPLEMENTASI

## 4.1 Struktur Proyek

```
demo/
├── pom.xml                              # Dependencies Maven
├── mvnw / mvnw.cmd                      # Maven Wrapper
├── README.md                            # Dokumentasi
├── documentation/                       # Screenshot & laporan
└── src/main/
    ├── java/com/example/demo/
    │   ├── DemoApplication.java         # @SpringBootApplication
    │   ├── auth/                        # Autentikasi (login/register)
    │   │   ├── AuthController.java
    │   │   ├── AuthResponse.java
    │   │   ├── LoginRequest.java
    │   │   └── RegisterRequest.java
    │   ├── config/                      # Konfigurasi
    │   │   ├── DataInitializer.java     # Seed data
    │   │   └── WebClientConfig.java     # WebClient bean
    │   ├── controller/                  # REST Controllers
    │   │   ├── MockController.java      # Mock services
    │   │   ├── OrchestrationController.java
    │   │   └── ProductController.java
    │   ├── dto/                         # Data Transfer Objects
    │   │   ├── OrderRequest.java
    │   │   ├── ProductDto.java
    │   │   ├── SagaResponseDto.java
    │   │   └── StepResultDto.java
    │   ├── model/                       # Entity
    │   │   └── User.java
    │   ├── repository/                  # JPA Repository
    │   │   └── UserRepository.java
    │   ├── security/                    # Spring Security + JWT
    │   │   ├── CustomUserDetailsService.java
    │   │   ├── JwtAuthFilter.java
    │   │   ├── JwtUtil.java
    │   │   └── SecurityConfig.java
    │   └── service/                     # Business Logic
    │       ├── OrchestrationService.java
    │       └── orchestrationstep/
    │           ├── Step1Service.java
    │           ├── Step2Service.java
    │           └── Step3Service.java
    └── resources/
        ├── application.properties       # Konfigurasi aplikasi
        └── static/
            └── index.html               # Frontend SPA

Total: 25 file Java, 1 file HTML/CSS/JS
```

## 4.2 Konfigurasi Aplikasi

### application.properties

```properties
spring.application.name=demo

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/trws_db
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.username=postgres
spring.datasource.password=Milliano2005
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# JWT
jwt.secret=K3yB04rdC4tD0gR4bb1tF1shB1rdH0rs3L10nSn4k3M0us3D34rEl3ph4nt!
jwt.expiration=86400000
```

### POM.xml — Dependencies Utama

| Dependency | Fungsi |
|-----------|--------|
| `spring-boot-starter-web` | REST API + embedded Tomcat |
| `spring-boot-starter-webclient` | WebClient untuk HTTP calls |
| `spring-boot-starter-security` | Autentikasi & otorisasi |
| `spring-boot-starter-data-jpa` | ORM / database access |
| `postgresql` | Driver PostgreSQL |
| `jjwt-api`, `jjwt-impl`, `jjwt-jackson` | JWT generation & validation |

## 4.3 Implementasi Autentikasi

### Arsitektur Security

```
Request → JwtAuthFilter → SecurityContextHolder → Controller
                │
                ├─ Ada token? → Validasi JWT → Load user dari DB → Set authentication
                │
                └─ Tidak ada token? → Lanjutkan (akan ditolak jika endpoint butuh auth)
```

### SecurityConfig.java

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/mock1/**", "/api/mock2/**", "/api/mock3/**").permitAll()
                .requestMatchers("/", "/index.html", "/static/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
```

### Alur Login

1. User mengirim `POST /api/auth/login` dengan `email` dan `password`
2. `AuthController` memanggil `AuthenticationManager.authenticate()`
3. Spring Security memvalidasi kredensial melalui `CustomUserDetailsService`
4. Jika valid, `JwtUtil.generateToken(email)` membuat JWT
5. Token dikembalikan ke client

### Alur Register

1. User mengirim `POST /api/auth/register` dengan `name`, `email`, `password`
2. `AuthController` mengecek apakah email sudah terdaftar
3. Password dienkripsi dengan `BCryptPasswordEncoder`
4. User baru disimpan ke database
5. JWT langsung dibuat dan dikembalikan

### JwtAuthFilter

```java
protected void doFilterInternal(HttpServletRequest request, ...) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
        String token = authHeader.substring(7);
        if (jwtUtil.validateToken(token)) {
            String email = jwtUtil.extractEmail(token);
            User user = userRepository.findByEmail(email).orElse(null);
            if (user != null) {
                UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
    }
    filterChain.doFilter(request, response);
}
```

## 4.4 Implementasi Saga Orchestrator

### OrchestrationService.java — Core Logic

```java
@Service
public class OrchestrationService {

    public SagaResponseDto executeSaga(OrderRequest order, String simulateFailAt) {
        SagaResponseDto result = new SagaResponseDto();
        result.setOrder(order);

        // Step 1: Payment
        boolean fail1 = "step1".equalsIgnoreCase(simulateFailAt);
        StepResultDto step1Result = step1Service.execute(order, fail1);
        result.addHistory("step1", step1Result);
        if (!step1Result.isSuccess()) {
            result.setStatus("FAILED");
            result.setFailedAt("STEP1");
            return result;
        }

        // Step 2: Inventory
        boolean fail2 = "step2".equalsIgnoreCase(simulateFailAt);
        StepResultDto step2Result = step2Service.execute(order, fail2);
        result.addHistory("step2", step2Result);
        if (!step2Result.isSuccess()) {
            // Rollback Step 1
            StepResultDto rollback1 = step1Service.rollback(order);
            result.addHistory("rollback1", rollback1);
            result.setStatus("FAILED");
            result.setFailedAt("STEP2");
            return result;
        }

        // Step 3: Shipping
        boolean fail3 = "step3".equalsIgnoreCase(simulateFailAt);
        StepResultDto step3Result = step3Service.execute(order, fail3);
        result.addHistory("step3", step3Result);
        if (!step3Result.isSuccess()) {
            // Rollback Step 2, lalu Step 1
            StepResultDto rollback2 = step2Service.rollback(order);
            result.addHistory("rollback2", rollback2);
            StepResultDto rollback1 = step1Service.rollback(order);
            result.addHistory("rollback1", rollback1);
            result.setStatus("FAILED");
            result.setFailedAt("STEP3");
            return result;
        }

        result.setStatus("SUCCESS");
        return result;
    }
}
```

### Step Services

Setiap `StepService` menggunakan `WebClient` untuk memanggil mock endpoint:

```java
// Step1Service.java (Payment)
public StepResultDto execute(OrderRequest order, boolean simulateFail) {
    String uri = simulateFail ? "/mock1/fail" : "/mock1";
    Map<String, Object> response = webClient.post()
        .uri(uri)
        .bodyValue(order)
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
        .block();
    // ...return result
}

public StepResultDto rollback(OrderRequest order) {
    Map<String, Object> response = webClient.post()
        .uri("/mock1/rollback")
        .bodyValue(order)
        .retrieve()
        .bodyToMono(...)
        .block();
    // ...return result
}
```

## 4.5 Implementasi Mock Services

### MockController.java

MockController menyediakan 9 endpoint yang mensimulasikan tiga microservices:

| Service | Success | Fail | Rollback |
|---------|---------|------|----------|
| **Payment** | `POST /api/mock1` → return invoice | `POST /api/mock1/fail` → throw error | `POST /api/mock1/rollback` → return refund |
| **Inventory** | `POST /api/mock2` → return reservation ID | `POST /api/mock2/fail` → throw error | `POST /api/mock2/rollback` → return release |
| **Shipping** | `POST /api/mock3` → return tracking number | `POST /api/mock3/fail` → throw error | `POST /api/mock3/rollback` → return cancellation |

Setiap mock endpoint menghasilkan response JSON dengan data simulasi yang realistis, termasuk timestamp, ID transaksi, dan pesan dalam Bahasa Indonesia.

## 4.6 Implementasi Frontend

### index.html — Single Page Application

Frontend dibangun dengan HTML, CSS, dan JavaScript murni tanpa framework eksternal. Total 1468 baris kode.

### Struktur View

Halaman terdiri dari 5 view yang di-switch menggunakan JavaScript:

| View | ID | Fungsi |
|------|----|--------|
| **Auth** | `#view-auth` | Login & Register (tampilan default) |
| **Store** | `#view-store` | Grid produk, hero banner |
| **Checkout** | `#view-checkout` | Form pemesanan + simulasi error |
| **Processing** | `#view-processing` | Visualisasi real-time saga flow |
| **Result** | `#view-result` | Detail hasil transaksi |

### State Management (JavaScript)

```javascript
let products = [];           // Data produk dari API
let cart = [];               // Keranjang belanja
let selectedFail = '';       // Simulasi error (step1/step2/step3)
let orderResult = null;      // Response saga terakhir
let authToken = null;        // JWT token dari localStorage
let authUser = null;         // Data user dari localStorage
```

### Fitur Visualisasi Saga

Processing view menampilkan 3 kotak step yang terhubung dengan panah:

```
[💳 Payment] ──➡️ [📦 Inventory] ──➡️ [🚚 Shipping]
```

Setiap step memiliki state visual:
- **idle** (abu-abu) — menunggu
- **loading** (biru, animasi pulse) — sedang diproses
- **success** (hijau) — berhasil ✅
- **fail** (merah) — gagal ❌
- **rollback** (kuning) — kompensasi ↩️

## 4.7 Alur Saga Lengkap

### Flowchart Sukses

```
                    ┌─────────┐
                    │  START  │
                    └────┬────┘
                         │
                         ▼
              ┌──────────────────┐
              │  Login/Register  │
              └────────┬─────────┘
                       │
                       ▼
              ┌──────────────────┐
              │  Pilih Produk    │
              │  + Add to Cart   │
              └────────┬─────────┘
                       │
                       ▼
              ┌──────────────────┐
              │  Checkout        │
              │  (isi data +     │
              │   pilih error)   │
              └────────┬─────────┘
                       │
                       ▼
              ┌──────────────────┐
              │  POST /saga      │
              └────────┬─────────┘
                       │
              ┌────────┴────────┐
              │                 │
              ▼                 ▼
     ┌────────────────┐  ┌────────────────┐
     │ Step 1: Payment│  │ Step 1: Payment│
     │ /mock1         │  │ /mock1/fail    │
     └───────┬────────┘  └───────┬────────┘
             │ success           │ fail
             ▼                   ▼
     ┌────────────────┐  ┌────────────────┐
     │ Step 2: Invtry │  │  STATUS: FAIL  │
     │ /mock2         │  │  (No Rollback) │
     └───────┬────────┘  └────────────────┘
             │ success
             ▼
     ┌────────────────┐
     │ Step 3: Shipng │
     │ /mock3         │
     └───────┬────────┘
             │ success
             ▼
     ┌────────────────┐
     │ STATUS: SUCCESS│
     └────────────────┘
```

### Flowchart Gagal Step 2 (dengan Rollback)

```
     ┌────────────────┐
     │ Step 1: Payment│ ──── Success ✅
     └───────┬────────┘
             │
             ▼
     ┌────────────────┐
     │ Step 2: Invtry │ ──── Fail ❌ (simulated)
     └───────┬────────┘
             │
             ▼
     ┌────────────────────────┐
     │ Rollback Step 1: Refund│ ──── Success ↩️
     └────────────┬───────────┘
                  │
                  ▼
     ┌────────────────┐
     │ STATUS: FAILED │
     │ at STEP2       │
     └────────────────┘
```

---

<br>

# BAB V CARA PENGGUNAAN

## 5.1 Persiapan Environment

### Software yang Diperlukan

1. **Java Development Kit (JDK) 17** — [Download](https://adoptium.net/)
2. **PostgreSQL 14+** — [Download](https://www.postgresql.org/download/)
3. **Git** — [Download](https://git-scm.com/)
4. **Maven** (opsional) — [Download](https://maven.apache.org/)

### Setup Database

1. Buka **pgAdmin** atau terminal **psql**
2. Buat database baru:

```sql
CREATE DATABASE trws_db;
```

3. Pastikan kredensial di `application.properties` sesuai:

```properties
spring.datasource.username=postgres
spring.datasource.password=Milliano2005
```

## 5.2 Menjalankan Aplikasi

### Langkah 1: Clone Repository

```bash
git clone https://github.com/FGP27/MockService-kelompok6.git
cd MockService-kelompok6
```

### Langkah 2: Jalankan Aplikasi

```bash
mvnw spring-boot:run
```

Atau:

```bash
mvn spring-boot:run
```

### Langkah 3: Buka Browser

Akses **http://localhost:8080**

### Langkah 4: Login

Gunakan akun demo:

| Email | Password | Role |
|-------|----------|------|
| `Admin@gmail.com` | `admin123` | ADMIN |
| `user@gmail.com` | `user123` | USER |

Atau daftar akun baru melalui form Register.

## 5.3 Skenario Testing

### Skenario 1: Transaksi Sukses

| Langkah | Tindakan | Hasil |
|---------|----------|-------|
| 1 | Pilih error simulation: **None** | Toggle "None" aktif |
| 2 | Klik **Place Order** | Masuk ke Processing view |
| 3 | Tunggu animasi | Step 1 → hijau, Step 2 → hijau, Step 3 → hijau |
| 4 | Lihat Result | Status: **SUCCESS** ✅ |

### Skenario 2: Gagal di Payment (Step 1)

| Langkah | Tindakan | Hasil |
|---------|----------|-------|
| 1 | Pilih error simulation: **Step 1** | Toggle "Step 1" aktif |
| 2 | Klik **Place Order** | Masuk ke Processing view |
| 3 | Tunggu animasi | Step 1 → merah ❌, Step 2 & 3 idle |
| 4 | Lihat Result | Status: **FAILED at STEP1**, tanpa rollback |

### Skenario 3: Gagal di Inventory (Step 2)

| Langkah | Tindakan | Hasil |
|---------|----------|-------|
| 1 | Pilih error simulation: **Step 2** | Toggle "Step 2" aktif |
| 2 | Klik **Place Order** | Masuk ke Processing view |
| 3 | Tunggu animasi | Step 1 → hijau ✅, Step 2 → merah ❌, Step 1 rollback → kuning ↩️ |
| 4 | Lihat Result | Status: **FAILED at STEP2**, rollback: Refund |

### Skenario 4: Gagal di Shipping (Step 3)

| Langkah | Tindakan | Hasil |
|---------|----------|-------|
| 1 | Pilih error simulation: **Step 3** | Toggle "Step 3" aktif |
| 2 | Klik **Place Order** | Masuk ke Processing view |
| 3 | Tunggu animasi | Step 1 → hijau ✅, Step 2 → hijau ✅, Step 3 → merah ❌, rollback Step 2 & 1 → kuning ↩️ |
| 4 | Lihat Result | Status: **FAILED at STEP3**, rollback: Release stok + Refund |

---

<br>

# BAB VI PENUTUP

## 6.1 Kesimpulan

1. **Saga Orchestration Pattern** berhasil diimplementasikan menggunakan Java Spring Boot dengan WebClient untuk komunikasi antar service. Sistem mampu mengeksekusi tiga step transaksi (Payment → Inventory → Shipping) secara berurutan.

2. **Mekanisme Rollback** berjalan sesuai prinsip Saga Pattern: jika terjadi kegagalan di suatu step, sistem menjalankan compensating transaction secara terbalik (reverse order) untuk membatalkan step-step yang sudah berhasil.

3. **Visualisasi Real-time** pada frontend memudahkan pengguna memahami alur transaksi dengan menampilkan status setiap step (idle, loading, success, fail, rollback) secara animatif.

4. **Simulasi Error** memungkinkan pengujian berbagai skenario kegagalan (di Step 1, 2, atau 3) untuk memvalidasi mekanisme rollback.

5. **Autentikasi JWT** memberikan keamanan pada endpoint-endpoint yang membutuhkan otorisasi, sementara endpoint publik (produk, auth, mock) tetap dapat diakses tanpa token.

6. **Integrasi dengan PostgreSQL** menyediakan penyimpanan data pengguna yang persisten dengan memanfaatkan Hibernate ORM untuk auto DDL.

## 6.2 Saran

1. **Pemisahan Microservices**: Untuk simulasi yang lebih realistis, setiap service (Payment, Inventory, Shipping) dapat di-deploy secara terpisah sebagai aplikasi Spring Boot yang independen.

2. **Message Broker**: Penggunaan message broker seperti RabbitMQ atau Apache Kafka dapat meningkatkan reliabilitas komunikasi antar service dan mendukung event-driven choreography saga.

3. **Persistensi Transaksi**: Data transaksi saga dapat disimpan ke database untuk keperluan auditing, monitoring, dan recovery jika terjadi kegagalan sistem.

4. **Monitoring & Logging**: Implementasi centralized logging (ELK Stack) dan distributed tracing (Jaeger/Zipkin) untuk memantau kesehatan sistem.

5. **Containerization**: Penggunaan Docker dan Kubernetes untuk deployment, scaling, dan manajemen microservices yang lebih baik.

6. **Unit Testing**: Penambahan unit test dan integration test untuk menjamin kualitas kode dan mencegah regresi.

7. **Frontend Framework**: Migrasi frontend ke framework modern seperti React atau Vue.js untuk maintainability dan pengalaman pengguna yang lebih baik.

---

<br>

# LAMPIRAN

## Lampiran A: Contoh Response API

### A.1 Semua Step Sukses

```json
{
  "status": "SUCCESS",
  "failedAt": null,
  "history": {
    "step1": {
      "step": 1, "success": true, "rollback": false,
      "data": {
        "status": "SUCCESS",
        "service": "payment-service",
        "message": "Pembayaran Rp 150.000 via Bank BCA berhasil",
        "invoice": "INV-1784614971424",
        "amount": 150000
      }
    },
    "step2": {
      "step": 2, "success": true, "rollback": false,
      "data": {
        "status": "SUCCESS",
        "service": "inventory-service",
        "message": "Stok Sepatu Running (1 pcs) berhasil di-reserve",
        "reservationId": "RSV-1784614971447"
      }
    },
    "step3": {
      "step": 3, "success": true, "rollback": false,
      "data": {
        "status": "SUCCESS",
        "service": "shipping-service",
        "message": "Pengiriman ke Jakarta berhasil dijadwalkan",
        "trackingNumber": "JNE-1784614971460",
        "courier": "JNE Reguler"
      }
    }
  }
}
```

### A.2 Gagal di Step 2 (dengan Rollback)

```json
{
  "status": "FAILED",
  "failedAt": "STEP2",
  "history": {
    "step1": { "success": true, "data": { ... } },
    "step2": { "success": false, "error": "500: Stok tidak mencukupi" },
    "rollback1": { "success": true, "data": { "message": "Refund berhasil" } }
  }
}
```

## Lampiran B: Screenshot Aplikasi

Berikut adalah screenshot aplikasi yang dapat dilihat di folder `documentation/`:

| File | Deskripsi |
|------|-----------|
| `All_Step_Success.png` | Tampilan semua step sukses |
| `Step_1_Fail.png` | Tampilan gagal di Step 1 |
| `Step_2_Fail.png` | Tampilan gagal di Step 2 dengan rollback |
| `Step_3_Fail.png` | Tampilan gagal di Step 3 dengan rollback |
| `All_Fail_Web.png` | Tampilan error jaringan |

---

*Dokumen ini disusun sebagai laporan proyek mata kuliah Web Services — Kelompok 6*
