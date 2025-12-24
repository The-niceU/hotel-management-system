package cn.mafangui.hotel.controller.worker;

import cn.mafangui.hotel.entity.User;
import cn.mafangui.hotel.response.AjaxResult;
import cn.mafangui.hotel.response.MsgType;
import cn.mafangui.hotel.response.ResponseTool;
import cn.mafangui.hotel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@RestController
@RequestMapping(value = "/op/user")
@Validated
public class OpUserController {
    @Autowired
    private UserService userService;

    @GetMapping(value = "")
    public AjaxResult getAllUser() {
        return ResponseTool.success(userService.selectAllUser());
    }

    @GetMapping(value = "/count")
    public AjaxResult getUserCount() {
        return ResponseTool.success(userService.getUserCount());
    }

    @DeleteMapping(value = "/{userId}")
    public AjaxResult deleteUser(@PathVariable Integer userId) {
        int re = userService.deleteUser(userId);
        if (re != 1)
            return ResponseTool.failed("删除失败或用户不存在");
        return ResponseTool.success("删除成功");
    }

    @PostMapping(value = "/add")
    public AjaxResult userAdd(@RequestBody @Validated UserCreateRequest request) {
        User user = buildUserFromCreate(request);
        int re = userService.addUser(user);
        if (re != 1)
            return ResponseTool.failed("添加失败，请稍后再试");
        return ResponseTool.success("添加成功");
    }

    @PostMapping(value = "/update")
    public AjaxResult userUpdate(@RequestBody @Validated UserUpdateRequest request) {
        User user = new User();
        user.setUserId(request.getUserId());
        user.setName(request.getName());
        user.setGender(request.getGender());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setAddress(request.getAddress());
        user.setIdcard(request.getIdcard());
        if (userService.updateUser(user) == 1)
            return ResponseTool.success("修改成功");
        return ResponseTool.failed("修改失败，请检查或稍后再试");
    }

    @PostMapping(value = "/updatePassword")
    public AjaxResult updatePassword(@RequestBody @Validated PasswordChangeRequest request) {
        User user = userService.selectByUsernameAndPassword(request.getUsername(), request.getOldPassword());
        if (user == null) {
            return ResponseTool.failed("原密码不正确或用户不存在");
        }
        user.setPassword(request.getNewPassword());
        if (userService.updateUser(user) == 1)
            return ResponseTool.success("修改成功");
        return ResponseTool.failed("修改失败，请稍后再试");
    }

    @GetMapping(value = "/{userId}")
    public AjaxResult getProfile(@PathVariable Integer userId) {
        User user = userService.selectById(userId);
        if (user == null) {
            return ResponseTool.failed(MsgType.PARAM_IS_INVALID);
        }
        user.setPassword(null);
        return ResponseTool.success(user);
    }

    @GetMapping(value = "/username/{username}")
    public AjaxResult getByUsername(@PathVariable String username) {
        User user = userService.selectByUsername(username);
        if (user == null) {
            return ResponseTool.failed(MsgType.PARAM_IS_INVALID);
        }
        user.setPassword(null);
        return ResponseTool.success(user);
    }

    private User buildUserFromCreate(UserCreateRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setName(request.getName());
        user.setGender(request.getGender());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setAddress(request.getAddress());
        user.setIdcard(request.getIdcard());
        return user;
    }

    private static class UserCreateRequest {
        @NotBlank(message = "用户名不能为空")
        @Size(max = 32, message = "用户名长度不能超过32位")
        private String username;
        @NotBlank(message = "密码不能为空")
        @Size(min = 6, max = 32, message = "密码长度需在6-32位之间")
        private String password;
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

    private static class UserUpdateRequest {
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

    private static class PasswordChangeRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;
        @NotBlank(message = "原密码不能为空")
        private String oldPassword;
        @NotBlank(message = "新密码不能为空")
        @Size(min = 6, max = 32, message = "新密码长度需在6-32位之间")
        private String newPassword;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

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
