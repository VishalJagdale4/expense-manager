package dev.commonlib.logger;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        ContentCachingRequestWrapper req =
                new ContentCachingRequestWrapper(request, 1024 * 16);
        ContentCachingResponseWrapper res =
                new ContentCachingResponseWrapper(response);

        // Log request BEFORE execution
        log.info(
                "REQUEST | time={} | method={} | uri={} | query={} | ip={} | body={}",
                LocalDateTime.now(),
                req.getMethod(),
                req.getRequestURI(),
                req.getQueryString(),
                getClientIp(req),
                getRequestBody(req)
        );

        try {
            filterChain.doFilter(req, res);
        } catch (Exception ex) {

            // 🔥 Log response ONLY on error
            log.error(
                    "ERROR | status={} | method={} | uri={} | query={} | ip={} | message={}",
                    res.getStatus(),
                    req.getMethod(),
                    req.getRequestURI(),
                    req.getQueryString(),
                    getClientIp(req),
                    ex.getMessage(),
                    ex
            );

            throw ex;
        } finally {
            res.copyBodyToResponse();
        }
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        if (request.getContentType() != null &&
                request.getContentType().contains("multipart")) {
            return "[SKIPPED]";
        }

        byte[] body = request.getContentAsByteArray();
        return body.length > 0
                ? new String(body, StandardCharsets.UTF_8)
                : "";
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        return ip != null ? ip.split(",")[0] : request.getRemoteAddr();
    }
}
