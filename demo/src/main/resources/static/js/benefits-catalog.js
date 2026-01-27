const API_BASE_URL = '/api';

let currentVolunteer = null;
let allBenefits = [];
let filteredBenefits = [];
let currentSort = 'points-asc';

document.addEventListener('DOMContentLoaded', () => {
    setupEventListeners();
});

function setupEventListeners() {
    document.getElementById('loadCatalog').addEventListener('click', loadCatalog);
    document.getElementById('volunteerEmail').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            loadCatalog();
        }
    });

    document.getElementById('applyFilters').addEventListener('click', applyFilters);
    document.getElementById('clearFilters').addEventListener('click', clearFilters);

    document.getElementById('categoryFilter').addEventListener('change', applyFilters);
    document.getElementById('providerFilter').addEventListener('change', applyFilters);
    document.getElementById('affordableFilter').addEventListener('change', applyFilters);

    document.querySelectorAll('.sort-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.sort-btn').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            currentSort = btn.dataset.sort;
            sortAndDisplayBenefits();
        });
    });
}

async function loadCatalog() {
    const email = document.getElementById('volunteerEmail').value.trim();
    const loadingIndicator = document.getElementById('loadingIndicator');
    const messageContainer = document.getElementById('messageContainer');
    const catalogContent = document.getElementById('catalogContent');

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
        catalogContent.style.display = 'none';

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
        document.getElementById('volunteerPoints').textContent = `${currentVolunteer.totalPoints || 0} pontos`;

        // Get catalog for volunteer
        const catalogResponse = await fetch(`${API_BASE_URL}/benefits/volunteer/${currentVolunteer.id}/catalog`);

        if (!catalogResponse.ok) {
            throw new Error('Erro ao carregar catalogo de beneficios');
        }

        allBenefits = await catalogResponse.json();
        filteredBenefits = [...allBenefits];

        // Load providers for filter
        await loadProviders();

        // Update statistics
        updateStatistics();

        // Display benefits
        sortAndDisplayBenefits();

        // Show content
        catalogContent.style.display = 'block';

    } catch (error) {
        console.error('Error loading catalog:', error);
        showMessage('Erro ao carregar catalogo. Por favor, tente novamente.', 'error');
    } finally {
        loadingIndicator.style.display = 'none';
    }
}

async function loadProviders() {
    try {
        const response = await fetch(`${API_BASE_URL}/benefits/providers`);
        if (response.ok) {
            const providers = await response.json();
            const providerFilter = document.getElementById('providerFilter');
            providerFilter.innerHTML = '<option value="">Todos os fornecedores</option>';
            providers.forEach(provider => {
                const option = document.createElement('option');
                option.value = provider;
                option.textContent = provider;
                providerFilter.appendChild(option);
            });
        }
    } catch (error) {
        console.error('Error loading providers:', error);
    }
}

function updateStatistics() {
    const volunteerPoints = currentVolunteer.totalPoints || 0;

    const totalCount = allBenefits.length;
    const affordableCount = allBenefits.filter(b => b.pointsRequired <= volunteerPoints).length;
    const uaCount = allBenefits.filter(b => b.category === 'UA').length;
    const partnerCount = allBenefits.filter(b => b.category === 'PARTNER').length;

    document.getElementById('totalBenefits').textContent = totalCount;
    document.getElementById('affordableBenefits').textContent = affordableCount;
    document.getElementById('uaBenefits').textContent = uaCount;
    document.getElementById('partnerBenefits').textContent = partnerCount;
}

function applyFilters() {
    const categoryFilter = document.getElementById('categoryFilter').value;
    const providerFilter = document.getElementById('providerFilter').value;
    const affordableFilter = document.getElementById('affordableFilter').value;
    const volunteerPoints = currentVolunteer.totalPoints || 0;

    filteredBenefits = allBenefits.filter(benefit => {
        // Category filter
        if (categoryFilter && benefit.category !== categoryFilter) {
            return false;
        }

        // Provider filter
        if (providerFilter && !benefit.provider.toLowerCase().includes(providerFilter.toLowerCase())) {
            return false;
        }

        // Affordable filter
        if (affordableFilter === 'affordable' && benefit.pointsRequired > volunteerPoints) {
            return false;
        }
        if (affordableFilter === 'not-affordable' && benefit.pointsRequired <= volunteerPoints) {
            return false;
        }

        return true;
    });

    sortAndDisplayBenefits();
}

