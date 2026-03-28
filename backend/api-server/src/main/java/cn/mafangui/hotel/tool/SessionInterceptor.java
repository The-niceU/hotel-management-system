package cn.mafangui.hotel.tool;

import cn.mafangui.hotel.response.AjaxResult;
import cn.mafangui.hotel.response.MsgType;
import cn.mafangui.hotel.response.ResponseTool;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.util.Locale;

public class SessionInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(SessionInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (isPublicRequest(request)) {
            return true;
        }
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            return true;
        }
        writeDeniedResponse(request, response, MsgType.NOT_LOGIN);
        return false;
    }

    private boolean isPublicRequest(HttpServletRequest request) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String contextPath = request.getContextPath();
        String uri = request.getRequestURI();
        String relativePath = uri.startsWith(contextPath) ? uri.substring(contextPath.length()) : uri;
        String normalizedPath = relativePath.toLowerCase(Locale.ROOT);
        log.info("SessionInterceptor request path={}, normalized={}, contextPath={}", uri, normalizedPath,
                contextPath);
        return normalizedPath.startsWith("/login")
                || normalizedPath.startsWith("/register")
                || normalizedPath.startsWith("/hotel");
    }

    private void writeDeniedResponse(HttpServletRequest request, HttpServletResponse response, MsgType msgType)
            throws Exception {
        setCorsMappings(request, response);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        AjaxResult result = ResponseTool.failed(msgType);
        ObjectMapper mapper = new ObjectMapper();
        try (PrintWriter writer = response.getWriter()) {
            writer.write(mapper.writeValueAsString(result));
            writer.flush();
        }
    }

    private void setCorsMappings(HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin", origin == null ? "*" : origin);
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with,Authorization,Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
    }
}
