package com.example.studentmanagement.controller;

import com.example.studentmanagement.middleware.MiddlewareStatusStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MiddlewareStatusController {

    private final MiddlewareStatusStore statusStore;

    public MiddlewareStatusController(MiddlewareStatusStore statusStore) {
        this.statusStore = statusStore;
    }

    @GetMapping("/middleware/status")
    public MiddlewareStatusStore.RequestStatus latestStatus() {
        return statusStore.getLatest();
    }
}
