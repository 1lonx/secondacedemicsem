package com.mipt.sem2.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Component
@Order(2)
@Slf4j
public class AccessLogFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                     FilterChain chain) throws ServletException, IOException {
        long start = System.currentTimeMillis();
        ContentCachingResponseWrapper wrapped = new ContentCachingResponseWrapper(response);

        try {
            chain.doFilter(request, wrapped);
        } finally {
            log.info("HTTP {} {} -> status={} timeMs={} trace={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    wrapped.getStatus(),
                    System.currentTimeMillis() - start,
                    MDC.get("traceId"));
            wrapped.copyBodyToResponse();
        }
    }
}
