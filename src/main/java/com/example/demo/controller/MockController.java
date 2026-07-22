package com.example.demo.controller;

import com.example.demo.dto.OrderRequest;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MockController {

    @RequestMapping(value = "/mock1", method = {RequestMethod.GET, RequestMethod.POST})
    public Map<String, Object> mock1(@RequestBody(required = false) OrderRequest order) {
        String product = order != null ? order.getProduct() : "Produk";
        int qty = order != null ? order.getQuantity() : 1;
        double price = order != null ? order.getPrice() : 0;
        String method = order != null ? order.getPaymentMethod() : "Bank BCA";
        String name = order != null ? order.getCustomerName() : "Customer";
        double total = price * qty;

        System.out.println("[MOCK1 - PAYMENT] Pembayaran dari " + name + " untuk " + product + " x" + qty + " = Rp " + total);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "SUCCESS");
        response.put("step", "1");
        response.put("service", "payment-service");
        response.put("message", "Pembayaran Rp " + String.format("%,.0f", total) + " via " + method + " berhasil");
        response.put("invoice", "INV-" + System.currentTimeMillis());
        response.put("paymentMethod", method);
        response.put("amount", total);
        response.put("customerName", name);
        response.put("timestamp", LocalDateTime.now().toString());

        return response;
    }

    @RequestMapping(value = "/mock1/rollback", method = {RequestMethod.GET, RequestMethod.POST})
    public Map<String, Object> mock1Rollback(@RequestBody(required = false) OrderRequest order) {
        String method = order != null ? order.getPaymentMethod() : "Bank BCA";
        double total = order != null ? order.getPrice() * order.getQuantity() : 0;
        String name = order != null ? order.getCustomerName() : "Customer";

        System.out.println("[MOCK1 - PAYMENT] Refund untuk " + name);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "SUCCESS");
        response.put("step", "1");
        response.put("service", "payment-service");
        response.put("message", "Refund Rp " + String.format("%,.0f", total) + " ke " + method + " berhasil");
        response.put("customerName", name);
        response.put("amount", total);
        response.put("timestamp", LocalDateTime.now().toString());

        return response;
    }

    @RequestMapping(value = "/mock1/fail", method = {RequestMethod.GET, RequestMethod.POST})
    public Map<String, Object> mock1Fail(@RequestBody(required = false) OrderRequest order) {
        String name = order != null ? order.getCustomerName() : "Customer";
        System.out.println("[MOCK1 - PAYMENT] Pembayaran " + name + " GAGAL");
        throw new RuntimeException("Pembayaran gagal: saldo tidak mencukupi");
    }

    @RequestMapping(value = "/mock2", method = {RequestMethod.GET, RequestMethod.POST})
    public Map<String, Object> mock2(@RequestBody(required = false) OrderRequest order) {
        String product = order != null ? order.getProduct() : "Produk";
        int qty = order != null ? order.getQuantity() : 1;
        String name = order != null ? order.getCustomerName() : "Customer";

        System.out.println("[MOCK2 - INVENTORY] Cek stok " + product + " x" + qty + " untuk " + name);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "SUCCESS");
        response.put("step", "2");
        response.put("service", "inventory-service");
        response.put("message", "Stok " + product + " (" + qty + " pcs) berhasil di-reserve");
        response.put("product", product);
        response.put("quantity", qty);
        response.put("reservationId", "RSV-" + System.currentTimeMillis());
        response.put("warehouse", "Jakarta Pusat");
        response.put("timestamp", LocalDateTime.now().toString());

        return response;
    }

    @RequestMapping(value = "/mock2/rollback", method = {RequestMethod.GET, RequestMethod.POST})
    public Map<String, Object> mock2Rollback(@RequestBody(required = false) OrderRequest order) {
        String product = order != null ? order.getProduct() : "Produk";
        int qty = order != null ? order.getQuantity() : 1;

        System.out.println("[MOCK2 - INVENTORY] Release stok " + product + " x" + qty);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "SUCCESS");
        response.put("step", "2");
        response.put("service", "inventory-service");
        response.put("message", "Stok " + product + " (" + qty + " pcs) berhasil dikembalikan ke gudang");
        response.put("product", product);
        response.put("quantity", qty);
        response.put("timestamp", LocalDateTime.now().toString());

        return response;
    }

    @RequestMapping(value = "/mock2/fail", method = {RequestMethod.GET, RequestMethod.POST})
    public Map<String, Object> mock2Fail(@RequestBody(required = false) OrderRequest order) {
        String product = order != null ? order.getProduct() : "Produk";
        System.out.println("[MOCK2 - INVENTORY] Stok " + product + " GAGAL di-reserve");
        throw new RuntimeException("Stok " + product + " tidak mencukupi");
    }

    @RequestMapping(value = "/mock3", method = {RequestMethod.GET, RequestMethod.POST})
    public Map<String, Object> mock3(@RequestBody(required = false) OrderRequest order) {
        String product = order != null ? order.getProduct() : "Paket";
        String name = order != null ? order.getCustomerName() : "Customer";
        String address = order != null ? order.getAddress() : "Jakarta";

        System.out.println("[MOCK3 - SHIPPING] Kirim " + product + " ke " + name + " (" + address + ")");

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "SUCCESS");
        response.put("step", "3");
        response.put("service", "shipping-service");
        response.put("message", "Pengiriman " + product + " ke " + address + " berhasil dijadwalkan");
        response.put("shippingId", "SHP-" + System.currentTimeMillis());
        response.put("courier", "JNE Reguler");
        response.put("trackingNumber", "JNE-" + System.currentTimeMillis());
        response.put("destination", address);
        response.put("customerName", name);
        response.put("estimatedDelivery", LocalDateTime.now().plusDays(3).toString());
        response.put("timestamp", LocalDateTime.now().toString());

        return response;
    }

    @RequestMapping(value = "/mock3/rollback", method = {RequestMethod.GET, RequestMethod.POST})
    public Map<String, Object> mock3Rollback(@RequestBody(required = false) OrderRequest order) {
        String product = order != null ? order.getProduct() : "Paket";
        String name = order != null ? order.getCustomerName() : "Customer";

        System.out.println("[MOCK3 - SHIPPING] Batal kirim " + product + " untuk " + name);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "SUCCESS");
        response.put("step", "3");
        response.put("service", "shipping-service");
        response.put("message", "Pengiriman " + product + " untuk " + name + " berhasil dibatalkan");
        response.put("customerName", name);
        response.put("timestamp", LocalDateTime.now().toString());

        return response;
    }

    @RequestMapping(value = "/mock3/fail", method = {RequestMethod.GET, RequestMethod.POST})
    public Map<String, Object> mock3Fail(@RequestBody(required = false) OrderRequest order) {
        String address = order != null ? order.getAddress() : "alamat";
        System.out.println("[MOCK3 - SHIPPING] Pengiriman ke " + address + " GAGAL");
        throw new RuntimeException("Alamat pengiriman tidak valid: " + address);
    }
}
