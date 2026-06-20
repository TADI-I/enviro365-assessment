# Enviro365 Investments — Withdrawal Portal
**Junior Developer Assessment · June 2026**
Author: Tadiwanashe Songore | Package: `com.enviro.assessment.junior.tadii`

---

## Quick Start

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

---

## Architecture

```
enviro365/
├── backend/                         # Spring Boot (Java 17)
│   └── src/main/java/com/enviro/assessment/junior/tadii/
│       ├── controller/              # REST controllers
│       ├── service/                 # Business logic
│       ├── repository/              # Spring Data JPA
│       ├── model/                   # JPA entities
│       ├── dto/                     # Data Transfer Objects
│       └── exception/               # Global exception handling
└── frontend/                        # React + Vite
    └── src/
        ├── App.jsx                  # Main app (portfolio, form, history)
        └── services/api.js          # API service layer
```

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

### Export

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/export/csv` | Download all withdrawals as CSV |
| GET | `/api/export/csv?investorId=1` | Download investor-specific CSV |

---

## Business Rules Implemented

1. **Retirement age check** — Retirement product withdrawals only allowed if investor age > 65
2. **Balance check** — Withdrawal cannot exceed current balance
3. **90% cap** — Withdrawal cannot exceed 90% of current balance
4. **Proper error feedback** — All rule violations return descriptive error messages

---

## Advanced Requirements Implemented

| Feature | Implementation |
|---------|---------------|
| ✅ Global Exception Handling | `GlobalExceptionHandler.java` — `@RestControllerAdvice` |
| ✅ DTO Layer | `InvestorPortfolioDTO`, `ProductDTO`, `WithdrawalRequestDTO`, `WithdrawalResponseDTO`, `ApiResponse<T>` |
| ✅ Input Validation | `@Valid`, `@NotNull`, `@DecimalMin` on `WithdrawalRequestDTO` |
| ✅ Unit Tests | `WithdrawalServiceTest.java` — Mockito tests for all 3 business rules |
| ✅ UI Validation | Client-side checks before API call (amount > 0, max 90%, product required) |

---

## Seed Data

On startup, 2 investors are seeded:

| Investor | Age | Products |
|----------|-----|---------|
| Robert Khoza | 73 | Retirement Annuity (R850k), Tax-Free Savings (R120k) |
| Lindiwe Dlamini | 35 | Unit Trust (R45k), Retirement Pension (R200k — blocked) |

---

## AI Usage Disclosure

AI tools (Claude by Anthropic) were used to assist with:
- Boilerplate code generation (entity classes, repositories, pom.xml structure)
- Code review and business rule verification
- README formatting

All AI-generated code was reviewed, understood, and customised by the author.
The architecture decisions, business logic structure, and validation strategy
were designed and verified by the author independently.
