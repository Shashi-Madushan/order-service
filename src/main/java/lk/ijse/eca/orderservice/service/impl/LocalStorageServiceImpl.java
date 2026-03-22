package lk.ijse.eca.orderservice.service.impl;

import lk.ijse.eca.orderservice.service.LocalStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Service
@Slf4j
public class LocalStorageServiceImpl implements LocalStorageService {

    @Value("${local.storage.base-path:./receipts}")
    private String basePath;

    @Override
    public String saveFile(byte[] content, String fileName, String contentType) throws IOException {
        log.debug("Saving file locally: {}", fileName);

        try {
            Path baseDir = Paths.get(basePath);
            if (!Files.exists(baseDir)) {
                Files.createDirectories(baseDir);
                log.debug("Created base directory: {}", baseDir);
            }

            Path filePath = baseDir.resolve(fileName);
            Path parentDir = filePath.getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
                log.debug("Created parent directory: {}", parentDir);
            }

            Files.write(filePath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            
            String fileUrl = "file://" + filePath.toString();
            log.info("File saved locally: {}", fileUrl);
            return fileUrl;

        } catch (IOException e) {
            log.error("Failed to save file locally: {}", fileName, e);
            throw new IOException("Failed to save file: " + fileName, e);
        }
    }

    @Override
    public byte[] readFile(String fileName) throws IOException {
        log.debug("Reading local file: {}", fileName);

        try {
            Path filePath = Paths.get(basePath, fileName);
            
            if (!Files.exists(filePath)) {
                log.warn("File not found locally: {}", fileName);
                throw new IOException("File not found: " + fileName);
            }

            byte[] content = Files.readAllBytes(filePath);
            log.debug("File read successfully: {}", fileName);
            return content;

        } catch (IOException e) {
            log.error("Failed to read local file: {}", fileName, e);
            throw new IOException("Failed to read file: " + fileName, e);
        }
    }

    @Override
    public void deleteFile(String fileName) {
        log.debug("Deleting local file: {}", fileName);

        try {
            Path filePath = Paths.get(basePath, fileName);
            boolean deleted = Files.deleteIfExists(filePath);

            if (deleted) {
                log.info("File deleted successfully: {}", fileName);
            } else {
                log.warn("File not found for deletion: {}", fileName);
            }

        } catch (IOException e) {
            log.error("Failed to delete local file: {}", fileName, e);
            throw new RuntimeException("Failed to delete file: " + fileName, e);
        }
    }

    @Override
    public boolean fileExists(String fileName) {
        log.debug("Checking if local file exists: {}", fileName);

        try {
            Path filePath = Paths.get(basePath, fileName);
            boolean exists = Files.exists(filePath);
            log.debug("Local file exists {}: {}", fileName, exists);
            return exists;

        } catch (Exception e) {
            log.error("Failed to check local file existence: {}", fileName, e);
            return false;
        }
    }
}
