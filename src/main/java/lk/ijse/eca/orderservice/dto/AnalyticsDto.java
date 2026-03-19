package lk.ijse.eca.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsDto {

    private LocalDate startDate;
    private LocalDate endDate;
    
    // Revenue Analytics
    private BigDecimal totalRevenue;
    private BigDecimal averageOrderValue;
    private Long totalOrders;
    
    // Sales by Payment Method
    private List<PaymentMethodAnalytics> paymentMethodAnalytics;
    
    // Daily Sales Summary
    private List<DailySalesSummary> dailySalesSummary;
    
    // Top Customers
    private List<TopCustomer> topCustomers;
    
    // Top Products
    private List<TopProduct> topProducts;
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentMethodAnalytics {
        private String paymentMethod;
        private Long orderCount;
        private BigDecimal totalAmount;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DailySalesSummary {
        private LocalDate date;
        private Long orderCount;
        private BigDecimal totalRevenue;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TopCustomer {
        private String customerId;
        private Long orderCount;
        private BigDecimal totalSpent;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TopProduct {
        private String productId;
        private String productName;
        private Integer totalQuantitySold;
        private BigDecimal totalRevenue;
    }
}
