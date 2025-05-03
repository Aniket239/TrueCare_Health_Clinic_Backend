package com.True.Care.security;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public JwtFilter jwtFilterBean() {
        return new JwtFilter();
    }

    @Bean
    public FilterRegistrationBean<JwtFilter> jwtFilter(JwtFilter jwtFilter) {
        FilterRegistrationBean<JwtFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(jwtFilter);
        registrationBean.addUrlPatterns("/users"); // Protect this endpoint
        return registrationBean;
    }
}
