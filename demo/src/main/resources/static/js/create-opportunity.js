const API_BASE_URL = '/api';

// Load promoters on page load
document.addEventListener('DOMContentLoaded', () => {
    loadPromoters();
    setupFormValidation();
    setupEventListeners();
});

function setupEventListeners() {
    const form = document.getElementById('opportunityForm');
    const resetBtn = document.getElementById('resetBtn');

    form.addEventListener('submit', handleSubmit);
    resetBtn.addEventListener('click', () => {
        form.reset();
        clearAllErrors();
        clearMessage();
    });
}

function setupFormValidation() {
    const inputs = document.querySelectorAll('input, textarea, select');

    inputs.forEach(input => {
        input.addEventListener('blur', () => validateField(input));
        input.addEventListener('input', () => {
            if (input.classList.contains('error')) {
                validateField(input);
            }
        });
    });
}

async function loadPromoters() {
    try {
        const response = await fetch(`${API_BASE_URL}/promoters`);

        if (response.ok) {
            const promoters = await response.json();
            const select = document.getElementById('promoterId');

            promoters.forEach(promoter => {
                const option = document.createElement('option');
                option.value = promoter.id;
                option.textContent = promoter.name;
                select.appendChild(option);
            });
        }
    } catch (error) {
        console.error('Error loading promoters:', error);
    }
}

function validateField(field) {
    const errorElement = document.getElementById(`${field.name}Error`);
    let errorMessage = '';

    // Clear previous error
    field.classList.remove('error');
    errorElement.classList.remove('show');

    // Validate based on field type and constraints
    if (field.hasAttribute('required') && !field.value.trim()) {
        errorMessage = 'Este campo é obrigatório';
    } else if (field.name === 'title') {
        if (field.value.length < 3) {
            errorMessage = 'O título deve ter no mínimo 3 caracteres';
        }
    } else if (field.name === 'description') {
        if (field.value.length < 10) {
            errorMessage = 'A descrição deve ter no mínimo 10 caracteres';
        }
    } else if (field.type === 'number') {
        const value = parseInt(field.value);
        const min = parseInt(field.min);
        const max = parseInt(field.max);

        if (isNaN(value)) {
            errorMessage = 'Por favor, insira um número válido';
        } else if (value < min) {
            errorMessage = `O valor mínimo é ${min}`;
        } else if (max && value > max) {
            errorMessage = `O valor máximo é ${max}`;
        }
    }

    if (errorMessage) {
        field.classList.add('error');
        errorElement.textContent = errorMessage;
        errorElement.classList.add('show');
        return false;
    }

    return true;
}

function validateForm() {
    const form = document.getElementById('opportunityForm');
    const fields = form.querySelectorAll('input, textarea, select');
    let isValid = true;

    fields.forEach(field => {
        if (!validateField(field)) {
            isValid = false;
        }
    });

    return isValid;
}

function clearAllErrors() {
    const errorElements = document.querySelectorAll('.error-message');
    const fields = document.querySelectorAll('input, textarea, select');

    errorElements.forEach(el => el.classList.remove('show'));
    fields.forEach(field => field.classList.remove('error'));
}

function showMessage(message, type = 'success') {
    const messageContainer = document.getElementById('messageContainer');
    messageContainer.innerHTML = `
        <div class="message ${type}">
            ${message}
        </div>
    `;

    // Auto-hide success messages after 5 seconds
    if (type === 'success') {
        setTimeout(() => {
            messageContainer.innerHTML = '';
        }, 5000);
    }
}

function clearMessage() {
    document.getElementById('messageContainer').innerHTML = '';
}

async function handleSubmit(event) {
    event.preventDefault();

    clearMessage();

    if (!validateForm()) {
        showMessage('Por favor, corrija os erros no formulário', 'error');
        return;
    }

    const submitBtn = document.getElementById('submitBtn');
    const originalText = submitBtn.textContent;

    try {
        submitBtn.disabled = true;
        submitBtn.textContent = 'Criando...';

        const formData = new FormData(event.target);
        const data = {
            title: formData.get('title'),
            description: formData.get('description'),
            skills: formData.get('skills'),
            category: formData.get('category'),
            duration: parseInt(formData.get('duration')),
            vacancies: parseInt(formData.get('vacancies')),
            points: parseInt(formData.get('points')),
            promoterId: parseInt(formData.get('promoterId'))
        };

        const response = await fetch(`${API_BASE_URL}/opportunities`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        if (!response.ok) {
            const errorData = await response.json();

            if (errorData.errors) {
                // Handle validation errors
                Object.keys(errorData.errors).forEach(field => {
                    const errorElement = document.getElementById(`${field}Error`);
                    const fieldElement = document.getElementById(field);

                    if (errorElement && fieldElement) {
                        fieldElement.classList.add('error');
                        errorElement.textContent = errorData.errors[field];
                        errorElement.classList.add('show');
                    }
                });
                showMessage('Por favor, corrija os erros no formulário', 'error');
            } else {
                throw new Error(errorData.message || 'Erro ao criar oportunidade');
            }
            return;
        }

        const createdOpportunity = await response.json();

        showMessage(`Oportunidade "${createdOpportunity.title}" criada com sucesso!`, 'success');

        // Reset form
        event.target.reset();
        clearAllErrors();

        // Redirect to opportunities page after 2 seconds
        setTimeout(() => {
            window.location.href = '/opportunities.html';
        }, 2000);

    } catch (error) {
        console.error('Error creating opportunity:', error);
        showMessage(error.message || 'Erro ao criar oportunidade. Por favor, tente novamente.', 'error');
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = originalText;
    }
}
