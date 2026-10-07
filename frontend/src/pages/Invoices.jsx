import { useEffect, useMemo, useState } from 'react'
import { api } from '../services/api'

function money(value) {
  return `₹${Number(value || 0).toFixed(2)}`
}

const today = () => new Date().toISOString().slice(0, 10)
const plusDays = (days) => {
  const date = new Date()
  date.setDate(date.getDate() + days)
  return date.toISOString().slice(0, 10)
}

const buildEmptyItem = () => ({ itemName: '', quantity: 1, unitPrice: '' })
const RAZORPAY_SCRIPT_SRC = 'https://checkout.razorpay.com/v1/checkout.js'

const initialInvoiceForm = () => ({
  invoiceNumber: '',
  amount: '',
  issueDate: today(),
  dueDate: plusDays(14),
  status: 'DRAFT',
})

export default function Invoices({ client, isClient = false, initialInvoices, initialSelectedInvoice, onBack, onPaymentSuccess }) {
  const [invoices, setInvoices] = useState(initialInvoices || [])
  const [loading, setLoading] = useState(!initialInvoices)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [showCreate, setShowCreate] = useState(!isClient)
  const [invoiceForm, setInvoiceForm] = useState(initialInvoiceForm())
  const [invoiceItems, setInvoiceItems] = useState([buildEmptyItem()])
  const [saving, setSaving] = useState(false)
  const [selectedInvoice, setSelectedInvoice] = useState(initialSelectedInvoice || null)
  const [isPaying, setIsPaying] = useState(false)

  const loadRazorpayScript = () => new Promise((resolve, reject) => {
    if (window.Razorpay) {
      resolve()
      return
    }

    const existingScript = document.querySelector(`script[src="${RAZORPAY_SCRIPT_SRC}"]`)
    if (existingScript) {
      existingScript.addEventListener('load', () => resolve(), { once: true })
      existingScript.addEventListener('error', () => reject(new Error('Razorpay script failed to load.')), { once: true })
      return
    }

    const script = document.createElement('script')
    script.src = RAZORPAY_SCRIPT_SRC
    script.async = true
    script.onload = () => resolve()
    script.onerror = () => reject(new Error('Razorpay script failed to load.'))
    document.body.appendChild(script)
  })

  const loadInvoices = async () => {
    setLoading(true)
    setError('')
    try {
      const data = client?.id
        ? await api.getInvoices(client.id)
        : await api.getMyInvoices()

      setInvoices(Array.isArray(data) ? data : [])
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    if (initialInvoices) {
      setInvoices(initialInvoices)
      setLoading(false)
      return
    }
    loadInvoices()
  }, [client?.id, initialInvoices])

  const totalAmount = useMemo(() => {
    return invoiceItems.reduce((sum, item) => {
      const quantity = Number(item.quantity || 0)
      const unitPrice = Number(item.unitPrice || 0)
      return sum + (quantity * unitPrice)
    }, 0)
  }, [invoiceItems])

  const updateItem = (index, field, value) => {
    setInvoiceItems((current) => current.map((item, itemIndex) => {
      if (itemIndex !== index) return item
      return { ...item, [field]: value }
    }))
  }

  const addItem = () => {
    setInvoiceItems((current) => [...current, buildEmptyItem()])
  }

  const removeItem = (index) => {
    setInvoiceItems((current) => {
      if (current.length === 1) return [buildEmptyItem()]
      return current.filter((_, itemIndex) => itemIndex !== index)
    })
  }

  const resetForm = () => {
    setInvoiceForm(initialInvoiceForm())
    setInvoiceItems([buildEmptyItem()])
    setShowCreate(false)
  }

  const submit = async (event) => {
    event.preventDefault()
    setError('')

    if (!invoiceForm.invoiceNumber.trim()) {
      setError('Invoice number is required.')
      return
    }
    if (!invoiceItems.length || invoiceItems.some((item) => !item.itemName.trim() || Number(item.unitPrice || 0) <= 0 || Number(item.quantity || 0) <= 0)) {
      setError('Add at least one valid invoice item with a name, quantity, and fixed price.')
      return
    }
    if (!invoiceForm.issueDate || !invoiceForm.dueDate) {
      setError('Issue date and due date are required.')
      return
    }

    setSaving(true)
    try {
      const payload = {
        clientId: client.id,
        client: { id: client.id },
        invoiceNumber: invoiceForm.invoiceNumber.trim(),
        subTotal: totalAmount,
        issueDate: invoiceForm.issueDate,
        dueDate: invoiceForm.dueDate,
        status: invoiceForm.status || 'DRAFT',
        invoiceItemList: invoiceItems.map((item) => ({
          itemName: item.itemName.trim(),
          quantity: Number(item.quantity || 1),
          unitPrice: Number(item.unitPrice || 0),
          amount: Number((Number(item.unitPrice || 0) * Number(item.quantity || 1)).toFixed(2)),
        })),
      }

      await api.createInvoice(payload)
      resetForm()
      await loadInvoices()
    } catch (err) {
      setError(err.message)
    } finally {
      setSaving(false)
    }
  }

  const handlePayInvoice = async (invoice) => {
    if (!isClient) {
      return
    }

    const total = Number(invoice.totalAmount || invoice.subTotal || 0)
    const paid = Number(invoice.amountPaid || 0)
    const balance = Number(invoice.balanceAmount ?? (total - paid))

    if (!(balance > 0)) {
      return
    }

    setError('')
    setSuccess('')
    setIsPaying(true)

    try {
      await loadRazorpayScript()

      const razorpayConfig = await api.getRazorpayConfig()
      const createdOrder = await api.createPayment({
        invoiceId: invoice.id,
        amount: Number(balance.toFixed(2)),
      })

      const razorpay = new window.Razorpay({
        key: razorpayConfig.key,
        amount: Math.round(Number(createdOrder.amount || balance) * 100),
        currency: 'INR',
        name: 'Client Manager',
        description: `Invoice #${invoice.id}`,
        order_id: createdOrder.razorpayOrderId || createdOrder.id,
        handler: async (response) => {
          try {
            await api.verifyPayment({
              razorpayOrderId: response.razorpay_order_id,
              razorpayPaymentId: response.razorpay_payment_id,
              razorpaySignature: response.razorpay_signature,
            })

            await loadInvoices()
            setSelectedInvoice(null)
            setSuccess(`Payment successful for invoice #${invoice.id}.`)
            onPaymentSuccess?.({
              ...invoice,
              amountPaid: total,
              balanceAmount: 0,
              amountStatus: 'FULL',
              status: 'PAID',
            })
          } catch (verifyError) {
            setError(verifyError.message || 'Payment verification failed.')
          } finally {
            setIsPaying(false)
          }
        },
        modal: {
          ondismiss: () => setIsPaying(false),
        },
        theme: {
          color: '#2563eb',
        },
      })

      razorpay.open()
    } catch (err) {
      setError(err.message || 'Unable to start Razorpay checkout.')
      setIsPaying(false)
    }
  }

  return (
    <>
      <button className="back-button" onClick={onBack}>← {client.name}</button>

      <header className="page-header compact">
        <div>
          <div className="eyebrow">INVOICES</div>
          <h1>{client.name}</h1>
          <p>Raise invoices and track what has been paid.</p>
        </div>
        {!isClient && (
          <button className="secondary-button" onClick={() => setShowCreate((value) => !value)}>
            {showCreate ? 'Close form' : '+ Raise invoice'}
          </button>
        )}
      </header>

      {error && <div className="alert error">{error}</div>}
      {success && <div className="alert success">{success}</div>}

      {showCreate && !isClient && (
        <section className="panel form-panel">
          <div className="panel-header">
            <div>
              <h2>Raise invoice</h2>
              <p>Client: {client.name}</p>
            </div>
          </div>

          <form onSubmit={submit}>
            <div className="form-grid">
              <label className="span-2">
                <span>Invoice number</span>
                <input
                  id="invoice-number"
                  name="invoiceNumber"
                  value={invoiceForm.invoiceNumber}
                  onChange={(e) => setInvoiceForm((current) => ({ ...current, invoiceNumber: e.target.value }))}
                  placeholder="INV-1001"
                  required
                />
              </label>

              <label>
                <span>Status</span>
                <select
                  id="invoice-status"
                  name="status"
                  value={invoiceForm.status}
                  onChange={(e) => setInvoiceForm((current) => ({ ...current, status: e.target.value }))}
                >
                  <option value="DRAFT">DRAFT</option>
                  <option value="SENT">SENT</option>
                  <option value="PENDING">PENDING</option>
                  <option value="PAID">PAID</option>
                  <option value="OVERDUE">OVERDUE</option>
                </select>
              </label>

              <label>
                <span>Issue date</span>
                <input
                  id="invoice-issue-date"
                  name="issueDate"
                  type="date"
                  value={invoiceForm.issueDate}
                  onChange={(e) => setInvoiceForm((current) => ({ ...current, issueDate: e.target.value }))}
                  required
                />
              </label>

              <label>
                <span>Due date</span>
                <input
                  id="invoice-due-date"
                  name="dueDate"
                  type="date"
                  value={invoiceForm.dueDate}
                  onChange={(e) => setInvoiceForm((current) => ({ ...current, dueDate: e.target.value }))}
                  required
                />
              </label>
            </div>

            <div className="invoice-items-block">
              <div className="panel-subheader">
                <h3>Invoice items</h3>
                <button type="button" className="secondary-button small" onClick={addItem}>+ Add item</button>
              </div>

              <div className="invoice-items">
                {invoiceItems.map((item, index) => (
                  <div className="invoice-item-row" key={index}>
                    <label className="item-description">
                      <span>Item name</span>
                      <input
                        id={`invoice-item-name-${index}`}
                        name={`invoiceItemName-${index}`}
                        value={item.itemName}
                        onChange={(e) => updateItem(index, 'itemName', e.target.value)}
                        placeholder="Website design"
                      />
                    </label>

                    <label>
                      <span>Qty</span>
                      <input
                        id={`invoice-item-qty-${index}`}
                        name={`invoiceItemQty-${index}`}
                        type="number"
                        min="1"
                        step="1"
                        value={item.quantity}
                        onChange={(e) => updateItem(index, 'quantity', e.target.value)}
                      />
                    </label>

                    <label>
                      <span>Unit price</span>
                      <input
                        id={`invoice-item-price-${index}`}
                        name={`invoiceItemPrice-${index}`}
                        type="number"
                        min="0.01"
                        step="0.01"
                        value={item.unitPrice}
                        onChange={(e) => updateItem(index, 'unitPrice', e.target.value)}
                        placeholder="0.00"
                      />
                    </label>

                    <div className="item-total">
                      <span>Total</span>
                      <strong>{money(Number(item.quantity || 0) * Number(item.unitPrice || 0))}</strong>
                    </div>

                    <div className="item-remove">
                      <button type="button" className="secondary-button small" onClick={() => removeItem(index)}>Remove</button>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            <div className="invoice-form-footer">
              <div className="invoice-total"><span>Invoice total</span><strong>{money(totalAmount)}</strong></div>
              <button className="primary-button" disabled={saving || totalAmount <= 0}>
                {saving ? 'Creating…' : 'Create invoice'}
              </button>
            </div>
          </form>
        </section>
      )}

      <section className="panel">
        <div className="panel-header">
          <div className="panel-title-row"><h2>Invoice list</h2><span className="count-pill">{invoices.length}</span></div>
          <button className="secondary-button" onClick={loadInvoices} disabled={loading}>Refresh</button>
        </div>

        {loading ? (
          <div className="empty-state">Loading invoices…</div>
        ) : invoices.length === 0 ? (
          <div className="empty-state">
            <strong>No invoices yet.</strong>
            <span>{isClient ? 'Invoices assigned to you will appear here.' : `Create an invoice above for ${client.name}.`}</span>
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
                {invoices.map((invoice) => {
                  const total = Number(invoice.totalAmount || 0)
                  const paid = Number(invoice.amountPaid || 0)
                  const balance = invoice.balanceAmount ?? (total - paid)
                  const status = invoice.amountStatus || invoice.status || 'PENDING'

                  return (
                    <tr
                      key={invoice.id}
                      className="invoice-clickable"
                      role="button"
                      tabIndex="0"
                      onClick={() => setSelectedInvoice(invoice)}
                      onKeyDown={(event) => {
                        if (event.key === 'Enter' || event.key === ' ') setSelectedInvoice(invoice)
                      }}
                    >
                      <td>
                        <button type="button" className="link-button" onClick={(event) => {
                          event.stopPropagation()
                          setSelectedInvoice(invoice)
                        }}>
                          #{invoice.id} View details
                        </button>
                      </td>
                      <td>{money(total)}</td>
                      <td>{money(paid)}</td>
                      <td>{money(balance)}</td>
                      <td><span className="status-pill">{status}</span></td>
                    </tr>
                  )
                })}
              </tbody>
            </table>
          </div>
        )}
      </section>

      {selectedInvoice && (
        <section className="panel invoice-detail-panel">
          <div className="panel-header">
            <div>
              <div className="eyebrow">INVOICE DETAILS</div>
              <h2>Invoice #{selectedInvoice.id}</h2>
              <p>Review the invoice items and totals before making a payment.</p>
            </div>
            <button type="button" className="secondary-button" onClick={() => setSelectedInvoice(null)}>
              Close details
            </button>
          </div>

          <div className="invoice-detail-meta">
            <span><strong>Invoice number</strong>{selectedInvoice.invoiceNumber}</span>
            <span><strong>Issue date</strong>{selectedInvoice.issueDate}</span>
            <span><strong>Due date</strong>{selectedInvoice.dueDate}</span>
            <span><strong>Status</strong>{selectedInvoice.amountStatus || selectedInvoice.status || 'PENDING'}</span>
          </div>

          <div className="table-wrap">
            <table>
              <thead>
                <tr><th>Item</th><th>Qty</th><th>Unit price</th><th>Amount</th></tr>
              </thead>
              <tbody>
                {(selectedInvoice.invoiceItemList || []).map((item, index) => (
                  <tr key={`${selectedInvoice.id}-item-${index}`}>
                    <td>{item.itemName}</td>
                    <td>{item.quantity}</td>
                    <td>{money(item.unitPrice)}</td>
                    <td>{money(item.amount)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          <div className="invoice-detail-footer">
            <strong>Total: {money(selectedInvoice.totalAmount ?? selectedInvoice.subTotal)}</strong>
            {isClient && Number(selectedInvoice.balanceAmount ?? (Number(selectedInvoice.totalAmount || selectedInvoice.subTotal || 0) - Number(selectedInvoice.amountPaid || 0))) > 0 && (
              <button
                type="button"
                className="primary-button"
                onClick={() => handlePayInvoice(selectedInvoice)}
                disabled={isPaying}
              >
                {isPaying ? 'Processing…' : 'Pay now'}
              </button>
            )}
          </div>
        </section>
      )}
    </>
  )
}
