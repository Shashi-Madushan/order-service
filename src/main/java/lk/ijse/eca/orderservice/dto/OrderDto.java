package lk.ijse.eca.orderservice.dto;

import jakarta.validation.constraints.*;
import lk.ijse.eca.orderservice.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {

    public interface OnCreate {}

    private Long orderId;

    @NotNull(groups = OnCreate.class, message = "Order date is required")
    private LocalDateTime orderDate;

    @NotBlank(groups = OnCreate.class, message = "Customer ID is required")
    private String customerId;

    @NotNull(groups = OnCreate.class, message = "Order status is required")
    private Order.OrderStatus orderStatus;

    @NotNull(groups = OnCreate.class, message = "Payment method is required")
    private Order.PaymentMethod paymentMethod;

    @NotNull(groups = OnCreate.class, message = "Subtotal amount is required")
    @DecimalMin(value = "0.01", message = "Subtotal amount must be greater than 0")
    private BigDecimal subtotalAmount;

    @NotNull(groups = OnCreate.class, message = "Tax amount is required")
    @DecimalMin(value = "0.00", message = "Tax amount cannot be negative")
    private BigDecimal taxAmount;

    @NotNull(groups = OnCreate.class, message = "Discount amount is required")
    @DecimalMin(value = "0.00", message = "Discount amount cannot be negative")
    private BigDecimal discountAmount;

    @NotNull(groups = OnCreate.class, message = "Total amount is required")
    @DecimalMin(value = "0.01", message = "Total amount must be greater than 0")
    private BigDecimal totalAmount;

    private String receiptUrl;

    private String invoiceUrl;

    private List<OrderItemDto> orderItems;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemDto {
        private Long orderItemId;
        private String productId;
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
        private BigDecimal discountAmount;
    }
}
