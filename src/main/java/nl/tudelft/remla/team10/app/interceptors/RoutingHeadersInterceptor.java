package nl.tudelft.remla.team10.app.interceptors;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class RoutingHeadersInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String version = request.getHeader("set-ce-version");
        if (version != null) {
            Cookie cookie = new Cookie("ce-version", version);
            response.addCookie(cookie);
        }
        return true;
    }

}
