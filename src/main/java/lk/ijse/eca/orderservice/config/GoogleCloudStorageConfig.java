package lk.ijse.eca.orderservice.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@Slf4j
public class GoogleCloudStorageConfig {

    @Value("${gcp.credentials.file:}")
    private String credentialsFile;

    @Value("${gcp.project.id}")
    private String projectId;

    @Bean
    public Storage storage() throws IOException {
        log.debug("Initializing Google Cloud Storage with project: {}", projectId);

        StorageOptions storageOptions;
        
        if (credentialsFile != null && !credentialsFile.isEmpty()) {
            // Use service account key file
            log.debug("Using service account key file: {}", credentialsFile);
            InputStream credentialsStream = new ClassPathResource(credentialsFile).getInputStream();
            GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
            
            storageOptions = StorageOptions.newBuilder()
                    .setProjectId(projectId)
                    .setCredentials(credentials)
                    .build();
        } else {
            // Use default credentials (e.g., from environment or metadata server)
            log.debug("Using default Google Cloud credentials");
            storageOptions = StorageOptions.newBuilder()
                    .setProjectId(projectId)
                    .build();
        }

        Storage storage = storageOptions.getService();
        log.info("Google Cloud Storage initialized successfully for project: {}", projectId);
        return storage;
    }
}
