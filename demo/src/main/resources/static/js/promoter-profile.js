const API_BASE_URL = '/api/promoters';

let currentProfile = null;

document.addEventListener('DOMContentLoaded', function() {
    setupEventListeners();
});

function setupEventListeners() {
    document.getElementById('profileForm').addEventListener('submit', handleFormSubmit);
    document.getElementById('searchBtn').addEventListener('click', handleSearch);
    document.getElementById('cancelBtn').addEventListener('click', resetForm);

    document.getElementById('searchEmail').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            handleSearch();
        }
    });
}

async function handleFormSubmit(event) {
    event.preventDefault();

    if (!validateForm()) {
        return;
    }

    const editId = document.getElementById('editProfileId').value;

    if (editId) {
        await updateProfile(editId);
    } else {
        await createProfile();
    }
}

function validateForm() {
    let isValid = true;
    const editId = document.getElementById('editProfileId').value;

    const name = document.getElementById('profileName').value.trim();
    if (name.length < 2 || name.length > 100) {
        showFieldError('nameError');
        isValid = false;
    } else {
        hideFieldError('nameError');
    }

    if (!editId) {
        const email = document.getElementById('profileEmail').value.trim();
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            showFieldError('emailError');
            isValid = false;
        } else {
            hideFieldError('emailError');
        }
    }

    const organization = document.getElementById('profileOrganization').value.trim();
    if (organization.length < 2 || organization.length > 200) {
        showFieldError('orgError');
        isValid = false;
    } else {
        hideFieldError('orgError');
    }

    return isValid;
}

function showFieldError(errorId) {
    const errorElement = document.getElementById(errorId);
    if (errorElement) {
        errorElement.style.display = 'block';
    }
}

function hideFieldError(errorId) {
    const errorElement = document.getElementById(errorId);
    if (errorElement) {
        errorElement.style.display = 'none';
    }
}

async function createProfile() {
    const profileData = {
        name: document.getElementById('profileName').value.trim(),
        email: document.getElementById('profileEmail').value.trim(),
        organization: document.getElementById('profileOrganization').value.trim(),
        description: document.getElementById('profileDescription').value.trim() || null,
        phone: document.getElementById('profilePhone').value.trim() || null,
        website: document.getElementById('profileWebsite').value.trim() || null,
        address: document.getElementById('profileAddress').value.trim() || null,
        logoUrl: document.getElementById('profileLogoUrl').value.trim() || null,
        organizationType: document.getElementById('profileOrgType').value || null,
        areaOfActivity: document.getElementById('profileAreaOfActivity').value.trim() || null,
        foundedYear: document.getElementById('profileFoundedYear').value.trim() || null,
        numberOfEmployees: document.getElementById('profileEmployees').value || null,
        socialMedia: document.getElementById('profileSocialMedia').value.trim() || null
    };

    try {
        const response = await fetch(`${API_BASE_URL}/profile`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(profileData)
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Erro ao criar perfil');
        }

        const createdProfile = await response.json();
        showMessage('Perfil criado com sucesso!', 'success');
        displayProfile(createdProfile);
        resetForm();

    } catch (error) {
        showMessage(error.message, 'error');
    }
}

