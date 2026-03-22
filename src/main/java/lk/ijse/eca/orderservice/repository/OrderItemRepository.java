package lk.ijse.eca.orderservice.repository;

import lk.ijse.eca.orderservice.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.orderId = :orderId")
    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);

    List<OrderItem> findByProductId(String productId);

    @Query("SELECT oi.productId, oi.productName, SUM(oi.quantity), SUM(oi.totalPrice) FROM OrderItem oi JOIN oi.order o WHERE o.orderDate BETWEEN :startDate AND :endDate GROUP BY oi.productId, oi.productName ORDER BY SUM(oi.totalPrice) DESC")
    List<Object[]> getTopSellingProducts(@Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);

    @Query("SELECT oi.productId, SUM(oi.quantity) FROM OrderItem oi JOIN oi.order o WHERE o.orderDate BETWEEN :startDate AND :endDate GROUP BY oi.productId ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> getMostSoldProductsByQuantity(@Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);

    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi JOIN oi.order o WHERE o.orderDate BETWEEN :startDate AND :endDate AND oi.productId = :productId")
    Long getTotalQuantitySold(@Param("productId") String productId, @Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);

    @Query("SELECT SUM(oi.totalPrice) FROM OrderItem oi JOIN oi.order o WHERE o.orderDate BETWEEN :startDate AND :endDate AND oi.productId = :productId")
    BigDecimal getTotalRevenueByProduct(@Param("productId") String productId, @Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);
}
