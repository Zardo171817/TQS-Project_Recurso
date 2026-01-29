const API_BASE_URL = '/api';

let allOpportunities = [];
let currentPromoter = null;

document.addEventListener('DOMContentLoaded', () => {
    // Check if user is logged in as promoter
    const user = getUser();
    if (!user || user.userType !== 'PROMOTER') {
        showMessage('Esta pagina e apenas para promotores.', 'error');
        return;
    }

    loadPromoterOpportunities();
});

async function loadPromoterOpportunities() {
    const loadingIndicator = document.getElementById('loadingIndicator');
    const opportunitiesContainer = document.getElementById('opportunitiesContainer');
    const messageContainer = document.getElementById('messageContainer');
    const summaryCards = document.getElementById('summaryCards');

    try {
        loadingIndicator.style.display = 'block';
        opportunitiesContainer.innerHTML = '';
        messageContainer.innerHTML = '';

        const user = getUser();
        const userEmail = user.email;

        // First get the promoter by email
        const promoterResponse = await fetch(`${API_BASE_URL}/promoters/email/${encodeURIComponent(userEmail)}`);

        if (!promoterResponse.ok) {
            if (promoterResponse.status === 404) {
                showMessage('Perfil de promotor nao encontrado. Por favor, crie o seu perfil primeiro.', 'error');
                opportunitiesContainer.innerHTML = `
                    <div class="empty-state">
                        <h3>Perfil nao encontrado</h3>
                        <p>Precisa de criar o seu perfil de promotor para gerir oportunidades.</p>
                        <a href="/promoter-profile.html" class="btn btn-primary" style="margin-top: 1rem;">Criar Perfil</a>
                    </div>
                `;
                return;
            }
            throw new Error('Erro ao verificar promotor');
        }

        currentPromoter = await promoterResponse.json();
        localStorage.setItem('promoterId', currentPromoter.id);

        // Load opportunities for this promoter
        const opportunitiesResponse = await fetch(`${API_BASE_URL}/opportunities/promoter/${currentPromoter.id}`);

        if (!opportunitiesResponse.ok) {
            throw new Error('Erro ao carregar oportunidades');
        }

        allOpportunities = await opportunitiesResponse.json();

        // Update summary
        updateSummary();
        summaryCards.style.display = 'grid';

        // Display opportunities
        displayOpportunities(allOpportunities);

    } catch (error) {
        console.error('Error loading opportunities:', error);
        showMessage('Erro ao carregar oportunidades. Por favor, tente novamente.', 'error');
    } finally {
        loadingIndicator.style.display = 'none';
    }
}

function updateSummary() {
    const total = allOpportunities.length;
    const open = allOpportunities.filter(o => o.status === 'OPEN').length;
    const concluded = allOpportunities.filter(o => o.status === 'CONCLUDED').length;

    document.getElementById('totalCount').textContent = total;
    document.getElementById('openCount').textContent = open;
    document.getElementById('concludedCount').textContent = concluded;

    // Count pending applications (would need additional API call)
    document.getElementById('applicationsCount').textContent = '-';
}

function displayOpportunities(opportunities) {
    const container = document.getElementById('opportunitiesContainer');

    if (opportunities.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <h3>Nenhuma oportunidade criada</h3>
                <p>Ainda nao criou nenhuma oportunidade de voluntariado.</p>
                <a href="/create-opportunity.html" class="btn btn-primary" style="margin-top: 1rem;">Criar Primeira Oportunidade</a>
            </div>
        `;
        return;
    }

    container.innerHTML = opportunities.map(opportunity => `
        <div class="opportunity-card" data-opportunity-id="${opportunity.id}">
            <div class="opportunity-header">
                <h3>
                    ${escapeHtml(opportunity.title)}
                    <span class="category-badge">${escapeHtml(opportunity.category || 'Sem categoria')}</span>
                </h3>
                <span class="status-badge status-${opportunity.status}">
                    ${getStatusText(opportunity.status)}
                </span>
            </div>

            <p style="color: #555; margin-bottom: 1rem;">${escapeHtml(truncateText(opportunity.description, 200))}</p>

            <div class="opportunity-stats">
                <div class="stat-item">
                    <strong>${opportunity.vacancies}</strong> vagas
                </div>
                <div class="stat-item">
                    <strong>${opportunity.duration}</strong> dias
                </div>
                <div class="stat-item">
                    <strong>${opportunity.points}</strong> pontos
                </div>
            </div>

            <div class="opportunity-actions">
                <a href="/opportunity-details.html?id=${opportunity.id}" class="btn btn-secondary">Ver Detalhes</a>
                ${opportunity.status === 'OPEN' ? `
                    <a href="/applications.html" class="btn btn-primary">Ver Candidaturas</a>
                    <a href="/conclude-opportunity.html" class="btn btn-secondary">Concluir</a>
                ` : ''}
            </div>

            <div style="margin-top: 1rem; color: #95a5a6; font-size: 0.85rem;">
                Criada em: ${formatDate(opportunity.createdAt)}
            </div>
        </div>
    `).join('');
}

function getStatusText(status) {
    const statusMap = {
        'OPEN': 'Aberta',
        'CONCLUDED': 'Concluida',
        'CANCELLED': 'Cancelada'
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
}

function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function truncateText(text, maxLength) {
    if (!text) return '';
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength) + '...';
}

function formatDate(dateString) {
    if (!dateString) return 'Data nao disponivel';
    const date = new Date(dateString);
    return date.toLocaleDateString('pt-PT', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
}
