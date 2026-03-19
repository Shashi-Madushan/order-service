package lk.ijse.eca.orderservice.controller;

import lk.ijse.eca.orderservice.dto.OrderDto;
import lk.ijse.eca.orderservice.service.DocumentService;
import lk.ijse.eca.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Positive;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Slf4j
@Validated
public class DocumentController {

    private final DocumentService documentService;
    private final OrderService orderService;

    @PostMapping("/orders/{orderId}/receipt")
    public ResponseEntity<String> generateReceipt(
            @PathVariable @Positive(message = "Order ID must be a positive number") Long orderId) throws IOException {
        log.info("POST /api/v1/documents/orders/{}/receipt", orderId);
        
        OrderDto order = orderService.getOrder(orderId);
        String receiptUrl = documentService.generateReceipt(order);
        
        // Update order with receipt URL
        order.setReceiptUrl(receiptUrl);
        orderService.updateOrder(orderId, order);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(receiptUrl);
    }

    @PostMapping("/orders/{orderId}/invoice")
    public ResponseEntity<String> generateInvoice(
            @PathVariable @Positive(message = "Order ID must be a positive number") Long orderId) throws IOException {
        log.info("POST /api/v1/documents/orders/{}/invoice", orderId);
        
        OrderDto order = orderService.getOrder(orderId);
        String invoiceUrl = documentService.generateInvoice(order);
        
        // Update order with invoice URL
        order.setInvoiceUrl(invoiceUrl);
        orderService.updateOrder(orderId, order);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceUrl);
    }

    @GetMapping("/receipts/{fileName}")
    public ResponseEntity<byte[]> downloadReceipt(
            @PathVariable String fileName) throws IOException {
        log.info("GET /api/v1/documents/receipts/{}", fileName);
        
        byte[] content = documentService.downloadReceipt(fileName);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(content);
    }

    @GetMapping("/invoices/{fileName}")
    public ResponseEntity<byte[]> downloadInvoice(
            @PathVariable String fileName) throws IOException {
        log.info("GET /api/v1/documents/invoices/{}", fileName);
        
        byte[] content = documentService.downloadInvoice(fileName);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(content);
    }
}
