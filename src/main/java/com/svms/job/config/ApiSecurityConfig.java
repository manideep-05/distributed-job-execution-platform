package com.svms.job.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.svms.job.security.ApiKeyAuthFilter;

@Configuration
public class ApiSecurityConfig {

    @Bean
    public FilterRegistrationBean<ApiKeyAuthFilter> apiKeyFilter(
            ApiKeyAuthFilter filter) {
        FilterRegistrationBean<ApiKeyAuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns("/api/jobs/*");
        registrationBean.setOrder(1); // Set precedence
        return registrationBean;
    }

}
