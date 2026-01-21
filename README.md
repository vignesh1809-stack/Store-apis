# Store APIs - Spring Boot E-commerce Backend

A robust and scalable RESTful API for an e-commerce platform built with Spring Boot. This application provides a complete backend solution featuring secure authentication, product management, shopping cart functionality, order processing, and seamless payment integration with Stripe.

## 🚀 Features

*   **🔐 Authentication & Security**
    *   Secure user registration and login using JWT (JSON Web Tokens).
    *   Role-Based Access Control (RBAC) ensuring secure access for Users and Admins.
*   **📦 Product Management**
    *   Browse, search, and filter products.
    *   Detailed product information and inventory management.
*   **🛒 Shopping Cart**
    *   Add, remove, and update items in the cart.
    *   Persistent cart management for authenticated users.
*   **📦 Order Management**
    *   Place orders and track order status.
    *   Order history for users.
*   **💳 Payments**
    *   Integrated with **Stripe** for secure checkout experiences.
    *   Webhook handling for real-time payment status updates.
*   **🛡️ Admin Dashboard**
    *   Admin-specific endpoints for managing products, categories, and viewing system-wide orders.

## 🛠️ Tech Stack

*   **Core**: [Java 17](https://www.java.com/)
*   **Framework**: [Spring Boot 3.4.1](https://spring.io/projects/spring-boot)
*   **Database**: [MySQL 8.0](https://www.mysql.com/), [Flyway](https://flywaydb.org/) (schema migration)
*   **Security**: [Spring Security](https://spring.io/projects/spring-security), [JWT](https://jwt.io/)
*   **Payment Gateway**: [Stripe API](https://stripe.com/)
*   **Tools & Libraries**:
    *   [Lombok](https://projectlombok.org/) (Boilerplate reduction)
    *   [MapStruct](https://mapstruct.org/) (DTO mapping)
    *   [Maven](https://maven.apache.org/) (Build tool)
    *   [OpenAPI/Swagger](https://swagger.io/) (API Documentation)

## 📂 Project Structure

```
src/main/java/com/codewithmosh/store
├── config          # Security and app configuration
├── controller      # REST Controllers (API Endpoints)
├── dto             # Data Transfer Objects
├── entities        # JPA Entities (Database Tables)
├── exception       # Global Exception Handling
├── filters         # JWT Authentication Filters
├── mappers         # MapStruct interfaces
├── repositories    # Spring Data JPA Repositories
└── service         # Business Logic Layer
```

## 🚀 Getting Started

### Prerequisites
Ensure you have the following installed:
*   Java 17 or higher
*   Maven 3.6+
*   MySQL 8.0+
*   A [Stripe Account](https://stripe.com/) for API keys

### ⚙️ Configuration & Environment Variables

This application uses environment variables for sensitive configuration. You can set these in your IDE or export them in your terminal.

| Variable | Description | Example |
| :--- | :--- | :--- |
| `JWT_SECRET` | Secret key for signing JWT tokens | `my-very-long-secret-key-12345` |
| `STRIPE_KEY` | Stripe Secret Key (from Stripe Dashboard) | `sk_test_...` |
| `WEBHOOK_SECRET_KEY` | Stripe Webhook Signing Secret | `whsec_...` |

**Setting up Stripe Webhooks (Required for Order Completion):**
1.  Go to the [Stripe Dashboard > Webhooks](https://dashboard.stripe.com/test/webhooks).
2.  Add an endpoint:
    *   URL: `http://localhost:8080/checkout/webhook` (Use `ngrok` for local dev or Stripe CLI)
    *   Events: `checkout.session.completed`, `payment_intent.succeeded`
3.  Reveal the **Signing Secret** and use it as `WEBHOOK_SECRET_KEY`.

### 🏃‍♂️ Running the Application

1.  **Clone the repository**:
    ```bash
    git clone <repository-url>
    cd Store-apis
    ```

2.  **Configure Environment**:
    Export the required variables:
    ```bash
    export JWT_SECRET="your-secret"
    export STRIPE_KEY="your-stripe-key"
    export WEBHOOK_SECRET_KEY="your-webhook-key"
    ```

3.  **Start MySQL**:
    Ensure MySQL is running on port `3306`. The application expects a database named `store_api`, user `root`, and password `Start#123` (default in `application.yaml`). 
    *Note: You may need to update `src/main/resources/application.yaml` if your local DB creds differ.*

4.  **Run with Maven**:
    ```bash
    ./mvnw spring-boot:run
    ```

The API will be available at: `http://localhost:8080`

## 📡 API Endpoints

### 🔐 Authentication (`/auth`)
*   `POST /auth/login` - Authenticate user and get JWT
*   `POST /auth/register` - Register a new user

### 🛍️ Products (`/products`)
*   `GET /products` - List all products
*   `GET /products/{id}` - Get product details
*   `POST /products` - Create new product (Admin)
*   `PUT /products/{id}` - Update product (Admin)
*   `DELETE /products/{id}` - Delete product (Admin)

### 👤 Users (`/users`)
*   `GET /users/profile` - Get current user profile
*   `PUT /users/profile` - Update profile

### 🛒 Shopping Cart (`/cart`)
*   `GET /cart` - Get current user's cart
*   `POST /cart/items` - Add item to cart
*   `DELETE /cart/items/{itemId}` - Remove item from cart

### 📦 Orders (`/orders`)
*   `GET /orders` - List user's orders
*   `GET /orders/{id}` - Get order details
*   `POST /orders` - Create a new order

### 💳 Checkout (`/checkout`)
*   `POST /checkout` - Initiate Stripe checkout session
*   `POST /checkout/webhook` - Handle Stripe webhook events

### 🛡️ Admin (`/admin`)
*   `GET /admin/orders` - View all system orders
*   `GET /admin/stats` - View sales statistics

## ❓ Troubleshooting

**Q: "Could not resolve placeholder 'STRIPE_KEY'" error?**
A: You forgot to set the environment variable. Run `export STRIPE_KEY=your_key` before starting the app.

**Q: Database connection fails?**
A: Check if MySQL is running. Verify credentials in `src/main/resources/application.yaml`.

**Q: Payment succeeds but order status doesn't update?**
A: This usually means the Stripe Webhook didn't reach your local server. Use the [Stripe CLI](https://stripe.com/docs/stripe-cli) to forward events:
```bash
stripe listen --forward-to localhost:8080/checkout/webhook
```
Then use the webhook secret valid provided by the CLI.
