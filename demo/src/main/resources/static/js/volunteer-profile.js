const API_BASE_URL = '/api/volunteers';

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
        phone: document.getElementById('profilePhone').value.trim() || null,
        skills: document.getElementById('profileSkills').value.trim() || null,
        interests: document.getElementById('profileInterests').value.trim() || null,
        availability: document.getElementById('profileAvailability').value.trim() || null,
        bio: document.getElementById('profileBio').value.trim() || null
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
        phone: document.getElementById('profilePhone').value.trim() || null,
        skills: document.getElementById('profileSkills').value.trim() || null,
        interests: document.getElementById('profileInterests').value.trim() || null,
        availability: document.getElementById('profileAvailability').value.trim() || null,
        bio: document.getElementById('profileBio').value.trim() || null
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

    const skillsTags = profile.skills ? profile.skills.split(',').map(s =>
        `<span class="tag skill">${s.trim()}</span>`).join('') : '<span class="tag">Nenhuma</span>';

    const interestsTags = profile.interests ? profile.interests.split(',').map(i =>
        `<span class="tag interest">${i.trim()}</span>`).join('') : '<span class="tag">Nenhum</span>';

    const availabilityTags = profile.availability ? profile.availability.split(',').map(a =>
        `<span class="tag availability">${a.trim()}</span>`).join('') : '<span class="tag">Nao informada</span>';

    const createdAt = profile.profileCreatedAt ?
        new Date(profile.profileCreatedAt).toLocaleDateString('pt-BR') : 'Nao disponivel';

    const updatedAt = profile.profileUpdatedAt ?
        new Date(profile.profileUpdatedAt).toLocaleDateString('pt-BR') : 'Nunca atualizado';

    container.innerHTML = `
        <div class="profile-card">
            <h4>${profile.name}</h4>
            <p class="profile-email">${profile.email}</p>

            <div class="points-badge">${profile.totalPoints || 0} Pontos</div>

            <div class="profile-info">
                <div class="info-item">
                    <label>Telefone</label>
                    <p>${profile.phone || 'Nao informado'}</p>
                </div>
                <div class="info-item">
                    <label>Criado em</label>
                    <p>${createdAt}</p>
                </div>
                <div class="info-item">
                    <label>Ultima Atualizacao</label>
                    <p>${updatedAt}</p>
                </div>
                <div class="info-item">
                    <label>ID</label>
                    <p>#${profile.id}</p>
                </div>
            </div>

            <div class="info-item" style="margin-bottom: 1rem;">
                <label>Competencias</label>
                <div class="tags-container">${skillsTags}</div>
            </div>

            <div class="info-item" style="margin-bottom: 1rem;">
                <label>Interesses</label>
                <div class="tags-container">${interestsTags}</div>
            </div>

            <div class="info-item" style="margin-bottom: 1rem;">
                <label>Disponibilidade</label>
                <div class="tags-container">${availabilityTags}</div>
            </div>

            <div class="profile-bio">
                <label>Biografia</label>
                <p>${profile.bio || 'Nenhuma biografia informada.'}</p>
            </div>

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
    document.getElementById('profilePhone').value = currentProfile.phone || '';
    document.getElementById('profileSkills').value = currentProfile.skills || '';
    document.getElementById('profileInterests').value = currentProfile.interests || '';
    document.getElementById('profileAvailability').value = currentProfile.availability || '';
    document.getElementById('profileBio').value = currentProfile.bio || '';

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
    document.getElementById('formTitle').textContent = 'Criar Novo Perfil';
    document.getElementById('submitBtn').textContent = 'Criar Perfil';
    document.getElementById('cancelBtn').style.display = 'none';

    hideFieldError('nameError');
    hideFieldError('emailError');
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
