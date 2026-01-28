const API_BASE_URL = '/api';

let allApplications = [];

document.addEventListener('DOMContentLoaded', () => {
    loadPromoters();
    setupEventListeners();
});

function setupEventListeners() {
    document.getElementById('loadApplications').addEventListener('click', loadApplications);
    document.getElementById('statusFilter').addEventListener('change', filterApplications);
}

async function loadPromoters() {
    try {
        const response = await fetch(`${API_BASE_URL}/promoters`);
        if (!response.ok) {
            throw new Error('Erro ao carregar promotores');
        }

        const promoters = await response.json();
        const select = document.getElementById('promoterSelect');
        const user = getUser();
        const userEmail = user ? user.email : null;
        let autoSelectPromoter = null;

        promoters.forEach(promoter => {
            const option = document.createElement('option');
            option.value = promoter.id;
            option.textContent = promoter.name;
            select.appendChild(option);

            // Auto-select if this is the logged in promoter
            if (userEmail && promoter.email === userEmail) {
                option.selected = true;
                autoSelectPromoter = promoter;
                localStorage.setItem('promoterId', promoter.id);
            }
        });

        // Auto-load applications if promoter was auto-selected
        if (autoSelectPromoter) {
            loadApplications();
        }

    } catch (error) {
        console.error('Error loading promoters:', error);
        showMessage('Erro ao carregar promotores. Por favor, tente novamente.', 'error');
    }
}

async function loadApplications() {
    const promoterId = document.getElementById('promoterSelect').value;
    const loadingIndicator = document.getElementById('loadingIndicator');
    const applicationsContainer = document.getElementById('applicationsContainer');
    const messageContainer = document.getElementById('messageContainer');
    const resultsInfo = document.getElementById('resultsInfo');

    if (!promoterId) {
        showMessage('Por favor, selecione um promotor.', 'error');
        return;
    }

    try {
        loadingIndicator.style.display = 'block';
        applicationsContainer.innerHTML = '';
        messageContainer.innerHTML = '';
        resultsInfo.innerHTML = '';

        const response = await fetch(`${API_BASE_URL}/applications/promoter/${promoterId}`);

        if (!response.ok) {
            throw new Error('Erro ao carregar candidaturas');
        }

        allApplications = await response.json();
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
        resultsInfo.innerHTML = `${total} candidaturas recebidas`;
    }
}

function displayApplications(applications) {
    const container = document.getElementById('applicationsContainer');

    if (applications.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <h3>Nenhuma candidatura encontrada</h3>
                <p>Este promotor ainda nao recebeu candidaturas para as suas oportunidades.</p>
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

            <div class="volunteer-info">
                <p><strong>Voluntario:</strong> ${escapeHtml(application.volunteerName)}</p>
                <p><strong>Email:</strong> ${escapeHtml(application.volunteerEmail)}</p>
            </div>

            ${application.motivation ? `
                <div class="motivation-section">
                    <h4>Motivacao</h4>
                    <p class="motivation-text">${escapeHtml(application.motivation)}</p>
                </div>
            ` : ''}

            <div class="application-footer">
                <span class="application-date">
                    Candidatura enviada em: ${formatDate(application.appliedAt)}
                </span>
                ${application.status === 'PENDING' ? `
                    <div class="application-actions">
                        <button class="btn btn-secondary" onclick="updateApplicationStatus(${application.id}, 'ACCEPTED')">
                            Aceitar
                        </button>
                        <button class="btn btn-danger" onclick="updateApplicationStatus(${application.id}, 'REJECTED')">
                            Rejeitar
                        </button>
                    </div>
                ` : ''}
            </div>
        </div>
    `).join('');
}

async function updateApplicationStatus(applicationId, newStatus) {
    try {
        const response = await fetch(`${API_BASE_URL}/applications/${applicationId}/status?status=${newStatus}`, {
            method: 'PATCH'
        });

        if (!response.ok) {
            throw new Error('Erro ao atualizar status');
        }

        showMessage(`Candidatura ${newStatus === 'ACCEPTED' ? 'aceite' : 'rejeitada'} com sucesso!`, 'success');
        loadApplications();

    } catch (error) {
        console.error('Error updating application status:', error);
        showMessage('Erro ao atualizar status da candidatura. Por favor, tente novamente.', 'error');
    }
}

function getStatusText(status) {
    const statusMap = {
        'PENDING': 'Pendente',
        'ACCEPTED': 'Aceite',
        'REJECTED': 'Rejeitado'
    };
    return statusMap[status] || status;
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
