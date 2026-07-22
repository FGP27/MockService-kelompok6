package com.example.demo.service;

import com.example.demo.dto.OrderRequest;
import com.example.demo.dto.SagaResponseDto;
import com.example.demo.dto.StepResultDto;
import com.example.demo.service.orchestrationstep.Step1Service;
import com.example.demo.service.orchestrationstep.Step2Service;
import com.example.demo.service.orchestrationstep.Step3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrchestrationService {

    private static final Logger log = LoggerFactory.getLogger(OrchestrationService.class);

    private final Step1Service step1Service;
    private final Step2Service step2Service;
    private final Step3Service step3Service;

    @Autowired
    public OrchestrationService(Step1Service step1Service, Step2Service step2Service, Step3Service step3Service) {
        this.step1Service = step1Service;
        this.step2Service = step2Service;
        this.step3Service = step3Service;
    }

    public StepResultDto callStep1() {
        return step1Service.execute();
    }

    public StepResultDto callStep2() {
        return step2Service.execute();
    }

    public StepResultDto callStep3() {
        return step3Service.execute();
    }

    public SagaResponseDto executeSaga(OrderRequest order, String simulateFailAt) {
        log.info("[SAGA] ============ MEMULAI TRANSAKSI E-COMMERCE ============");
        log.info("[SAGA] Produk: {}, Qty: {}, Total: Rp {}",
                order.getProduct(), order.getQuantity(), order.getPrice() * order.getQuantity());
        log.info("[SAGA] Pelanggan: {}, Metode Bayar: {}, Alamat: {}",
                order.getCustomerName(), order.getPaymentMethod(), order.getAddress());

        if (simulateFailAt != null) {
            log.info("[SAGA] Simulasi error diaktifkan untuk: {}", simulateFailAt);
        }

        SagaResponseDto finalResult = new SagaResponseDto();
        finalResult.setOrder(order);

        boolean fail1 = "step1".equalsIgnoreCase(simulateFailAt);
        StepResultDto step1Result = step1Service.execute(order, fail1);
        finalResult.addHistory("step1", step1Result);

        if (!step1Result.isSuccess()) {
            log.error("[SAGA] === TRANSAKSI GAGAL DI STEP 1 (PAYMENT) ===");
            finalResult.setStatus("FAILED");
            finalResult.setFailedAt("STEP1");
            return finalResult;
        }

        boolean fail2 = "step2".equalsIgnoreCase(simulateFailAt);
        StepResultDto step2Result = step2Service.execute(order, fail2);
        finalResult.addHistory("step2", step2Result);

        if (!step2Result.isSuccess()) {
            log.error("[SAGA] === TRANSAKSI GAGAL DI STEP 2 (INVENTORY) ===");
            log.error("[SAGA] === ROLLBACK: REFUND PEMBAYARAN... ===");
            StepResultDto rollback1 = step1Service.rollback(order);
            finalResult.addHistory("rollback1", rollback1);

            finalResult.setStatus("FAILED");
            finalResult.setFailedAt("STEP2");
            return finalResult;
        }

        boolean fail3 = "step3".equalsIgnoreCase(simulateFailAt);
        StepResultDto step3Result = step3Service.execute(order, fail3);
        finalResult.addHistory("step3", step3Result);

        if (!step3Result.isSuccess()) {
            log.error("[SAGA] === TRANSAKSI GAGAL DI STEP 3 (SHIPPING) ===");
            log.error("[SAGA] === ROLLBACK: RELEASE STOK & REFUND... ===");
            StepResultDto rollback2 = step2Service.rollback(order);
            finalResult.addHistory("rollback2", rollback2);

            StepResultDto rollback1 = step1Service.rollback(order);
            finalResult.addHistory("rollback1", rollback1);

            finalResult.setStatus("FAILED");
            finalResult.setFailedAt("STEP3");
            return finalResult;
        }

        log.info("[SAGA] ============ TRANSAKSI E-COMMERCE BERHASIL ============");
        finalResult.setStatus("SUCCESS");
        return finalResult;
    }

    public SagaResponseDto executeSaga(String simulateFailAt) {
        OrderRequest dummy = new OrderRequest("Produk", 1, 0, "Customer", "Alamat", "Bank BCA");
        return executeSaga(dummy, simulateFailAt);
    }
}
