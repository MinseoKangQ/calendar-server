package com.server.calendar.util.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import com.server.calendar.util.response.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

@RequiredArgsConstructor
public class JwtTokenFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String token = extractToken(request);

        if (token != null && !jwtTokenProvider.validateToken(token)) {
            unauthorizedResponse(servletResponse, "존재하지 않는 토큰입니다.");
            return;
        }

        if (token != null) {
            setSecurityContext(token);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void setSecurityContext(String token) {
        Claims claims = jwtTokenProvider.getClaimsFromToken(token);
        String rolesStr = claims.get("roles", String.class);
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + rolesStr);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                claims.getSubject(), null, Collections.singletonList(authority));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }


    private void unauthorizedResponse(ServletResponse servletResponse, String message) throws IOException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        CustomApiResponse<?> apiResponse = CustomApiResponse.createFailWithoutData(HttpServletResponse.SC_UNAUTHORIZED, message);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(apiResponse);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse);
    }
}
