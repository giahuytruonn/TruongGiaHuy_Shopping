# ShopNow - Spring Boot Shopping Web Application

A full-stack e-commerce web application built with Spring Boot, Thymeleaf, Spring Security, and MariaDB.

This project demonstrates practical backend engineering skills for interviews: authentication/authorization, layered architecture, shopping cart workflow, order processing, email integration, and an AI shopping assistant powered by Gemini.

## Why this project is interview-ready

- Clean MVC + Service + Repository architecture with JPA entities and relational mapping.
- Role-based access control (`ADMIN`, `CUSTOMER`) using Spring Security.
- End-to-end shopping flow: browse products -> add to cart -> checkout -> order history.
- AI assistant integration with retrieval-augmented context from both documents and database.
- Real-world integration points: SMTP email sending, SQL seed data, environment variable usage.

## Core features

### Customer features
- Browse products and search by keyword.
- View product details and comments.
- Add/update/remove items in cart.
- Checkout to create orders.
- View order list and order detail.
- Ask shopping questions in AI chat page.

### Admin features
- Full product management (add/edit/delete).
- Customer management in admin panel.
- Access control on protected routes.

### AI assistant
- Chat endpoint: `POST /chat/message`.
- Uses Gemini API (`gemini-2.0-flash-exp`) through `google-genai` client.
- Builds context from:
  - `src/main/resources/documents/shopping-info.txt`
  - live database data (products, orders, customer context)

## Tech stack

- **Language:** Java 21
- **Framework:** Spring Boot 3.5.6
- **Web:** Spring MVC + Thymeleaf
- **Security:** Spring Security 6
- **Data:** Spring Data JPA + Hibernate
- **Database:** MariaDB
- **Validation:** Jakarta Validation + Hibernate Validator
- **Mail:** Spring Boot Mail Starter (SMTP)
- **AI:** Google Gemini (`com.google.genai`)
- **Build tool:** Maven Wrapper (`mvnw`, `mvnw.cmd`)

## Architecture overview

- **Controller layer** (`src/main/java/iuh/controller`): request handling and page navigation.
- **Service layer** (`src/main/java/iuh/service`): business logic (cart, orders, chat, email, customer context).
- **Repository layer** (`src/main/java/iuh/repository`): database access with Spring Data JPA.
- **Model layer** (`src/main/java/iuh/model`): entity mapping (`Product`, `Customer`, `Order`, `OrderLine`, etc.).
- **View layer** (`src/main/resources/templates`): Thymeleaf templates for UI.

## Security model

Configured in `src/main/java/iuh/config/SecurityConfig.java`:

- Public: `/`, `/home`, `/login`, static files, `/chat/**`, product browsing routes.
- `ADMIN` only: product CRUD routes, `/admin/**`.
- `ADMIN` + `CUSTOMER`: cart, orders, comment actions.
- Form login page: `/login`.

## Data model (high-level)

- `Category` 1 - * `Product`
- `Product` 1 - * `Comment`
- `Customer` 1 - * `Order`
- `Order` * - * `Product` via `OrderLine` (composite key)

## Getting started

### 1) Prerequisites

- JDK 21
- MariaDB (running locally)
- Maven (optional, wrapper is included)

### 2) Clone

```bash
git clone https://github.com/<your-username>/<your-repo>.git
cd TruongGiaHuy_Shopping
```

### 3) Create database

```sql
CREATE DATABASE shoppingdb;
```

### 4) Configure application

Current datasource is in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/shoppingdb
spring.datasource.username=root
spring.datasource.password=sa
```

Update these values for your local MariaDB account.

### 5) Configure environment variables

Create a `.env` file in project root:

```dotenv
GEMINI_API_KEY=your_gemini_api_key
```


### 6) Configure SMTP (optional but recommended)

Email settings are in `application.properties` (`spring.mail.*`).
If SMTP is not configured correctly, checkout email sending can fail.

### 7) Run application

Windows (PowerShell):

```powershell
.\mvnw.cmd spring-boot:run
```

Or with Maven:

```bash
mvn spring-boot:run
```

### 8) Open in browser

- Home: `http://localhost:8080/home`
- Product list: `http://localhost:8080/product`
- Chat assistant: `http://localhost:8080/chat`

## Seed data

Initial data is loaded from:

- `src/main/resources/sql/data.sql`

Includes categories, products, comments, customers, orders, and order lines.

## Suggested demo script for interview (3-5 minutes)

1. Show architecture folders (`controller`, `service`, `repository`, `model`).
2. Login and explain role-based authorization.
3. Browse products -> add to cart -> checkout flow.
4. Open chat page and ask a product question to show Gemini integration.
5. Explain how context retrieval combines static documents + database data.

## Known considerations

- Some credentials are currently configured directly in project files; move all secrets to environment variables before public deployment.
- PayOS dependency exists in `pom.xml` and `.env`, but payment integration flow is not fully wired in the current controller layer.
- Unit/integration tests are minimal (`src/test/java/iuh/ApplicationTests.java`).

## Future improvements

- Add DTOs + mapper layer for clearer API boundaries.
- Add pagination/sorting for product and order pages.
- Add comprehensive unit + integration tests.
- Move to Docker Compose for reproducible local setup.
- Add CI pipeline (build + test + lint).

## Author

**Truong Gia Huy**  
Student project - Spring Boot Shopping Application

---
If you use this project for portfolio/interview, consider adding screenshots (home, product detail, cart, order list, chat) directly in this README for stronger visual impact.
