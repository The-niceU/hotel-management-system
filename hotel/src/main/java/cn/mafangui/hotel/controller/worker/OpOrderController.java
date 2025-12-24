package cn.mafangui.hotel.controller.worker;

import cn.mafangui.hotel.entity.Order;
import cn.mafangui.hotel.enums.OrderStatus;
import cn.mafangui.hotel.enums.OrderOperationResult;
import cn.mafangui.hotel.response.AjaxResult;
import cn.mafangui.hotel.response.MsgType;
import cn.mafangui.hotel.response.ResponseTool;
import cn.mafangui.hotel.service.OrderService;
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
import java.util.List;

/**
 * 订单接口
 */
@RestController
@RequestMapping(value = "/op/order")
@Validated
public class OpOrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 添加预订
     * 订单状态默认为未付款状态
     */
    @PostMapping(value = "/add")
    public AjaxResult addOrder(@RequestBody @Validated OrderCreateRequest request) {
        Order order = request.toOrder();
        int re = orderService.addOrder(order);
        if (re != 1)
            return ResponseTool.failed(MsgType.FAILED);
        return ResponseTool.success("添加成功.");
    }

    @DeleteMapping(value = "/{orderId}")
    public AjaxResult deleteOrder(@PathVariable Integer orderId) {
        int re = orderService.delete(orderId);
        if (re != 1)
            return ResponseTool.failed(MsgType.FAILED);
        return ResponseTool.success("删除成功.");
    }

    @PostMapping(value = "/update")
    public AjaxResult updateOrder(@RequestBody @Validated OrderUpdateRequest request) {
        Order order = request.toOrder();
        int re = orderService.update(order);
        if (re != 1)
            return ResponseTool.failed(MsgType.FAILED);
        return ResponseTool.success("修改成功.");
    }

    /**
     * 订单支付
     * 
     * @param orderId
     * @return
     */
    @PostMapping(value = "/pay")
    public AjaxResult payOrder(@RequestBody @Validated OrderIdRequest request) {
        OrderOperationResult result = orderService.payOrder(request.getOrderId());
        if (!result.isSuccess())
            return ResponseTool.failed(result.getMessage());
        return ResponseTool.success("支付成功.");
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
        return ResponseTool.success("取消成功.");
    }

    /**
     * 订单超时
     * 
     * @param orderId
     * @return
     */
    @PostMapping(value = "/overtime")
    public AjaxResult orderOver(@RequestBody @Validated OrderIdRequest request) {
        Order order = new Order(request.getOrderId(), OrderStatus.OVERTIME.getCode());
        int updated = orderService.update(order);
        return updated == 1 ? ResponseTool.success("设置超时成功") : ResponseTool.failed(MsgType.FAILED);
    }

    @GetMapping(value = "")
    public AjaxResult getAllOrder() {
        return ResponseTool.success(orderService.AllOrders());
    }

    @GetMapping(value = "/count")
    public AjaxResult getOrderCount() {
        return ResponseTool.success(orderService.getOrderCount());
    }

    /**
     * 根据userID查询所有订单
     * 
     * @param userId
     * @return
     */
    @GetMapping(value = "/user/{userId}")
    public AjaxResult getByUser(@PathVariable int userId) {
        return ResponseTool.success(orderService.selectByUserId(userId));
    }

    /**
     * 根据订单号查询订单
     * 
     * @param orderId
     * @return
     */
    @GetMapping(value = "/{orderId}")
    public AjaxResult getById(@PathVariable Integer orderId) {
        return ResponseTool.success(orderService.selectById(orderId));
    }

    /**
     * 根据姓名、预留手机号查找订单
     * 主要用于客户入住
     * 
     * @param name
     * @param phone
     * @return
     */
    @GetMapping(value = "/withNameAndPhone")
    public AjaxResult getByNameAndPhone(@RequestParam @NotBlank(message = "姓名不能为空") String name,
            @RequestParam @NotBlank(message = "手机号不能为空") String phone) {
        return ResponseTool.success(orderService.selectByNameAndPhone(name, phone));
    }

    private static class OrderCreateRequest {
        @NotNull(message = "订单类型ID不能为空")
        private Integer orderTypeId;
        @NotBlank(message = "订单类型不能为空")
        private String orderType;
        @NotNull(message = "用户ID不能为空")
        private Integer userId;
        @NotBlank(message = "预订姓名不能为空")
        private String name;
        @NotBlank(message = "预订手机号不能为空")
        private String phone;
        @NotNull(message = "房型ID不能为空")
        private Integer roomTypeId;
        @NotBlank(message = "房型名称不能为空")
        private String roomType;
        @NotNull(message = "预订日期不能为空")
        @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
        private Date orderDate;
        @NotNull(message = "天数不能为空")
        @Positive(message = "天数必须大于0")
        private Integer orderDays;
        @NotNull(message = "费用不能为空")
        @PositiveOrZero(message = "费用不能为负")
        private Double orderCost;

        public Order toOrder() {
            Order order = new Order(orderTypeId, orderType, userId, name, phone, roomTypeId, roomType,
                    orderDate, orderDays, OrderStatus.UNPAID.getCode(), orderCost);
            return order;
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

    private static class OrderUpdateRequest extends OrderCreateRequest {
        @NotNull(message = "订单ID不能为空")
        private Integer orderId;

        @Override
        public Order toOrder() {
            Order order = super.toOrder();
            order.setOrderId(orderId);
            return order;
        }

        public Integer getOrderId() {
            return orderId;
        }

        public void setOrderId(Integer orderId) {
            this.orderId = orderId;
        }
    }

    private static class OrderIdRequest {
        @NotNull(message = "订单ID不能为空")
        @Min(value = 1, message = "订单ID必须为正数")
        private Integer orderId;

        public Integer getOrderId() {
            return orderId;
        }

        public void setOrderId(Integer orderId) {
            this.orderId = orderId;
        }
    }
}
