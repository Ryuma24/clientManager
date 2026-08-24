import { useEffect, useState } from 'react'
import Modal from '../components/Modal'
import { api } from '../services/api'

export default function Clients({ clients: externalClients, onClientsChanged, onOpenClient, onOpenInvoices }) {
  const [clients, setClients] = useState(externalClients || [])
  const [loading, setLoading] = useState(!externalClients)
  const [error, setError] = useState('')
  const [showCreate, setShowCreate] = useState(false)
  const [form, setForm] = useState({ name: '', email: '', phone: '' })
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    if (Array.isArray(externalClients)) {
      setClients(externalClients)
      setLoading(false)
    }
  }, [externalClients])

  const load = async () => {
    setLoading(true)
    setError('')
    try {
      const data = await api.getClients()
      setClients(Array.isArray(data) ? data : [])
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const createClient = async (event) => {
    event.preventDefault()
    setSaving(true)
    setError('')
    try {
      await api.createClient(form)
      setForm({ name: '', email: '', phone: '' })
      setShowCreate(false)
      await load()
      await onClientsChanged?.()
    } catch (err) {
      setError(err.message)
    } finally {
      setSaving(false)
    }
  }

  const deleteClient = async (id) => {
    if (!window.confirm('Delete this client?')) return
    setError('')
    try {
      await api.deleteClient(id)
      await load()
      await onClientsChanged?.()
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <>
      <header className="page-header">
        <div>
          <div className="eyebrow">WORKSPACE</div>
          <h1>Clients</h1>
          <p>Manage customers, invoices, and payments from one place.</p>
        </div>
        <button className="primary-button" onClick={() => setShowCreate(true)}>+ Add client</button>
      </header>

      {error && <div className="alert error">{error}</div>}

      <section className="panel">
        <div className="panel-header">
          <div className="panel-title-row"><h2>All clients</h2><span className="count-pill">{clients.length}</span></div>
          <button className="secondary-button" onClick={load} disabled={loading}>Refresh</button>
        </div>

        {loading ? (
          <div className="empty-state">Loading clients…</div>
        ) : clients.length === 0 ? (
          <div className="empty-state">
            <strong>No clients yet.</strong>
            <span>Add your first client to start raising invoices.</span>
          </div>
        ) : (
          <div className="table-wrap">
            <table>
              <thead>
                <tr><th>Name</th><th>Email</th><th>Phone</th><th>Actions</th></tr>
              </thead>
              <tbody>
                {clients.map((client) => (
                  <tr key={client.id}>
                    <td><button className="link-button" onClick={() => onOpenClient(client)}>{client.name}</button></td>
                    <td>{client.email || '—'}</td>
                    <td>{client.phone || '—'}</td>
                    <td>
                      <div className="row-actions">
                        <button className="secondary-button small" onClick={() => onOpenInvoices(client)}>Invoices</button>
                        <button className="danger-link" onClick={() => deleteClient(client.id)}>Delete</button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>

      {showCreate && (
        <Modal title="Add client" onClose={() => !saving && setShowCreate(false)}>
          <form className="form-stack" onSubmit={createClient}>
            <label>
              Name
              <input
                id="client-name"
                name="name"
                value={form.name}
                onChange={(e) => setForm({ ...form, name: e.target.value })}
                required
              />
            </label>
            <label>
              Email
              <input
                id="client-email"
                name="email"
                type="email"
                value={form.email}
                onChange={(e) => setForm({ ...form, email: e.target.value })}
              />
            </label>
            <label>
              Phone
              <input
                id="client-phone"
                name="phone"
                value={form.phone}
                onChange={(e) => setForm({ ...form, phone: e.target.value })}
              />
            </label>
            <button className="primary-button" disabled={saving}>
              {saving ? 'Creating…' : 'Create client'}
            </button>
          </form>
        </Modal>
      )}
    </>
  )
}
