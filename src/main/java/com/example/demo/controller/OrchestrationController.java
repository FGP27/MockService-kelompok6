package com.example.demo.controller;

import com.example.demo.dto.OrderRequest;
import com.example.demo.dto.SagaResponseDto;
import com.example.demo.dto.StepResultDto;
import com.example.demo.service.OrchestrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orchestration")
public class OrchestrationController {

    private final OrchestrationService orchestrationService;

    @Autowired
    public OrchestrationController(OrchestrationService orchestrationService) {
        this.orchestrationService = orchestrationService;
    }

    @GetMapping("/step1")
    public StepResultDto testStep1() {
        return orchestrationService.callStep1();
    }

    @GetMapping("/step2")
    public StepResultDto testStep2() {
        return orchestrationService.callStep2();
    }

    @GetMapping("/step3")
    public StepResultDto testStep3() {
        return orchestrationService.callStep3();
    }

    @PostMapping("/saga")
    public SagaResponseDto executeSaga(
            @RequestBody OrderRequest order,
            @RequestParam(required = false) String failAt) {
        return orchestrationService.executeSaga(order, failAt);
    }

    @GetMapping("/saga")
    public SagaResponseDto executeSagaGet(@RequestParam(required = false) String failAt) {
        return orchestrationService.executeSaga(failAt);
    }
}
