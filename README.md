# Order Service

A comprehensive Spring Boot microservice for managing orders in a retail POS system. This service handles order creation, management, order items, payment processing, and provides RESTful APIs for integration with other system components including analytics and reporting features.

## 📋 Project Description

The Order Service is a core component of a microservices-based retail Point of Sale (POS) system. It provides robust functionality for managing customer orders, order items, payment methods, order status tracking, and comprehensive analytics. The service is built using Spring Boot with Spring Cloud for microservice architecture, featuring MySQL for data persistence, and comprehensive validation mechanisms.

### Key Features

- **Order Management**: Complete CRUD operations for customer orders
- **Order Items Management**: Add, update, and remove items from orders
- **Status Tracking**: Track orders through multiple statuses (PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED, REFUNDED)
- **Payment Methods**: Support for CASH, CREDIT_CARD, DEBIT_CARD, MOBILE_PAYMENT, BANK_TRANSFER, GIFT_CARD
- **Order Analytics**: Sales analytics, daily sales reports, top customers, and top products
- **Order Querying**: Query orders by customer, status, payment method, and date range
- **Flexible Pricing**: Support for subtotal, tax, discount, and total amount calculations
- **Receipt & Invoice**: URLs for receipt and invoice storage
- **Customer Integration**: Integration with IAM Service for customer validation
- **Product Integration**: Integration with Product Service for product validation

## 🛠 Technology Stack

### Core Framework
- **Spring Boot**: 4.0.3 - Main application framework
- **Spring Cloud**: 2025.1.0 - Microservice architecture support
- **Spring Data JPA**: Database operations and ORM
- **Spring Validation**: Input validation framework
- **Spring RestClient**: HTTP client for inter-service communication

### Database & Persistence
- **MySQL**: Primary database for order data
- **Spring Data JPA**: Repository pattern and database operations
- **Hibernate**: ORM framework for database interactions

### Development & Build Tools
- **Java**: 25 - Programming language
- **Maven**: Dependency management and build tool
- **Lombok**: Code generation for boilerplate reduction
- **MapStruct**: 1.6.3 - Bean mapping framework

### Additional Libraries
- **Spring Boot Actuator**: Application monitoring and management
- **Spring Boot DevTools**: Development-time tools
- **Netflix Eureka Client**: Service discovery
- **Spring Cloud Config**: Centralized configuration management
- **Google Cloud Storage**: File storage for receipts and invoices
- **Spring Boot Starter Validation**: Bean validation support

## 🚀 Setup / Getting Started Instructions

### Prerequisites

- **Java 25** or higher
- **Maven 3.8+**
- **MySQL 8.0+**
- **Spring Cloud Config Server** (running on port 9000)
- **Netflix Eureka Service Registry** (running on port 8761)
- **API Gateway** (running on port 7001)
- **IAM Service** (for customer validation)
- **Product Service** (for product validation)

### Installation Steps

1. **Clone the Repository**
   ```bash
   git clone <repository-url>
   cd Project-Services/order-service
   ```

2. **Database Setup**
   ```bash
   # Start MySQL
   mysql -u root -p
   
   # Create database (automatically created by the application)
   CREATE DATABASE IF NOT EXISTS order_db;
   ```

3. **Configuration**
   - Ensure Spring Cloud Config Server is running on `http://localhost:9000`
   - Ensure Eureka Service Registry is running on `http://localhost:8761`
   - Configuration files are located in `src/main/resources/`
   - Default profile: `dev`

4. **Build the Application**
   ```bash
   ./mvnw clean install
   ```

5. **Run the Application**
   ```bash
   ./mvnw spring-boot:run
   ```
   
   Or using the Maven wrapper:
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```

6. **Verify Service**
   - The service will start on port 8082 (or as configured)
   - Health check endpoint: `http://localhost:8082/actuator/health`
   - API Gateway routing: `http://localhost:7001/api/v1/orders`

### Docker Setup (Optional)

```bash
# Build Docker image
docker build -t order-service .

# Run with Docker
docker run -p 8082:8082 \
  -e SPRING_PROFILES_ACTIVE=dev \
  -e SPRING_CLOUD_CONFIG_URI=http://config-server:9000 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/order_db \
  order-service
```

## 📚 API Endpoints

### Order Management Endpoints

