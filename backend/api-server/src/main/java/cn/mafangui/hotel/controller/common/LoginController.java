package cn.mafangui.hotel.controller.common;

import cn.mafangui.hotel.entity.User;
import cn.mafangui.hotel.entity.Worker;
import cn.mafangui.hotel.response.AjaxResult;
import cn.mafangui.hotel.response.ResponseTool;
import cn.mafangui.hotel.service.UserService;
import cn.mafangui.hotel.service.WorkerService;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping(value = "/login")
@Validated
public class LoginController {
    @Autowired
    private UserService userService;
    @Autowired
    private WorkerService workerService;

    @PostMapping(value = "/user")
    public AjaxResult userLogin(@RequestBody @Validated LoginRequest requestBody,
            HttpServletRequest request) {
        User user = userService.selectByUsernameAndPassword(requestBody.getUsername(), requestBody.getPassword());
        if (user == null) {
            return ResponseTool.failed("用户名或密码不正确");
        }
        HttpSession session = request.getSession();
        session.setAttribute("userId", user.getUserId());
        session.setAttribute("username", user.getUsername());
        HashMap map = new HashMap<>();
        map.put("sessionId", session.getId());
        map.put("userId", user.getUserId());
        return ResponseTool.success(map);
    }

    @PostMapping(value = "/admin")
    public AjaxResult workerLogin(@RequestBody @Validated LoginRequest requestBody,
            HttpServletRequest request) {
        Worker worker = workerService.login(requestBody.getUsername(), requestBody.getPassword());
        if (worker == null) {
            return ResponseTool.failed("用户名或密码不正确");
        }
        HttpSession session = request.getSession();
        session.setAttribute("userId", worker.getWorkerId());
        session.setAttribute("role", worker.getRole());
        HashMap<String, Object> map = new HashMap<>();
        map.put("sessionId", session.getId());
        map.put("role", worker.getRole());
        map.put("userId", worker.getWorkerId());
        return ResponseTool.success(map);
    }

    private static class LoginRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;
        @NotBlank(message = "密码不能为空")
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
