package com.example.studentmanagement.middleware;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/** Holds the most recently completed request status for the web UI. */
@Component
public class MiddlewareStatusStore {

    private volatile RequestStatus latest = new RequestStatus("-", "-", 0, 0, null);

    public void update(String method, String path, int statusCode, long elapsedMs) {
        latest = new RequestStatus(method, path, statusCode, elapsedMs, LocalDateTime.now());
    }

    public RequestStatus getLatest() {
        return latest;
    }

    public record RequestStatus(String method, String path, int statusCode,
                                long elapsedMs, LocalDateTime time) {
    }
}
