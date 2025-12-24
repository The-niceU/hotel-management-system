package cn.mafangui.hotel.controller.user;

import cn.mafangui.hotel.entity.User;
import cn.mafangui.hotel.response.AjaxResult;
import cn.mafangui.hotel.response.MsgType;
import cn.mafangui.hotel.response.ResponseTool;
import cn.mafangui.hotel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@RestController
@RequestMapping(value = "/user")
@Validated
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 更新用户信息
     * 
     * @param userId
     * @param name
     * @param gender
     * @param phone
     * @param email
     * @param address
     * @param idcard
     * @return
     */
    @PostMapping(value = "/update")
    public AjaxResult userUpdate(@RequestBody @Validated UserProfileUpdateRequest body, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Integer sessionUserId = resolveUserId(session);
        if (sessionUserId == null || !sessionUserId.equals(body.getUserId())) {
            return ResponseTool.failed(MsgType.PERMISSION_DENIED);
        }
        User user = new User();
        user.setUserId(sessionUserId);
        user.setName(body.getName());
        user.setGender(body.getGender());
        user.setPhone(body.getPhone());
        user.setEmail(body.getEmail());
        user.setAddress(body.getAddress());
        user.setIdcard(body.getIdcard());
        if (userService.updateUser(user) == 1)
            return ResponseTool.success("修改成功");
        return ResponseTool.failed("修改失败，请检查或稍后再试");
    }

    /**
     * 更改密码
     * 
     * @param username
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @PostMapping(value = "/updatePassword")
    public AjaxResult updatePassword(@RequestBody @Validated PasswordUpdateRequest body, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String username = resolveUsername(session);
        if (!StringUtils.hasText(username)) {
            return ResponseTool.failed(MsgType.PERMISSION_DENIED);
        }
        User user = userService.selectByUsernameAndPassword(username, body.getOldPassword());
        if (user == null) {
            return ResponseTool.failed("原密码不正确");
        }
        user.setPassword(body.getNewPassword());
        if (userService.updateUser(user) == 1)
            return ResponseTool.success("修改成功");
        return ResponseTool.failed("修改失败");
    }

    /**
     * 获取个人资料
     * 
     * @param request
     * @return
     */
    @GetMapping(value = "/profile")
    public AjaxResult getProfile(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String username = resolveUsername(session);
        if (!StringUtils.hasText(username)) {
            return ResponseTool.failed(MsgType.PERMISSION_DENIED);
        }
        User user = userService.selectByUsername(username);
        if (user == null)
            return ResponseTool.failed("未知错误");
        user.setPassword(null);
        String maskedIdCard = maskIdCard(user.getIdcard());
        user.setIdcard(maskedIdCard);
        return ResponseTool.success(user);
    }

    /**
     * 注销
     * 
     * @param request
     * @return
     */
    @PostMapping(value = "/logout")
    public AjaxResult logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute("userId");
            session.removeAttribute("username");
        }
        return ResponseTool.success("注销成功");
    }

    private Integer resolveUserId(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object userId = session.getAttribute("userId");
        return userId instanceof Integer ? (Integer) userId : null;
    }

    private String resolveUsername(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object username = session.getAttribute("username");
        return username instanceof String ? (String) username : null;
    }

    private String maskIdCard(String idcard) {
        if (!StringUtils.hasText(idcard) || idcard.length() <= 8) {
            return idcard;
        }
        StringBuilder sb = new StringBuilder(idcard);
        int start = Math.min(5, sb.length());
        int end = Math.min(12, sb.length());
        for (int i = start; i < end; i++) {
            sb.setCharAt(i, '*');
        }
        return sb.toString();
    }

    private static class UserProfileUpdateRequest {
        @NotNull(message = "用户ID不能为空")
        private Integer userId;
        @NotBlank(message = "姓名不能为空")
        @Size(max = 32, message = "姓名长度不能超过32位")
        private String name;
        @NotBlank(message = "性别不能为空")
        private String gender;
        @NotBlank(message = "手机号不能为空")
        @Pattern(regexp = "^[0-9\\-\\+]{6,16}$", message = "手机号格式不正确")
        private String phone;
        @Email(message = "邮箱格式不正确")
        private String email;
        @Size(max = 64, message = "地址长度不能超过64位")
        private String address;
        @Size(max = 32, message = "身份证号码长度不能超过32位")
        private String idcard;

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getIdcard() {
            return idcard;
        }

        public void setIdcard(String idcard) {
            this.idcard = idcard;
        }
    }

    private static class PasswordUpdateRequest {
        @NotBlank(message = "原密码不能为空")
        private String oldPassword;
        @NotBlank(message = "新密码不能为空")
        @Size(min = 6, max = 32, message = "新密码长度需在6-32位之间")
        private String newPassword;

        public String getOldPassword() {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }

}
