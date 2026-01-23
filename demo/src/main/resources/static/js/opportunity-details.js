const API_BASE_URL = '/api';

document.addEventListener('DOMContentLoaded', () => {
    loadOpportunityDetails();
    setupEventListeners();
});

function setupEventListeners() {
    const applyButton = document.getElementById('applyButton');
    applyButton.addEventListener('click', handleApply);
}

function getOpportunityIdFromUrl() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('id');
}

async function loadOpportunityDetails() {
    const loadingIndicator = document.getElementById('loadingIndicator');
    const opportunityDetails = document.getElementById('opportunityDetails');
    const messageContainer = document.getElementById('messageContainer');

    const opportunityId = getOpportunityIdFromUrl();

    if (!opportunityId) {
        loadingIndicator.style.display = 'none';
        messageContainer.innerHTML = `
            <div class="message error">
                ID da oportunidade nao especificado.
                <a href="/volunteer-opportunities.html">Voltar para a lista de oportunidades</a>
            </div>
        `;
        return;
    }

    try {
        loadingIndicator.style.display = 'block';
        opportunityDetails.style.display = 'none';
        messageContainer.innerHTML = '';

        const response = await fetch(`${API_BASE_URL}/opportunities/${opportunityId}`);

        if (!response.ok) {
            if (response.status === 404) {
                throw new Error('Oportunidade nao encontrada');
            }
            throw new Error('Erro ao carregar detalhes da oportunidade');
        }

        const opportunity = await response.json();
        displayOpportunityDetails(opportunity);

    } catch (error) {
        console.error('Error loading opportunity details:', error);
        messageContainer.innerHTML = `
            <div class="message error">
                ${escapeHtml(error.message)}.
                <a href="/volunteer-opportunities.html">Voltar para a lista de oportunidades</a>
            </div>
        `;
    } finally {
        loadingIndicator.style.display = 'none';
    }
}

function displayOpportunityDetails(opportunity) {
    const opportunityDetails = document.getElementById('opportunityDetails');

    document.getElementById('opportunityTitle').textContent = opportunity.title;
    document.getElementById('opportunityCategory').textContent = opportunity.category || 'Sem categoria';
    document.getElementById('opportunityDuration').textContent = `${opportunity.duration} dias`;
    document.getElementById('opportunityVacancies').textContent = `${opportunity.vacancies} vagas`;
    document.getElementById('opportunityPoints').textContent = `${opportunity.points} pontos`;
    document.getElementById('opportunityDescription').textContent = opportunity.description;
    document.getElementById('opportunityPromoter').textContent = opportunity.promoterName;
    document.getElementById('opportunityCreatedAt').textContent = `Criada em: ${formatDate(opportunity.createdAt)}`;

    const skillsContainer = document.getElementById('opportunitySkills');
    const skills = opportunity.skills.split(',').map(s => s.trim()).filter(s => s);
    skillsContainer.innerHTML = skills.map(skill =>
        `<span class="skill-tag">${escapeHtml(skill)}</span>`
    ).join('');

    document.title = `${opportunity.title} - Marketplace de Voluntariado`;

    opportunityDetails.style.display = 'block';
}

function handleApply() {
    const messageContainer = document.getElementById('messageContainer');
    messageContainer.innerHTML = `
        <div class="message success">
            Funcionalidade de candidatura sera implementada em breve. Obrigado pelo seu interesse!
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
