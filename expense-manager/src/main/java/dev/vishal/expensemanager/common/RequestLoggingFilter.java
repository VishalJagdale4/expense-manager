package dev.vishal.expensemanager.common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private static final int MAX_PAYLOAD_LENGTH = 4 * 1024; // 4 KB

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        ContentCachingRequestWrapper wrappedRequest =
                new ContentCachingRequestWrapper(request, MAX_PAYLOAD_LENGTH);

        long startTime = System.currentTimeMillis();


        long duration = System.currentTimeMillis() - startTime;

        if (log.isInfoEnabled()) {
            Map<String, Object> logMap = new HashMap<>();
            logMap.put("timestamp", Instant.now().toString());
            logMap.put("method", request.getMethod());
            logMap.put("url", request.getRequestURL());
            logMap.put("query", request.getQueryString());
            logMap.put("status", response.getStatus());
            logMap.put("duration_ms", duration);
            logMap.put("client_ip", request.getRemoteAddr());

            // Log body only for non-GET & small payloads
            if (!"GET".equalsIgnoreCase(request.getMethod())) {
                String body = getRequestBody(wrappedRequest);
                logMap.put("request_body", body);
            }

            log.info("Request: {}", logMap);

            filterChain.doFilter(wrappedRequest, response);
        }
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        if (content.length == 0) {
            return null;
        }

        // Prevent huge payload logging
        int maxLength = Math.min(content.length, 2000);
        return new String(content, 0, maxLength, StandardCharsets.UTF_8);
    }
}
