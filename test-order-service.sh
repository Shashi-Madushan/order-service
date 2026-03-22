#!/bin/bash

# =============================================================================
# Order Service Comprehensive Test Script
# =============================================================================
# This script tests all endpoints of the Order Service
# Make sure the order-service is running on port 8082
# =============================================================================


# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
BASE_URL="http://localhost:7001"
API_PREFIX="/api/v1/orders"
ORDERS_URL="${BASE_URL}${API_PREFIX}"

# Counters
TESTS_PASSED=0
TESTS_FAILED=0

# Helper functions
print_header() {
    echo ""
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}"
}

print_success() {
    echo -e "${GREEN}✓ PASS:${NC} $1"
    ((TESTS_PASSED++))
}

print_error() {
    echo -e "${RED}✗ FAIL:${NC} $1"
    ((TESTS_FAILED++))
}

print_info() {
    echo -e "${YELLOW}ℹ INFO:${NC} $1"
}

make_request() {
    local method=$1
    local url=$2
    local data=$3
    local content_type="${4:-application/json}"

    if [ -n "$data" ]; then
        curl -s -X "$method" \
            -H "Content-Type: $content_type" \
            -d "$data" \
            "$url" \
            -w "\nHTTP_CODE:%{http_code}\n"
    else
        curl -s -X "$method" \
            "$url" \
            -w "\nHTTP_CODE:%{http_code}\n"
    fi
}

check_response() {
    local response=$1
    local expected_code=$2
    local test_name=$3

    local http_code=$(echo "$response" | grep "HTTP_CODE:" | cut -d: -f2)
    local body=$(echo "$response" | sed '/HTTP_CODE:/d')

    if [ "$http_code" == "$expected_code" ]; then
        print_success "$test_name (HTTP $http_code)"
        echo "$body"
        return 0
    else
        print_error "$test_name (Expected HTTP $expected_code, got $http_code)"
        echo "$body"
        return 1
    fi
}

# Wait for service to be ready
print_header "WAITING FOR SERVICE"
echo "Checking if API Gateway is available at ${BASE_URL}..."
for i in {1..30}; do
    if curl -s "${BASE_URL}/actuator/health" > /dev/null 2>&1; then
        print_success "API Gateway is up and running"
        break
    fi
    if [ $i -eq 30 ]; then
        print_error "API Gateway did not start within 30 seconds"
        exit 1
    fi
    echo -n "."
    sleep 1
done

# =============================================================================
# ORDER CRUD OPERATIONS
# =============================================================================
print_header "TESTING ORDER CRUD OPERATIONS"

# 1. Create Order
print_info "Creating a new order..."
CREATE_RESPONSE=$(make_request "POST" "$ORDERS_URL" '{
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
        },
        {
            "productId": "PROD002",
            "productName": "Test Product 2",
            "quantity": 1,
            "unitPrice": 50.00,
            "totalPrice": 50.00,
            "discountAmount": 0.00
        }
    ]
}')

if check_response "$CREATE_RESPONSE" "201" "Create Order"; then
    CREATED_ORDER=$(echo "$CREATE_RESPONSE" | sed '/HTTP_CODE:/d')
    ORDER_ID=$(echo "$CREATED_ORDER" | grep -o '"orderId":[0-9]*' | cut -d: -f2)
    print_info "Created Order ID: $ORDER_ID"
fi

# 2. Get All Orders
print_info "Getting all orders..."
GET_ALL_RESPONSE=$(make_request "GET" "$ORDERS_URL")
check_response "$GET_ALL_RESPONSE" "200" "Get All Orders"

# 3. Get Order by ID (if order was created)
if [ -n "$ORDER_ID" ]; then
    print_info "Getting order by ID: $ORDER_ID"
    GET_ONE_RESPONSE=$(make_request "GET" "$ORDERS_URL/$ORDER_ID")
    check_response "$GET_ONE_RESPONSE" "200" "Get Order by ID"
fi

# 4. Get Orders by Customer ID
print_info "Getting orders by customer ID: CUST001"
GET_BY_CUSTOMER_RESPONSE=$(make_request "GET" "$ORDERS_URL/customer/CUST001")
check_response "$GET_BY_CUSTOMER_RESPONSE" "200" "Get Orders by Customer ID"

# 5. Get Orders by Status
print_info "Getting orders by status: PENDING"
GET_BY_STATUS_RESPONSE=$(make_request "GET" "$ORDERS_URL/status/PENDING")
check_response "$GET_BY_STATUS_RESPONSE" "200" "Get Orders by Status"

# 6. Get Orders by Payment Method
print_info "Getting orders by payment method: CASH"
GET_BY_PAYMENT_RESPONSE=$(make_request "GET" "$ORDERS_URL/payment-method/CASH")
check_response "$GET_BY_PAYMENT_RESPONSE" "200" "Get Orders by Payment Method"

# 7. Get Orders by Date Range
print_info "Getting orders by date range"
START_DATE="2024-03-01T00:00:00"
END_DATE="2024-12-31T23:59:59"
GET_BY_DATE_RESPONSE=$(make_request "GET" "$ORDERS_URL/date-range?startDate=$START_DATE&endDate=$END_DATE")
check_response "$GET_BY_DATE_RESPONSE" "200" "Get Orders by Date Range"

# 8. Update Order (if order was created)
if [ -n "$ORDER_ID" ]; then
    print_info "Updating order: $ORDER_ID"
    UPDATE_RESPONSE=$(make_request "PUT" "$ORDERS_URL/$ORDER_ID" '{
        "orderDate": "2024-03-22T10:00:00",
        "customerId": "CUST001",
        "orderStatus": "CONFIRMED",
        "paymentMethod": "CREDIT_CARD",
        "subtotalAmount": 120.00,
        "taxAmount": 12.00,
        "discountAmount": 10.00,
        "totalAmount": 122.00
    }')
    check_response "$UPDATE_RESPONSE" "200" "Update Order"
