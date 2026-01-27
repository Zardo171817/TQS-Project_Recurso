const API_BASE_URL = '/api';

let currentVolunteer = null;
let confirmedParticipations = [];

document.addEventListener('DOMContentLoaded', () => {
    setupEventListeners();
});

function setupEventListeners() {
    document.getElementById('loadPoints').addEventListener('click', loadVolunteerPoints);
    document.getElementById('volunteerEmail').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            loadVolunteerPoints();
        }
    });
}

async function loadVolunteerPoints() {
    const email = document.getElementById('volunteerEmail').value.trim();
    const loadingIndicator = document.getElementById('loadingIndicator');
    const messageContainer = document.getElementById('messageContainer');
    const pointsContent = document.getElementById('pointsContent');

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
        pointsContent.style.display = 'none';

        // Get volunteer points by email
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

        // Get confirmed participations
        const participationsResponse = await fetch(`${API_BASE_URL}/volunteers/${currentVolunteer.id}/confirmed-participations`);

        if (participationsResponse.ok) {
            confirmedParticipations = await participationsResponse.json();
        } else {
            confirmedParticipations = [];
        }

        // Update statistics
        updateStatistics();

        // Display participations
        displayParticipations();

        // Get ranking position
        await loadRankingPosition();

        // Show content
        pointsContent.style.display = 'block';

    } catch (error) {
        console.error('Error loading points:', error);
        showMessage('Erro ao carregar informacoes. Por favor, tente novamente.', 'error');
    } finally {
        loadingIndicator.style.display = 'none';
    }
}

function updateStatistics() {
    const participationsCount = confirmedParticipations.length;
    const totalPoints = currentVolunteer.totalPoints || 0;
    const averagePoints = participationsCount > 0 ? Math.round(totalPoints / participationsCount) : 0;

    document.getElementById('participationsCount').textContent = participationsCount;
    document.getElementById('averagePoints').textContent = averagePoints;
}

async function loadRankingPosition() {
    try {
        const rankingResponse = await fetch(`${API_BASE_URL}/volunteers/ranking`);

        if (rankingResponse.ok) {
            const ranking = await rankingResponse.json();
            const position = ranking.findIndex(v => v.id === currentVolunteer.id) + 1;

            if (position > 0) {
                const rankingPositionEl = document.getElementById('rankingPosition');
                rankingPositionEl.textContent = `#${position} no Ranking`;
                rankingPositionEl.style.display = 'block';
            }
        }
    } catch (error) {
        console.error('Error loading ranking position:', error);
    }
}

function displayParticipations() {
    const container = document.getElementById('participationsContainer');

    if (confirmedParticipations.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <h3>Nenhuma participacao confirmada</h3>
                <p>Ainda nao tem participacoes confirmadas que geraram pontos.</p>
                <p>Continue a candidatar-se a oportunidades de voluntariado!</p>
                <a href="/volunteer-opportunities.html" class="btn btn-primary" style="margin-top: 1rem;">Ver Oportunidades</a>
            </div>
        `;
        return;
    }

    container.innerHTML = confirmedParticipations.map(participation => `
        <div class="participation-card">
            <div class="participation-info">
                <h4>${escapeHtml(participation.opportunityTitle)}</h4>
                <p>Confirmado em: ${formatDate(participation.confirmedAt)}</p>
            </div>
            <div class="participation-points">
                +${participation.pointsAwarded} pts
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
