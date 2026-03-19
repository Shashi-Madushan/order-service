package lk.ijse.eca.orderservice.client;

import lk.ijse.eca.orderservice.exception.ProductServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@Slf4j
public class ProductServiceClient {

    private final RestClient restClient;

    public ProductServiceClient(@LoadBalanced RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("http://PRODUCT-SERVICE")
                .build();
    }

    public void validateProduct(String productId) {
        log.debug("Validating product in Product-Service for ID: {}", productId);
        try {
            restClient.get()
                    .uri("/api/v1/products/{id}", productId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("Failed to validate product for ID: {}", productId, e);
            throw new ProductServiceException(
                    "Unable to validate product: " + productId, e);
        }
    }
}