fi

# 9. Update Order Status (if order was created)
if [ -n "$ORDER_ID" ]; then
    print_info "Updating order status to CONFIRMED"
    UPDATE_STATUS_RESPONSE=$(make_request "PUT" "$ORDERS_URL/$ORDER_ID/status?newStatus=CONFIRMED")
    check_response "$UPDATE_STATUS_RESPONSE" "200" "Update Order Status"
fi

# 10. Add Order Item (if order was created)
if [ -n "$ORDER_ID" ]; then
    print_info "Adding item to order: $ORDER_ID"
    ADD_ITEM_RESPONSE=$(make_request "POST" "$ORDERS_URL/$ORDER_ID/items" '{
        "productId": "PROD003",
        "productName": "Test Product 3",
        "quantity": 3,
        "unitPrice": 15.00,
        "totalPrice": 45.00,
        "discountAmount": 0.00
    }')
    check_response "$ADD_ITEM_RESPONSE" "200" "Add Order Item"
    
    # Extract order item ID from response for removal test
    if [ $? -eq 0 ]; then
        ORDER_ITEM_ID=$(echo "$ADD_ITEM_RESPONSE" | grep -o '"orderItemId":[0-9]*' | head -1 | cut -d: -f2)
    fi
fi

# 11. Remove Order Item (if order item was created)
if [ -n "$ORDER_ITEM_ID" ]; then
    print_info "Removing order item: $ORDER_ITEM_ID"
    REMOVE_ITEM_RESPONSE=$(make_request "DELETE" "$ORDERS_URL/items/$ORDER_ITEM_ID")
    check_response "$REMOVE_ITEM_RESPONSE" "204" "Remove Order Item"
fi

# =============================================================================
# ANALYTICS ENDPOINTS
# =============================================================================
print_header "TESTING ANALYTICS ENDPOINTS"

ANALYTICS_START="2024-01-01T00:00:00"
ANALYTICS_END="2024-12-31T23:59:59"

# 12. Get Sales Analytics
print_info "Getting sales analytics"
SALES_ANALYTICS_RESPONSE=$(make_request "GET" "$ORDERS_URL/analytics/sales?startDate=$ANALYTICS_START&endDate=$ANALYTICS_END")
check_response "$SALES_ANALYTICS_RESPONSE" "200" "Get Sales Analytics"

# 13. Get Daily Sales Analytics
print_info "Getting daily sales analytics"
DAILY_SALES_RESPONSE=$(make_request "GET" "$ORDERS_URL/analytics/daily-sales?startDate=$ANALYTICS_START&endDate=$ANALYTICS_END")
check_response "$DAILY_SALES_RESPONSE" "200" "Get Daily Sales Analytics"

# 14. Get Customer Analytics
print_info "Getting customer analytics"
CUSTOMER_ANALYTICS_RESPONSE=$(make_request "GET" "$ORDERS_URL/analytics/customers?startDate=$ANALYTICS_START&endDate=$ANALYTICS_END")
check_response "$CUSTOMER_ANALYTICS_RESPONSE" "200" "Get Customer Analytics"

# 15. Get Product Analytics
print_info "Getting product analytics"
PRODUCT_ANALYTICS_RESPONSE=$(make_request "GET" "$ORDERS_URL/analytics/products?startDate=$ANALYTICS_START&endDate=$ANALYTICS_END")
check_response "$PRODUCT_ANALYTICS_RESPONSE" "200" "Get Product Analytics"

# 16. Get Order Count by Status
print_info "Getting order count by status: PENDING"
COUNT_RESPONSE=$(make_request "GET" "$ORDERS_URL/count/status/PENDING")
check_response "$COUNT_RESPONSE" "200" "Get Order Count by Status"

# =============================================================================
# ERROR HANDLING TESTS
# =============================================================================
print_header "TESTING ERROR HANDLING"

# 17. Get Non-Existent Order (should return 404)
print_info "Testing 404 for non-existent order"
NOT_FOUND_RESPONSE=$(make_request "GET" "$ORDERS_URL/999999")
check_response "$NOT_FOUND_RESPONSE" "404" "Get Non-Existent Order (404)"

# 18. Create Order with Invalid Data (should return 422)
print_info "Testing 422 for invalid order data"
INVALID_DATA_RESPONSE=$(make_request "POST" "$ORDERS_URL" '{
    "customerId": "",
    "totalAmount": -100
}')
check_response "$INVALID_DATA_RESPONSE" "422" "Create Order with Invalid Data (422)"

# 19. Delete Order (if order was created)
if [ -n "$ORDER_ID" ]; then
    print_info "Deleting order: $ORDER_ID"
    DELETE_RESPONSE=$(make_request "DELETE" "$ORDERS_URL/$ORDER_ID")
    check_response "$DELETE_RESPONSE" "204" "Delete Order"
    
    # Verify deletion
    print_info "Verifying order deletion"
    VERIFY_DELETE_RESPONSE=$(make_request "GET" "$ORDERS_URL/$ORDER_ID")
    check_response "$VERIFY_DELETE_RESPONSE" "404" "Verify Order Deletion (404)"
fi

# =============================================================================
# SUMMARY
# =============================================================================
print_header "TEST SUMMARY"
echo -e "${GREEN}Tests Passed: $TESTS_PASSED${NC}"
echo -e "${RED}Tests Failed: $TESTS_FAILED${NC}"
echo ""

if [ $TESTS_FAILED -eq 0 ]; then
    echo -e "${GREEN}All tests passed!${NC}"
    exit 0
else
    echo -e "${RED}Some tests failed!${NC}"
    exit 1
fi
