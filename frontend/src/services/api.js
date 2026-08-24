const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

function getToken() {
  return localStorage.getItem('token')
}

async function request(path, options = {}) {
  const token = getToken()
  const headers = new Headers(options.headers || {})

  if (options.body && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json')
  }

  if (token) {
    headers.set('Authorization', `Bearer ${token}`)
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers,
  })

  const contentType = response.headers.get('content-type') || ''
  const data = contentType.includes('application/json')
    ? await response.json()
    : await response.text()

  if (!response.ok) {
    const message = typeof data === 'string' ? data : data?.message
    throw new Error(message || `Request failed with ${response.status}`)
  }

  return data
}

export const api = {
  register: (payload) => request('/auth/register', {
    method: 'POST',
    body: JSON.stringify(payload),
  }),

  login: (payload) => request('/auth/login', {
    method: 'POST',
    body: JSON.stringify(payload),
  }),

  me: () => request('/user/me'),

  getClients: () => request('/clients/get'),

  createClient: (payload) => request('/clients/create', {
    method: 'POST',
    body: JSON.stringify(payload),
  }),

  deleteClient: (id) => request(`/clients/delete/${id}`, {
    method: 'DELETE',
  }),

  // These two paths assume your InvoiceController exposes the service through:
  // GET  /invoices/client/{clientId}
  // GET  /invoices/my
  // POST /invoices/create
  getInvoices: (clientId) => request(`/invoices/client/${clientId}`),
  getMyInvoices: () => request('/invoices/my'),

  createInvoice: (payload) => request('/invoices/create', {
    method: 'POST',
    body: JSON.stringify(payload),
  }),

  createPayment: (payload) => request('/payments/create', {
    method: 'POST',
    body: JSON.stringify(payload),
  }),

  verifyPayment: (payload) => request('/payments/verify', {
    method: 'POST',
    body: JSON.stringify(payload),
  }),

  getRazorpayConfig: () => request('/api/config'),
}

export function saveToken(token) {
  localStorage.setItem('token', token)
}

export function clearToken() {
  localStorage.removeItem('token')
}

export function isAuthenticated() {
  return Boolean(getToken())
}
