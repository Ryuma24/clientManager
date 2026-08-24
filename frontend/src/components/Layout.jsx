import { clearToken } from '../services/api'

export default function Layout({ user, onLogout, children, active, onNavigate }) {
  const navigate = (page) => onNavigate(page)
  const isClientRole = user?.role === 'CLIENT' || user?.role === 'ROLE_CLIENT'

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">
          <div className="brand-mark">CM</div>
          <div>
            <strong>Client Manager</strong>
            <span>Invoice workspace</span>
          </div>
        </div>

        <nav className="nav">
          <button className={active === 'dashboard' ? 'nav-item active' : 'nav-item'} onClick={() => navigate('dashboard')}>Dashboard</button>
          {!isClientRole && (
            <button className={active === 'clients' ? 'nav-item active' : 'nav-item'} onClick={() => navigate('clients')}>Clients</button>
          )}
        </nav>

        <div className="sidebar-footer">
          <div className="user-mini">
            <div className="avatar">{(user?.username || 'U').slice(0, 1).toUpperCase()}</div>
            <div>
              <strong>{user?.username || 'User'}</strong>
              <span>{user?.email || ''}</span>
            </div>
          </div>
          <button className="ghost-button" onClick={() => { clearToken(); onLogout() }}>Sign out</button>
        </div>
      </aside>
      <main className="main-content">{children}</main>
    </div>
  )
}
