package lk.ijse.eca.orderservice.repository;

import lk.ijse.eca.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerIdOrderByOrderDateDesc(String customerId);

    List<Order> findByOrderStatus(Order.OrderStatus orderStatus);

    List<Order> findByPaymentMethod(Order.PaymentMethod paymentMethod);

    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate ORDER BY o.orderDate DESC")
    List<Order> findByOrderDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderStatus = :status")
    Long countByOrderStatus(@Param("status") Order.OrderStatus status);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalRevenueBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT o.paymentMethod, COUNT(o), SUM(o.totalAmount) FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate GROUP BY o.paymentMethod")
    List<Object[]> getSalesByPaymentMethod(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT DATE(o.orderDate), COUNT(o), SUM(o.totalAmount) FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate GROUP BY DATE(o.orderDate) ORDER BY DATE(o.orderDate)")
    List<Object[]> getDailySalesSummary(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT o.customerId, COUNT(o), SUM(o.totalAmount) FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate GROUP BY o.customerId ORDER BY SUM(o.totalAmount) DESC")
    List<Object[]> getTopCustomersBySales(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
