package lk.ijse.eca.orderservice.service;

import lk.ijse.eca.orderservice.dto.AnalyticsDto;
import lk.ijse.eca.orderservice.dto.OrderDto;
import lk.ijse.eca.orderservice.entity.Order;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {

    OrderDto createOrder(OrderDto dto);

    OrderDto updateOrder(Long orderId, OrderDto dto);

    void deleteOrder(Long orderId);

    OrderDto getOrder(Long orderId);

    List<OrderDto> getAllOrders();

    List<OrderDto> getOrdersByCustomerId(String customerId);

    List<OrderDto> getOrdersByStatus(Order.OrderStatus status);

    List<OrderDto> getOrdersByPaymentMethod(Order.PaymentMethod paymentMethod);

    List<OrderDto> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    // Analytics Methods
    AnalyticsDto getSalesAnalytics(LocalDateTime startDate, LocalDateTime endDate);

    AnalyticsDto getDailySalesAnalytics(LocalDateTime startDate, LocalDateTime endDate);

    AnalyticsDto getCustomerAnalytics(LocalDateTime startDate, LocalDateTime endDate);

    AnalyticsDto getProductAnalytics(LocalDateTime startDate, LocalDateTime endDate);

    Long getOrderCountByStatus(Order.OrderStatus status);

    void updateOrderStatus(Long orderId, Order.OrderStatus newStatus);

    OrderDto addOrderItem(Long orderId, OrderDto.OrderItemDto orderItemDto);

    void removeOrderItem(Long orderItemId);
}
