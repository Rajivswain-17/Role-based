package com.example.CampusAccessManager.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // Extract JWT token from request header
            String jwt = getJwtFromRequest(request);

            // If token exists and is valid
            if (StringUtils.hasText(jwt) && jwtUtil.validateToken(jwt)) {
                
                // Get username from token
                String username = jwtUtil.getUsernameFromToken(jwt);

                // Load user details from database
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Create authentication object
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, 
                        null, 
                        userDetails.getAuthorities()
                    );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set authentication in Spring Security context
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            System.out.println("Could not set user authentication: " + ex.getMessage());
        }

        // Continue to the next filter
        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header
     * Header format: "Bearer eyJhbGciOiJIUzUxMiJ9..."
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        return null;
    }
}
