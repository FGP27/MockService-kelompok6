# Saga Orchestration Pattern - Simulasi Distributed Transaction

Project ini mensimulasikan penerapan **Saga Orchestration Pattern** untuk menangani transaksi terdistribusi antar microservices.

---

## Tentang Project

Project ini mensimulasikan alur transaksi pemesanan yang terdiri dari 3 langkah berurutan:

1. **Payment Service** - Memproses pembayaran
2. **Inventory Service** - Memotong stok barang
3. **Shipping Service** - Menjadwalkan pengiriman

Jika salah satu langkah gagal, sistem akan secara otomatis menjalankan **Rollback** secara mundur untuk membatalkan semua langkah yang sudah berhasil sebelumnya.

---

## Logika Rollback

- Gagal di Step 2 -> Rollback Step 1
- Gagal di Step 3 -> Rollback Step 2, lalu Rollback Step 1

---

## Cara Testing

Jalankan aplikasi terlebih dahulu, kemudian akses endpoint berikut melalui browser atau Postman.

**Simulasi sukses semua step:**
```
GET http://localhost:8080/api/orchestration/saga
```

**Simulasi gagal di Step 2 (memicu rollback Step 1):**
```
GET http://localhost:8080/api/orchestration/saga?failAt=step2
```

**Simulasi gagal di Step 3 (memicu rollback Step 2 dan Step 1):**
```
GET http://localhost:8080/api/orchestration/saga?failAt=step3
```

---

## Struktur Kode

| `controller/OrchestrationController.java` | Endpoint utama untuk menjalankan saga |
| `controller/MockController.java` | Simulasi server microservice eksternal |
| `service/OrchestrationService.java` | Logika utama orchestrator dan rollback |
| `service/orchestrationstep` | Folder Berisikan Step Setiap Mock |
| `service/orchestrationstep/Step1Service.java` | Logika eksekusi dan rollback Payment |
| `service/orchestrationstep/Step2Service.java` | Logika eksekusi dan rollback Inventory |
| `service/orchestrationstep/Step3Service.java` | Logika eksekusi dan rollback Shipping |
| `dto/StepResultDto.java` | Struktur data hasil per step |
| `dto/SagaResponseDto.java` | Struktur data response saga secara keseluruhan |
