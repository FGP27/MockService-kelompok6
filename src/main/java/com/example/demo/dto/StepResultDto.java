package com.example.demo.dto;

public class StepResultDto {
    private int step;
    private boolean success;
    private boolean rollback;
    private Object data;
    private String error;

    public StepResultDto() {
    }

    public StepResultDto(int step, boolean success, boolean rollback, Object data, String error) {
        this.step = step;
        this.success = success;
        this.rollback = rollback;
        this.data = data;
        this.error = error;
    }

    // Getters and Setters

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isRollback() {
        return rollback;
    }

    public void setRollback(boolean rollback) {
        this.rollback = rollback;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
