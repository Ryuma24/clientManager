# Client Manager Frontend

React + Vite frontend for the Spring Boot Client Manager backend.

## Run

```bash
cp .env.example .env
npm install
npm run dev
```

Set:

```env
VITE_API_BASE_URL=http://localhost:8080
```

## Current flow

- Register / login
- JWT stored in localStorage and sent as `Authorization: Bearer <token>`
- `/user/me` bootstrap
- Client list
- Create and delete clients
- Client detail
- View invoices for a selected client
- Raise an invoice for the selected client
- Add/remove invoice line items
- Automatic invoice total calculation
- Refresh invoice list after creation

## Backend routes used by this version

- `POST /auth/register`
- `POST /auth/login`
- `GET /user/me`
- `GET /clients/get`
- `POST /clients/create`
- `DELETE /clients/delete/{clientId}`
- `GET /invoices/client/{clientId}`
- `POST /invoices/create`

The invoice routes are based on the frontend contract currently used by the app. If your `InvoiceController` maps them differently, change only `src/services/api.js`.

## Navigation

This app intentionally uses the existing simple state-based navigation in `App.jsx`; it does not use React Router.

## CORS

For local development the backend should allow:

- `http://localhost:5173`
- `http://127.0.0.1:5173`

## Notes

The payment UI is intentionally not included yet. The Razorpay backend flow should be stable before wiring Checkout and webhook status into the UI.
