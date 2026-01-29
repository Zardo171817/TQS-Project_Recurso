const API_BASE_URL = '/api';

let allApplications = [];
let currentVolunteer = null;

document.addEventListener('DOMContentLoaded', () => {
    setupEventListeners();
    // Auto-load applications if user is logged in as volunteer
    autoLoadApplications();
});

function autoLoadApplications() {
    const user = getUser();
    if (user && user.userType === 'VOLUNTEER' && user.email) {
        document.getElementById('volunteerEmail').value = user.email;
        loadVolunteerApplications();
    }
}

function setupEventListeners() {
    document.getElementById('loadApplications').addEventListener('click', loadVolunteerApplications);
    document.getElementById('statusFilter').addEventListener('change', filterApplications);
    document.getElementById('volunteerEmail').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            loadVolunteerApplications();
        }
    });
}

async function loadVolunteerApplications() {
    const email = document.getElementById('volunteerEmail').value.trim();
    const loadingIndicator = document.getElementById('loadingIndicator');
    const applicationsContainer = document.getElementById('applicationsContainer');
    const messageContainer = document.getElementById('messageContainer');
    const resultsInfo = document.getElementById('resultsInfo');
    const statusSummary = document.getElementById('statusSummary');
    const volunteerInfoHeader = document.getElementById('volunteerInfoHeader');

    if (!email) {
        showMessage('Por favor, introduza o seu email.', 'error');
        return;
    }

    if (!isValidEmail(email)) {
        showMessage('Por favor, introduza um email valido.', 'error');
        return;
    }

    try {
        loadingIndicator.style.display = 'block';
        applicationsContainer.innerHTML = '';
        messageContainer.innerHTML = '';
        resultsInfo.innerHTML = '';
        statusSummary.style.display = 'none';
        volunteerInfoHeader.style.display = 'none';

        // First, get volunteer by email
        const volunteerResponse = await fetch(`${API_BASE_URL}/volunteers/email/${encodeURIComponent(email)}`);

        if (!volunteerResponse.ok) {
            if (volunteerResponse.status === 404) {
                showMessage('Voluntario nao encontrado. Verifique o email ou candidate-se a uma oportunidade primeiro.', 'error');
                return;
            }
            throw new Error('Erro ao verificar voluntario');
        }

        currentVolunteer = await volunteerResponse.json();

        // Display volunteer info
        document.getElementById('volunteerName').textContent = currentVolunteer.name;
        document.getElementById('volunteerEmailDisplay').textContent = currentVolunteer.email;
        volunteerInfoHeader.style.display = 'block';

        // Now get applications for this volunteer
        const applicationsResponse = await fetch(`${API_BASE_URL}/applications/volunteer/${currentVolunteer.id}`);

        if (!applicationsResponse.ok) {
            throw new Error('Erro ao carregar candidaturas');
        }

        allApplications = await applicationsResponse.json();

        // Update status summary
        updateStatusSummary();
        statusSummary.style.display = 'flex';

        filterApplications();

    } catch (error) {
        console.error('Error loading applications:', error);
        messageContainer.innerHTML = `
            <div class="message error">
                Erro ao carregar candidaturas. Por favor, tente novamente.
            </div>
        `;
    } finally {
        loadingIndicator.style.display = 'none';
    }
}

function updateStatusSummary() {
    const pending = allApplications.filter(app => app.status === 'PENDING').length;
    const accepted = allApplications.filter(app => app.status === 'ACCEPTED').length;
    const rejected = allApplications.filter(app => app.status === 'REJECTED').length;

    document.getElementById('totalCount').textContent = allApplications.length;
    document.getElementById('pendingCount').textContent = pending;
    document.getElementById('acceptedCount').textContent = accepted;
    document.getElementById('rejectedCount').textContent = rejected;
}

function filterApplications() {
    const statusFilter = document.getElementById('statusFilter').value;

    let filtered = allApplications;
    if (statusFilter) {
        filtered = allApplications.filter(app => app.status === statusFilter);
    }

    displayApplications(filtered);
    updateResultsInfo(filtered.length, allApplications.length);
}

function updateResultsInfo(shown, total) {
    const resultsInfo = document.getElementById('resultsInfo');
    const statusFilter = document.getElementById('statusFilter').value;

    if (statusFilter) {
        resultsInfo.innerHTML = `Mostrando ${shown} de ${total} candidaturas`;
    } else {
        resultsInfo.innerHTML = `${total} candidaturas encontradas`;
    }
}

function displayApplications(applications) {
    const container = document.getElementById('applicationsContainer');

    if (applications.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <h3>Nenhuma candidatura encontrada</h3>
                <p>Ainda nao se candidatou a nenhuma oportunidade de voluntariado.</p>
                <a href="/opportunities.html" class="btn btn-primary" style="margin-top: 1rem;">Ver Oportunidades</a>
            </div>
        `;
        return;
    }

    container.innerHTML = applications.map(application => `
        <div class="application-card" data-application-id="${application.id}">
            <div class="application-header">
                <h3>
                    <a href="/opportunity-details.html?id=${application.opportunityId}" class="opportunity-link">
                        ${escapeHtml(application.opportunityTitle)}
                    </a>
                </h3>
                <span class="status-badge status-${application.status}">
                    ${getStatusText(application.status)}
                </span>
            </div>

            <div class="opportunity-info">
                <p><strong>Oportunidade:</strong> ${escapeHtml(application.opportunityTitle)}</p>
                <p><strong>Estado:</strong> ${getStatusDescription(application.status)}</p>
            </div>

            ${application.motivation ? `
                <div class="motivation-section">
                    <h4>Sua Motivacao</h4>
                    <p class="motivation-text">${escapeHtml(application.motivation)}</p>
                </div>
            ` : ''}

            <div class="application-footer">
                <span class="application-date">
                    Candidatura enviada em: ${formatDate(application.appliedAt)}
                </span>
            </div>
        </div>
    `).join('');
}

function getStatusText(status) {
    const statusMap = {
        'PENDING': 'Pendente',
        'ACCEPTED': 'Aceite',
        'REJECTED': 'Rejeitada'
    };
    return statusMap[status] || status;
}

function getStatusDescription(status) {
    const descriptions = {
        'PENDING': 'A sua candidatura esta a ser analisada pelo promotor.',
        'ACCEPTED': 'Parabens! A sua candidatura foi aceite.',
        'REJECTED': 'Infelizmente, a sua candidatura nao foi aceite.'
    };
    return descriptions[status] || status;
}

function showMessage(message, type) {
    const messageContainer = document.getElementById('messageContainer');
    messageContainer.innerHTML = `
        <div class="message ${type}">
            ${message}
        </div>
    `;
    setTimeout(() => {
        messageContainer.innerHTML = '';
    }, 5000);
}

function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function formatDate(dateString) {
    if (!dateString) return 'Data nao disponivel';
    const date = new Date(dateString);
    return date.toLocaleDateString('pt-PT', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}
