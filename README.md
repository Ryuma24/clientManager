# Client Manager

Client Manager is a freelancer-focused client, invoicing, and payment application. It gives freelancers a place to organize client relationships and issue itemized invoices, while clients get a portal to review balances and pay through Razorpay Checkout.

## Product Highlights

- **Two role-specific experiences:** freelancer account management and a client invoice/payment portal.
- **Client relationship management:** create and manage client profiles and associate client logins with profiles.
- **Itemized invoicing:** create invoices with line-item names, quantities, unit prices, issue dates, due dates, and invoice statuses.
- **Client invoice access:** clients see invoices attached to their linked profiles, with invoice detail, payment totals, and outstanding balances.
- **Razorpay Checkout:** create orders on the backend and collect payment through Razorpay Checkout.
- **Server-side payment reconciliation:** validate Checkout signatures and process Razorpay `payment.captured` webhooks through a shared payment finalizer.
- **Repeat-safe payment finalization:** lock the payment row and apply the invoice balance change once, even if Checkout verification and webhook delivery arrive more than once.
- **JWT security:** stateless authentication, BCrypt password hashing, role restrictions, and ownership checks for invoice/payment operations.

## Payment Webhook

The Razorpay webhook is implemented at `POST /payments/webhook/razorpay`.

The endpoint accepts the raw webhook body, validates `X-Razorpay-Signature` using a dedicated webhook secret, and handles `payment.captured` events. It checks the captured order and amount against the pending payment record, then invokes the same transactional, row-locked finalizer used by the browser Checkout verification endpoint. Duplicate notifications for a payment already finalized do not increment the invoice again.

To enable delivery in a deployment, configure `RAZORPAY_WEBHOOK_SECRET` in the backend environment and register the deployed webhook URL in Razorpay Dashboard with the `payment.captured` event selected. The webhook secret is distinct from the Razorpay API key secret.

## User Flows

### Freelancer

1. Register or sign in with a `USER` account.
2. Create client profiles and associate client accounts.
3. Open a client profile and create an invoice with one or more line items.
4. Review invoices and follow payment progress.

### Client

1. Register or sign in with a `CLIENT` account.
2. Open the client portal to review invoices assigned to linked client profiles.
3. Inspect invoice items, total, payments, and balance.
4. Pay an outstanding balance through Razorpay Checkout.

## Architecture

| Layer | Implementation |
| --- | --- |
| Frontend | React 19, Vite, state-based navigation |
| Backend API | Java 17, Spring Boot 3.5.4 |
| Authentication and authorization | Spring Security, JWT, BCrypt |
| Persistence | Spring Data JPA, PostgreSQL |
| Schema versioning | Flyway; Hibernate schema validation |
| Payments | Razorpay Checkout, Razorpay Java SDK, signed webhook handling |

```text
React/Vite frontend
    | JWT bearer requests
    v
Spring Boot REST API
    | role + ownership authorization
    v
Service layer -> Spring Data JPA -> PostgreSQL/Supabase

Razorpay Checkout callback -----> /payments/verify
Razorpay payment.captured event -> /payments/webhook/razorpay
                                     |
                                     v
                           shared payment finalizer
```

Client profiles and accounts have a many-to-many relationship stored in `user_client`. The invoice links to its client through `client_id` and records its creator through `created_by_user_id`. After account/profile association, invoice access is based on stored IDs rather than email matching.

## API Overview

Authenticated routes require `Authorization: Bearer <JWT>`. Role and ownership checks are applied by Spring Security and service logic.

| Method | Endpoint | Access | Description |
| --- | --- | --- | --- |
| `POST` | `/auth/register` | Public | Register a `USER` or `CLIENT` account |
| `POST` | `/auth/login` | Public | Authenticate and receive a JWT |
| `GET` | `/api/config` | Public | Return the publishable Razorpay Checkout key |
| `GET` | `/user/me` | `USER`, `CLIENT` | Return the authenticated profile |
| `GET` | `/clients/get` | `USER` | List client profiles linked to the freelancer |
| `POST` | `/clients/create` | `USER` | Create a client profile and link accounts |
| `DELETE` | `/clients/delete/{clientId}` | `USER` | Unlink or delete an owned profile |
| `POST` | `/invoices/create` | `USER` | Create an invoice for a linked client |
| `GET` | `/invoices/client/{clientId}` | `USER`, `CLIENT` | List invoices for a client, subject to ownership |
| `GET` | `/invoices/my` | `CLIENT` | List invoices for the authenticated client |
| `POST` | `/payments/create` | `CLIENT` | Create a Razorpay order and pending payment |
| `POST` | `/payments/verify` | `CLIENT` | Verify Checkout signature for the caller's own order |
| `POST` | `/payments/webhook/razorpay` | Public with HMAC validation | Receive Razorpay `payment.captured` events |

