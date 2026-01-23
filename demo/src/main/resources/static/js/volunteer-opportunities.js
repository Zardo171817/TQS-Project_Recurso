const API_BASE_URL = '/api';

let allOpportunities = [];

// Load opportunities on page load
document.addEventListener('DOMContentLoaded', () => {
    loadOpportunities();
    setupEventListeners();
});

function setupEventListeners() {
    const applyFiltersBtn = document.getElementById('applyFilters');
    const clearFiltersBtn = document.getElementById('clearFilters');
    const filterSkills = document.getElementById('filterSkills');

    applyFiltersBtn.addEventListener('click', applyFilters);
    clearFiltersBtn.addEventListener('click', clearFilters);

    // Allow pressing Enter to apply filters
    filterSkills.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            applyFilters();
        }
    });

    document.getElementById('filterMinDuration').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            applyFilters();
        }
    });

    document.getElementById('filterMaxDuration').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            applyFilters();
        }
    });
}

async function loadOpportunities() {
    const loadingIndicator = document.getElementById('loadingIndicator');
    const opportunitiesContainer = document.getElementById('opportunitiesContainer');
    const messageContainer = document.getElementById('messageContainer');
    const resultsInfo = document.getElementById('resultsInfo');

    try {
        loadingIndicator.style.display = 'block';
        opportunitiesContainer.innerHTML = '';
        messageContainer.innerHTML = '';
        resultsInfo.innerHTML = '';

        const response = await fetch(`${API_BASE_URL}/opportunities`);

        if (!response.ok) {
            throw new Error('Erro ao carregar oportunidades');
        }

        allOpportunities = await response.json();
        displayOpportunities(allOpportunities);
        updateResultsInfo(allOpportunities.length, allOpportunities.length);

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

async function applyFilters() {
    const loadingIndicator = document.getElementById('loadingIndicator');
    const opportunitiesContainer = document.getElementById('opportunitiesContainer');
    const messageContainer = document.getElementById('messageContainer');
    const resultsInfo = document.getElementById('resultsInfo');

    const category = document.getElementById('filterCategory').value;
    const skills = document.getElementById('filterSkills').value.trim();
    const minDuration = document.getElementById('filterMinDuration').value;
    const maxDuration = document.getElementById('filterMaxDuration').value;

    // Validate duration range
    if (minDuration && maxDuration && parseInt(minDuration) > parseInt(maxDuration)) {
        messageContainer.innerHTML = `
            <div class="message error">
                A duracao minima nao pode ser maior que a duracao maxima.
            </div>
        `;
        return;
    }

    try {
        loadingIndicator.style.display = 'block';
        opportunitiesContainer.innerHTML = '';
        messageContainer.innerHTML = '';

        // Build query parameters
        const params = new URLSearchParams();
        if (category) params.append('category', category);
        if (skills) params.append('skills', skills);
        if (minDuration) params.append('minDuration', minDuration);
        if (maxDuration) params.append('maxDuration', maxDuration);

        const url = `${API_BASE_URL}/opportunities/filter?${params.toString()}`;
        const response = await fetch(url);

        if (!response.ok) {
            throw new Error('Erro ao filtrar oportunidades');
        }

        const filteredOpportunities = await response.json();
        displayOpportunities(filteredOpportunities);
        updateResultsInfo(filteredOpportunities.length, allOpportunities.length);

        if (filteredOpportunities.length === 0 && hasActiveFilters()) {
            messageContainer.innerHTML = `
                <div class="message" style="background-color: #fff3cd; color: #856404; border: 1px solid #ffc107;">
                    Nenhuma oportunidade encontrada com os filtros selecionados. Tente ajustar os criterios de busca.
                </div>
            `;
        }

    } catch (error) {
        console.error('Error filtering opportunities:', error);
        messageContainer.innerHTML = `
            <div class="message error">
                Erro ao filtrar oportunidades. Por favor, tente novamente.
            </div>
        `;
    } finally {
        loadingIndicator.style.display = 'none';
    }
}

function clearFilters() {
    document.getElementById('filterCategory').value = '';
    document.getElementById('filterSkills').value = '';
    document.getElementById('filterMinDuration').value = '';
    document.getElementById('filterMaxDuration').value = '';
    document.getElementById('messageContainer').innerHTML = '';

    displayOpportunities(allOpportunities);
    updateResultsInfo(allOpportunities.length, allOpportunities.length);
}

function hasActiveFilters() {
    return document.getElementById('filterCategory').value ||
           document.getElementById('filterSkills').value.trim() ||
           document.getElementById('filterMinDuration').value ||
           document.getElementById('filterMaxDuration').value;
}

function updateResultsInfo(shown, total) {
    const resultsInfo = document.getElementById('resultsInfo');
    if (hasActiveFilters()) {
        resultsInfo.innerHTML = `Mostrando ${shown} de ${total} oportunidades`;
    } else {
        resultsInfo.innerHTML = `${total} oportunidades disponiveis`;
    }
}

function displayOpportunities(opportunities) {
    const container = document.getElementById('opportunitiesContainer');

    if (opportunities.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <h3>Nenhuma oportunidade encontrada</h3>
                <p>Nao ha oportunidades disponiveis no momento. Volte mais tarde!</p>
            </div>
        `;
        return;
    }

    container.innerHTML = opportunities.map(opportunity => `
        <div class="opportunity-card" data-opportunity-id="${opportunity.id}">
            <div class="opportunity-header">
                <h3>${escapeHtml(opportunity.title)}</h3>
                <span class="category-badge">${escapeHtml(opportunity.category || 'Sem categoria')}</span>
            </div>

            <div class="opportunity-meta">
                <span class="meta-item" title="Duracao">
                    <span class="meta-icon">&#9201;</span> ${opportunity.duration} dias
                </span>
                <span class="meta-item" title="Vagas">
                    <span class="meta-icon">&#128101;</span> ${opportunity.vacancies} vagas
                </span>
                <span class="meta-item" title="Pontos">
                    <span class="meta-icon">&#11088;</span> ${opportunity.points} pontos
                </span>
            </div>

            <div class="opportunity-description">
                ${escapeHtml(truncateText(opportunity.description, 150))}
            </div>

            <div class="opportunity-skills">
                <strong>Competencias:</strong> ${escapeHtml(opportunity.skills)}
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
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}
