package com.phirom_02.blog_api.security;

import com.phirom_02.blog_api.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Custom filter for handling JWT authentication in incoming requests.
 * This filter extracts the JWT token from the request's Authorization header,
 * validates the token using {@link AuthService}, and sets the authentication
 * in the {@link SecurityContextHolder}.
 * If the token is valid, it extracts the user ID and attaches it to the request.
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthService authService;

    /**
     * Filters incoming requests to extract and validate the JWT token.
     * If the token is valid, it sets the authentication context and attaches the user ID to the request.
     *
     * @param request     the incoming HTTP request
     * @param response    the HTTP response
     * @param filterChain the filter chain to pass the request and response
     * @throws ServletException if an error occurs during filtering
     * @throws IOException      if an I/O error occurs during filtering
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // Extract the JWT token from the Authorization header
            String token = extractToken(request);

            // If the token is not null, validate and set the authentication
            if (token != null) {
                // Validate the token and get the user details
                UserDetails userDetails = authService.validateToken(token);

                // Create an authentication token with the user details and set it in the security context
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // Set the authentication in the security context
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // If the user is authenticated, set the user ID in the request for later use
                if (userDetails instanceof BlogUserDetails) {
                    request.setAttribute("userId", ((BlogUserDetails) userDetails).getId());
                }
            }
        } catch (Exception e) {
            // If token validation fails, log a warning and proceed without authentication
        }

        // Continue with the filter chain (request will move to the next filter or controller)
        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the JWT token from the Authorization header in the request.
     *
     * @param request the incoming HTTP request
     * @return the JWT token, or null if the token is not present
     */
    private String extractToken(HttpServletRequest request) {
        // Get the Authorization header from the request
        String bearerToken = request.getHeader("Authorization");

        // If the Authorization header is present and starts with "Bearer ", extract the token
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove "Bearer " prefix and return the token
        }

        // If the token is not found, return null
        return null;
    }
}
