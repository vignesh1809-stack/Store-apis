# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project overview

This is a Spring Boot 3 (Java 17) REST API for an online store, using:
- Spring Web, Spring Data JPA, Spring Security
- MySQL with Flyway for database migrations
- JWT-based authentication/authorization
- Stripe for payments
- MapStruct + Lombok for mapping between entities and DTOs
- springdoc-openapi for generating OpenAPI/Swagger docs

The main application entrypoint is `src/main/java/com/codewithmosh/store/StoreApplication.java`.

## Common commands

All commands below use the Maven Wrapper. Prefer `./mvnw` over a globally installed `mvn`.

### Build and run

- Build (with tests):
  - `./mvnw clean package`
- Build (skip tests):
  - `./mvnw clean package -DskipTests`
- Run the application with the default (dev) profile:
  - `./mvnw spring-boot:run`

### Profiles and configuration

Profiles are configured under `src/main/resources`:
- `application.yaml` – base configuration; sets `spring.profiles.active: dev` and configures JWT and Stripe related properties via environment variables.
- `application-dev.yaml` – dev datasource/JPA settings.
- `application-prod.yaml` – prod datasource and website URL; some values come from environment variables.

To run with a specific profile:
- Dev (default):
  - `./mvnw spring-boot:run`
- Explicit dev:
  - `SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run`
- Prod:
  - `SPRING_PROFILES_ACTIVE=prod ./mvnw spring-boot:run`

JWT, Stripe, and webhook integration rely on environment variables referenced in `application.yaml` (for example, JWT secret and Stripe keys). Set these in your shell or via your IDE run configuration; do not hard-code them.

### Database migrations (Flyway)

Flyway is configured in `pom.xml` and migrations live in `src/main/resources/db/migration`.

Typical commands:
- Apply migrations:
  - `./mvnw flyway:migrate`
- (Destructive) reset DB and re-apply migrations:
  - `./mvnw flyway:clean flyway:migrate`

Be cautious with `flyway:clean` as it will drop objects in the configured schema.

### Tests

Tests live under `src/test/java`.

- Run all tests:
  - `./mvnw test`
- Run a single test class (example):
  - `./mvnw -Dtest=StoreApplicationTests test`
- Run a specific test method (example):
  - `./mvnw -Dtest=StoreApplicationTests#contextLoads test`

Currently there is a basic context-load test in `StoreApplicationTests`; add further tests under the same package hierarchy.

## High-level architecture

### Package layout (business logic)

All application code is under `src/main/java/com/codewithmosh/store` and is organized by concern:

- `config` – Application configuration
  - `SecurityConfig` – Spring Security configuration, HTTP security, filter chain, and authorization setup.
  - `JwtConfig` – Centralizes JWT-related configuration (e.g., secret, token lifetimes) sourced from `application.yaml`.
  - `StripeConfig` – Stripe client configuration using Stripe API keys from configuration/environment.

- `controller` – REST controllers, each focusing on a slice of domain functionality
  - `AuthController` – Authentication endpoints (login, registration, token handling) delegating to `AuthService`/`JwtService`.
  - `UserController` – CRUD and profile-related endpoints for users.
  - `ProductController` – Product catalog operations (list, retrieve, create, update, delete) via `ProductService`/`ProductRepository`.
  - `CartController` – Shopping cart management for the current user (add/update/remove items) via `CartService`.
  - `OrderController` – Order placement, listing, and retrieval via `OrderService`.
  - `AdminController` – Administrative operations (e.g., privileged product/order/user actions), protected by Spring Security roles.
  - `CheckoutController` – Checkout flow, payment initiation, and callbacks using `CheckoutService` and payment abstractions.
  - `GlobalExceptionHandler` – `@ControllerAdvice` for mapping custom exceptions to structured error responses using `ErrorDto`.

- `service` – Business logic and integrations
  - `AuthService` – User registration/authentication and user creation orchestration.
  - `JwtService` / `Jwt` – JWT creation, validation, and parsing; integrates with `JwtConfig` and security filters.
  - `UserService` – User-related operations (lookup, update, profile management) backed by `UserRepository`/`ProfileRepository`.
  - `ProductService` (implicit via controller–repository flow) – operations over `Product`, `Category`, etc.
  - `CartService` – Manages `Cart` and `CartItem` aggregates for a user session.
  - `OrderService` – Creates and manages `Order` and `OrderItems` from cart state.
  - `PaymentGateway` – Abstraction for payment provider; `StripePaymentService` is the Stripe-specific implementation.
  - `StripePaymentService` – Stripe integration for payments, using `StripeConfig` and Stripe DTOs.
  - `CheckoutService`, `CheckoutSession`, `PaymentResult`, `WebhookRequest` – Model the checkout flow, Stripe sessions, results, and webhook payloads.

- `entities` – JPA entities and enums representing persisted domain objects
  - Core entities: `User`, `Profile`, `Role`, `Address`, `Category`, `Product`, `Cart`, `CartItem`, `Order`, `OrderItems`.
  - Status/enum types: `Status`, `Reatiler` (and related domain status/role enums).
  - These entities are wired to Spring Data repositories and mapped to DTOs using MapStruct mappers.

