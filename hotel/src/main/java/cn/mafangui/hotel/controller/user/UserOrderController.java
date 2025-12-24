package cn.mafangui.hotel.controller.user;

import cn.mafangui.hotel.entity.Order;
import cn.mafangui.hotel.entity.User;
import cn.mafangui.hotel.enums.OrderOperationResult;
import cn.mafangui.hotel.enums.OrderStatus;
import cn.mafangui.hotel.response.AjaxResult;
import cn.mafangui.hotel.response.ResponseTool;
import cn.mafangui.hotel.service.OrderService;
import cn.mafangui.hotel.service.UserService;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Date;

/**
 * 订单接口
 */
@RestController
@RequestMapping(value = "/user/order")
@Validated
public class UserOrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;

    /**
     * 添加预订
     * 订单状态默认为未付款状态
     * 
     * @param orderTypeId
     * @param orderType
     * @param userId
     * @param name
     * @param phone
     * @param roomTypeId
     * @param roomType
     * @param orderDate
     * @param orderDays
     * @param orderCost
     * @return
     */
    @PostMapping(value = "/add")
    public AjaxResult addOrder(@RequestBody @Validated OrderCreateRequest request) {
        Order order = request.toOrder();
        int re = orderService.addOrder(order);
        if (re != 1)
            return ResponseTool.failed("预订失败");
        return ResponseTool.success("预订成功");
    }

    /**
     * 删除订单
     * 
     * @param orderId
     * @return
     */
    @DeleteMapping(value = "/{orderId}")
    public AjaxResult deleteOrderByUser(@PathVariable @Min(value = 1, message = "订单ID必须大于0") Integer orderId) {
        Order order = new Order(orderId, OrderStatus.WAS_DELETED.getCode());
        int re = orderService.update(order);
        if (re != 1)
            return ResponseTool.failed("删除失败");
        return ResponseTool.success("删除成功");
    }

    /**
     * 订单支付
     * 
     * @param orderId
     * @return
     */
    @PostMapping(value = "/pay")
    public AjaxResult payOrder(@RequestBody @Validated OrderPaymentRequest request) {
        User user = userService.selectByUsernameAndPassword(request.getUsername(), request.getPassword());
        if (user == null)
            return ResponseTool.failed("密码错误");
        OrderOperationResult result = orderService.payOrder(request.getOrderId());
        if (!result.isSuccess())
            return ResponseTool.failed(result.getMessage());
        return ResponseTool.success("支付成功");
    }

    /**
     * 取消订单
     * 
     * @param orderId
     * @return
     */
    @PostMapping(value = "/cancel")
    public AjaxResult cancelOrder(@RequestBody @Validated OrderIdRequest request) {
        OrderOperationResult result = orderService.cancelOrder(request.getOrderId());
        if (!result.isSuccess())
            return ResponseTool.failed(result.getMessage());
        return ResponseTool.success("取消成功");
    }

    /**
     * 客户查询个人所有订单（不包括被自己删除的）
     * 
     * @param userId
     * @return
     */
    @GetMapping(value = "")
    public AjaxResult getAllByUser(@RequestParam("userId") @Positive(message = "用户ID必须大于0") Integer userId) {
        return ResponseTool.success(orderService.UsersAllOrders(userId));
    }

    /**
     * 根据订单号查询订单
     * 
     * @param orderId
     * @return
     */
    @GetMapping(value = "/{orderId}")
    public AjaxResult getById(@PathVariable("orderId") @Positive(message = "订单ID必须大于0") Integer orderId) {
        return ResponseTool.success(orderService.selectById(orderId));
    }

    private static class OrderCreateRequest {
        @NotNull(message = "订单类型ID不能为空")
        private Integer orderTypeId;
        @NotBlank(message = "订单类型不能为空")
        private String orderType;
        @NotNull(message = "用户ID不能为空")
        @Positive(message = "用户ID必须大于0")
        private Integer userId;
        @NotBlank(message = "预订姓名不能为空")
        private String name;
        @NotBlank(message = "预订手机号不能为空")
        private String phone;
        @NotNull(message = "房型ID不能为空")
        @Positive(message = "房型ID必须大于0")
        private Integer roomTypeId;
        @NotBlank(message = "房型名称不能为空")
        private String roomType;
        @NotNull(message = "预订日期不能为空")
        @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
        private Date orderDate;
        @NotNull(message = "住宿天数不能为空")
        @Positive(message = "住宿天数必须大于0")
        private Integer orderDays;
        @NotNull(message = "订单金额不能为空")
        @PositiveOrZero(message = "订单金额不能为负数")
        private Double orderCost;

        public Order toOrder() {
            return new Order(orderTypeId, orderType, userId, name, phone, roomTypeId,
                    roomType, orderDate, orderDays, OrderStatus.UNPAID.getCode(), orderCost);
        }

        public Integer getOrderTypeId() {
            return orderTypeId;
        }

        public void setOrderTypeId(Integer orderTypeId) {
            this.orderTypeId = orderTypeId;
        }

        public String getOrderType() {
            return orderType;
        }

        public void setOrderType(String orderType) {
            this.orderType = orderType;
        }

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

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public Integer getRoomTypeId() {
            return roomTypeId;
        }

        public void setRoomTypeId(Integer roomTypeId) {
            this.roomTypeId = roomTypeId;
        }

        public String getRoomType() {
            return roomType;
        }

        public void setRoomType(String roomType) {
            this.roomType = roomType;
        }

        public Date getOrderDate() {
            return orderDate;
        }

        public void setOrderDate(Date orderDate) {
            this.orderDate = orderDate;
        }

        public Integer getOrderDays() {
            return orderDays;
        }

        public void setOrderDays(Integer orderDays) {
            this.orderDays = orderDays;
        }

        public Double getOrderCost() {
            return orderCost;
        }

        public void setOrderCost(Double orderCost) {
            this.orderCost = orderCost;
        }
    }

    private static class OrderIdRequest {
        @NotNull(message = "订单ID不能为空")
        @Min(value = 1, message = "订单ID必须大于0")
        private Integer orderId;

        public Integer getOrderId() {
            return orderId;
        }

        public void setOrderId(Integer orderId) {
            this.orderId = orderId;
        }
    }

    private static class OrderPaymentRequest extends OrderIdRequest {
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
