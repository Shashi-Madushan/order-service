package lk.ijse.eca.orderservice.service.impl;

import lk.ijse.eca.orderservice.dto.AnalyticsDto;
import lk.ijse.eca.orderservice.dto.OrderDto;
import lk.ijse.eca.orderservice.entity.Order;
import lk.ijse.eca.orderservice.entity.OrderItem;
import lk.ijse.eca.orderservice.exception.OrderNotFoundException;
import lk.ijse.eca.orderservice.mapper.OrderMapper;
import lk.ijse.eca.orderservice.repository.OrderItemRepository;
import lk.ijse.eca.orderservice.repository.OrderRepository;
import lk.ijse.eca.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderDto createOrder(OrderDto dto) {
        log.debug("Creating order for customer: {}", dto.getCustomerId());

        Order order = orderMapper.toEntityWithItems(dto);

        if (order.getOrderItems() != null) {
            order.getOrderItems().forEach(item -> item.setOrder(order));
        }

        Order saved = orderRepository.save(order);

        log.info("Order created successfully with ID: {}", saved.getOrderId());
        return orderMapper.toDtoWithItems(saved);
    }

    @Override
    @Transactional
    public OrderDto updateOrder(Long orderId, OrderDto dto) {
        log.debug("Updating order with ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("Order not found for update: {}", orderId);
                    return new OrderNotFoundException(orderId);
                });

        orderMapper.updateEntity(dto, order);
        Order updated = orderRepository.save(order);
        log.info("Order updated successfully: {}", orderId);
        return orderMapper.toDtoWithItems(updated);
    }

    @Override
    @Transactional
    public void deleteOrder(Long orderId) {
        log.debug("Deleting order with ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("Order not found for deletion: {}", orderId);
                    return new OrderNotFoundException(orderId);
                });

        orderRepository.delete(order);
        log.info("Order deleted successfully: {}", orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrder(Long orderId) {
        log.debug("Fetching order with ID: {}", orderId);
        return orderRepository.findById(orderId)
                .map(orderMapper::toDtoWithItems)
                .orElseThrow(() -> {
                    log.warn("Order not found: {}", orderId);
                    return new OrderNotFoundException(orderId);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getAllOrders() {
        log.debug("Fetching all orders");
        List<OrderDto> orders = orderRepository.findAll()
                .stream()
                .map(orderMapper::toDtoWithItems)
                .collect(Collectors.toList());
        log.debug("Fetched {} order(s)", orders.size());
        return orders;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersByCustomerId(String customerId) {
        log.debug("Fetching orders for customer: {}", customerId);
        return orderRepository.findByCustomerIdOrderByOrderDateDesc(customerId)
                .stream()
                .map(orderMapper::toDtoWithItems)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersByStatus(Order.OrderStatus status) {
        log.debug("Fetching orders by status: {}", status);
        return orderRepository.findByOrderStatus(status)
                .stream()
                .map(orderMapper::toDtoWithItems)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersByPaymentMethod(Order.PaymentMethod paymentMethod) {
        log.debug("Fetching orders by payment method: {}", paymentMethod);
        return orderRepository.findByPaymentMethod(paymentMethod)
                .stream()
                .map(orderMapper::toDtoWithItems)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Fetching orders between {} and {}", startDate, endDate);
        return orderRepository.findByOrderDateBetween(startDate, endDate)
                .stream()
                .map(orderMapper::toDtoWithItems)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AnalyticsDto getSalesAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Generating sales analytics between {} and {}", startDate, endDate);
        
        BigDecimal totalRevenue = orderRepository.calculateTotalRevenueBetween(startDate, endDate);
        Long totalOrders = orderRepository.countByOrderStatus(Order.OrderStatus.DELIVERED);
        BigDecimal averageOrderValue = totalOrders > 0 ? 
                totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        List<Object[]> paymentMethodData = orderRepository.getSalesByPaymentMethod(startDate, endDate);
        List<AnalyticsDto.PaymentMethodAnalytics> paymentAnalytics = paymentMethodData.stream()
                .map(data -> AnalyticsDto.PaymentMethodAnalytics.builder()
                        .paymentMethod((String) data[0])
                        .orderCount((Long) data[1])
                        .totalAmount((BigDecimal) data[2])
                        .build())
                .collect(Collectors.toList());

        return AnalyticsDto.builder()
                .startDate(startDate.toLocalDate())
                .endDate(endDate.toLocalDate())
                .totalRevenue(totalRevenue)
                .averageOrderValue(averageOrderValue)
                .totalOrders(totalOrders)
                .paymentMethodAnalytics(paymentAnalytics)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AnalyticsDto getDailySalesAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Generating daily sales analytics between {} and {}", startDate, endDate);
        
        List<Object[]> dailyData = orderRepository.getDailySalesSummary(startDate, endDate);
        List<AnalyticsDto.DailySalesSummary> dailySummary = dailyData.stream()
                .map(data -> AnalyticsDto.DailySalesSummary.builder()
                        .date(((java.sql.Date) data[0]).toLocalDate())
                        .orderCount((Long) data[1])
                        .totalRevenue((BigDecimal) data[2])
                        .build())
                .collect(Collectors.toList());

        return AnalyticsDto.builder()
                .startDate(startDate.toLocalDate())
                .endDate(endDate.toLocalDate())
                .dailySalesSummary(dailySummary)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AnalyticsDto getCustomerAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Generating customer analytics between {} and {}", startDate, endDate);
        
        List<Object[]> customerData = orderRepository.getTopCustomersBySales(startDate, endDate);
        List<AnalyticsDto.TopCustomer> topCustomers = customerData.stream()
                .map(data -> AnalyticsDto.TopCustomer.builder()
                        .customerId((String) data[0])
                        .orderCount((Long) data[1])
                        .totalSpent((BigDecimal) data[2])
                        .build())
                .collect(Collectors.toList());

        return AnalyticsDto.builder()
                .startDate(startDate.toLocalDate())
                .endDate(endDate.toLocalDate())
                .topCustomers(topCustomers)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AnalyticsDto getProductAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Generating product analytics between {} and {}", startDate, endDate);
        
        List<Object[]> productData = orderItemRepository.getTopSellingProducts(startDate, endDate);
        List<AnalyticsDto.TopProduct> topProducts = productData.stream()
                .map(data -> AnalyticsDto.TopProduct.builder()
                        .productId((String) data[0])
                        .productName((String) data[1])
                        .totalQuantitySold(((Number) data[2]).intValue())
                        .totalRevenue((BigDecimal) data[3])
                        .build())
                .collect(Collectors.toList());

        return AnalyticsDto.builder()
                .startDate(startDate.toLocalDate())
                .endDate(endDate.toLocalDate())
                .topProducts(topProducts)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getOrderCountByStatus(Order.OrderStatus status) {
        log.debug("Counting orders by status: {}", status);
        return orderRepository.countByOrderStatus(status);
    }

    @Override
    @Transactional
    public void updateOrderStatus(Long orderId, Order.OrderStatus newStatus) {
        log.debug("Updating order {} status to: {}", orderId, newStatus);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("Order not found for status update: {}", orderId);
                    return new OrderNotFoundException(orderId);
                });

        order.setOrderStatus(newStatus);
        orderRepository.save(order);
        log.info("Order {} status updated to: {}", orderId, newStatus);
    }

    @Override
    @Transactional
    public OrderDto addOrderItem(Long orderId, OrderDto.OrderItemDto orderItemDto) {
        log.debug("Adding item to order: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("Order not found for adding item: {}", orderId);
                    return new OrderNotFoundException(orderId);
                });

        OrderItem orderItem = orderMapper.mapOrderItemDtoToEntity(orderItemDto);
        orderItem.setOrder(order);
        orderItemRepository.save(orderItem);

        // Refresh order so the latest line items are included
        Order refreshedOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        log.info("Order item added to order: {}", orderId);
        return orderMapper.toDtoWithItems(refreshedOrder);
    }

    @Override
    @Transactional
    public void removeOrderItem(Long orderItemId) {
        log.debug("Removing order item: {}", orderItemId);
        
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> {
                    log.warn("Order item not found for removal: {}", orderItemId);
                    return new OrderNotFoundException("Order item not found: " + orderItemId);
                });

        orderItemRepository.delete(orderItem);
        log.info("Order item removed: {}", orderItemId);
    }
}
