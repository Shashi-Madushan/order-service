package lk.ijse.eca.orderservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface LocalStorageService {

    String saveFile(byte[] content, String fileName, String contentType) throws IOException;

    byte[] readFile(String fileName) throws IOException;

    void deleteFile(String fileName);

    boolean fileExists(String fileName);
}
