document.addEventListener('DOMContentLoaded', function() {
    var API_BASE = '/api/redemptions';

    var searchForm = document.getElementById('searchForm');
    var providerInput = document.getElementById('providerName');
    var messageContainer = document.getElementById('messageContainer');
    var resultsSection = document.getElementById('resultsSection');

    searchForm.addEventListener('submit', function(e) {
        e.preventDefault();
        var provider = providerInput.value.trim();
        if (!provider) {
            showMessage('Por favor, insira o nome do parceiro.', 'error');
            return;
        }
        loadPartnerStats(provider);
    });

    function loadPartnerStats(provider) {
        showMessage('Carregando dados...', 'info');

        fetch(API_BASE + '/partner/' + encodeURIComponent(provider) + '/stats')
        .then(function(response) {
            if (!response.ok) {
                if (response.status === 404) {
                    throw new Error('Nenhum beneficio PARTNER encontrado para o fornecedor: ' + provider);
                }
                return response.json().then(function(err) { throw err; });
            }
            return response.json();
        })
        .then(function(stats) {
            clearMessage();
            displayStats(stats);
            displayBenefitDetails(stats.benefitDetails);
            displayRedemptionsTable(stats.recentRedemptions);
            resultsSection.classList.remove('hidden');
        })
        .catch(function(error) {
            resultsSection.classList.add('hidden');
            showMessage('Erro: ' + (error.message || 'Erro desconhecido'), 'error');
        });
    }

    function displayStats(stats) {
        document.getElementById('statTotalBenefits').textContent = stats.totalBenefits || 0;
        document.getElementById('statTotalRedemptions').textContent = stats.totalRedemptions || 0;
        document.getElementById('statTotalPoints').textContent = stats.totalPointsRedeemed || 0;
    }

    function displayBenefitDetails(benefitDetails) {
        var container = document.getElementById('benefitDetailsContainer');

        if (!benefitDetails || benefitDetails.length === 0) {
            container.innerHTML =
                '<div class="empty-state">' +
                    '<h4>Nenhum beneficio encontrado</h4>' +
                    '<p>Este parceiro nao possui beneficios registados.</p>' +
                '</div>';
            return;
        }

        var html = '';
        benefitDetails.forEach(function(detail) {
            var statusClass = detail.active ? 'status-active' : 'status-inactive';
            var statusText = detail.active ? 'Ativo' : 'Inativo';

            html += '<div class="benefit-detail-card">' +
                '<h4>' + escapeHtml(detail.benefitName) + '</h4>' +
                '<div class="benefit-desc">' + escapeHtml(detail.benefitDescription) + '</div>' +
                '<div class="benefit-stats">' +
                    '<span class="points-tag">' + detail.pointsRequired + ' pontos</span>' +
                    '<span class="redemptions-tag">' + detail.totalRedemptions + ' resgates</span>' +
                    '<span class="status-tag ' + statusClass + '">' + statusText + '</span>' +
                    '<div class="benefit-stat">' +
                        '<span class="stat-text">Total pontos resgatados:</span>' +
                        '<span class="stat-value">' + detail.totalPointsRedeemed + '</span>' +
                    '</div>' +
                '</div>' +
            '</div>';
        });

        container.innerHTML = html;
    }

    function displayRedemptionsTable(redemptions) {
        var container = document.getElementById('redemptionsTableContainer');

        if (!redemptions || redemptions.length === 0) {
            container.innerHTML =
                '<div class="empty-state">' +
                    '<h4>Nenhum resgate encontrado</h4>' +
                    '<p>Ainda nao foram feitos resgates dos seus beneficios.</p>' +
                '</div>';
            return;
        }

        var html = '<table class="redemptions-table">' +
            '<thead><tr>' +
                '<th>Voluntario</th>' +
                '<th>Beneficio</th>' +
                '<th>Pontos</th>' +
                '<th>Estado</th>' +
                '<th>Data</th>' +
            '</tr></thead><tbody>';

        redemptions.forEach(function(redemption) {
            var statusClass = redemption.status === 'COMPLETED' ? 'status-completed' : 'status-cancelled';
            var statusText = redemption.status === 'COMPLETED' ? 'Concluido' : 'Cancelado';
            var date = redemption.redeemedAt ? formatDate(redemption.redeemedAt) : '-';

            html += '<tr>' +
                '<td>' + escapeHtml(redemption.volunteerName || '-') + '</td>' +
                '<td>' + escapeHtml(redemption.benefitName || '-') + '</td>' +
                '<td>' + redemption.pointsSpent + '</td>' +
                '<td><span class="' + statusClass + '">' + statusText + '</span></td>' +
                '<td>' + date + '</td>' +
            '</tr>';
        });

        html += '</tbody></table>';
        container.innerHTML = html;
    }

    function formatDate(dateStr) {
        if (!dateStr) return '-';
        var date = new Date(dateStr);
        if (isNaN(date.getTime())) return dateStr;
        var day = String(date.getDate()).padStart(2, '0');
        var month = String(date.getMonth() + 1).padStart(2, '0');
        var year = date.getFullYear();
        var hours = String(date.getHours()).padStart(2, '0');
        var minutes = String(date.getMinutes()).padStart(2, '0');
        return day + '/' + month + '/' + year + ' ' + hours + ':' + minutes;
    }

    function showMessage(text, type) {
        messageContainer.innerHTML = '<div class="message ' + type + '">' + text + '</div>';
        if (type !== 'info') {
            setTimeout(function() {
                clearMessage();
            }, 5000);
        }
    }

    function clearMessage() {
        messageContainer.innerHTML = '';
    }

    function escapeHtml(text) {
        if (!text) return '';
        var div = document.createElement('div');
        div.appendChild(document.createTextNode(text));
        return div.innerHTML;
    }
});
