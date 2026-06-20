# Enviro365 Investments — Withdrawal Portal
**Junior Developer Assessment · June 2026**
Author: Tadiwanashe Songore | Package: `com.enviro.assessment.junior.tadii`

---

## Quick Start

### Prerequisites
- Java 17+ (project compiles with `--release 21`; tested on Java 24)
- Maven 3.9+
- Node.js 18+ (for frontend)

### Backend (Spring Boot)
```bash
cd backend
mvn spring-boot:run
# Runs on http://localhost:8080
# H2 console: http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:enviro365db)
```

### Frontend (React + Vite)
```bash
cd frontend
npm install
npm run dev
# Runs on http://localhost:5173
```

### Run backend tests
```bash
cd backend
mvn test
```

---

## Architecture

```
enviro365/
├── backend/                         # Spring Boot (Java)
│   └── src/
│       ├── main/java/com/enviro/assessment/junior/tadii/
│       │   ├── controller/          # REST controllers (thin — delegate to services)
│       │   ├── service/             # Business logic & rules
│       │   ├── repository/          # Spring Data JPA interfaces
│       │   ├── model/               # JPA entities (database tables)
│       │   ├── dto/                 # Data Transfer Objects (API request/response shapes)
│       │   ├── exception/           # Global exception handling
│       │   └── DataSeeder.java      # Seeds H2 with sample data on startup
│       └── test/java/com/enviro/assessment/junior/tadii/
│           └── WithdrawalServiceTest.java   # Unit tests (JUnit 5 + Mockito)
└── frontend/                        # React + Vite
    └── src/
        ├── App.jsx                  # Main app (portfolio, form, history)
        └── services/api.js          # API service layer
```

**Layering, and why:**
- **Model** — pure JPA entities, no business logic. Maps 1:1 to database tables.
- **Repository** — interfaces only; Spring Data JPA generates SQL from method names (e.g. `findByInvestorId`).
- **Service** — where business rules and calculations live. The only layer that should be unit tested in isolation.
- **Controller** — thin HTTP layer. Converts JSON ↔ Java, delegates everything to a service.
- **DTO** — flat, purpose-built objects returned by the API. Prevents infinite-loop serialization on bidirectional JPA relationships (`Investor` ↔ `InvestmentProduct`) and decouples the API shape from the database schema.

---

## A note on Lombok

This project does **not** use Lombok. It was originally scaffolded with Lombok (`@Data`, `@RequiredArgsConstructor`, etc.) but Lombok's annotation processor was incompatible with Java 24 (`com.sun.tools.javac.code.TypeTag :: UNKNOWN` compiler error) at the time of building this. Rather than pin the project to an older JDK, all getters, setters, and constructors were written out explicitly. This has the side benefit of making the generated code visible and easy to discuss/explain — there's no "magic" happening at compile time.

---

## Data Storage

The app uses **H2, an in-memory database** (`spring.jpa.hibernate.ddl-auto=create-drop`). Tables are created fresh on every application startup and destroyed on shutdown — there is no data file persisted to disk. `DataSeeder.java` runs automatically on startup (`CommandLineRunner`) and populates two sample investors with investment products so the app is immediately usable without manual setup.

---

## API Documentation

### Investors

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/investors` | List all investors |
| GET | `/api/investors/{id}/portfolio` | Get full portfolio with products |

### Withdrawals

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/withdrawals` | Submit withdrawal notice |
| GET | `/api/withdrawals/investor/{id}` | Get investor's withdrawal history |
| GET | `/api/withdrawals` | Get all withdrawals (admin) |

**POST /api/withdrawals — Request Body:**
```json
{
  "investorId": 1,
  "productId": 2,
  "amount": 10000.00
}
```

**Response (success):**
```json
{
  "success": true,
  "message": "Withdrawal notice created successfully",
  "data": {
    "id": 1,
    "investorName": "Robert Khoza",
    "productName": "Retirement Annuity Fund",
    "amount": 10000.00,
    "balanceAfterWithdrawal": 840000.00,
    "status": "APPROVED"
  }
}
```

**Response (business rule violation):**
```json
{
  "success": false,
  "message": "Retirement withdrawals are only allowed for investors older than 65 years."
}
```

All responses (success or failure) use the same `ApiResponse<T>` envelope: `{ success, message, data }`. This means the frontend has one consistent way of handling every API call rather than guessing the shape per endpoint.

### Export

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/export/csv` | Download all withdrawals as CSV |
| GET | `/api/export/csv?investorId=1` | Download investor-specific CSV |

---

## Business Rules Implemented

All four live in `WithdrawalService.createWithdrawal()`:

1. **Retirement age check** — Retirement product withdrawals only allowed if investor age > 65. Age is calculated live from `dateOfBirth`, never stored, so it's always accurate.
2. **Balance check** — Withdrawal cannot exceed current balance.
3. **90% cap** — Withdrawal cannot exceed 90% of current balance. All money math uses `BigDecimal`, not `double`, to avoid floating-point rounding errors with currency.
4. **Proper error feedback** — All rule violations throw a `BusinessRuleException`, caught by `GlobalExceptionHandler` and returned as a structured JSON error (HTTP 422) with a human-readable message.

---

## Advanced Requirements Implemented

| Feature | Implementation |
|---------|---------------|
| ✅ Global Exception Handling | `GlobalExceptionHandler.java` — `@RestControllerAdvice` intercepts exceptions app-wide and returns consistent JSON errors instead of default Spring error pages |
| ✅ DTO Layer | `InvestorPortfolioDTO`, `ProductDTO`, `WithdrawalRequestDTO`, `WithdrawalResponseDTO`, `ApiResponse<T>` |
| ✅ Input Validation | `@Valid` + `@NotNull` / `@DecimalMin` on `WithdrawalRequestDTO`, enforced server-side before the controller method body runs |
| ✅ Unit Tests | `WithdrawalServiceTest.java` (JUnit 5 + Mockito) — tests all 3 business rules plus a happy-path case, using mocked repositories so no real database is touched |
| ✅ UI Validation | Client-side checks in `WithdrawalForm` (React) before the API call — instant feedback, but never a substitute for backend validation |

---

## Seed Data

On startup, 2 investors are seeded:

| Investor | Age | Products |
|----------|-----|---------|
| Robert Khoza | 73 | Retirement Annuity (R850k), Tax-Free Savings (R120k) |
| Lindiwe Dlamini | 35 | Unit Trust (R45k), Retirement Pension (R200k — blocked by age rule) |

Use Lindiwe's retirement product to demonstrate the age-restriction business rule failing correctly.

---

## AI Usage Disclosure

AI tools (Claude by Anthropic) were used to assist with:
- Initial boilerplate code generation (entity classes, repositories, controller scaffolding)
- Debugging the Lombok/Java 24 compiler incompatibility and migrating to explicit getters/setters
- Code review and business rule verification
- README formatting

All AI-generated code was reviewed, understood, and customised by the author. The architecture decisions, business logic structure, layering strategy, and validation approach were designed and verified by the author, who can walk through and justify every layer of the system (model → repository → service → controller → DTO) and each of the four mandatory business rules on request.