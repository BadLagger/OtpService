package sf.mifi.grechko.config;

import com.nimbusds.jose.JOSEException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import sf.mifi.grechko.dto.UserRole;
import sf.mifi.grechko.repository.UserRepository;
import sf.mifi.grechko.service.JwtService;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                String username = jwtService.parseToken(token);
                UserRole role = userRepository.findRoleByLogin(username);
                
                if (role == null) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Wrong Role Id");
                    return;
                }

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        username, null, List.of(new SimpleGrantedAuthority(role.toString()))
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (ParseException | JOSEException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
