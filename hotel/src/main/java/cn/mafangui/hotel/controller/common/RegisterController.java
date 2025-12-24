package cn.mafangui.hotel.controller.common;

import cn.mafangui.hotel.entity.User;
import cn.mafangui.hotel.entity.Worker;
import cn.mafangui.hotel.enums.Role;
import cn.mafangui.hotel.response.AjaxResult;
import cn.mafangui.hotel.response.ResponseTool;
import cn.mafangui.hotel.service.UserService;
import cn.mafangui.hotel.service.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@RestController
@RequestMapping(value = "/register")
@Validated
public class RegisterController {

    @Autowired
    UserService userService;
    @Autowired
    WorkerService workerService;

    @PostMapping(value = "/user")
    public AjaxResult userRegister(@RequestBody @Validated UserRegisterRequest request) {
        User user = new User(request.getUsername(), request.getPassword(), request.getName(), request.getGender(),
                request.getPhone(), request.getEmail(), request.getAddress(), request.getIdcard());
        int result = userService.insertUser(user);
        if (result == 1) {
            return ResponseTool.success(result);
        } else
            return ResponseTool.failed("注册失败,请稍后再试");
    }

    @PostMapping(value = "/admin")
    public AjaxResult adminRegister(@RequestBody @Validated WorkerRegisterRequest request) {
        Worker worker = new Worker(request.getUsername(), request.getPassword(), request.getName(), request.getGender(),
                request.getPhone(), request.getEmail(), request.getAddress());
        worker.setRole(Role.ADMIN.getValue());
        int result = workerService.insert(worker);
        if (result == 1) {
            return ResponseTool.success(result);
        } else
            return ResponseTool.failed("注册失败,请稍后再试");
    }

    @PostMapping(value = "/operator")
    public AjaxResult operatorRegister(@RequestBody @Validated WorkerRegisterRequest request) {
        Worker worker = new Worker(request.getUsername(), request.getPassword(), request.getName(), request.getGender(),
                request.getPhone(), request.getEmail(), request.getAddress());
        worker.setRole(Role.OPERATOR.getValue());
        int result = workerService.insert(worker);
        if (result == 1) {
            return ResponseTool.success(result);
        } else
            return ResponseTool.failed("注册失败,请稍后再试");
    }

    private static class UserRegisterRequest {
        @NotBlank(message = "用户名不能为空")
        @Size(max = 16, message = "用户名长度不能超过16位")
        private String username;
        @NotBlank(message = "密码不能为空")
        @Size(min = 6, max = 32, message = "密码长度需在6-32位")
        private String password;
        @NotBlank(message = "姓名不能为空")
        private String name;
        @NotBlank(message = "性别不能为空")
        private String gender;
        @NotBlank(message = "手机号不能为空")
        @Pattern(regexp = "^[0-9\\-\\+]{6,16}$", message = "手机号格式不正确")
        private String phone;
        @Email(message = "邮箱格式不正确")
        private String email;
        private String address;
        @Size(max = 32, message = "身份证号码长度过长")
        private String idcard;

        // getters and setters
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

    private static class WorkerRegisterRequest {
        @NotBlank(message = "用户名不能为空")
        @Size(max = 32, message = "用户名长度不能超过32位")
        private String username;
        @NotBlank(message = "密码不能为空")
        @Size(min = 6, max = 32, message = "密码长度需在6-32位")
        private String password;
        @NotBlank(message = "姓名不能为空")
        private String name;
        @NotBlank(message = "性别不能为空")
        private String gender;
        @NotBlank(message = "手机号不能为空")
        @Pattern(regexp = "^[0-9\\-\\+]{6,16}$", message = "手机号格式不正确")
        private String phone;
        @Email(message = "邮箱格式不正确")
        private String email;
        private String address;

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
    }
}
