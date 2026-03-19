package lk.ijse.eca.orderservice.service.impl;

import lk.ijse.eca.orderservice.dto.OrderDto;
import lk.ijse.eca.orderservice.service.DocumentService;
import lk.ijse.eca.orderservice.service.GoogleCloudStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentServiceImpl implements DocumentService {

    private final GoogleCloudStorageService storageService;

    @Override
    public String generateReceipt(OrderDto order) throws IOException {
        String fileName = "receipts/receipt_" + order.getOrderId() + ".txt";
        String receiptContent = buildReceiptContent(order);
        
        byte[] content = receiptContent.getBytes();
        return storageService.uploadFile(new org.springframework.mock.web.MockMultipartFile(
                fileName, fileName, "text/plain", content), fileName);
    }

    @Override
    public String generateInvoice(OrderDto order) throws IOException {
        String fileName = "invoices/invoice_" + order.getOrderId() + ".txt";
        String invoiceContent = buildInvoiceContent(order);
        
        byte[] content = invoiceContent.getBytes();
        return storageService.uploadFile(new org.springframework.mock.web.MockMultipartFile(
                fileName, fileName, "text/plain", content), fileName);
    }

    @Override
    public byte[] downloadReceipt(String fileName) throws IOException {
        return storageService.downloadFile(fileName);
    }

    @Override
    public byte[] downloadInvoice(String fileName) throws IOException {
        return storageService.downloadFile(fileName);
    }

    private String buildReceiptContent(OrderDto order) {
        StringBuilder sb = new StringBuilder();
        sb.append("================================\n");
        sb.append("           PAYMENT RECEIPT       \n");
        sb.append("================================\n\n");
        sb.append("Order ID: ").append(order.getOrderId()).append("\n");
        sb.append("Date: ").append(order.getOrderDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n");
        sb.append("Customer ID: ").append(order.getCustomerId()).append("\n");
        sb.append("Payment Method: ").append(order.getPaymentMethod()).append("\n\n");
        sb.append("ITEMS:\n");
        
        if (order.getOrderItems() != null) {
            for (OrderDto.OrderItemDto item : order.getOrderItems()) {
                sb.append(String.format("  %s x%d - $%.2f\n", 
                    item.getProductName(), item.getQuantity(), item.getTotalPrice()));
            }
        }
        
        sb.append("\n--------------------------------\n");
        sb.append(String.format("Subtotal: $%.2f\n", order.getSubtotalAmount()));
        sb.append(String.format("Tax: $%.2f\n", order.getTaxAmount()));
        sb.append(String.format("Discount: $%.2f\n", order.getDiscountAmount()));
        sb.append(String.format("TOTAL: $%.2f\n", order.getTotalAmount()));
        sb.append("================================\n");
        
        return sb.toString();
    }

    private String buildInvoiceContent(OrderDto order) {
        StringBuilder sb = new StringBuilder();
        sb.append("================================\n");
        sb.append("            INVOICE              \n");
        sb.append("================================\n\n");
        sb.append("Invoice ID: INV-").append(order.getOrderId()).append("\n");
        sb.append("Order Date: ").append(order.getOrderDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n");
        sb.append("Customer ID: ").append(order.getCustomerId()).append("\n");
        sb.append("Status: ").append(order.getOrderStatus()).append("\n\n");
        sb.append("BILLING DETAILS:\n");
        
        if (order.getOrderItems() != null) {
            for (OrderDto.OrderItemDto item : order.getOrderItems()) {
                sb.append(String.format("  %s (%s)\n", item.getProductName(), item.getProductId()));
                sb.append(String.format("    Quantity: %d x $%.2f = $%.2f\n", 
                    item.getQuantity(), item.getUnitPrice(), item.getTotalPrice()));
            }
        }
        
        sb.append("\n--------------------------------\n");
        sb.append(String.format("Subtotal: $%.2f\n", order.getSubtotalAmount()));
        sb.append(String.format("Tax: $%.2f\n", order.getTaxAmount()));
        sb.append(String.format("Discount: $%.2f\n", order.getDiscountAmount()));
        sb.append(String.format("TOTAL AMOUNT: $%.2f\n", order.getTotalAmount()));
        sb.append("Payment Method: ").append(order.getPaymentMethod()).append("\n");
        sb.append("================================\n");
        
        return sb.toString();
    }
}
