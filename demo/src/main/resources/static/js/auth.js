// Authentication utility functions

function getUser() {
    const userStr = localStorage.getItem('user');
    if (userStr) {
        try {
            return JSON.parse(userStr);
        } catch (e) {
            return null;
        }
    }
    return null;
}

function isLoggedIn() {
    return getUser() !== null;
}

function logout() {
    localStorage.removeItem('user');
    localStorage.removeItem('volunteerId');
    localStorage.removeItem('promoterId');
    window.location.href = '/login.html';
}

function getUserType() {
    const user = getUser();
    return user ? user.userType : null;
}

function getUserId() {
    const user = getUser();
    return user ? user.id : null;
}

function getUserName() {
    const user = getUser();
    return user ? user.name : null;
}

function getUserEmail() {
    const user = getUser();
    return user ? user.email : null;
}

function isVolunteer() {
    return getUserType() === 'VOLUNTEER';
}

function isPromoter() {
    return getUserType() === 'PROMOTER';
}

function isPartner() {
    return getUserType() === 'PARTNER';
}

function requireAuth() {
    if (!isLoggedIn()) {
        window.location.href = '/login.html';
        return false;
    }
    return true;
}

function requireRole(role) {
    if (!requireAuth()) return false;
    if (getUserType() !== role) {
        alert('Não tem permissão para aceder a esta página.');
        window.location.href = '/index.html';
        return false;
    }
    return true;
}

function requireRoles(roles) {
    if (!requireAuth()) return false;
    if (!roles.includes(getUserType())) {
        alert('Não tem permissão para aceder a esta página.');
        window.location.href = '/index.html';
        return false;
    }
    return true;
}

// Navigation configuration per user type
const navConfig = {
    public: [
        { href: '/index.html', label: 'Início' },
        { href: '/opportunities.html', label: 'Oportunidades' },
        { href: '/benefits-catalog.html', label: 'Catálogo Benefícios' },
        { href: '/volunteer-ranking.html', label: 'Ranking' }
    ],
    VOLUNTEER: [
        { href: '/index.html', label: 'Início' },
        { href: '/opportunities.html', label: 'Oportunidades' },
        { href: '/volunteer-applications.html', label: 'Minhas Candidaturas' },
        { href: '/volunteer-profile.html', label: 'Meu Perfil' },
        { href: '/volunteer-points.html', label: 'Meus Pontos' },
        { href: '/volunteer-points-history.html', label: 'Histórico Pontos' },
        { href: '/benefits-catalog.html', label: 'Catálogo Benefícios' },
        { href: '/redeem-points.html', label: 'Resgatar Pontos' },
        { href: '/volunteer-ranking.html', label: 'Ranking' }
    ],
    PROMOTER: [
        { href: '/index.html', label: 'Início' },
        { href: '/opportunities.html', label: 'Oportunidades' },
        { href: '/create-opportunity.html', label: 'Criar Oportunidade' },
        { href: '/applications.html', label: 'Candidaturas' },
        { href: '/promoter-profile.html', label: 'Meu Perfil' },
        { href: '/conclude-opportunity.html', label: 'Concluir Oportunidade' },
        { href: '/volunteer-ranking.html', label: 'Ranking' }
    ],
    PARTNER: [
        { href: '/index.html', label: 'Início' },
        { href: '/benefits-catalog.html', label: 'Catálogo Benefícios' },
        { href: '/partner-benefits.html', label: 'Meus Benefícios' },
        { href: '/partner-redemptions.html', label: 'Ver Resgates' }
    ]
};

function buildNavbar() {
    const navLinks = document.querySelector('.nav-links');
    if (!navLinks) return;

    const user = getUser();
    const userType = user ? user.userType : null;
    const currentPage = window.location.pathname;

    // Clear existing links
    navLinks.innerHTML = '';

    // Get appropriate nav items
    const navItems = userType ? navConfig[userType] : navConfig.public;

    // Build navigation links
    navItems.forEach(item => {
        const li = document.createElement('li');
        const isActive = currentPage === item.href ||
                        (currentPage === '/' && item.href === '/index.html');
        li.innerHTML = `<a href="${item.href}" ${isActive ? 'class="active"' : ''}>${item.label}</a>`;
        navLinks.appendChild(li);
    });

    // Add auth section
    if (user) {
        // User info
        const userInfo = document.createElement('li');
        userInfo.className = 'auth-link user-info';
        const typeLabel = userType === 'VOLUNTEER' ? 'Voluntário' :
                         userType === 'PROMOTER' ? 'Promotor' : 'Parceiro';
        userInfo.innerHTML = `<span class="user-badge">${user.name} (${typeLabel})</span>`;
        navLinks.appendChild(userInfo);

        // Logout button
        const logoutLi = document.createElement('li');
        logoutLi.className = 'auth-link';
        logoutLi.innerHTML = `<a href="#" onclick="logout(); return false;" class="btn-logout">Sair</a>`;
        navLinks.appendChild(logoutLi);
    } else {
        // Login link
        const loginLi = document.createElement('li');
        loginLi.className = 'auth-link';
        const isLoginActive = currentPage === '/login.html';
        loginLi.innerHTML = `<a href="/login.html" ${isLoginActive ? 'class="active"' : ''}>Login</a>`;
        navLinks.appendChild(loginLi);

        // Register link
        const registerLi = document.createElement('li');
        registerLi.className = 'auth-link';
        const isRegisterActive = currentPage === '/register.html';
        registerLi.innerHTML = `<a href="/register.html" ${isRegisterActive ? 'class="active"' : ''}>Registar</a>`;
        navLinks.appendChild(registerLi);
    }
}

// Check page access based on user role
function checkPageAccess() {
    const currentPage = window.location.pathname;
    const user = getUser();
    const userType = user ? user.userType : null;

    // Pages that require specific roles
    const volunteerPages = [
        '/volunteer-applications.html',
        '/volunteer-profile.html',
        '/volunteer-points.html',
        '/volunteer-points-history.html',
        '/redeem-points.html'
    ];

    const promoterPages = [
        '/create-opportunity.html',
        '/applications.html',
        '/promoter-profile.html',
        '/conclude-opportunity.html'
    ];

    const partnerPages = [
        '/partner-benefits.html',
        '/partner-redemptions.html'
    ];

    // Check volunteer pages
    if (volunteerPages.includes(currentPage)) {
        if (!user) {
            window.location.href = '/login.html';
            return false;
        }
        if (userType !== 'VOLUNTEER') {
            alert('Esta página é apenas para voluntários.');
            window.location.href = '/index.html';
            return false;
        }
    }

    // Check promoter pages
    if (promoterPages.includes(currentPage)) {
        if (!user) {
            window.location.href = '/login.html';
            return false;
        }
        if (userType !== 'PROMOTER') {
            alert('Esta página é apenas para promotores.');
            window.location.href = '/index.html';
            return false;
        }
    }

    // Check partner pages
    if (partnerPages.includes(currentPage)) {
        if (!user) {
            window.location.href = '/login.html';
            return false;
        }
        if (userType !== 'PARTNER') {
            alert('Esta página é apenas para parceiros.');
            window.location.href = '/index.html';
            return false;
        }
    }

    return true;
}

// Auto-update navbar and check access when DOM loads
document.addEventListener('DOMContentLoaded', function() {
    checkPageAccess();
    buildNavbar();
});
