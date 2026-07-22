package com.example.demo.service.orchestrationstep;

import com.example.demo.dto.OrderRequest;
import com.example.demo.dto.StepResultDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class Step2Service {

    private static final Logger log = LoggerFactory.getLogger(Step2Service.class);

    private final WebClient webClient;

    @Autowired
    public Step2Service(WebClient webClient) {
        this.webClient = webClient;
    }

    public StepResultDto execute(OrderRequest order, boolean simulateFail) {
        String uri = simulateFail ? "/mock2/fail" : "/mock2";
        String product = order != null ? order.getProduct() : "Produk";
        int qty = order != null ? order.getQuantity() : 1;
        log.info("[ORCHESTRATION][STEP 2] === MENYIAPKAN INVENTORI === Produk: {}, Jumlah: {}", product, qty);

        StepResultDto result = new StepResultDto();
        result.setStep(2);
        result.setRollback(false);

        try {
            Map<String, Object> response = webClient.post()
                    .uri(uri)
                    .bodyValue(order != null ? order : new OrderRequest())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            log.info("[ORCHESTRATION][STEP 2] === INVENTORI BERHASIL === Reserve ID: {}, Produk: {}",
                    response.get("reservationId"), response.get("product"));

            result.setSuccess(true);
            result.setData(response);

        } catch (Exception e) {
            log.error("[ORCHESTRATION][STEP 2] === INVENTORI GAGAL === Error: {}", e.getMessage());

            result.setSuccess(false);
            result.setError(e.getMessage());
        }

        return result;
    }

    public StepResultDto execute() {
        return execute(null, false);
    }

    public StepResultDto rollback(OrderRequest order) {
        String product = order != null ? order.getProduct() : "Produk";
        int qty = order != null ? order.getQuantity() : 1;
        log.info("[ORCHESTRATION][STEP 2] === ROLLBACK INVENTORI === Mengembalikan stok {} x{} ke gudang", product, qty);

        StepResultDto result = new StepResultDto();
        result.setStep(2);
        result.setRollback(true);

        try {
            Map<String, Object> response = webClient.post()
                    .uri("/mock2/rollback")
                    .bodyValue(order != null ? order : new OrderRequest())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            log.info("[ORCHESTRATION][STEP 2] === ROLLBACK INVENTORI BERHASIL === Stok dikembalikan");

            result.setSuccess(true);
            result.setData(response);

        } catch (Exception e) {
            log.error("[ORCHESTRATION][STEP 2] === ROLLBACK INVENTORI GAGAL === Error: {}", e.getMessage());

            result.setSuccess(false);
            result.setError(e.getMessage());
        }

        return result;
    }

    public StepResultDto rollback() {
        return rollback(null);
    }
}
