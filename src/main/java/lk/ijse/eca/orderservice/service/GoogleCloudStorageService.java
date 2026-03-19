package lk.ijse.eca.orderservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface GoogleCloudStorageService {

    String uploadFile(MultipartFile file, String fileName) throws IOException;

    byte[] downloadFile(String fileName) throws IOException;

    void deleteFile(String fileName);

    String generatePresignedUrl(String fileName);

    boolean fileExists(String fileName);
}