function clearFilters() {
    document.getElementById('categoryFilter').value = '';
    document.getElementById('providerFilter').value = '';
    document.getElementById('affordableFilter').value = '';

    filteredBenefits = [...allBenefits];
    sortAndDisplayBenefits();
}

function sortAndDisplayBenefits() {
    const sortedBenefits = [...filteredBenefits];

    if (currentSort === 'points-asc') {
        sortedBenefits.sort((a, b) => a.pointsRequired - b.pointsRequired);
    } else if (currentSort === 'points-desc') {
        sortedBenefits.sort((a, b) => b.pointsRequired - a.pointsRequired);
    }

    displayBenefits(sortedBenefits);
}

function displayBenefits(benefits) {
    const container = document.getElementById('benefitsContainer');
    const resultsCount = document.getElementById('resultsCount');

    resultsCount.textContent = `Mostrando ${benefits.length} beneficio${benefits.length !== 1 ? 's' : ''}`;

    if (benefits.length === 0) {
        container.innerHTML = `
            <div class="empty-state" style="grid-column: 1 / -1;">
                <h3>Nenhum beneficio encontrado</h3>
                <p>Nao ha beneficios disponiveis com os filtros selecionados.</p>
                <button class="btn btn-primary" onclick="clearFilters()" style="margin-top: 1rem;">Limpar Filtros</button>
            </div>
        `;
        return;
    }

    const volunteerPoints = currentVolunteer.totalPoints || 0;

    container.innerHTML = benefits.map(benefit => {
        const canAfford = benefit.pointsRequired <= volunteerPoints;
        const affordableClass = canAfford ? 'affordable' : 'not-affordable';
        const pointsClass = canAfford ? '' : 'not-enough';
        const statusBadgeClass = canAfford ? 'can-redeem' : 'cannot-redeem';
        const statusText = canAfford ? 'Disponivel para resgate' : `Faltam ${benefit.pointsRequired - volunteerPoints} pontos`;
        const categoryClass = benefit.category.toLowerCase();

        return `
            <div class="benefit-card ${affordableClass}">
                <div class="benefit-image">
                    ${benefit.imageUrl ? `<img src="${escapeHtml(benefit.imageUrl)}" alt="${escapeHtml(benefit.name)}" style="width: 100%; height: 100%; object-fit: cover;">` : getBenefitIcon(benefit.category)}
                </div>
                <div class="benefit-content">
                    <div class="benefit-header">
                        <h4>${escapeHtml(benefit.name)}</h4>
                        <span class="category-badge ${categoryClass}">${benefit.category}</span>
                    </div>
                    <p class="benefit-description">${escapeHtml(benefit.description)}</p>
                    <div class="benefit-provider">
                        <span>📍</span>
                        <span>${escapeHtml(benefit.provider)}</span>
                    </div>
                    <div class="benefit-footer">
                        <div class="points-required ${pointsClass}">
                            ${benefit.pointsRequired} pontos
                        </div>
                        <span class="status-badge ${statusBadgeClass}">${statusText}</span>
                    </div>
                </div>
            </div>
        `;
    }).join('');
}

function getBenefitIcon(category) {
    if (category === 'UA') {
        return '🎓';
    }
    return '🤝';
}

function showMessage(message, type) {
    const messageContainer = document.getElementById('messageContainer');
    messageContainer.innerHTML = `
        <div class="message ${type}">
            ${message}
        </div>
    `;
    if (type === 'success') {
        setTimeout(() => {
            messageContainer.innerHTML = '';
        }, 5000);
    }
}

function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}
