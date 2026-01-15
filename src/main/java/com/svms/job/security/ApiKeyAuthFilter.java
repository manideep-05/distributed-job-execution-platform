package com.svms.job.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.svms.job.api.ratelimit.RateLimiterService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    @Value("${job.api.key}")
    private String expectedApiKey;

    private final RateLimiterService rateLimiterService;

    public ApiKeyAuthFilter(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String apiKey = request.getHeader("X-API-KEY");
        if (apiKey == null || !apiKey.equals(expectedApiKey)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Unauthorized access - invalid API key");
            return;
        }

        if (!rateLimiterService.allow(apiKey)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Rate limit exceeded");
            return;
        }
        filterChain.doFilter(request, response);
    }

}
