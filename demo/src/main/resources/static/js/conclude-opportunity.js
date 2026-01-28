document.addEventListener('DOMContentLoaded', function() {
    const promoterSelect = document.getElementById('promoterSelect');
    const opportunitySelect = document.getElementById('opportunitySelect');
    const loadOpportunityBtn = document.getElementById('loadOpportunity');
    const opportunityContainer = document.getElementById('opportunityContainer');
    const participantsContainer = document.getElementById('participantsContainer');
    const concludeBtn = document.getElementById('concludeBtn');
    const summaryContainer = document.getElementById('summaryContainer');
    const messageContainer = document.getElementById('messageContainer');
    const loadingIndicator = document.getElementById('loadingIndicator');

    let currentOpportunity = null;
    let acceptedApplications = [];

    loadPromoters();

    promoterSelect.addEventListener('change', function() {
        const promoterId = this.value;
        if (promoterId) {
            loadOpenOpportunities(promoterId);
            opportunitySelect.disabled = false;
        } else {
            opportunitySelect.innerHTML = '<option value="">Selecione uma oportunidade</option>';
            opportunitySelect.disabled = true;
            loadOpportunityBtn.disabled = true;
        }
    });

    opportunitySelect.addEventListener('change', function() {
        loadOpportunityBtn.disabled = !this.value;
    });

    loadOpportunityBtn.addEventListener('click', loadOpportunityData);
    concludeBtn.addEventListener('click', concludeOpportunity);

    async function loadPromoters() {
        try {
            const response = await fetch('/api/promoters');
            const promoters = await response.json();
            const user = getUser();
            const userEmail = user ? user.email : null;
            let autoSelectPromoter = null;

            promoters.forEach(promoter => {
                const option = document.createElement('option');
                option.value = promoter.id;
                option.textContent = `${promoter.name} - ${promoter.organization}`;
                promoterSelect.appendChild(option);

                // Auto-select if this is the logged in promoter
                if (userEmail && promoter.email === userEmail) {
                    option.selected = true;
                    autoSelectPromoter = promoter;
                    localStorage.setItem('promoterId', promoter.id);
                }
            });

            // Auto-load opportunities if promoter was auto-selected
            if (autoSelectPromoter) {
                loadOpenOpportunities(autoSelectPromoter.id);
                opportunitySelect.disabled = false;
            }
        } catch (error) {
            showMessage('Erro ao carregar promotores: ' + error.message, 'error');
        }
    }

    async function loadOpenOpportunities(promoterId) {
        try {
            showLoading(true);
            const response = await fetch(`/api/opportunities/promoter/${promoterId}/status/OPEN`);
            const opportunities = await response.json();

            opportunitySelect.innerHTML = '<option value="">Selecione uma oportunidade</option>';

            opportunities.forEach(opp => {
                const option = document.createElement('option');
                option.value = opp.id;
                option.textContent = `${opp.title} (${opp.points} pontos)`;
                opportunitySelect.appendChild(option);
            });

            if (opportunities.length === 0) {
                showMessage('Nenhuma oportunidade aberta encontrada para este promotor.', 'info');
            }
        } catch (error) {
            showMessage('Erro ao carregar oportunidades: ' + error.message, 'error');
        } finally {
            showLoading(false);
        }
    }

    async function loadOpportunityData() {
        const opportunityId = opportunitySelect.value;
        if (!opportunityId) return;

        try {
            showLoading(true);
            clearMessage();

            const oppResponse = await fetch(`/api/opportunities/${opportunityId}`);
            currentOpportunity = await oppResponse.json();

            const appsResponse = await fetch(`/api/opportunities/${opportunityId}/accepted-applications`);
            acceptedApplications = await appsResponse.json();

            displayOpportunity();
            displayParticipants();
            opportunityContainer.style.display = 'block';
            summaryContainer.style.display = 'none';

        } catch (error) {
            showMessage('Erro ao carregar dados: ' + error.message, 'error');
        } finally {
            showLoading(false);
        }
    }

    function displayOpportunity() {
        document.getElementById('opportunityTitle').textContent = currentOpportunity.title;
        document.getElementById('opportunityDescription').textContent = currentOpportunity.description;
        document.getElementById('opportunityPoints').textContent = `${currentOpportunity.points} pontos por participante`;
        document.getElementById('opportunityVacancies').textContent = currentOpportunity.vacancies;
        document.getElementById('opportunityCategory').textContent = currentOpportunity.category;

        const statusBadge = document.getElementById('opportunityStatus');
        statusBadge.textContent = currentOpportunity.status === 'OPEN' ? 'Aberta' : 'Concluida';
        statusBadge.className = `status-badge status-${currentOpportunity.status}`;
    }

    function displayParticipants() {
        participantsContainer.innerHTML = '';

        if (acceptedApplications.length === 0) {
            participantsContainer.innerHTML = '<div class="no-participants">Nenhum voluntario aceite para esta oportunidade.</div>';
            concludeBtn.disabled = true;
            return;
        }

        const selectAllRow = document.createElement('div');
        selectAllRow.className = 'select-all-row';
        selectAllRow.innerHTML = `
            <label>
                <input type="checkbox" id="selectAll" checked> Selecionar todos os participantes
            </label>
            <span>Total: ${acceptedApplications.length} voluntarios aceites</span>
        `;
        participantsContainer.appendChild(selectAllRow);

        document.getElementById('selectAll').addEventListener('change', function() {
            const checkboxes = participantsContainer.querySelectorAll('.participant-checkbox input[type="checkbox"]');
            checkboxes.forEach(cb => cb.checked = this.checked);
            updateConcludeButton();
        });

        acceptedApplications.forEach(app => {
            const card = document.createElement('div');
            card.className = 'participant-card';

            const isConfirmed = app.participationConfirmed;

            card.innerHTML = `
                <div class="participant-info">
                    <h4>${app.volunteerName}</h4>
                    <p>${app.volunteerEmail}</p>
                    ${app.motivation ? `<p style="font-style: italic; margin-top: 0.5rem;">"${app.motivation}"</p>` : ''}
                </div>
                ${isConfirmed ?
                    `<span class="confirmed-badge">Participacao ja confirmada - ${app.pointsAwarded} pontos</span>` :
                    `<div class="participant-checkbox">
                        <input type="checkbox" id="app-${app.id}" data-app-id="${app.id}" checked>
                        <label for="app-${app.id}">Confirmar participacao</label>
                    </div>`
                }
            `;
            participantsContainer.appendChild(card);
        });

        participantsContainer.querySelectorAll('.participant-checkbox input[type="checkbox"]').forEach(cb => {
            cb.addEventListener('change', updateConcludeButton);
        });

        updateConcludeButton();
    }

    function updateConcludeButton() {
        const checkboxes = participantsContainer.querySelectorAll('.participant-checkbox input[type="checkbox"]:checked');
        concludeBtn.disabled = checkboxes.length === 0;

        const totalPoints = checkboxes.length * currentOpportunity.points;
        concludeBtn.textContent = `Concluir Oportunidade e Atribuir ${totalPoints} Pontos`;
    }

    async function concludeOpportunity() {
        const checkboxes = participantsContainer.querySelectorAll('.participant-checkbox input[type="checkbox"]:checked');
        const applicationIds = Array.from(checkboxes).map(cb => parseInt(cb.dataset.appId));

        if (applicationIds.length === 0) {
            showMessage('Selecione pelo menos um participante para confirmar.', 'warning');
            return;
        }

        try {
            showLoading(true);
            concludeBtn.disabled = true;

            const response = await fetch(`/api/opportunities/${currentOpportunity.id}/conclude`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    promoterId: parseInt(promoterSelect.value),
                    applicationIds: applicationIds
                })
            });

            if (!response.ok) {
                const error = await response.text();
                throw new Error(error || 'Erro ao concluir oportunidade');
            }

            const result = await response.json();
            displaySummary(result);
            showMessage('Oportunidade concluida com sucesso!', 'success');

            loadOpenOpportunities(promoterSelect.value);
            opportunityContainer.style.display = 'none';

        } catch (error) {
            showMessage('Erro ao concluir oportunidade: ' + error.message, 'error');
            concludeBtn.disabled = false;
        } finally {
            showLoading(false);
        }
    }

    function displaySummary(result) {
        summaryContainer.style.display = 'block';
        const summaryContent = document.getElementById('summaryContent');

        let html = `
            <div class="summary-item">
                <span>Oportunidade:</span>
                <strong>${result.opportunityTitle}</strong>
            </div>
            <div class="summary-item">
                <span>Status:</span>
                <span class="status-badge status-CONCLUDED">Concluida</span>
            </div>
            <div class="summary-item">
                <span>Data de Conclusao:</span>
                <span>${new Date(result.concludedAt).toLocaleString('pt-PT')}</span>
            </div>
            <div class="summary-item">
                <span>Total de Participantes Confirmados:</span>
                <strong>${result.totalParticipantsConfirmed}</strong>
            </div>
            <div class="summary-item">
                <span>Total de Pontos Atribuidos:</span>
                <strong>${result.totalPointsAwarded} pontos</strong>
            </div>
        `;

        if (result.confirmedParticipants && result.confirmedParticipants.length > 0) {
            html += '<h4 style="margin-top: 1rem; margin-bottom: 0.5rem;">Participantes Confirmados:</h4>';
            result.confirmedParticipants.forEach(p => {
                html += `
                    <div class="summary-item">
                        <span>${p.volunteerName} (${p.volunteerEmail})</span>
                        <span>+${p.pointsAwarded} pts (Total: ${p.totalPoints} pts)</span>
                    </div>
                `;
            });
        }

        summaryContent.innerHTML = html;
    }

    function showMessage(message, type) {
        messageContainer.innerHTML = `<div class="message ${type}">${message}</div>`;
    }

    function clearMessage() {
        messageContainer.innerHTML = '';
    }

    function showLoading(show) {
        loadingIndicator.style.display = show ? 'block' : 'none';
    }
});
