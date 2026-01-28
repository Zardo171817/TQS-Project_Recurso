const API_BASE_URL = '/api';

let currentVolunteer = null;
let pointsHistory = [];
let filteredHistory = [];

document.addEventListener('DOMContentLoaded', () => {
    setupEventListeners();
    // Auto-load history if user is logged in as volunteer
    autoLoadHistory();
});

function autoLoadHistory() {
    const user = getUser();
    if (user && user.userType === 'VOLUNTEER' && user.email) {
        document.getElementById('volunteerEmail').value = user.email;
        loadPointsHistory();
    }
}

function setupEventListeners() {
    document.getElementById('loadHistory').addEventListener('click', loadPointsHistory);
    document.getElementById('volunteerEmail').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            loadPointsHistory();
        }
    });
    document.getElementById('categoryFilter').addEventListener('change', applyFilters);
    document.getElementById('sortOrder').addEventListener('change', applyFilters);
}

async function loadPointsHistory() {
    const email = document.getElementById('volunteerEmail').value.trim();
    const loadingIndicator = document.getElementById('loadingIndicator');
    const messageContainer = document.getElementById('messageContainer');
    const historyContent = document.getElementById('historyContent');

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
        messageContainer.innerHTML = '';
        historyContent.style.display = 'none';

        // Get volunteer by email
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
        document.getElementById('totalPoints').textContent = currentVolunteer.totalPoints || 0;

        // Get points history
        const historyResponse = await fetch(`${API_BASE_URL}/volunteers/${currentVolunteer.id}/points-history`);

        if (historyResponse.ok) {
            pointsHistory = await historyResponse.json();
        } else {
            pointsHistory = [];
        }

        // Populate category filter
        populateCategoryFilter();

        // Update statistics
        updateStatistics();

        // Apply filters and display history
        applyFilters();

        // Show content
        historyContent.style.display = 'block';

    } catch (error) {
        console.error('Error loading points history:', error);
        showMessage('Erro ao carregar historico de pontos. Por favor, tente novamente.', 'error');
    } finally {
        loadingIndicator.style.display = 'none';
    }
}

function populateCategoryFilter() {
    const categoryFilter = document.getElementById('categoryFilter');
    const categories = [...new Set(pointsHistory.map(item => item.opportunityCategory))];

    // Clear existing options except the first one
    categoryFilter.innerHTML = '<option value="">Todas as categorias</option>';

    categories.forEach(category => {
        const option = document.createElement('option');
        option.value = category;
        option.textContent = category;
        categoryFilter.appendChild(option);
    });
}

function updateStatistics() {
    const opportunitiesCount = pointsHistory.length;
    const totalPointsEarned = pointsHistory.reduce((sum, item) => sum + (item.pointsAwarded || 0), 0);
    const averagePoints = opportunitiesCount > 0 ? Math.round(totalPointsEarned / opportunitiesCount) : 0;

    document.getElementById('opportunitiesCount').textContent = opportunitiesCount;
    document.getElementById('totalPointsEarned').textContent = totalPointsEarned;
    document.getElementById('averagePoints').textContent = averagePoints;
}

function applyFilters() {
    const categoryFilter = document.getElementById('categoryFilter').value;
    const sortOrder = document.getElementById('sortOrder').value;

    // Filter by category
    filteredHistory = pointsHistory.filter(item => {
        if (categoryFilter && item.opportunityCategory !== categoryFilter) {
            return false;
        }
        return true;
    });

    // Sort
    filteredHistory.sort((a, b) => {
        switch (sortOrder) {
            case 'date-desc':
                return new Date(b.confirmedAt || 0) - new Date(a.confirmedAt || 0);
            case 'date-asc':
                return new Date(a.confirmedAt || 0) - new Date(b.confirmedAt || 0);
            case 'points-desc':
                return (b.pointsAwarded || 0) - (a.pointsAwarded || 0);
            case 'points-asc':
                return (a.pointsAwarded || 0) - (b.pointsAwarded || 0);
            default:
                return 0;
        }
    });

    displayHistory();
}

function displayHistory() {
    const container = document.getElementById('historyContainer');

    if (filteredHistory.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <h3>Nenhum historico de pontos encontrado</h3>
                <p>Ainda nao tem participacoes confirmadas que geraram pontos.</p>
                <p>Continue a candidatar-se e participar em oportunidades de voluntariado!</p>
                <a href="/opportunities.html" class="btn btn-primary" style="margin-top: 1rem;">Ver Oportunidades</a>
            </div>
        `;
        return;
    }

    container.innerHTML = filteredHistory.map(item => `
        <div class="history-card">
            <div class="history-info">
                <span class="category-badge">${escapeHtml(item.opportunityCategory || 'Sem categoria')}</span>
                <h4>${escapeHtml(item.opportunityTitle)}</h4>
                <p class="description">${escapeHtml(item.opportunityDescription || '')}</p>
                <p class="date">Confirmado em: ${formatDate(item.confirmedAt)}</p>
            </div>
            <div class="history-points">
                +${item.pointsAwarded} pts
            </div>
        </div>
    `).join('');
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
