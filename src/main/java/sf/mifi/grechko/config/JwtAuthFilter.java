package sf.mifi.grechko.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sf.mifi.grechko.entity.User;
import sf.mifi.grechko.repository.UserRepository;
import sf.mifi.grechko.service.JwtService;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        log.debug("Get request with checking");
        String path = request.getServletPath();

        if (path.startsWith("/auth")) {
            log.info("Authorization request");
            filterChain.doFilter(request, response);
            return;
        }


        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error("No or wrong authorization header");
            filterChain.doFilter(request, response);
            return;
        }

        log.debug("Header exists");

        String jwt = authHeader.substring(7);

        if (jwtService.isBlacklisted(jwt)) {
            log.error("Token is blocked for {}", jwtService.extractUsername(jwt));
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Token is blocked (logout)");
            return;
        }

        log.debug("Header not in blacklist");

        username = jwtService.extractUsername(jwt);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userRep = userRepository.findByLogin(username);

            if (userRep.isPresent()) {
                User user = userRep.get();
                log.debug("User Role: {}", user.getRole());
                UserDetails userDetails = org.springframework.security.core.userdetails.User
                        .withUsername(user.getLogin())
                        .password(user.getPasswd())
                        .authorities(user.getRole().toString())
                        .build();

                if (jwtService.isTokenValid(jwt, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } else {
                log.error("User {} not found in DB", username);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("User not found");
                return;
            }
        }
        filterChain.doFilter(request, response);
        log.debug("Exit from checking");
    }
}
