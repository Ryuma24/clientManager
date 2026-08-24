import { useState } from 'react'
import { api, saveToken } from '../services/api'

export default function AuthPage({ initialMode = 'login', onAuthenticated }) {
  const [mode, setMode] = useState(initialMode)
  const [form, setForm] = useState({ username: '', email: '', password: '', role: 'USER' })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const submit = async (event) => {
    event.preventDefault()
    setError('')
    setLoading(true)

    const payload = mode === 'login'
      ? { username: form.username, password: form.password, role: form.role }
      : { ...form, role: form.role }

    try {
      const result = mode === 'login'
        ? await api.login(payload)
        : await api.register(payload)

      const token = result?.token || result?.accessToken || result?.jwt
      if (!token) throw new Error('No token was returned by the authentication API.')

      saveToken(token)
      onAuthenticated()
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-page">
      <div className="auth-card">
        <div className="eyebrow">CLIENT MANAGER</div>
        <h1>{mode === 'login' ? 'Welcome back' : 'Create your account'}</h1>
        <p className="muted">Manage clients, invoices and payments from one place.</p>

        <div className="segmented">
          <button className={mode === 'login' ? 'selected' : ''} onClick={() => setMode('login')}>Login</button>
          <button className={mode === 'register' ? 'selected' : ''} onClick={() => setMode('register')}>Register</button>
        </div>

        {error && <div className="alert error">{error}</div>}

        <form onSubmit={submit} className="form-stack">
          <label>
            Username
            <input
              id="auth-username"
              name="username"
              value={form.username}
              onChange={(e) => setForm({ ...form, username: e.target.value })}
              required
            />
          </label>

          {mode === 'register' && (
            <label>
              Email
              <input
                id="auth-email"
                name="email"
                type="email"
                value={form.email}
                onChange={(e) => setForm({ ...form, email: e.target.value })}
                required
              />
            </label>
          )}

          <label>
            Password
            <input
              id="auth-password"
              name="password"
              type="password"
              value={form.password}
              onChange={(e) => setForm({ ...form, password: e.target.value })}
              required
            />
          </label>

          <fieldset className="role-selector">
            <legend>{mode === 'register' ? 'Register as' : 'Login as'}</legend>
            <label>
              <input
                id="role-user"
                name="role"
                type="radio"
                value="USER"
                checked={form.role === 'USER'}
                onChange={(e) => setForm({ ...form, role: e.target.value })}
              />
              User
            </label>
            <label>
              <input
                id="role-client"
                name="role"
                type="radio"
                value="CLIENT"
                checked={form.role === 'CLIENT'}
                onChange={(e) => setForm({ ...form, role: e.target.value })}
              />
              Client
            </label>
          </fieldset>

          <button className="primary-button" disabled={loading}>
            {loading ? 'Please wait…' : mode === 'login' ? 'Sign in' : 'Create account'}
          </button>
        </form>
      </div>
    </div>
  )
}
