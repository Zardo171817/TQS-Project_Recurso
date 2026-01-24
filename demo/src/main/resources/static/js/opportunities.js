const API_BASE_URL = '/api';

let allOpportunities = [];
let allPromoters = new Map();

// Load opportunities on page load
document.addEventListener('DOMContentLoaded', () => {
    loadOpportunities();
    setupEventListeners();
    setupModalListeners();
});

function setupEventListeners() {
    const applyFiltersBtn = document.getElementById('applyFilters');
    const clearFiltersBtn = document.getElementById('clearFilters');
    const filterSkills = document.getElementById('filterSkills');

    applyFiltersBtn.addEventListener('click', applyFilters);
    clearFiltersBtn.addEventListener('click', clearFilters);

    // Allow pressing Enter to apply filters
    filterSkills.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') applyFilters();
    });

    document.getElementById('filterMinDuration').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') applyFilters();
    });

    document.getElementById('filterMaxDuration').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') applyFilters();
    });
}

function setupModalListeners() {
    // Edit Modal
    const editModal = document.getElementById('editModal');
    const closeModal = document.getElementById('closeModal');
    const cancelEdit = document.getElementById('cancelEdit');
    const editForm = document.getElementById('editForm');

    closeModal.addEventListener('click', () => editModal.style.display = 'none');
    cancelEdit.addEventListener('click', () => editModal.style.display = 'none');
    editForm.addEventListener('submit', handleEditSubmit);

    // Delete Modal
    const deleteModal = document.getElementById('deleteModal');
    const confirmDelete = document.getElementById('confirmDelete');
    const cancelDelete = document.getElementById('cancelDelete');

    confirmDelete.addEventListener('click', handleDeleteConfirm);
    cancelDelete.addEventListener('click', () => deleteModal.style.display = 'none');

    // Close modals when clicking outside
    window.addEventListener('click', (event) => {
        if (event.target === editModal) {
            editModal.style.display = 'none';
        }
        if (event.target === deleteModal) {
            deleteModal.style.display = 'none';
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

        // Extract unique promoters
        allPromoters.clear();
        allOpportunities.forEach(opp => {
            if (!allPromoters.has(opp.promoterId)) {
                allPromoters.set(opp.promoterId, opp.promoterName);
            }
        });

        populatePromoterFilter();
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

async function applyFilters() {
    const loadingIndicator = document.getElementById('loadingIndicator');
    const opportunitiesContainer = document.getElementById('opportunitiesContainer');
    const messageContainer = document.getElementById('messageContainer');

    const category = document.getElementById('filterCategory').value;
    const skills = document.getElementById('filterSkills').value.trim();
    const promoterId = document.getElementById('filterPromoter').value;
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

        let filteredOpportunities = await response.json();

        // Apply promoter filter client-side (since API doesn't support it in filter endpoint)
        if (promoterId) {
            filteredOpportunities = filteredOpportunities.filter(
                opp => opp.promoterId.toString() === promoterId
            );
        }

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
    document.getElementById('filterPromoter').value = '';
    document.getElementById('filterMinDuration').value = '';
    document.getElementById('filterMaxDuration').value = '';
    document.getElementById('messageContainer').innerHTML = '';

    displayOpportunities(allOpportunities);
    updateResultsInfo(allOpportunities.length, allOpportunities.length);
}

function hasActiveFilters() {
    return document.getElementById('filterCategory').value ||
           document.getElementById('filterSkills').value.trim() ||
           document.getElementById('filterPromoter').value ||
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
                <p>Nao ha oportunidades disponiveis no momento. Volte mais tarde ou crie uma nova oportunidade!</p>
                <a href="/create-opportunity.html" class="btn btn-primary">Criar Oportunidade</a>
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

            <div class="opportunity-actions" style="margin-top: 1rem; display: flex; gap: 0.5rem; flex-wrap: wrap;">
                <a href="/opportunity-details.html?id=${opportunity.id}" class="btn btn-primary">Detalhes</a>
                <button class="btn btn-secondary btn-edit" onclick="openEditModal(${opportunity.id})">Editar</button>
                <button class="btn btn-danger btn-delete" onclick="openDeleteModal(${opportunity.id}, '${escapeHtml(opportunity.title).replace(/'/g, "\\'")}')">Cancelar</button>
            </div>
        </div>
    `).join('');
}

function openEditModal(opportunityId) {
    const opportunity = allOpportunities.find(opp => opp.id === opportunityId);
    if (!opportunity) return;

    document.getElementById('editId').value = opportunity.id;
    document.getElementById('editTitle').value = opportunity.title;
    document.getElementById('editDescription').value = opportunity.description;
    document.getElementById('editSkills').value = opportunity.skills;
    document.getElementById('editCategory').value = opportunity.category;
    document.getElementById('editDuration').value = opportunity.duration;
    document.getElementById('editVacancies').value = opportunity.vacancies;
    document.getElementById('editPoints').value = opportunity.points;

    document.getElementById('editModal').style.display = 'flex';
}

function openDeleteModal(opportunityId, title) {
    document.getElementById('deleteId').value = opportunityId;
    document.getElementById('deleteOpportunityTitle').textContent = title;
    document.getElementById('deleteModal').style.display = 'flex';
}

async function handleEditSubmit(event) {
    event.preventDefault();

    const id = document.getElementById('editId').value;
    const data = {
        title: document.getElementById('editTitle').value,
        description: document.getElementById('editDescription').value,
        skills: document.getElementById('editSkills').value,
        category: document.getElementById('editCategory').value,
        duration: parseInt(document.getElementById('editDuration').value),
        vacancies: parseInt(document.getElementById('editVacancies').value),
        points: parseInt(document.getElementById('editPoints').value)
    };

    try {
        const response = await fetch(`${API_BASE_URL}/opportunities/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        if (!response.ok) {
            throw new Error('Erro ao atualizar oportunidade');
        }

        document.getElementById('editModal').style.display = 'none';
        showMessage('Oportunidade atualizada com sucesso!', 'success');
        loadOpportunities();

    } catch (error) {
        console.error('Error updating opportunity:', error);
        showMessage('Erro ao atualizar oportunidade. Por favor, tente novamente.', 'error');
    }
}

async function handleDeleteConfirm() {
    const id = document.getElementById('deleteId').value;

    try {
        const response = await fetch(`${API_BASE_URL}/opportunities/${id}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error('Erro ao cancelar oportunidade');
        }

        document.getElementById('deleteModal').style.display = 'none';
        showMessage('Oportunidade cancelada com sucesso!', 'success');
        loadOpportunities();

    } catch (error) {
        console.error('Error deleting opportunity:', error);
        showMessage('Erro ao cancelar oportunidade. Por favor, tente novamente.', 'error');
    }
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
