package cn.mafangui.hotel.enums;

public enum OrderOperationResult {
    SUCCESS(true, "操作成功"),
    ORDER_NOT_FOUND(false, "订单不存在"),
    INVALID_STATUS(false, "订单状态不允许当前操作"),
    ROOMTYPE_UPDATE_FAILED(false, "更新房型余量失败"),
    ORDER_UPDATE_FAILED(false, "更新订单状态失败");

    private final boolean success;
    private final String message;

    OrderOperationResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
