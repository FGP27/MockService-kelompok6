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
public class Step1Service {

    private static final Logger log = LoggerFactory.getLogger(Step1Service.class);

    private final WebClient webClient;

    @Autowired
    public Step1Service(WebClient webClient) {
        this.webClient = webClient;
    }

    public StepResultDto execute(OrderRequest order, boolean simulateFail) {
        String uri = simulateFail ? "/mock1/fail" : "/mock1";
        String name = order != null ? order.getCustomerName() : "Customer";
        log.info("[ORCHESTRATION][STEP 1] === MEMPROSES PEMBAYARAN === Pelanggan: {}, Total: Rp {}, Metode: {}",
                name, order != null ? order.getPrice() * order.getQuantity() : 0,
                order != null ? order.getPaymentMethod() : "-");

        StepResultDto result = new StepResultDto();
        result.setStep(1);
        result.setRollback(false);

        try {
            Map<String, Object> response = webClient.post()
                    .uri(uri)
                    .bodyValue(order != null ? order : new OrderRequest())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            log.info("[ORCHESTRATION][STEP 1] === PEMBAYARAN BERHASIL === Invoice: {}, Jumlah: Rp {}",
                    response.get("invoice"), response.get("amount"));

            result.setSuccess(true);
            result.setData(response);

        } catch (Exception e) {
            log.error("[ORCHESTRATION][STEP 1] === PEMBAYARAN GAGAL === Error: {}", e.getMessage());

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
        log.info("[ORCHESTRATION][STEP 1] === ROLLBACK PAYMENT === Mengembalikan dana untuk {}", name);

        StepResultDto result = new StepResultDto();
        result.setStep(1);
        result.setRollback(true);

        try {
            Map<String, Object> response = webClient.post()
                    .uri("/mock1/rollback")
                    .bodyValue(order != null ? order : new OrderRequest())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            log.info("[ORCHESTRATION][STEP 1] === ROLLBACK PAYMENT BERHASIL === Refund sukses");

            result.setSuccess(true);
            result.setData(response);

        } catch (Exception e) {
            log.error("[ORCHESTRATION][STEP 1] === ROLLBACK PAYMENT GAGAL === Error: {}", e.getMessage());

            result.setSuccess(false);
            result.setError(e.getMessage());
        }

        return result;
    }

    public StepResultDto rollback() {
        return rollback(null);
    }
}
