# ALL SUCCESS STEP
{
  "status": "SUCCESS",
  "failedAt": null,
  "history": {
    "step1": {
      "step": 1,
      "success": true,
      "rollback": false,
      "data": {
        "status": "SUCCESS",
        "step": "1",
        "service": "payment-service",
        "message": "Pembayaran berhasil diproses",
        "transactionId": "TRX-1784614971424",
        "timestamp": "2026-07-21T13:22:51.424886300"
      },
      "error": null
    },
    "step2": {
      "step": 2,
      "success": true,
      "rollback": false,
      "data": {
        "status": "SUCCESS",
        "step": "2",
        "service": "inventory-service",
        "message": "Stok barang berhasil disimpan (reserved)",
        "reservationId": "RSV-1784614971447",
        "timestamp": "2026-07-21T13:22:51.447887200"
      },
      "error": null
    },
    "step3": {
      "step": 3,
      "success": true,
      "rollback": false,
      "data": {
        "status": "SUCCESS",
        "step": "3",
        "service": "shipping-service",
        "message": "Pengiriman berhasil dijadwalkan",
        "shippingId": "SHP-1784614971460",
        "timestamp": "2026-07-21T13:22:51.460908600"
      },
      "error": null
    }
  }
}

# STEP 1 Fail
{
  "status": "FAILED",
  "failedAt": "STEP1",
  "history": {
    "step1": {
      "step": 1,
      "success": false,
      "rollback": false,
      "data": null,
      "error": "500 Internal Server Error from GET http://localhost:8080/api/mock1/fail"
    }
  }
}

# STEP 2 Fail
{
  "status": "FAILED",
  "failedAt": "STEP2",
  "history": {
    "step1": {
      "step": 1,
      "success": true,
      "rollback": false,
      "data": {
        "status": "SUCCESS",
        "step": "1",
        "service": "payment-service",
        "message": "Pembayaran berhasil diproses",
        "transactionId": "TRX-1784614941158",
        "timestamp": "2026-07-21T13:22:21.158105900"
      },
      "error": null
    },
    "step2": {
      "step": 2,
      "success": false,
      "rollback": false,
      "data": null,
      "error": "500 Internal Server Error from GET http://localhost:8080/api/mock2/fail"
    },
    "rollback1": {
      "step": 1,
      "success": true,
      "rollback": true,
      "data": {
        "status": "SUCCESS",
        "step": "1",
        "service": "payment-service",
        "message": "Pembayaran berhasil dibatalkan (Refunded)",
        "timestamp": "2026-07-21T13:22:21.265639"
      },
      "error": null
    }
  }
}

# STEP 3 Fail

{
  "status": "FAILED",
  "failedAt": "STEP3",
  "history": {
    "step1": {
      "step": 1,
      "success": true,
      "rollback": false,
      "data": {
        "status": "SUCCESS",
        "step": "1",
        "service": "payment-service",
        "message": "Pembayaran berhasil diproses",
        "transactionId": "TRX-1784614657877",
        "timestamp": "2026-07-21T13:17:37.878494100"
      },
      "error": null
    },
    "step2": {
      "step": 2,
      "success": true,
      "rollback": false,
      "data": {
        "status": "SUCCESS",
        "step": "2",
        "service": "inventory-service",
        "message": "Stok barang berhasil disimpan (reserved)",
        "reservationId": "RSV-1784614657889",
        "timestamp": "2026-07-21T13:17:37.889497100"
      },
      "error": null
    },
    "step3": {
      "step": 3,
      "success": false,
      "rollback": false,
      "data": null,
      "error": "500 Internal Server Error from GET http://localhost:8080/api/mock3/fail"
    },
    "rollback2": {
      "step": 2,
      "success": true,
      "rollback": true,
      "data": {
        "status": "SUCCESS",
        "step": "2",
        "service": "inventory-service",
        "message": "Stok barang berhasil dikembalikan (released)",
        "timestamp": "2026-07-21T13:17:37.950049200"
      },
      "error": null
    },
    "rollback1": {
      "step": 1,
      "success": true,
      "rollback": true,
      "data": {
        "status": "SUCCESS",
        "step": "1",
        "service": "payment-service",
        "message": "Pembayaran berhasil dibatalkan (Refunded)",
        "timestamp": "2026-07-21T13:17:37.969041800"
      },
      "error": null
    }
  }
}
