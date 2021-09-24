package ee.ria.tara.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public class SessionManagementFilter extends OncePerRequestFilter {
    private static final RequestMatcher LOGIN_REQUEST_MATCHER = new AntPathRequestMatcher("/login");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (LOGIN_REQUEST_MATCHER.matches(request)) {
            createNewSession(request, session);
            resetXsrfToken(response);
        }
        filterChain.doFilter(request, response);
    }

    private void resetXsrfToken(HttpServletResponse response) {
        Cookie cookie = new Cookie("XSRF-TOKEN", UUID.randomUUID().toString());
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    private void createNewSession(HttpServletRequest request, HttpSession session) {
        invalidateSession(session);
        request.getSession(true);
    }

    private void invalidateSession(HttpSession session) {
        if (session != null) {
            log.info("Session has been invalidated: {}", session.getId());
            session.invalidate();
        }
    }
}