import { useEffect, useState } from 'react'
import { api } from '../services/api'

export default function ClientDetail({ client, onBack, onInvoices, onCreateInvoice }) {
  const [invoices, setInvoices] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    let active = true
    setLoading(true)
    setError('')

    api.getInvoices(client.id)
      .then((data) => active && setInvoices(Array.isArray(data) ? data : []))
      .catch((err) => active && setError(err.message))
      .finally(() => active && setLoading(false))

    return () => { active = false }
  }, [client.id])

  return (
    <>
      <button className="back-button" onClick={onBack}>← Clients</button>

      <header className="page-header compact">
        <div>
          <div className="eyebrow">CLIENT</div>
          <h1>{client.name}</h1>
          <p>{client.email || 'No email'} {client.phone ? `· ${client.phone}` : ''}</p>
        </div>
        <div className="header-actions">
          <button className="secondary-button" onClick={onInvoices}>View invoices</button>
          <button className="primary-button" onClick={onCreateInvoice}>Raise invoice</button>
        </div>
      </header>

      {error && <div className="alert error">{error}</div>}

      <section className="panel">
        <div className="panel-header">
          <div className="panel-title-row">
            <h2>Invoices</h2>
            <span className="count-pill">{invoices.length}</span>
          </div>
          <button className="secondary-button" onClick={onInvoices}>Open all</button>
        </div>

        {loading ? (
          <div className="empty-state">Loading invoices…</div>
        ) : invoices.length === 0 ? (
          <div className="empty-state">
            <strong>No invoices yet.</strong>
            <span>Raise the first invoice for {client.name}.</span>
          </div>
        ) : (
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Invoice</th>
                  <th>Total</th>
                  <th>Paid</th>
                  <th>Balance</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {invoices.slice(0, 5).map((invoice) => {
                  const total = Number(invoice.totalAmount ?? 0)
                  const paid = Number(invoice.amountPaid ?? 0)
                  const balance = invoice.balanceAmount ?? (total - paid)
                  const status = invoice.amountStatus || invoice.status || 'PENDING'

                  return (
                    <tr key={invoice.id}>
                      <td>#{invoice.id}</td>
                      <td>{total.toFixed(2)}</td>
                      <td>{paid.toFixed(2)}</td>
                      <td>{Number(balance).toFixed(2)}</td>
                      <td><span className="status-pill">{status}</span></td>
                    </tr>
                  )
                })}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </>
  )
}