| Method | Endpoint | Description | Request Type |
|--------|----------|-------------|--------------|
| POST | `/api/v1/orders` | Create new order | `application/json` |
| GET | `/api/v1/orders` | Get all orders | - |
| GET | `/api/v1/orders/{orderId}` | Get order by ID | - |
| PUT | `/api/v1/orders/{orderId}` | Update order | `application/json` |
| DELETE | `/api/v1/orders/{orderId}` | Delete order | - |
| GET | `/api/v1/orders/customer/{customerId}` | Get orders by customer | - |
| GET | `/api/v1/orders/status/{orderStatus}` | Get orders by status | - |
| GET | `/api/v1/orders/payment-method/{paymentMethod}` | Get orders by payment method | - |
| GET | `/api/v1/orders/date-range` | Get orders by date range | Query params: startDate, endDate |

### Order Item Management Endpoints

| Method | Endpoint | Description | Request Type |
|--------|----------|-------------|--------------|
| POST | `/api/v1/orders/{orderId}/items` | Add item to order | `application/json` |
| DELETE | `/api/v1/orders/items/{orderItemId}` | Remove item from order | - |

### Order Status Management

| Method | Endpoint | Description | Request Type |
|--------|----------|-------------|--------------|
| PUT | `/api/v1/orders/{orderId}/status` | Update order status | Query param: newStatus |

### Analytics Endpoints

| Method | Endpoint | Description | Request Type |
|--------|----------|-------------|--------------|
| GET | `/api/v1/orders/analytics/sales` | Get sales analytics | Query params: startDate, endDate |
| GET | `/api/v1/orders/analytics/daily-sales` | Get daily sales summary | Query params: startDate, endDate |
| GET | `/api/v1/orders/analytics/customers` | Get top customers | Query params: startDate, endDate |
| GET | `/api/v1/orders/analytics/products` | Get top products | Query params: startDate, endDate |
| GET | `/api/v1/orders/count/status/{orderStatus}` | Count orders by status | - |

### Order Status Values
- `PENDING` - Order awaiting confirmation
- `CONFIRMED` - Order confirmed
- `PROCESSING` - Order being processed
- `SHIPPED` - Order shipped
- `DELIVERED` - Order delivered
- `CANCELLED` - Order cancelled
- `REFUNDED` - Order refunded

### Payment Method Values
- `CASH` - Cash payment
- `CREDIT_CARD` - Credit card payment
- `DEBIT_CARD` - Debit card payment
- `MOBILE_PAYMENT` - Mobile payment
- `BANK_TRANSFER` - Bank transfer
- `GIFT_CARD` - Gift card payment

## 🧪 Test Scripts

### Comprehensive Test Suite

The project includes a comprehensive test script (`test-order-service.sh`) that tests all API endpoints through the API Gateway.

#### Running Tests

```bash
# Make the script executable
chmod +x test-order-service.sh

# Run all tests
./test-order-service.sh
```

#### Test Coverage

The test script covers:

1. **Order CRUD Operations**
   - Order creation with items
   - Order updates and deletions
   - Order retrieval operations

2. **Order Querying**
   - Get orders by customer ID
   - Get orders by status
   - Get orders by payment method
   - Get orders by date range

3. **Order Item Management**
   - Add items to orders
   - Remove items from orders

4. **Order Status Management**
   - Update order status

5. **Analytics Operations**
   - Sales analytics
   - Daily sales summary
   - Top customers
   - Top products
   - Order count by status

6. **Edge Case Tests**
   - Non-existent orders (404)
   - Invalid data validation (422)
   - Order deletion verification

#### Test Configuration

- **API Gateway URL**: `http://localhost:7001`
- **Test Data**: Automatically generated unique test data
- **Results**: Color-coded output with pass/fail summary

### Manual Testing Examples

#### Create Order
```bash
curl -X POST "http://localhost:7001/api/v1/orders" \
  -H "Content-Type: application/json" \
  -d '{
    "orderDate": "2024-03-22T10:00:00",
    "customerId": "CUST001",
    "orderStatus": "PENDING",
    "paymentMethod": "CASH",
    "subtotalAmount": 100.00,
    "taxAmount": 10.00,
    "discountAmount": 5.00,
    "totalAmount": 105.00,
    "orderItems": [
      {
        "productId": "PROD001",
        "productName": "Test Product 1",
        "quantity": 2,
        "unitPrice": 25.00,
        "totalPrice": 50.00,
        "discountAmount": 0.00
      }
    ]
  }'
```

#### Get Orders by Status
```bash
curl -X GET "http://localhost:7001/api/v1/orders/status/PENDING"
```

