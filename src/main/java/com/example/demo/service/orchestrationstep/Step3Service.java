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
public class Step3Service {

    private static final Logger log = LoggerFactory.getLogger(Step3Service.class);

    private final WebClient webClient;

    @Autowired
    public Step3Service(WebClient webClient) {
        this.webClient = webClient;
    }

    public StepResultDto execute(OrderRequest order, boolean simulateFail) {
        String uri = simulateFail ? "/mock3/fail" : "/mock3";
        String name = order != null ? order.getCustomerName() : "Customer";
        String address = order != null ? order.getAddress() : "Alamat tujuan";
        String product = order != null ? order.getProduct() : "Produk";
        log.info("[ORCHESTRATION][STEP 3] === MENJADWALKAN PENGIRIMAN === Pelanggan: {}, Produk: {}, Alamat: {}", name, product, address);

        StepResultDto result = new StepResultDto();
        result.setStep(3);
        result.setRollback(false);

        try {
            Map<String, Object> response = webClient.post()
                    .uri(uri)
                    .bodyValue(order != null ? order : new OrderRequest())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            log.info("[ORCHESTRATION][STEP 3] === PENGIRIMAN BERHASIL === Resi: {}, Kurir: {}",
                    response.get("trackingNumber"), response.get("courier"));

            result.setSuccess(true);
            result.setData(response);

        } catch (Exception e) {
            log.error("[ORCHESTRATION][STEP 3] === PENGIRIMAN GAGAL === Error: {}", e.getMessage());

            result.setSuccess(false);
            result.setError(e.getMessage());
        }

        return result;
    }

    public StepResultDto execute() {
        return execute(null, false);
    }

    public StepResultDto rollback(OrderRequest order) {
        String name = order != null ? order.getCustomerName() : "Customer";
        String product = order != null ? order.getProduct() : "Produk";
        log.info("[ORCHESTRATION][STEP 3] === ROLLBACK SHIPPING === Membatalkan pengiriman {} untuk {}", product, name);

        StepResultDto result = new StepResultDto();
        result.setStep(3);
        result.setRollback(true);

        try {
            Map<String, Object> response = webClient.post()
                    .uri("/mock3/rollback")
                    .bodyValue(order != null ? order : new OrderRequest())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            log.info("[ORCHESTRATION][STEP 3] === ROLLBACK SHIPPING BERHASIL === Pengiriman dibatalkan");

            result.setSuccess(true);
            result.setData(response);

        } catch (Exception e) {
            log.error("[ORCHESTRATION][STEP 3] === ROLLBACK SHIPPING GAGAL === Error: {}", e.getMessage());

            result.setSuccess(false);
            result.setError(e.getMessage());
        }

        return result;
    }

    public StepResultDto rollback() {
        return rollback(null);
    }
}
