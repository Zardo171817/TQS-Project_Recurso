const API_BASE_URL = '/api';

let currentVolunteer = null;
let allBenefits = [];
let pendingRedeemBenefitId = null;

document.addEventListener('DOMContentLoaded', () => {
    setupEventListeners();
    // Auto-load redeem page if user is logged in as volunteer
    autoLoadRedeemPage();
});

function autoLoadRedeemPage() {
    const user = getUser();
    if (user && user.userType === 'VOLUNTEER' && user.email) {
        document.getElementById('volunteerEmail').value = user.email;
        loadRedeemPage();
    }
}

function setupEventListeners() {
    document.getElementById('loadRedeemPage').addEventListener('click', loadRedeemPage);
    document.getElementById('volunteerEmail').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            loadRedeemPage();
        }
    });

    document.getElementById('confirmRedeem').addEventListener('click', confirmRedeem);
    document.getElementById('cancelRedeem').addEventListener('click', closeModal);
}

async function loadRedeemPage() {
    const email = document.getElementById('volunteerEmail').value.trim();
    const loadingIndicator = document.getElementById('loadingIndicator');
    const messageContainer = document.getElementById('messageContainer');
    const redeemContent = document.getElementById('redeemContent');

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
        redeemContent.style.display = 'none';

        const volunteerResponse = await fetch(`${API_BASE_URL}/volunteers/email/${encodeURIComponent(email)}`);

        if (!volunteerResponse.ok) {
            if (volunteerResponse.status === 404) {
                showMessage('Voluntario nao encontrado. Verifique o email ou candidate-se a uma oportunidade primeiro.', 'error');
                return;
            }
            throw new Error('Erro ao verificar voluntario');
        }

        currentVolunteer = await volunteerResponse.json();

        document.getElementById('volunteerName').textContent = currentVolunteer.name;
        document.getElementById('volunteerEmailDisplay').textContent = currentVolunteer.email;
        document.getElementById('volunteerPoints').textContent = `${currentVolunteer.totalPoints || 0} pontos`;

        const catalogResponse = await fetch(`${API_BASE_URL}/benefits/volunteer/${currentVolunteer.id}/catalog`);

        if (!catalogResponse.ok) {
            throw new Error('Erro ao carregar catalogo de beneficios');
        }

        allBenefits = await catalogResponse.json();

        await loadStatistics();
        displayBenefits();
        await loadHistory();

        redeemContent.style.display = 'block';

    } catch (error) {
        console.error('Error loading redeem page:', error);
        showMessage('Erro ao carregar pagina. Por favor, tente novamente.', 'error');
    } finally {
        loadingIndicator.style.display = 'none';
    }
}

async function loadStatistics() {
    try {
        const [totalSpentResponse, countResponse] = await Promise.all([
            fetch(`${API_BASE_URL}/redemptions/volunteer/${currentVolunteer.id}/total-spent`),
            fetch(`${API_BASE_URL}/redemptions/volunteer/${currentVolunteer.id}/count`)
        ]);

        const totalSpent = totalSpentResponse.ok ? await totalSpentResponse.json() : 0;
        const totalCount = countResponse.ok ? await countResponse.json() : 0;

        const volunteerPoints = currentVolunteer.totalPoints || 0;
        const affordableCount = allBenefits.filter(b => b.pointsRequired <= volunteerPoints).length;

        document.getElementById('availablePoints').textContent = volunteerPoints;
        document.getElementById('totalSpent').textContent = totalSpent;
        document.getElementById('totalRedeemed').textContent = totalCount;
        document.getElementById('affordableCount').textContent = affordableCount;
    } catch (error) {
        console.error('Error loading statistics:', error);
    }
}