#### Add Order Item
```bash
curl -X POST "http://localhost:7001/api/v1/orders/1/items" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "PROD003",
    "productName": "Test Product 3",
    "quantity": 3,
    "unitPrice": 15.00,
    "totalPrice": 45.00,
    "discountAmount": 0.00
  }'
```

#### Get Sales Analytics
```bash
curl -X GET "http://localhost:7001/api/v1/orders/analytics/sales?startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59"
```

## 🏗 Project Structure

```
order-service/
├── src/main/java/lk/ijse/eca/orderservice/
│   ├── controller/          # REST API Controllers
│   │   └── OrderController.java
│   ├── dto/                 # Data Transfer Objects
│   │   ├── OrderDto.java
│   │   ├── AnalyticsDto.java
│   │   └── CustomerDto.java
│   ├── entity/              # JPA Entities
│   │   ├── Order.java
│   │   └── OrderItem.java
│   ├── exception/           # Custom Exceptions
│   │   ├── OrderNotFoundException.java
│   │   ├── CustomerServiceException.java
│   │   ├── ProductServiceException.java
│   │   └── StorageException.java
│   ├── repository/          # JPA Repositories
│   │   ├── OrderRepository.java
│   │   └── OrderItemRepository.java
│   ├── service/             # Service Layer
│   │   ├── OrderService.java
│   │   └── impl/
│   │       └── OrderServiceImpl.java
│   ├── mapper/              # MapStruct Mappers
│   │   └── OrderMapper.java
│   ├── client/              # Service Clients
│   │   ├── CustomerServiceClient.java
│   │   └── ProductServiceClient.java
│   ├── config/              # Configuration Classes
│   │   └── RestClientConfig.java
│   ├── handler/             # Exception Handlers
│   │   └── GlobalExceptionHandler.java
│   └── OrderServiceApplication.java
├── src/main/resources/
│   ├── application.yaml
│   ├── application-dev.yaml
│   └── gcp-credentials.json # GCP Storage credentials
├── test-order-service.sh    # Comprehensive test script
├── pom.xml                  # Maven configuration
└── README.md               # This file
```

## 🔧 Configuration

### Application Properties

Key configuration options:

```yaml
spring:
  application:
    name: order-service
  profiles:
    active: dev
  config:
    import: "optional:configserver:"
  cloud:
    config:
      uri: http://localhost:9000
  datasource:
    url: jdbc:mysql://localhost:3306/order_db?createDatabaseIfNotExist=true
    username: root
    password: mysql
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/

server:
  port: 8082

gcp:
  project:
    id: eca-project-2026
  credentials:
    file: gcp-credentials.json
```

### Environment Variables

- `SPRING_PROFILES_ACTIVE`: Active profile (dev/prod)
- `SPRING_CLOUD_CONFIG_URI`: Config server URI
- `SPRING_DATASOURCE_URL`: MySQL connection URL
- `SPRING_DATASOURCE_USERNAME`: MySQL username
- `SPRING_DATASOURCE_PASSWORD`: MySQL password
- `GCP_PROJECT_ID`: Google Cloud project ID
- `GCP_CREDENTIALS_FILE`: Path to GCP credentials file

## 📊 Monitoring & Health

### Actuator Endpoints

- `/actuator/health` - Application health status
- `/actuator/info` - Application information
- `/actuator/metrics` - Application metrics

### Logging

- **Log Level**: Configurable per profile (DEBUG for dev)
- **Log Format**: Structured logging with request tracking
- **File Logging**: Configured for production environments
- **Service Integration Logging**: Logs for customer and product service calls

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Run the test suite
6. Submit a pull request

## 📝 Student Information

- **Student Name**: Shashi Madushan
- **Student Number**: 2301691002
- **Project**: Order Service for Retail POS System
- **Course**: Enterprise Computing Architecture

## 📄 License

This project is part of an academic assignment for the Enterprise Computing Architecture course.

## 📞 Support

For any issues or questions regarding this service:

1. Check the test script output for common issues
2. Review the application logs
3. Verify all prerequisite services are running (Config Server, Eureka, API Gateway, MySQL)
4. Check the configuration files
5. Verify IAM Service and Product Service are accessible for customer/product validation

---

**Note**: This service is designed to be part of a larger microservices architecture and should be used in conjunction with other services such as API Gateway, Config Server, Service Registry, IAM Service, and Product Service for full functionality.
