package lk.ijse.eca.orderservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.groups.Default;
import lk.ijse.eca.orderservice.dto.AnalyticsDto;
import lk.ijse.eca.orderservice.dto.OrderDto;
import lk.ijse.eca.orderservice.entity.Order;
import lk.ijse.eca.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
@Validated
public class OrderController {

    private final OrderService orderService;

    private static final String CUSTOMER_ID_REGEXP = "^[A-Z0-9]{6,10}$";

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<OrderDto> createOrder(
            @Validated({Default.class, OrderDto.OnCreate.class})
            @RequestBody OrderDto dto) {
        log.info("POST /api/v1/orders - customerId: {}", dto.getCustomerId());
        OrderDto response = orderService.createOrder(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderDto> getOrder(
            @PathVariable @Positive(message = "Order ID must be a positive number") Long orderId) {
        log.info("GET /api/v1/orders/{}", orderId);
        OrderDto response = orderService.getOrder(orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        log.info("GET /api/v1/orders - retrieving all orders");
        List<OrderDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping(value = "/customer/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OrderDto>> getOrdersByCustomerId(
            @PathVariable
            @Pattern(regexp = CUSTOMER_ID_REGEXP, message = "Customer ID must be 6-10 alphanumeric characters")
            String customerId) {
        log.info("GET /api/v1/orders/customer/{}", customerId);
        List<OrderDto> orders = orderService.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping(value = "/status/{orderStatus}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OrderDto>> getOrdersByStatus(
            @PathVariable Order.OrderStatus orderStatus) {
        log.info("GET /api/v1/orders/status/{}", orderStatus);
        List<OrderDto> orders = orderService.getOrdersByStatus(orderStatus);
        return ResponseEntity.ok(orders);
    }

    @GetMapping(value = "/payment-method/{paymentMethod}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OrderDto>> getOrdersByPaymentMethod(
            @PathVariable Order.PaymentMethod paymentMethod) {
        log.info("GET /api/v1/orders/payment-method/{}", paymentMethod);
        List<OrderDto> orders = orderService.getOrdersByPaymentMethod(paymentMethod);
        return ResponseEntity.ok(orders);
    }

    @GetMapping(value = "/date-range", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OrderDto>> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("GET /api/v1/orders/date-range?startDate={}&endDate={}", startDate, endDate);
        List<OrderDto> orders = orderService.getOrdersByDateRange(startDate, endDate);
        return ResponseEntity.ok(orders);
    }

    @PutMapping(
            value = "/{orderId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<OrderDto> updateOrder(
            @PathVariable @Positive(message = "Order ID must be a positive number") Long orderId,
            @Valid @RequestBody OrderDto dto) {
        log.info("PUT /api/v1/orders/{}", orderId);
        OrderDto response = orderService.updateOrder(orderId, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> deleteOrder(
            @PathVariable @Positive(message = "Order ID must be a positive number") Long orderId) {
        log.info("DELETE /api/v1/orders/{}", orderId);
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok("Order deleted successfully");
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<String> updateOrderStatus(
            @PathVariable @Positive(message = "Order ID must be a positive number") Long orderId,
            @RequestParam Order.OrderStatus newStatus) {
        log.info("PUT /api/v1/orders/{}/status?newStatus={}", orderId, newStatus);
        orderService.updateOrderStatus(orderId, newStatus);
        return ResponseEntity.ok("Order status updated successfully");
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderDto> addOrderItem(
            @PathVariable @Positive(message = "Order ID must be a positive number") Long orderId,
            @Valid @RequestBody OrderDto.OrderItemDto orderItemDto) {
        log.info("POST /api/v1/orders/{}/items", orderId);
        OrderDto response = orderService.addOrderItem(orderId, orderItemDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/items/{orderItemId}")
    public ResponseEntity<String> removeOrderItem(
            @PathVariable @Positive(message = "Order item ID must be a positive number") Long orderItemId) {
        log.info("DELETE /api/v1/orders/items/{}", orderItemId);
        orderService.removeOrderItem(orderItemId);
        return ResponseEntity.ok("Order item removed successfully");
    }

    // Analytics Endpoints
    @GetMapping(value = "/analytics/sales", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AnalyticsDto> getSalesAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("GET /api/v1/orders/analytics/sales?startDate={}&endDate={}", startDate, endDate);
        AnalyticsDto analytics = orderService.getSalesAnalytics(startDate, endDate);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping(value = "/analytics/daily-sales", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AnalyticsDto> getDailySalesAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("GET /api/v1/orders/analytics/daily-sales?startDate={}&endDate={}", startDate, endDate);
        AnalyticsDto analytics = orderService.getDailySalesAnalytics(startDate, endDate);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping(value = "/analytics/customers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AnalyticsDto> getCustomerAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("GET /api/v1/orders/analytics/customers?startDate={}&endDate={}", startDate, endDate);
        AnalyticsDto analytics = orderService.getCustomerAnalytics(startDate, endDate);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping(value = "/analytics/products", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AnalyticsDto> getProductAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("GET /api/v1/orders/analytics/products?startDate={}&endDate={}", startDate, endDate);
        AnalyticsDto analytics = orderService.getProductAnalytics(startDate, endDate);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping(value = "/count/status/{orderStatus}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> getOrderCountByStatus(
            @PathVariable Order.OrderStatus orderStatus) {
        log.info("GET /api/v1/orders/count/status/{}", orderStatus);
        Long count = orderService.getOrderCountByStatus(orderStatus);
        return ResponseEntity.ok(count);
    }
}
