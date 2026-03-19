package lk.ijse.eca.orderservice.service;

import lk.ijse.eca.orderservice.dto.OrderDto;

import java.io.IOException;

public interface DocumentService {

    String generateReceipt(OrderDto order) throws IOException;

    String generateInvoice(OrderDto order) throws IOException;

    byte[] downloadReceipt(String fileName) throws IOException;

    byte[] downloadInvoice(String fileName) throws IOException;
}
