const API_BASE_URL = '/api';

let allOpportunities = [];
let allPromoters = new Map();

// Load opportunities on page load
document.addEventListener('DOMContentLoaded', () => {
    loadOpportunities();
    setupEventListeners();
});

function setupEventListeners() {
    const filterPromoter = document.getElementById('filterPromoter');
    const clearFilters = document.getElementById('clearFilters');

    filterPromoter.addEventListener('change', applyFilters);
    clearFilters.addEventListener('click', () => {
        filterPromoter.value = '';
        applyFilters();
    });
}

async function loadOpportunities() {
    const loadingIndicator = document.getElementById('loadingIndicator');
    const opportunitiesContainer = document.getElementById('opportunitiesContainer');
    const messageContainer = document.getElementById('messageContainer');

    try {
        loadingIndicator.style.display = 'block';
        opportunitiesContainer.innerHTML = '';
        messageContainer.innerHTML = '';

        const response = await fetch(`${API_BASE_URL}/opportunities`);

        if (!response.ok) {
            throw new Error('Erro ao carregar oportunidades');
        }

        allOpportunities = await response.json();

        // Extract unique promoters
        allOpportunities.forEach(opp => {
            if (!allPromoters.has(opp.promoterId)) {
                allPromoters.set(opp.promoterId, opp.promoterName);
            }
        });

        populatePromoterFilter();
        displayOpportunities(allOpportunities);

    } catch (error) {
        console.error('Error loading opportunities:', error);
        messageContainer.innerHTML = `
            <div class="message error">
                Erro ao carregar oportunidades. Por favor, tente novamente.
            </div>
        `;
    } finally {
        loadingIndicator.style.display = 'none';
    }
}

function populatePromoterFilter() {
    const filterPromoter = document.getElementById('filterPromoter');

    // Clear existing options except the first one
    while (filterPromoter.options.length > 1) {
        filterPromoter.remove(1);
    }

    // Add promoter options
    allPromoters.forEach((name, id) => {
        const option = document.createElement('option');
        option.value = id;
        option.textContent = name;
        filterPromoter.appendChild(option);
    });
}

function applyFilters() {
    const filterPromoter = document.getElementById('filterPromoter');
    const selectedPromoterId = filterPromoter.value;

    let filteredOpportunities = allOpportunities;

    if (selectedPromoterId) {
        filteredOpportunities = allOpportunities.filter(
            opp => opp.promoterId.toString() === selectedPromoterId
        );
    }

    displayOpportunities(filteredOpportunities);
}

function displayOpportunities(opportunities) {
    const container = document.getElementById('opportunitiesContainer');

    if (opportunities.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <h3>Nenhuma oportunidade encontrada</h3>
                <p>Não há oportunidades disponíveis no momento. Volte mais tarde ou crie uma nova oportunidade!</p>
                <a href="/create-opportunity.html" class="btn btn-primary">Criar Oportunidade</a>
            </div>
        `;
        return;
    }

    container.innerHTML = opportunities.map(opportunity => `
        <div class="opportunity-card" data-opportunity-id="${opportunity.id}">
            <h3>${escapeHtml(opportunity.title)}</h3>

            <div class="opportunity-meta">
                <span class="meta-item" title="Duração">
                    ⏱️ ${opportunity.duration} dias
                </span>
                <span class="meta-item" title="Vagas">
                    👥 ${opportunity.vacancies} vagas
                </span>
                <span class="meta-item" title="Pontos">
                    ⭐ ${opportunity.points} pontos
                </span>
            </div>

            <div class="opportunity-description">
                ${escapeHtml(opportunity.description)}
            </div>

            <div class="opportunity-skills">
                <strong>Habilidades:</strong> ${escapeHtml(opportunity.skills)}
            </div>

            <div class="promoter-info">
                <strong>Promotor:</strong> ${escapeHtml(opportunity.promoterName)}
            </div>

            <div class="opportunity-footer" style="margin-top: 1rem; color: #95a5a6; font-size: 0.85rem;">
                Criada em: ${formatDate(opportunity.createdAt)}
            </div>
        </div>
    `).join('');
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('pt-PT', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}
