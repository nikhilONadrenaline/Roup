# ROUP — Secondary Ticket & Collectibles Marketplace

A secure, high-performance secondary marketplace for limited-edition festival tickets, collectible merchandise, and other valuable event-related assets.

This project is built using a robust microservice architecture to handle continuous trading, multiple auction formats, secure escrow, and real-time order book updates.

## 🚀 Local Deployment

### Prerequisites
- **JDK 21** installed and configured.
- **Node.js and npm** installed.
- **IntelliJ IDEA** or any suitable IDE for running the backend services.
- Required database and infrastructure services configured.

### Steps to Run

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd <repository-folder>
   ```

2. **Set up the frontend**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

3. **Configure the backend**

   Open the backend services in IntelliJ IDEA or another suitable IDE. Configure the required environment variables and database connections in the application configuration files.

4. **Run the backend services**

   Start the required Spring Boot microservices individually. **User-Svc is currently under development and does not need to be started.**

5. **Access the application**

   Open the local URL displayed in the frontend terminal, typically `http://localhost:5173`.

### Notes
- Ensure all required infrastructure services, such as PostgreSQL, Redis, and the service registry, are running and accessible before starting the dependent microservices.
- Configure the appropriate environment variables and credentials before running the application.

## 🚀 Live Deployments

The application is currently live and deployed across Vercel (Frontend) and Render (Backend Microservices).

**Frontend Client:**
* **Web App:** [https://roup-eight.vercel.app](https://roup-eight.vercel.app)

**Backend Services:**
* **API Gateway:** [https://roup-gateway.onrender.com](https://roup-gateway.onrender.com)
* **Service Registry (Eureka):** [https://roup-eureka.onrender.com](https://roup-eureka.onrender.com)
* **Auth Service:** [https://roup-auth.onrender.com](https://roup-auth.onrender.com)
* **User Service:** [https://roup-user.onrender.com](https://roup-user.onrender.com)
* **Wallet Service:** [https://roup-wallet.onrender.com](https://roup-wallet.onrender.com)
* **Auction Service:** [https://roup-auction.onrender.com](https://roup-auction.onrender.com)
* **Orders Service:** [https://roup-orders.onrender.com](https://roup-orders.onrender.com)

---

## 🛠 Technology Stack

### Backend
* **Language & Framework:** Java 21, Spring Boot, Spring Cloud (Gateway, Netflix Eureka, OpenFeign)
* **Security:** Spring Security (JWT authentication)
* **Real-time:** Native Spring WebSocket API (without STOMP)
* **Databases:** PostgreSQL (Relational), MongoDB (Document), Redis (Caching & Streams)

### Frontend
* **Core:** React, Vite, JavaScript
* **Real-time:** Native browser WebSocket API

### Infrastructure
* Docker 
* API Gateway pattern with service discovery

---

## 🏗 Microservice Architecture

The platform is strictly separated by domain responsibilities to ensure data integrity, especially concerning financial balances and ticket ownership.

| Service | Primary Storage | Responsibility |
| :--- | :--- | :--- |
| **API Gateway** | N/A | Single public entry point, JWT validation, rate limiting, and request routing. |
| **Eureka Server** | N/A | Dynamic service discovery and registry. |
| **Auth Service** | PostgreSQL | Registration, login, secure password hashing, and JWT lifecycle. |
| **User Service** | PostgreSQL | User profiles, account status, and preferences. |
| **Auction Service** | MongoDB + Redis | Core matching engine. Handles continuous order books, limit/market orders, and auction lifecycles. |
| **Orders Service** | PostgreSQL/Mongo | Post-trade fulfillment workflow, escrow coordination, claim-token generation, and disputes. |
| **Wallet Service** | PostgreSQL | Financial domain owner. Manages available/reserved balances and the double-entry ledger. |

---

## 📈 Core Features & Trading Mechanisms

### 1. Continuous Limit/Market Order Book
The core trading mechanism matching buyers and sellers using FIFO price-time priority. Unfilled limit orders rest in the continuous book while market orders execute immediately against the best available price.

### 2. Supported Auction/Market Formats
* **Secondary-Marketplace:** Ticket/Relic trading platform with in-memory matching, real-time depth charts via WebSockets.
* **Dutch Auction:** Descending-price schedule until an eligible buyer accepts atomically.

### 3. Financial Safety & Escrow
* **Wallet Reservations:** Balances are split into `Available` and `Reserved`. Funds are reserved (not immediately transferred) upon placing bids or limit orders.
* **Double-Entry Ledger:** Every posted financial transaction maintains balanced debit and credit entries.


### 4. Anti-Scalping & Bot Protection
* **Price Collars:** Configurable resale maximums based on original face value multipliers.
* **System Safeguards:** Idempotency keys, rate limits and bounded queues.

---

## 🤝 Contributing & Development Rules

1. **Strict Data Ownership:** Services must not directly modify another service's database. Cross-boundary communication must happen via HTTP (OpenFeign) or durable events.
2. **Atomicity:** Do not assume `@Transactional` works across microservices. Use distributed workflows and reconciliation.
3. **Real-time State:** The server is the absolute authority for auction state and timers. Client timers are purely cosmetic.
