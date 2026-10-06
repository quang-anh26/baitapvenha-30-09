package com.example.studentmanagement.middleware;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Middleware (Filter trong Spring) - tuong duong RequestLoggingMiddleware cua ASP.NET Core.
 *  1. Ghi log request: [thoi gian] Method - Path
 *  2. Ghi log status code + thoi gian xu ly (ms) sau khi xu ly xong
 *  3. Chan URL co id <= 0 -> tra ve 400, khong cho vao Controller
 */
public class RequestLoggingMiddleware extends OncePerRequestFilter {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Khop: /students/{id}, /students/edit/{id}, /students/delete/{id} voi id la so nguyen (co the am)
    private static final Pattern ID_PATH = Pattern.compile("^/students/(?:edit/|delete/)?(-?\\d+)$");

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String time = LocalDateTime.now().format(TIME_FORMAT);
        String method = request.getMethod();
        String path = request.getRequestURI();
        long start = System.currentTimeMillis();

        System.out.println("[" + time + "] Method: " + method + " - Path: " + path);

        // ----- Chuc nang 3: chan id khong hop le -----
        if (isInvalidId(path)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("Student id không hợp lệ");
            logStatus(response, start);
            return; // khong goi chain.doFilter() => request KHONG vao Controller
        }

        try {
            chain.doFilter(request, response); // tuong duong: await _next(context);
        } finally {
            // ----- Chuc nang 2: log sau khi xu ly xong -----
            logStatus(response, start);
        }
    }

    private boolean isInvalidId(String path) {
        Matcher m = ID_PATH.matcher(path);
        if (!m.matches()) {
            return false;
        }
        try {
            return Long.parseLong(m.group(1)) <= 0;
        } catch (NumberFormatException e) {
            return true; // so qua lon -> coi nhu khong hop le
        }
    }

    private void logStatus(HttpServletResponse response, long start) {
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("Status Code: " + response.getStatus() + " (" + elapsed + " ms)");
    }
}