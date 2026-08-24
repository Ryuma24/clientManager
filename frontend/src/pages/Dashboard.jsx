export default function Dashboard({ user, clients, invoices = [], onNavigate, isClient = false, onOpenMyInvoices, onOpenInvoice }) {
  if (isClient) {
    const unpaidInvoices = invoices.filter((invoice) => {
      const total = Number(invoice.totalAmount ?? invoice.subTotal ?? 0)
      const paid = Number(invoice.amountPaid ?? 0)
      return paid < total
    })

    const paidInvoices = invoices.filter((invoice) => {
      const total = Number(invoice.totalAmount ?? invoice.subTotal ?? 0)
      const paid = Number(invoice.amountPaid ?? 0)
      return paid >= total
    })

    const formatMoney = (value) => `₹${Number(value || 0).toFixed(2)}`

    return (
      <>
        <header className="page-header">
          <div>
            <div className="eyebrow">CLIENT PORTAL</div>
            <h1>My payments</h1>
            <p>Welcome back, {user?.username || 'there'}.</p>
          </div>
          <button className="primary-button" onClick={onOpenMyInvoices}>View my invoices</button>
        </header>

        <section className="invoice-overview-grid">
          <div className="category-box category-pending">
            <div className="category-header">
              <span>Pending / partial</span>
              <strong>{unpaidInvoices.length}</strong>
            </div>
            {unpaidInvoices.length === 0 ? (
              <p className="category-empty">No unpaid invoices.</p>
            ) : (
              <ul className="invoice-mini-list">
                {unpaidInvoices.map((invoice) => (
                  <li
                    key={invoice.id}
                    className="invoice-clickable"
                    role="button"
                    tabIndex="0"
                    onClick={() => onOpenInvoice(invoice)}
                    onKeyDown={(event) => {
                      if (event.key === 'Enter' || event.key === ' ') onOpenInvoice(invoice)
                    }}
                  >
                    <div>
                      <button type="button" className="link-button" onClick={(event) => {
                        event.stopPropagation()
                        onOpenInvoice(invoice)
                      }}>
                        #{invoice.id} View details
                      </button>
                      <small>{invoice.status || 'PENDING'}</small>
                    </div>
                    <span>{formatMoney(invoice.totalAmount ?? invoice.subTotal ?? 0)}</span>
                  </li>
                ))}
              </ul>
            )}
          </div>

          <div className="category-box category-paid">
            <div className="category-header">
              <span>Paid</span>
              <strong>{paidInvoices.length}</strong>
            </div>
            {paidInvoices.length === 0 ? (
              <p className="category-empty">No paid invoices yet.</p>
            ) : (
              <ul className="invoice-mini-list">
                {paidInvoices.map((invoice) => (
                  <li
                    key={invoice.id}
                    className="invoice-clickable"
                    role="button"
                    tabIndex="0"
                    onClick={() => onOpenInvoice(invoice)}
                    onKeyDown={(event) => {
                      if (event.key === 'Enter' || event.key === ' ') onOpenInvoice(invoice)
                    }}
                  >
                    <div>
                      <button type="button" className="link-button" onClick={(event) => {
                        event.stopPropagation()
                        onOpenInvoice(invoice)
                      }}>
                        #{invoice.id} View details
                      </button>
                      <small>{invoice.status || 'PAID'}</small>
                    </div>
                    <span>{formatMoney(invoice.totalAmount ?? invoice.subTotal ?? 0)}</span>
                  </li>
                ))}
              </ul>
            )}
          </div>
        </section>
      </>
    )
  }

  return (
    <>
      <header className="page-header">
        <div>
          <div className="eyebrow">OVERVIEW</div>
          <h1>Dashboard</h1>
          <p>Welcome back, {user?.username || 'there'}.</p>
        </div>
        <button className="primary-button" onClick={() => onNavigate('clients')}>Manage clients</button>
      </header>

      <section className="metric-grid">
        <article className="metric-card"><span>Total clients</span><strong>{clients.length}</strong><small>Your current client list</small></article>
        <article className="metric-card"><span>Invoices</span><strong>—</strong><small>Connect invoice endpoints next</small></article>
        <article className="metric-card"><span>Outstanding</span><strong>—</strong><small>Derived from invoice balances</small></article>
      </section>

      <section className="panel">
        <div className="panel-header">
          <div><h2>Getting started</h2><p>Authentication is live. Your next workflows are ready for the UI.</p></div>
        </div>
        <div className="workflow-grid">
          <div><span className="step">01</span><strong>Clients</strong><p>Create and manage your customers.</p></div>
          <div><span className="step">02</span><strong>Invoices</strong><p>Create invoices for each client.</p></div>
          <div><span className="step">03</span><strong>Payments</strong><p>Connect your payment flow when the backend is ready.</p></div>
        </div>
      </section>
    </>
  )
}
