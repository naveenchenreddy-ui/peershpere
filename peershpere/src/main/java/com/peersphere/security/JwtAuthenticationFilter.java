package com.peersphere.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * This filter runs on EVERY HTTP request — before it reaches any controller.
 * Think of it as a security checkpoint at the entrance.
 *
 * OncePerRequestFilter — guarantees this filter runs exactly once per request.
 *
 * What it does:
 * 1. Checks if the request has an "Authorization: Bearer <token>" header
 * 2. If yes, extracts and validates the JWT
 * 3. If valid, tells Spring Security "this user is authenticated"
 * 4. Passes the request along to the next filter or controller
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Step 1: Look for the Authorization header
        final String authHeader = request.getHeader("Authorization");

        // If no Authorization header or it doesn't start with "Bearer ", skip this filter
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Step 2: Extract the JWT token (remove "Bearer " prefix)
        final String jwt = authHeader.substring(7);

        // Step 3: Extract the email from the token
        final String userEmail = jwtService.extractEmail(jwt);

        // Step 4: If we have an email AND user is not already authenticated
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load the user from the database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // Step 5: Validate the token
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // Step 6: Create an authentication object and set it in the security context
                // This tells Spring Security "yes, this user is authenticated for this request"
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Step 7: Continue to the next filter or the controller
        filterChain.doFilter(request, response);
    }
}