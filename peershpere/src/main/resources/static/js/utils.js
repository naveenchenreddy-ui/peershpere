// js/utils.js

/**
 * Check if user is logged in.
 * If not, redirect to login page.
 * Call this at the top of every protected page.
 */
function requireAuth() {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'login.html';
    }
}

/**
 * Get the stored user object from localStorage.
 */
function getCurrentUser() {
    return {
        token:    localStorage.getItem('token'),
        email:    localStorage.getItem('email'),
        fullName: localStorage.getItem('fullName'),
        role:     localStorage.getItem('role')
    };
}

/**
 * Save auth data after login/register.
 */
function saveAuth(data) {
    localStorage.setItem('token',    data.token);
    localStorage.setItem('email',    data.email);
    localStorage.setItem('fullName', data.fullName);
    localStorage.setItem('role',     data.role);
}

/**
 * Clear auth and redirect to login.
 */
function logout() {
    localStorage.clear();
    window.location.href = 'login.html';
}

/**
 * Get initials from a full name.
 * "Rahul Sharma" → "RS"
 */
function getInitials(name) {
    if (!name) return '?';
    return name.split(' ')
        .map(n => n[0])
        .join('')
        .toUpperCase()
        .slice(0, 2);
}

/**
 * Format a date string for display.
 */
function formatDate(dateStr) {
    if (!dateStr) return '';
    return new Date(dateStr).toLocaleDateString('en-IN', {
        day: 'numeric', month: 'short', year: 'numeric'
    });
}

function formatDateTime(dateStr) {
    if (!dateStr) return '';
    return new Date(dateStr).toLocaleString('en-IN', {
        day: 'numeric', month: 'short',
        hour: '2-digit', minute: '2-digit'
    });
}

/**
 * Time ago — "2 hours ago", "3 days ago"
 */
function timeAgo(dateStr) {
    const date = new Date(dateStr);
    const now  = new Date();
    const diff = Math.floor((now - date) / 1000);

    if (diff < 60)     return 'just now';
    if (diff < 3600)   return `${Math.floor(diff/60)}m ago`;
    if (diff < 86400)  return `${Math.floor(diff/3600)}h ago`;
    if (diff < 604800) return `${Math.floor(diff/86400)}d ago`;
    return formatDate(dateStr);
}

/**
 * Show an alert message inside a container.
 */
function showAlert(containerId, message, type = 'error') {
    const container = document.getElementById(containerId);
    if (!container) return;
    container.innerHTML = `
        <div class="alert alert-${type}">
            ${type === 'error' ? '⚠️' : '✅'} ${message}
        </div>`;
    setTimeout(() => container.innerHTML = '', 4000);
}

/**
 * Show loading spinner inside a container.
 */
function showLoading(containerId) {
    const el = document.getElementById(containerId);
    if (el) el.innerHTML = `
        <div class="loading-overlay">
            <div class="spinner"></div>
        </div>`;
}

/**
 * Render comma-separated tags as tag pills.
 */
function renderTags(tagsStr) {
    if (!tagsStr) return '';
    return tagsStr.split(',')
        .map(t => t.trim())
        .filter(Boolean)
        .map(t => `<span class="tag">${t}</span>`)
        .join('');
}

/**
 * Populate the sidebar user info on every protected page.
 */
function populateSidebar() {
    const user = getCurrentUser();
    const nameEl   = document.getElementById('sidebar-name');
    const emailEl  = document.getElementById('sidebar-email');
    const avatarEl = document.getElementById('sidebar-avatar');
    if (nameEl)   nameEl.textContent   = user.fullName || 'User';
    if (emailEl)  emailEl.textContent  = user.email || '';
    if (avatarEl) avatarEl.textContent = getInitials(user.fullName);
}

/**
 * Poll notification count every 30 seconds.
 * Updates the badge in the sidebar/topbar.
 */
function startNotificationPolling() {
    async function updateBadge() {
        try {
            const data = await NotifAPI.getCount();
            const badge = document.getElementById('notif-badge');
            if (badge) {
                badge.textContent = data.unreadCount;
                badge.style.display = data.unreadCount > 0 ? 'flex' : 'none';
            }
        } catch(e) { /* silent fail */ }
    }
    updateBadge();
    setInterval(updateBadge, 30000);
}

/**
 * Open a modal by ID.
 */
function openModal(id) {
    document.getElementById(id)?.classList.add('open');
}

/**
 * Close a modal by ID.
 */
function closeModal(id) {
    document.getElementById(id)?.classList.remove('open');
}
/**
 * Renders the sidebar into any element with id="sidebar-container".
 * Call this on every protected page.
 */
function renderSidebar(activePage) {
    const nav = [
        { href: 'dashboard.html',      icon: '🏠', label: 'Dashboard',      id: 'dashboard' },
        { href: 'profile.html',        icon: '👤', label: 'My Profile',      id: 'profile'   },
        { href: 'groups.html',         icon: '👥', label: 'Study Groups',    id: 'groups'    },
        { href: 'sessions.html',       icon: '📅', label: 'Sessions',        id: 'sessions'  },
        { href: 'notes.html',          icon: '📝', label: 'Notes',           id: 'notes'     },
        { href: 'resources.html',      icon: '🔗', label: 'Resources',       id: 'resources' },
        { href: 'forum.html',          icon: '💬', label: 'Forum',           id: 'forum'     },
        { href: 'progress.html',       icon: '📊', label: 'Progress',        id: 'progress'  },
        { href: 'notifications.html',  icon: '🔔', label: 'Notifications',   id: 'notifications' },
    ];

    const user = getCurrentUser();

    const html = `
    <div class="sidebar">
        <div class="sidebar-logo">
            <h2>🎓 PeerSphere</h2>
            <span>Collaborative Learning</span>
        </div>
        <nav class="sidebar-nav">
            <div class="nav-section-label">Main Menu</div>
            ${nav.map(item => `
                <a href="${item.href}"
                   class="nav-item ${activePage === item.id ? 'active' : ''}">
                    <span class="icon">${item.icon}</span>
                    ${item.label}
                    ${item.id === 'notifications'
                        ? '<span class="notif-badge" id="notif-badge" style="display:none;margin-left:auto;"></span>'
                        : ''}
                </a>
            `).join('')}
        </nav>
        <div class="sidebar-user">
            <div class="sidebar-avatar" id="sidebar-avatar">
                ${getInitials(user.fullName)}
            </div>
            <div class="sidebar-user-info">
                <div class="sidebar-user-name" id="sidebar-name">
                    ${user.fullName || 'User'}
                </div>
                <div class="sidebar-user-email" id="sidebar-email">
                    ${user.email || ''}
                </div>
            </div>
            <button onclick="logout()" title="Logout"
                style="background:none;border:none;cursor:pointer;font-size:1rem;color:var(--text-secondary);">
                🚪
            </button>
        </div>
    </div>`;

    const container = document.getElementById('sidebar-container');
    if (container) container.innerHTML = html;
    startNotificationPolling();
}