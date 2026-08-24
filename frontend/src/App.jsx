import { useEffect, useState } from 'react'
import Layout from './components/Layout'
import AuthPage from './pages/AuthPage'
import Dashboard from './pages/Dashboard'
import Clients from './pages/Clients'
import ClientDetail from './pages/ClientDetail'
import Invoices from './pages/Invoices'
import { api, clearToken, isAuthenticated } from './services/api'
import './styles/app.css'

export default function App() {
  const [authenticated, setAuthenticated] = useState(isAuthenticated())
  const [user, setUser] = useState(null)
  const [page, setPage] = useState('dashboard')
  const [selectedClient, setSelectedClient] = useState(null)
  const [invoiceClient, setInvoiceClient] = useState(null)
  const [selectedInvoice, setSelectedInvoice] = useState(null)
  const [clients, setClients] = useState([])
  const [clientInvoices, setClientInvoices] = useState([])
  const [booting, setBooting] = useState(true)
  const isClientRole = user?.role === 'CLIENT' || user?.role === 'ROLE_CLIENT'

  useEffect(() => {
    if (isClientRole && (page === 'clients' || page === 'client-detail')) {
      setSelectedClient(null)
      setInvoiceClient(null)
      setPage('dashboard')
    }
  }, [isClientRole, page])

  useEffect(() => {
    if (!authenticated) {
      setBooting(false)
      return
    }

    api.me()
      .then(setUser)
      .catch(() => {
        clearToken()
        setAuthenticated(false)
      })
      .finally(() => setBooting(false))
  }, [authenticated])

  const loadClients = async () => {
    const data = await api.getClients()
    const nextClients = Array.isArray(data) ? data : []
    setClients(nextClients)
    return nextClients
  }

  const loadClientInvoices = async () => {
    const data = await api.getMyInvoices()
    const nextInvoices = Array.isArray(data) ? data : []
    setClientInvoices(nextInvoices)
    return nextInvoices
  }

  useEffect(() => {
    if (!authenticated || !user) return

    if (isClientRole) {
      loadClientInvoices()
        .then(() => {
          setInvoiceClient({ id: null, name: user.username || 'My invoices' })
          setPage('dashboard')
        })
        .catch(() => {
          setClientInvoices([])
          setPage('dashboard')
        })
      return
    }

    loadClients().catch(() => setClients([]))
  }, [authenticated, user, isClientRole])

  if (booting) return <div className="loading-screen">Loading Client Manager…</div>
  if (!authenticated) return <AuthPage onAuthenticated={() => setAuthenticated(true)} />

  const openClient = (client) => {
    setSelectedClient(client)
    setPage('client-detail')
  }

  const openInvoices = (client) => {
    setInvoiceClient(client)
    setPage('invoices')
  }

  const openMyInvoices = () => {
    if (isClientRole) {
      setInvoiceClient({ id: null, name: user?.username || 'My invoices' })
      setSelectedInvoice(null)
      setPage('invoices')
      return
    }

    setPage('dashboard')
  }

  const handlePaymentSuccess = async (paidInvoice) => {
    setPage('dashboard')
    if (isClientRole) {
      if (paidInvoice) {
        setClientInvoices((current) => current.map((invoice) => (
          invoice.id === paidInvoice.id ? paidInvoice : invoice
        )))
        return
      }

      try {
        await loadClientInvoices()
      } catch {
        setClientInvoices([])
      }
    }
  }

  const logout = () => {
    clearToken()
    setUser(null)
    setSelectedClient(null)
    setInvoiceClient(null)
    setSelectedInvoice(null)
    setAuthenticated(false)
    setPage('dashboard')
  }

  const navigate = (next) => {
    if (isClientRole && next !== 'dashboard' && next !== 'invoices') {
      setSelectedClient(null)
      setInvoiceClient(null)
      setPage('dashboard')
      return
    }

    if (next === 'clients') {
      setSelectedClient(null)
      setInvoiceClient(null)
    }
    if (next === 'dashboard') {
      setSelectedClient(null)
      setInvoiceClient(null)
    }
    setPage(next)
  }

  return (
    <Layout
      user={user}
      active={page === 'client-detail' || page === 'invoices' ? 'clients' : page}
      onNavigate={navigate}
      onLogout={logout}
    >
      {page === 'dashboard' && (
        <Dashboard
          user={user}
          clients={clients}
          invoices={clientInvoices}
          isClient={isClientRole}
          onNavigate={navigate}
          onOpenMyInvoices={openMyInvoices}
          onOpenInvoice={(invoice) => {
            setInvoiceClient({ id: null, name: user?.username || 'My invoices' })
            setSelectedInvoice(invoice)
            setPage('invoices')
          }}
        />
      )}

      {!isClientRole && page === 'clients' && (
        <Clients
          clients={clients}
          onClientsChanged={loadClients}
          onOpenClient={openClient}
          onOpenInvoices={openInvoices}
        />
      )}

      {page === 'client-detail' && selectedClient && (
        <ClientDetail
          client={selectedClient}
          onBack={() => {
            setSelectedClient(null)
            setPage('clients')
          }}
          onInvoices={() => openInvoices(selectedClient)}
          onCreateInvoice={() => openInvoices(selectedClient)}
        />
      )}

      {page === 'invoices' && invoiceClient && (
        <Invoices
          client={invoiceClient}
          isClient={isClientRole}
          initialInvoices={isClientRole ? clientInvoices : undefined}
          initialSelectedInvoice={isClientRole ? selectedInvoice : undefined}
          onBack={() => {
            setInvoiceClient(null)
            if (isClientRole) {
              setPage('dashboard')
              return
            }
            setPage('client-detail')
            setSelectedClient(invoiceClient)
          }}
          onPaymentSuccess={handlePaymentSuccess}
        />
      )}
    </Layout>
  )
}