async function updateProfile(id) {
    const profileData = {
        name: document.getElementById('profileName').value.trim(),
        organization: document.getElementById('profileOrganization').value.trim(),
        description: document.getElementById('profileDescription').value.trim() || null,
        phone: document.getElementById('profilePhone').value.trim() || null,
        website: document.getElementById('profileWebsite').value.trim() || null,
        address: document.getElementById('profileAddress').value.trim() || null,
        logoUrl: document.getElementById('profileLogoUrl').value.trim() || null,
        organizationType: document.getElementById('profileOrgType').value || null,
        areaOfActivity: document.getElementById('profileAreaOfActivity').value.trim() || null,
        foundedYear: document.getElementById('profileFoundedYear').value.trim() || null,
        numberOfEmployees: document.getElementById('profileEmployees').value || null,
        socialMedia: document.getElementById('profileSocialMedia').value.trim() || null
    };

    try {
        const response = await fetch(`${API_BASE_URL}/profile/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(profileData)
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Erro ao atualizar perfil');
        }

        const updatedProfile = await response.json();
        showMessage('Perfil atualizado com sucesso!', 'success');
        displayProfile(updatedProfile);
        resetForm();

    } catch (error) {
        showMessage(error.message, 'error');
    }
}

async function handleSearch() {
    const email = document.getElementById('searchEmail').value.trim();

    if (!email) {
        showMessage('Por favor, insira um email para buscar.', 'error');
        return;
    }

    showLoading(true);

    try {
        const response = await fetch(`${API_BASE_URL}/profile/email/${encodeURIComponent(email)}`);

        if (!response.ok) {
            if (response.status === 404) {
                throw new Error('Perfil nao encontrado com este email.');
            }
            const errorData = await response.json();
            throw new Error(errorData.message || 'Erro ao buscar perfil');
        }

        const profile = await response.json();
        displayProfile(profile);
        showMessage('Perfil encontrado!', 'success');

    } catch (error) {
        showMessage(error.message, 'error');
        displayEmptyProfile();
    } finally {
        showLoading(false);
    }
}

function displayProfile(profile) {
    currentProfile = profile;
    const container = document.getElementById('profileDisplayContainer');

    const areaTags = profile.areaOfActivity ? profile.areaOfActivity.split(',').map(a =>
        `<span class="tag area">${a.trim()}</span>`).join('') : '<span class="tag">Nao informada</span>';

    const createdAt = profile.profileCreatedAt ?
        new Date(profile.profileCreatedAt).toLocaleDateString('pt-BR') : 'Nao disponivel';

    const updatedAt = profile.profileUpdatedAt ?
        new Date(profile.profileUpdatedAt).toLocaleDateString('pt-BR') : 'Nunca atualizado';

    container.innerHTML = `
        <div class="profile-card">
            <h4>${profile.name}</h4>
            <p class="profile-email">${profile.email}</p>
            <p class="profile-org">${profile.organization}</p>

            ${profile.organizationType ? `<div class="org-badge">${profile.organizationType}</div>` : ''}

            <div class="profile-info">
                <div class="info-item">
                    <label>Telefone</label>
                    <p>${profile.phone || 'Nao informado'}</p>
                </div>
                <div class="info-item">
                    <label>Website</label>
                    <p>${profile.website ? `<a href="${profile.website}" target="_blank">${profile.website}</a>` : 'Nao informado'}</p>
                </div>
                <div class="info-item">
                    <label>Ano de Fundacao</label>
                    <p>${profile.foundedYear || 'Nao informado'}</p>
                </div>
                <div class="info-item">
                    <label>Colaboradores</label>
                    <p>${profile.numberOfEmployees || 'Nao informado'}</p>
                </div>
                <div class="info-item">
                    <label>Criado em</label>
                    <p>${createdAt}</p>
                </div>
                <div class="info-item">
                    <label>Ultima Atualizacao</label>
                    <p>${updatedAt}</p>
                </div>
            </div>

            <div class="info-item" style="margin-bottom: 1rem;">
                <label>Endereco</label>
                <p>${profile.address || 'Nao informado'}</p>
            </div>

            <div class="info-item" style="margin-bottom: 1rem;">
                <label>Areas de Atuacao</label>
                <div class="tags-container">${areaTags}</div>
            </div>

            <div class="profile-description">
                <label>Descricao</label>
                <p>${profile.description || 'Nenhuma descricao informada.'}</p>
            </div>

            ${profile.socialMedia ? `
            <div class="info-item" style="margin-bottom: 1rem;">
                <label>Redes Sociais</label>
                <p>${profile.socialMedia}</p>
            </div>
            ` : ''}

            <div class="card-actions">
                <button class="btn-edit" onclick="editProfile(${profile.id})">Editar Perfil</button>
                <button class="btn-delete" onclick="deleteProfile(${profile.id})">Excluir Perfil</button>
            </div>
        </div>
    `;
}

function displayEmptyProfile() {
    const container = document.getElementById('profileDisplayContainer');
    container.innerHTML = `
        <div class="empty-profile">
            <h4>Nenhum perfil carregado</h4>
            <p>Busque um perfil existente ou crie um novo usando o formulario ao lado.</p>
        </div>
    `;
    currentProfile = null;
}

function editProfile(id) {
    if (!currentProfile || currentProfile.id !== id) {
        showMessage('Erro ao carregar dados do perfil.', 'error');
        return;
    }

    document.getElementById('editProfileId').value = currentProfile.id;
    document.getElementById('profileName').value = currentProfile.name || '';
    document.getElementById('profileEmail').value = currentProfile.email || '';
    document.getElementById('profileOrganization').value = currentProfile.organization || '';
    document.getElementById('profileDescription').value = currentProfile.description || '';
    document.getElementById('profilePhone').value = currentProfile.phone || '';
    document.getElementById('profileWebsite').value = currentProfile.website || '';
    document.getElementById('profileAddress').value = currentProfile.address || '';
    document.getElementById('profileLogoUrl').value = currentProfile.logoUrl || '';
    document.getElementById('profileOrgType').value = currentProfile.organizationType || '';
    document.getElementById('profileAreaOfActivity').value = currentProfile.areaOfActivity || '';
    document.getElementById('profileFoundedYear').value = currentProfile.foundedYear || '';
    document.getElementById('profileEmployees').value = currentProfile.numberOfEmployees || '';
    document.getElementById('profileSocialMedia').value = currentProfile.socialMedia || '';

    document.getElementById('emailGroup').style.display = 'none';

    document.getElementById('formTitle').textContent = 'Editar Perfil';
    document.getElementById('submitBtn').textContent = 'Atualizar Perfil';
    document.getElementById('cancelBtn').style.display = 'inline-block';

    document.getElementById('profileForm').scrollIntoView({ behavior: 'smooth' });
}

async function deleteProfile(id) {
    if (!confirm('Tem certeza que deseja excluir este perfil? Esta acao nao pode ser desfeita.')) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/profile/${id}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Erro ao excluir perfil');
        }

        showMessage('Perfil excluido com sucesso!', 'success');
        displayEmptyProfile();
        resetForm();

    } catch (error) {
        showMessage(error.message, 'error');
    }
}

function resetForm() {
    document.getElementById('profileForm').reset();
    document.getElementById('editProfileId').value = '';
    document.getElementById('emailGroup').style.display = 'block';
    document.getElementById('formTitle').textContent = 'Criar Perfil de Organizacao';
    document.getElementById('submitBtn').textContent = 'Criar Perfil';
    document.getElementById('cancelBtn').style.display = 'none';

    hideFieldError('nameError');
    hideFieldError('emailError');
    hideFieldError('orgError');
}

function showMessage(message, type) {
    const container = document.getElementById('messageContainer');
    container.innerHTML = `<div class="message ${type}">${message}</div>`;

    setTimeout(() => {
        container.innerHTML = '';
    }, 5000);
}

function showLoading(show) {
    const loadingIndicator = document.getElementById('loadingIndicator');
    if (loadingIndicator) {
        loadingIndicator.style.display = show ? 'block' : 'none';
    }
}
