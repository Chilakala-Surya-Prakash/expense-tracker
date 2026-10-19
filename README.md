# 💰 AI-Powered Expense Tracker & Budget Alert API

A modern, stateless RESTful backend built with **Java 21** and **Spring Boot 3** that helps users track daily expenses, monitor monthly budgets with real-time threshold alerts (`SAFE`, `WARNING`, `EXCEEDED`), and automatically log expenses from plain-English text using **Google Gemini 2.5 Flash AI**.

---

## 🚀 Key Features

- **🔐 Stateless JWT Authentication:** Secure user registration and login using Spring Security 6, `BCrypt` password hashing, and HMAC-SHA signed JSON Web Tokens (`jjwt`).
- **🤖 AI Natural Language Expense Parser:** Converts conversational sentences (e.g., *"Paid 450 rupees for Uber ride to office today"*) into structured database records (`title`, `amount`, `category`, `merchant`, `expenseDate`) via the Google Gemini REST API.
- **📊 Real-Time Monthly Budget Alerts:** Automatically evaluates total monthly spending against the user's custom monthly budget on every transaction:
  - `SAFE`: Under 80% of monthly budget used.
  - `WARNING`: 80% to 99.9% of monthly budget used.
  - `EXCEEDED`: 100%+ of monthly budget used.
- **📈 Category-Wise Spending Breakdown:** Aggregates monthly expenses by category (`FOOD`, `TRANSPORT`, `SHOPPING`, `BILLS`, `ENTERTAINMENT`, `HEALTH`, `EDUCATION`, `OTHER`) using custom JPQL queries.
- **🔄 Dynamic Budget Management:** Users can update their monthly budget at any time (`PUT /api/expenses/budget`) with instant recalculation.
- **🛡️ Centralized Exception Handling:** Clean, standardized JSON error responses across all endpoints using `@RestControllerAdvice`.

---

## 🛠️ Tech Stack

| Layer | Technology |
| :--- | :--- |
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.x (Spring Web, Spring Data JPA, Spring Security, Validation) |
| **Authentication** | JWT (`io.jsonwebtoken:jjwt 0.12.6`) + BCrypt |
| **AI Integration** | Google Gemini 2.5 Flash API (`RestClient` + Jackson) |
| **Database** | H2 In-Memory Database (Dev) / PostgreSQL Ready |
| **API Documentation** | SpringDoc OpenAPI 3 (Swagger UI) |
| **Build Tool** | Maven & Lombok |

---

## 🔌 API Endpoints

### 1. Authentication (`/api/auth`)

| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Public | Register a new user & initial monthly budget; returns JWT |
| `POST` | `/api/auth/login` | Public | Authenticate user credentials; returns JWT |

### 2. Expenses & Budget (`/api/expenses`)
*(Requires header: `Authorization: Bearer <JWT_TOKEN>`)*

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/expenses` | Log a manual expense & receive real-time budget alert status |
| `POST` | `/api/expenses/parse-ai` | Parse natural language text with Gemini AI and save expense |
| `GET` | `/api/expenses` | Fetch all expenses for the authenticated user (newest first) |
| `GET` | `/api/expenses/summary?year=YYYY&month=MM` | Get monthly total spent, remaining budget, % used, alert status & category breakdown |
| `PUT` | `/api/expenses/budget` | Update the authenticated user's monthly budget |

---

## ⚡ Getting Started

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/Chilakala-Surya-Prakash/expense-tracker.git](https://github.com/Chilakala-Surya-Prakash/expense-tracker.git)
   cd expense-tracker