function displayBenefits() {
    const container = document.getElementById('benefitsContainer');
    const resultsCount = document.getElementById('resultsCount');
    const volunteerPoints = currentVolunteer.totalPoints || 0;

    const sortedBenefits = [...allBenefits].sort((a, b) => a.pointsRequired - b.pointsRequired);

    resultsCount.textContent = `Mostrando ${sortedBenefits.length} beneficio${sortedBenefits.length !== 1 ? 's' : ''}`;

    if (sortedBenefits.length === 0) {
        container.innerHTML = `
            <div class="empty-state" style="grid-column: 1 / -1;">
                <h3>Nenhum beneficio disponivel</h3>
                <p>Nao ha beneficios ativos no momento.</p>
            </div>
        `;
        return;
    }

    container.innerHTML = sortedBenefits.map(benefit => {
        const canAfford = benefit.pointsRequired <= volunteerPoints;
        const affordableClass = canAfford ? 'affordable' : 'not-affordable';
        const pointsClass = canAfford ? '' : 'not-enough';
        const btnClass = canAfford ? 'can-redeem' : 'cannot-redeem';
        const btnText = canAfford ? 'Resgatar' : `Faltam ${benefit.pointsRequired - volunteerPoints} pts`;
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
                        <span>Fornecedor: ${escapeHtml(benefit.provider)}</span>
                    </div>
                    <div class="benefit-footer">
                        <div class="points-required ${pointsClass}">
                            ${benefit.pointsRequired} pontos
                        </div>
                        <button class="btn-redeem ${btnClass}"
                                ${canAfford ? `onclick="openRedeemModal(${benefit.id}, '${escapeHtml(benefit.name)}', ${benefit.pointsRequired})"` : 'disabled'}>
                            ${btnText}
                        </button>
                    </div>
                </div>
            </div>
        `;
    }).join('');
}

function openRedeemModal(benefitId, benefitName, pointsRequired) {
    pendingRedeemBenefitId = benefitId;
    const modal = document.getElementById('confirmModal');
    const message = document.getElementById('confirmMessage');

    message.innerHTML = `Deseja resgatar <strong>${benefitName}</strong> por <strong>${pointsRequired} pontos</strong>?<br><br>Pontos atuais: <strong>${currentVolunteer.totalPoints}</strong><br>Pontos apos resgate: <strong>${currentVolunteer.totalPoints - pointsRequired}</strong>`;

    modal.classList.add('active');
}

function closeModal() {
    pendingRedeemBenefitId = null;
    document.getElementById('confirmModal').classList.remove('active');
}

async function confirmRedeem() {
    if (!pendingRedeemBenefitId || !currentVolunteer) {
        return;
    }

    const confirmBtn = document.getElementById('confirmRedeem');
    confirmBtn.disabled = true;
    confirmBtn.textContent = 'Processando...';

    try {
        const response = await fetch(`${API_BASE_URL}/redemptions`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                volunteerId: currentVolunteer.id,
                benefitId: pendingRedeemBenefitId
            })
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Erro ao resgatar beneficio');
        }

        const redemption = await response.json();

        currentVolunteer.totalPoints = redemption.remainingPoints;
        document.getElementById('volunteerPoints').textContent = `${currentVolunteer.totalPoints} pontos`;

        closeModal();
        showMessage(`Beneficio "${redemption.benefitName}" resgatado com sucesso! Foram gastos ${redemption.pointsSpent} pontos.`, 'success');

        await loadStatistics();
        displayBenefits();
        await loadHistory();

    } catch (error) {
        console.error('Error redeeming benefit:', error);
        closeModal();
        showMessage(error.message || 'Erro ao resgatar beneficio. Por favor, tente novamente.', 'error');
    } finally {
        confirmBtn.disabled = false;
        confirmBtn.textContent = 'Confirmar Resgate';
    }
}

async function loadHistory() {
    try {
        const response = await fetch(`${API_BASE_URL}/redemptions/volunteer/${currentVolunteer.id}`);

        if (!response.ok) {
            return;
        }

        const redemptions = await response.json();
        const historySection = document.getElementById('historySection');
        const historyContainer = document.getElementById('historyContainer');

        if (redemptions.length === 0) {
            historySection.style.display = 'none';
            return;
        }

        historySection.style.display = 'block';

        historyContainer.innerHTML = redemptions.map(redemption => {
            const date = new Date(redemption.redeemedAt);
            const formattedDate = formatDate(date);

            return `
                <div class="history-card">
                    <div class="history-info">
                        <h4>${escapeHtml(redemption.benefitName)}</h4>
                        <p>${escapeHtml(redemption.benefitProvider)} - ${formattedDate}</p>
                    </div>
                    <div class="history-points">-${redemption.pointsSpent} pontos</div>
                </div>
            `;
        }).join('');

    } catch (error) {
        console.error('Error loading history:', error);
    }
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

function formatDate(date) {
    if (!(date instanceof Date) || isNaN(date)) return '';
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${day}/${month}/${year} ${hours}:${minutes}`;
}
