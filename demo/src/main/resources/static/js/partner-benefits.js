document.addEventListener('DOMContentLoaded', function() {
    const API_BASE = '/api/benefits';

    const benefitForm = document.getElementById('benefitForm');
    const benefitsListContainer = document.getElementById('benefitsListContainer');
    const messageContainer = document.getElementById('messageContainer');
    const loadingIndicator = document.getElementById('loadingIndicator');
    const formTitle = document.getElementById('formTitle');
    const submitBtn = document.getElementById('submitBtn');
    const cancelBtn = document.getElementById('cancelBtn');
    const editBenefitId = document.getElementById('editBenefitId');

    loadPartnerBenefits();

    benefitForm.addEventListener('submit', function(e) {
        e.preventDefault();
        if (!validateForm()) return;

        const id = editBenefitId.value;
        if (id) {
            updateBenefit(id);
        } else {
            createBenefit();
        }
    });

    cancelBtn.addEventListener('click', function() {
        resetForm();
    });

    function validateForm() {
        let valid = true;
        const name = document.getElementById('benefitName').value.trim();
        const description = document.getElementById('benefitDescription').value.trim();
        const points = document.getElementById('benefitPoints').value;
        const provider = document.getElementById('benefitProvider').value.trim();

        document.querySelectorAll('.field-error').forEach(el => el.style.display = 'none');

        if (!name) {
            document.getElementById('nameError').style.display = 'block';
            valid = false;
        }
        if (!description) {
            document.getElementById('descriptionError').style.display = 'block';
            valid = false;
        }
        if (!points || parseInt(points) < 1) {
            document.getElementById('pointsError').style.display = 'block';
            valid = false;
        }
        if (!provider) {
            document.getElementById('providerError').style.display = 'block';
            valid = false;
        }

        return valid;
    }

    function getFormData() {
        return {
            name: document.getElementById('benefitName').value.trim(),
            description: document.getElementById('benefitDescription').value.trim(),
            pointsRequired: parseInt(document.getElementById('benefitPoints').value),
            provider: document.getElementById('benefitProvider').value.trim(),
            imageUrl: document.getElementById('benefitImageUrl').value.trim() || null
        };
    }

    function createBenefit() {
        const data = getFormData();

        fetch(API_BASE + '/partner', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        })
        .then(function(response) {
            if (!response.ok) {
                return response.json().then(function(err) { throw err; });
            }
            return response.json();
        })
        .then(function(benefit) {
            showMessage('Beneficio "' + benefit.name + '" criado com sucesso!', 'success');
            resetForm();
            loadPartnerBenefits();
        })
        .catch(function(error) {
            showMessage('Erro ao criar beneficio: ' + (error.message || 'Erro desconhecido'), 'error');
        });
    }

    function updateBenefit(id) {
        const data = getFormData();

        fetch(API_BASE + '/partner/' + id, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        })
        .then(function(response) {
            if (!response.ok) {
                return response.json().then(function(err) { throw err; });
            }
            return response.json();
        })
        .then(function(benefit) {
            showMessage('Beneficio "' + benefit.name + '" atualizado com sucesso!', 'success');
            resetForm();
            loadPartnerBenefits();
        })
        .catch(function(error) {
            showMessage('Erro ao atualizar beneficio: ' + (error.message || 'Erro desconhecido'), 'error');
        });
    }

    function deleteBenefit(id, name) {
        if (!confirm('Tem certeza que deseja remover o beneficio "' + name + '"?')) return;

        fetch(API_BASE + '/partner/' + id, {
            method: 'DELETE'
        })
        .then(function(response) {
            if (!response.ok) {
                return response.json().then(function(err) { throw err; });
            }
            showMessage('Beneficio "' + name + '" removido com sucesso!', 'success');
            loadPartnerBenefits();
        })
        .catch(function(error) {
            showMessage('Erro ao remover beneficio: ' + (error.message || 'Erro desconhecido'), 'error');
        });
    }

    function editBenefit(benefit) {
        editBenefitId.value = benefit.id;
        document.getElementById('benefitName').value = benefit.name;
        document.getElementById('benefitDescription').value = benefit.description;
        document.getElementById('benefitPoints').value = benefit.pointsRequired;
        document.getElementById('benefitProvider').value = benefit.provider;
        document.getElementById('benefitImageUrl').value = benefit.imageUrl || '';

        formTitle.textContent = 'Editar Beneficio';
        submitBtn.textContent = 'Atualizar Beneficio';
        cancelBtn.style.display = 'inline-block';

        window.scrollTo({ top: 0, behavior: 'smooth' });
    }

    function loadPartnerBenefits() {
        loadingIndicator.style.display = 'block';

        fetch(API_BASE + '/partner')
        .then(function(response) {
            if (!response.ok) throw new Error('Erro ao carregar beneficios');
            return response.json();
        })
        .then(function(benefits) {
            loadingIndicator.style.display = 'none';
            renderBenefitsList(benefits);
            updateStats(benefits);
        })
        .catch(function(error) {
            loadingIndicator.style.display = 'none';
            showMessage('Erro ao carregar beneficios: ' + error.message, 'error');
        });
    }

    function renderBenefitsList(benefits) {
        if (benefits.length === 0) {
            benefitsListContainer.innerHTML =
                '<div class="empty-list">' +
                    '<h4>Nenhum beneficio encontrado</h4>' +
                    '<p>Adicione o primeiro beneficio usando o formulario ao lado.</p>' +
                '</div>';
            return;
        }

        var html = '';
        benefits.forEach(function(benefit) {
            html += '<div class="partner-benefit-card">' +
                '<h4>' + escapeHtml(benefit.name) + '</h4>' +
                '<div class="benefit-desc">' + escapeHtml(benefit.description) + '</div>' +
                '<div class="benefit-meta">' +
                    '<span class="points-tag">' + benefit.pointsRequired + ' pontos</span>' +
                    '<span class="provider-tag">' + escapeHtml(benefit.provider) + '</span>' +
                '</div>' +
                '<div class="card-actions">' +
                    '<button class="btn-edit" onclick=\'editBenefitFromCard(' + JSON.stringify(benefit).replace(/'/g, "\\'") + ')\'>Editar</button>' +
                    '<button class="btn-remove" onclick="deleteBenefitFromCard(' + benefit.id + ', \'' + escapeHtml(benefit.name).replace(/'/g, "\\'") + '\')">Remover</button>' +
                '</div>' +
            '</div>';
        });

        benefitsListContainer.innerHTML = html;
    }

    function updateStats(benefits) {
        document.getElementById('totalPartnerBenefits').textContent = benefits.length;

        var providers = [];
        benefits.forEach(function(b) {
            if (providers.indexOf(b.provider) === -1) {
                providers.push(b.provider);
            }
        });
        document.getElementById('totalProviders').textContent = providers.length;
    }

    function resetForm() {
        benefitForm.reset();
        editBenefitId.value = '';
        formTitle.textContent = 'Adicionar Novo Beneficio';
        submitBtn.textContent = 'Adicionar Beneficio';
        cancelBtn.style.display = 'none';
        document.querySelectorAll('.field-error').forEach(function(el) { el.style.display = 'none'; });
    }

    function showMessage(text, type) {
        messageContainer.innerHTML = '<div class="message ' + type + '">' + text + '</div>';
        setTimeout(function() {
            messageContainer.innerHTML = '';
        }, 5000);
    }

    function escapeHtml(text) {
        if (!text) return '';
        var div = document.createElement('div');
        div.appendChild(document.createTextNode(text));
        return div.innerHTML;
    }

    // Expose functions globally for onclick handlers
    window.editBenefitFromCard = function(benefit) {
        editBenefit(benefit);
    };

    window.deleteBenefitFromCard = function(id, name) {
        deleteBenefit(id, name);
    };
});