## Technology and Repository

```text
backend/   Spring Boot API, security, services, entities, Flyway migrations, tests
frontend/  React/Vite client and freelancer application
prisma/    Prisma schema/configuration; not used by the Spring Boot API
```

## Run Locally

### Requirements

- Java 17
- Node.js and npm compatible with the Vite version in `frontend/package.json`
- PostgreSQL
- Razorpay test credentials to exercise Checkout

### Backend

Configure a local PostgreSQL database and export the required environment variables. Never commit real secrets.

```bash
export JAVA_HOME=/path/to/jdk-17
export PATH="$JAVA_HOME/bin:$PATH"
export DB_URL='jdbc:postgresql://localhost:5432/client_manager'
export DB_USERNAME='postgres'
export DB_PASSWORD='your-local-db-password'
export JWT_SECRET="$(openssl rand -base64 32)"
export RAZORPAY_KEY_ID='your-razorpay-test-key-id'
export RAZORPAY_KEY_SECRET='your-razorpay-test-key-secret'

./mvnw -f backend/pom.xml spring-boot:run
```

The API listens on `http://localhost:8080`. To test webhooks locally, also set `RAZORPAY_WEBHOOK_SECRET` and expose the local endpoint through an HTTPS tunnel.

### Frontend

```bash
cd frontend
npm install
```

Create `frontend/.env`:

```env
VITE_API_BASE_URL=http://localhost:8080
```

Start the development server:

```bash
npm run dev
```

Open `http://localhost:5173`. Vite embeds environment variables at build time, so changes to a deployed API URL require a frontend rebuild and redeploy.

## Environment Variables

| Variable | Required | Purpose |
| --- | --- | --- |
| `DB_URL` | Yes | PostgreSQL JDBC URL |
| `DB_USERNAME` | Yes | PostgreSQL username |
| `DB_PASSWORD` | Yes | PostgreSQL password |
| `JWT_SECRET` | Yes | Private JWT signing secret, at least 32 UTF-8 bytes |
| `RAZORPAY_KEY_ID` | Yes | Razorpay key ID; the public Checkout key |
| `RAZORPAY_KEY_SECRET` | Yes | Private Razorpay API and Checkout-signature secret |
| `RAZORPAY_WEBHOOK_SECRET` | For webhooks | Separate Razorpay webhook-signature secret |
| `VITE_API_BASE_URL` | Frontend | Backend API base URL, embedded at frontend build time |

Changing `JWT_SECRET` invalidates existing tokens. Store secrets in local environment settings or the hosting provider’s secret manager, never in source control or frontend configuration.

## Database and Migrations

Flyway migrations are stored in `backend/src/main/resources/db/migration/`. Hibernate uses `ddl-auto=validate`, so it checks the schema but does not automatically change Supabase tables. Run migrations successfully before expecting the backend to start against a database.

Do not modify a migration already recorded as successful in `flyway_schema_history`; add a new versioned migration instead. The current migration set includes the client/account relationship, invoice creator linkage, enum conversion, and a unique index for Razorpay payment IDs. The payment-ID index migration intentionally stops if duplicate non-null IDs already exist, so resolve those rows before deployment.

## Build and Test

Compile the backend:

```bash
./mvnw -f backend/pom.xml -DskipTests compile
```

Run backend service and security tests:

```bash
./mvnw -f backend/pom.xml -Dtest='*ServiceTest,JwtAuthenticationFilterTest' test
```

Build the frontend:

```bash
cd frontend
npm run build
```

Automated tests do not confirm live database connectivity, deployed CORS configuration, Razorpay credentials, or webhook delivery. Verify those in their target environments.

## Deployment

- Backend: Render, `https://clientmanager-h4tm.onrender.com`
- Frontend: Vercel; set `VITE_API_BASE_URL` to the backend URL before building.
- Configure all backend environment variables in Render.
- Register `https://clientmanager-h4tm.onrender.com/payments/webhook/razorpay` in Razorpay Dashboard for the `payment.captured` event.
- Ensure backend CORS allows the deployed frontend origin.

## Current Product Boundaries

- Payment completion is idempotent, but retries of `POST /payments/create` can still create multiple pending Razorpay orders; request-level idempotency for order creation remains future work.
- Login and registration rate limiting and account abuse controls have not been added.
- Freelancer dashboard invoice and outstanding-value metrics are placeholders; invoice lists and details are available from client views.
- Invoice editing and deletion are not exposed in the current REST controller/UI.
