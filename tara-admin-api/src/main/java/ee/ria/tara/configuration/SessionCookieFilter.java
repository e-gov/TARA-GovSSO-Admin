package ee.ria.tara.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class SessionCookieFilter extends GenericFilterBean {

    private final String SESSION_COOKIE_NAME = "JSESSIONID";
    private final String SAME_SITE_ATTRIBUTE_VALUES = "; HttpOnly; Secure; SameSite=Strict; Path=/;";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) response;
        Cookie[] cookies = ((HttpServletRequest) request).getCookies();
        if (cookies != null && cookies.length > 0) {
            List<Cookie> cookieList = Arrays.asList(cookies);
            for (Cookie cookie: cookieList) {
                if (SESSION_COOKIE_NAME.equals(cookie.getName()))
                    resp.setHeader(HttpHeaders.SET_COOKIE, cookie.getName() + "=" + cookie.getValue() + SAME_SITE_ATTRIBUTE_VALUES);
            }
        }

        chain.doFilter(request, response);
    }
}