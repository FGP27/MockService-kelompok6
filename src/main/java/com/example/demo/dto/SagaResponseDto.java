package com.example.demo.dto;

import java.util.LinkedHashMap;
import java.util.Map;

public class SagaResponseDto {
    private String status;
    private String failedAt;
    private OrderRequest order;
    private Map<String, StepResultDto> history = new LinkedHashMap<>();

    public SagaResponseDto() {
    }

    public SagaResponseDto(String status, String failedAt) {
        this.status = status;
        this.failedAt = failedAt;
    }

    public void addHistory(String key, StepResultDto result) {
        this.history.put(key, result);
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getFailedAt() { return failedAt; }
    public void setFailedAt(String failedAt) { this.failedAt = failedAt; }
    public OrderRequest getOrder() { return order; }
    public void setOrder(OrderRequest order) { this.order = order; }
    public Map<String, StepResultDto> getHistory() { return history; }
    public void setHistory(Map<String, StepResultDto> history) { this.history = history; }
}
