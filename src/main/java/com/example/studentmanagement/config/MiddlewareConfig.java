package com.example.studentmanagement.config;

import com.example.studentmanagement.middleware.RequestLoggingMiddleware;
import com.example.studentmanagement.middleware.MiddlewareStatusStore;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * Gan middleware vao pipeline (tuong duong app.UseMiddleware<...>() trong Program.cs).
 * Filter luon chay TRUOC DispatcherServlet/Controller; thu tu chay duoc quyet dinh boi setOrder().
 */
@Configuration
public class MiddlewareConfig {

    @Bean
    public FilterRegistrationBean<RequestLoggingMiddleware> requestLoggingFilter(MiddlewareStatusStore statusStore) {
        FilterRegistrationBean<RequestLoggingMiddleware> bean = new FilterRegistrationBean<>();
        bean.setFilter(new RequestLoggingMiddleware(statusStore));
        bean.addUrlPatterns("/*");
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE); // so nho = chay truoc
        return bean;
    }
}
