package lk.ijse.eca.orderservice.service.impl;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import lk.ijse.eca.orderservice.service.GoogleCloudStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleCloudStorageServiceImpl implements GoogleCloudStorageService {

    private final Storage storage;

    @Value("${gcp.bucket.name}")
    private String bucketName;

    @Value("${gcp.base.url}")
    private String baseUrl;

    @Override
    public String uploadFile(MultipartFile file, String fileName) throws IOException {
        log.debug("Uploading file to GCS: {}", fileName);

        try {
            BlobId blobId = BlobId.of(bucketName, fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    .build();

            Blob blob = storage.create(blobInfo, file.getBytes());
            String fileUrl = baseUrl + "/" + fileName;

            log.info("File uploaded successfully to GCS: {}", fileUrl);
            return fileUrl;

        } catch (StorageException e) {
            log.error("Failed to upload file to GCS: {}", fileName, e);
            throw new StorageException("Failed to upload file: " + fileName, e);
        }
    }

    @Override
    public byte[] downloadFile(String fileName) throws IOException {
        log.debug("Downloading file from GCS: {}", fileName);

        try {
            BlobId blobId = BlobId.of(bucketName, fileName);
            Blob blob = storage.get(blobId);

            if (blob == null) {
                log.warn("File not found in GCS: {}", fileName);
                throw new StorageException("File not found: " + fileName);
            }

            byte[] content = blob.getContent();
            log.debug("File downloaded successfully from GCS: {}", fileName);
            return content;

        } catch (StorageException e) {
            log.error("Failed to download file from GCS: {}", fileName, e);
            throw new StorageException("Failed to download file: " + fileName, e);
        }
    }

    @Override
    public void deleteFile(String fileName) {
        log.debug("Deleting file from GCS: {}", fileName);

        try {
            BlobId blobId = BlobId.of(bucketName, fileName);
            boolean deleted = storage.delete(blobId);

            if (deleted) {
                log.info("File deleted successfully from GCS: {}", fileName);
            } else {
                log.warn("File not found for deletion in GCS: {}", fileName);
            }

        } catch (StorageException e) {
            log.error("Failed to delete file from GCS: {}", fileName, e);
            throw new StorageException("Failed to delete file: " + fileName, e);
        }
    }

    @Override
    public String generatePresignedUrl(String fileName) {
        log.debug("Generating presigned URL for GCS file: {}", fileName);

        try {
            BlobId blobId = BlobId.of(bucketName, fileName);
            URL url = storage.signUrl(
                    BlobInfo.newBuilder(blobId).build(),
                    15, // 15 minutes
                    TimeUnit.MINUTES,
                    Storage.SignUrlOption.httpMethodGet()
            );

            String presignedUrl = url.toString();
            log.debug("Presigned URL generated for GCS file: {}", fileName);
            return presignedUrl;

        } catch (StorageException e) {
            log.error("Failed to generate presigned URL for GCS file: {}", fileName, e);
            throw new StorageException("Failed to generate presigned URL: " + fileName, e);
        }
    }

    @Override
    public boolean fileExists(String fileName) {
        log.debug("Checking if file exists in GCS: {}", fileName);

        try {
            BlobId blobId = BlobId.of(bucketName, fileName);
            Blob blob = storage.get(blobId);
            boolean exists = blob != null;
            log.debug("File exists in GCS {}: {}", fileName, exists);
            return exists;

        } catch (StorageException e) {
            log.error("Failed to check file existence in GCS: {}", fileName, e);
            throw new StorageException("Failed to check file existence: " + fileName, e);
        }
    }

    public String getBucketName() {
        return bucketName;
    }

    public void listFiles() {
        log.debug("Listing files in GCS bucket: {}", bucketName);

        try {
            Bucket bucket = storage.get(bucketName);
            Page<Blob> blobs = bucket.list();

            log.info("Files in bucket {}:", bucketName);
            for (Blob blob : blobs.iterateAll()) {
                log.info("- {} ({} bytes)", blob.getName(), blob.getSize());
            }

        } catch (StorageException e) {
            log.error("Failed to list files in GCS bucket: {}", bucketName, e);
            throw new StorageException("Failed to list files in bucket: " + bucketName, e);
        }
    }
}
