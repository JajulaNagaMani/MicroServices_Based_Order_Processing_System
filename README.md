# OrderFlow — Microservices Order Management

OrderFlow is a full-stack order-management application. A React dashboard provides views and CRUD workflows for customers, products, orders, and notifications. The backend is implemented as four Spring Boot REST services, each backed by its own MySQL database. The order service communicates with the customer, product, and notification services over HTTP.

## Features

- Dashboard with order, inventory, customer, and notification metrics, charts, and recent orders.
- Create, view, update, and delete customers, products, orders, and notifications.
- Product stock adjustment endpoints.
- Order processing integrated with customer and product data, inventory updates, and notifications.
- Docker Compose configuration for the backend services and their MySQL databases.

## Architecture

| Component | Responsibility | Local port | Database |
| --- | --- | ---: | --- |
| Frontend | React dashboard (Vite) | `5173` | — |
| Customer service | Customer REST API | `8081` | `customer_db` |
| Product service | Product REST API and stock updates | `8082` | `product_db` |
| Order service | Order REST API and service-to-service orchestration | `8083` | `order_db` |
| Notification service | Notification REST API | `8084` | `notification_db` |
| MySQL — customer | Customer persistence | `3307` → container `3306` | `customer_db` |
| MySQL — product | Product persistence | `3308` → container `3306` | `product_db` |
| MySQL — order | Order persistence | `3309` → container `3306` | `order_db` |
| MySQL — notification | Notification persistence | `3310` → container `3306` | `notification_db` |

The Vite development server proxies `/api/customers`, `/api/products`, `/api/orders`, and `/api/notifications` to the corresponding backend service. The order service uses configurable base URLs to call the other backend services.

## Technology

- **Frontend:** React 18, Vite 6, Tailwind CSS 4, Axios, and Lucide icons.
- **Backend:** Java 25, Spring Boot 4.1.1, Spring Web, Spring Data JPA, Jakarta Validation, and Maven.
- **Persistence:** MySQL 8.
- **Local backend orchestration:** Docker Compose.

## Prerequisites

- Docker Desktop (or Docker Engine) with the Docker Compose plugin.
- Node.js and npm.
- For building/running backend services outside Docker: JDK 25.

## Run locally

### 1. Start the backend and databases

Run this command from the repository root:

```bash
docker compose -f microservices-order-system/docker-compose.yml up --build
```

Compose builds and starts all four services and their MySQL databases. The services wait for their respective databases to become healthy before starting. The order service also depends on the other three services.

### 2. Start the dashboard

In a separate terminal:

```bash
cd frontend
npm install
npm run dev
```

Open <http://localhost:5173>.

The dashboard loads its data from the backend APIs. If a service is not available, the dashboard reports that service's connection error while showing any data it could load.

### Stop the backend

From the repository root:

```bash
docker compose -f microservices-order-system/docker-compose.yml down
```

The named MySQL volumes are retained when the containers are stopped. Removing them with `docker compose down --volumes` permanently deletes the local database data.

## REST API

| Service | Base path | Operations |
| --- | --- | --- |
| Customers | `http://localhost:8081/api/customers` | `GET` list, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` |
| Products | `http://localhost:8082/api/products` | `GET` list, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` |
| Orders | `http://localhost:8083/api/orders` | `GET` list, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` |
| Notifications | `http://localhost:8084/api/notifications` | `GET` list, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` |

The product service also supports:

- `PUT /api/products/{id}/reduce-stock?quantity={quantity}`
- `PUT /api/products/{id}/increase-stock?quantity={quantity}`

All endpoints use JSON request/response bodies where applicable. Request fields and validation requirements are defined by each service's DTOs.

## Configuration

### Frontend

`frontend/.env.example` lists the supported environment variables. Copy it to `frontend/.env` when you need to override local defaults:

```bash
cp .env.example .env
```

For Vite development proxy targets, set service root URLs:

| Variable | Default |
| --- | --- |
| `VITE_CUSTOMER_SERVICE_URL` | `http://localhost:8081` |
| `VITE_PRODUCT_SERVICE_URL` | `http://localhost:8082` |
| `VITE_ORDER_SERVICE_URL` | `http://localhost:8083` |
| `VITE_NOTIFICATION_SERVICE_URL` | `http://localhost:8084` |

For a production frontend build, the API clients can instead be configured with complete API URLs using `VITE_CUSTOMER_API_URL`, `VITE_PRODUCT_API_URL`, `VITE_ORDER_API_URL`, and `VITE_NOTIFICATION_API_URL`. Vite environment variables are embedded at build time; do not put secrets in frontend variables.

### Backend

Each service reads its database connection from `JDBC_DATABASE_URL`, `JDBC_DATABASE_USERNAME`, and `JDBC_DATABASE_PASSWORD`. The order service also reads `CUSTOMER_SERVICE_URL`, `PRODUCT_SERVICE_URL`, and `NOTIFICATION_SERVICE_URL`. Docker Compose supplies the database and service URLs for its internal network.

The current Compose file includes development database credentials. Change them and configure an appropriate secrets mechanism before using this setup outside a local development environment.

## Development and tests

Build the frontend production bundle:

```bash
cd frontend
npm run build
```

Preview the built frontend locally:

```bash
npm run preview
```

Each backend service includes a Maven wrapper (`mvnw` / `mvnw.cmd`). From a service directory, run:

```bash
./mvnw test
```

On Windows PowerShell, use:

```powershell
.\mvnw.cmd test
```

The existing Java test sources are in the order and product services. Run their tests from their respective service directories.

## Repository layout

```text
.
├── frontend/
│   ├── src/
│   │   ├── components/       # Dashboard and customer/product/order/notification views
│   │   ├── hooks/            # Shared service data loading
│   │   └── services/         # HTTP API clients
│   ├── .env.example
│   └── package.json
└── microservices-order-system/
    ├── docker-compose.yml
    ├── customer-service/
    ├── product-service/
    ├── order-service/
    └── notification-service/
```