- `repositories` – Spring Data JPA repositories
  - `UserRepository`, `ProfileRepository`, `ProductRepository`, `CartRepository`, `OrderRepository`, `AddressRepository`, `CategoryRepository`.
  - These provide the persistence boundary; services depend on repositories rather than using `EntityManager` directly.

- `dto` – Data Transfer Objects used at API boundaries
  - Request DTOs: `CreateUserRequest`, `LoginUserDto`, `UpdateUserRequest`, `AddCartItemsDto`, `CheckoutRequestDto`, `updatingQuantityDto`, etc.
  - Response DTOs: `UserDto`, `ProductDto`, `CartDto`, `CartItemDto`, `CartProductDto`, `OrderDto`, `OrderItemDto`, `GetOrderDto`, `GetOrderItemDto`, `JwtTockenDto`, `ErrorDto`.
  - DTOs are typically mapped to and from entities via MapStruct mappers in the `mappers` package.

- `mappers` – MapStruct-based mappers connecting entities and DTOs
  - `ProductMapper`, `UserMapper`, `CartMapper`, `OrderMapper`.
  - These are MapStruct interfaces annotated with mapping rules; implementations are generated at build time based on the configuration in `pom.xml` (`mapstruct` and `mapstruct-processor` dependencies and `maven-compiler-plugin` annotation processor paths).

- `filters` – HTTP filters for cross-cutting concerns
  - `JwtTockenAuthentication` – JWT authentication filter that extracts, validates, and sets authentication on incoming requests.
  - `LoggingFilter` – Request/response logging around the HTTP layer.

- `exception` – Custom domain and API exceptions
  - Examples: `ProductNotFoundException`, `CartNotFoundException`, `CartItemNotFoundException`, `CartItemsNotFoundException`, `OrderNotFoundException`, `NoOrdersForTheUserException`, `PaymentException`, `UnAuthorizedUserException`.
  - These are centralized by `GlobalExceptionHandler` to produce consistent API error payloads.

- `validaters` – Custom Bean Validation constraints
  - `Lowercase` – Custom validation annotation.
  - `LowercaseValidator` – Constraint validator implementation enforcing lowercase semantics.

This structure follows a typical Spring layered architecture: controllers → services → repositories/entities, with DTO/mappers for API boundaries and filters/config for cross-cutting concerns.

### Database and migrations

- Flyway migration scripts live in `src/main/resources/db/migration` (e.g., `V1__initial_migration.sql`, `V2_productrecords.sql`).
- Migrations are versioned and applied automatically on app start (if configured) or via `flyway:migrate`.
- Dev environment uses a local MySQL instance with details defined in `application-dev.yaml`; production relies more heavily on environment variables (e.g., JDBC URL).

When adding schema changes:
1. Create a new migration script in `db/migration` with the next version (e.g., `V3__add_discounts.sql`).
2. Update entities and repositories to reflect the schema.
3. Run `./mvnw flyway:migrate` (or start the app with the appropriate profile) against a non-production database to validate.

### Security and authentication

- Spring Security is configured in `SecurityConfig` using `spring-boot-starter-security`.
- JWT support uses the `io.jsonwebtoken` (jjwt) libraries and a custom `JwtTockenAuthentication` filter.
- JWT configuration (secret, access/refresh token lifetimes) is pulled from `application.yaml` via `JwtConfig`.
- Role-based access is enforced at the HTTP layer (and optionally at method level, if annotations are used in services/controllers).

When making changes to authentication/authorization, ensure updates are consistent across:
- `JwtConfig` and `application.yaml` properties
- The `JwtService`/`Jwt` helper logic
- Filter registration and security rules in `SecurityConfig`

### OpenAPI and API documentation

- The project includes `springdoc-openapi-starter-webmvc-ui`, which auto-generates OpenAPI documentation for controllers.
- When you add or change controller methods, review the generated spec/Swagger UI to ensure request/response DTOs and status codes are correct.

### Payments and Stripe integration

- Stripe integration is encapsulated in:
  - `StripeConfig` – configures Stripe SDK with API keys.
  - `StripePaymentService` – implements `PaymentGateway` for actual payment calls.
  - `CheckoutService`, `CheckoutSession`, `PaymentResult`, and `WebhookRequest` – capture the checkout flow and webhook handling.
- Related configuration such as Stripe keys and webhook secrets are read from environment variables in `application.yaml`.

When extending payments (e.g., adding new payment flows or handling more webhook events), keep business logic in the `service` layer and avoid coupling controllers directly to Stripe SDK types.

## How to extend the codebase safely

- Follow the existing layered structure: expose new capabilities in a controller, add or extend service methods, and, if necessary, add repository methods and entities/DTOs.
- Prefer adding DTOs and MapStruct mappings rather than exposing JPA entities directly in controller responses.
- For database changes, always add a new Flyway migration and keep entities, repositories, and migrations in sync.
- For security-sensitive changes (auth, roles, payments), update configuration, services, and filters together and validate with tests and manual local runs.
