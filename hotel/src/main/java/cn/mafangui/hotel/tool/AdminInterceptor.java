package cn.mafangui.hotel.tool;

import cn.mafangui.hotel.enums.Role;
import cn.mafangui.hotel.response.AjaxResult;
import cn.mafangui.hotel.response.MsgType;
import cn.mafangui.hotel.response.ResponseTool;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;

public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession(false);
        Object roleAttr = session == null ? null : session.getAttribute("role");
        if (Role.ADMIN.getValue().equals(roleAttr)) {
            return true;
        }
        writeDeniedResponse(request, response, MsgType.PERMISSION_DENIED);
        return false;
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
