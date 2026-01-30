import http from 'k6/http';
import { check } from 'k6';

export function randomString(length = 10) {
    const charset = 'abcdefghijklmnopqrstuvwxyz0123456789';
    let result = '';
    for (let i = 0; i < length; i++) {
        result += charset.charAt(Math.floor(Math.random() * charset.length));
    }
    return result;
}

export function randomEmail() {
    return `user_${randomString(8)}@test.com`;
}

export function randomInt(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

export function randomChoice(array) {
    return array[Math.floor(Math.random() * array.length)];
}

export function registerUser(baseUrl, userData) {
    const payload = JSON.stringify({
        email: userData.email,
        password: userData.password,
        name: userData.name,
        userType: userData.userType
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const response = http.post(`${baseUrl}/api/auth/register`, payload, params);

    check(response, {
        'registration successful': (r) => r.status === 201 || r.status === 200,
    });

    return response;
}

export function loginUser(baseUrl, email, password) {
    const payload = JSON.stringify({
        email: email,
        password: password
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const response = http.post(`${baseUrl}/api/auth/login`, payload, params);

    const success = check(response, {
        'login successful': (r) => r.status === 200,
        'received user data': (r) => r.json('id') !== undefined,
    });

    if (success && response.status === 200) {
        return response.json();
    }

    return null;
}

export function createOpportunity(baseUrl, promoterId, data = {}) {
    const opportunity = {
        title: data.title || `Opportunity ${randomString(6)}`,
        description: data.description || 'Test opportunity for load testing',
        requiredSkills: data.requiredSkills || ['Communication', 'Teamwork'],
        category: data.category || 'EDUCATION',
        estimatedDuration: data.estimatedDuration || 4,
        numberOfVacancies: data.numberOfVacancies || 10,
        pointsAwarded: data.pointsAwarded || 50,
        status: data.status || 'OPEN',
        startDate: data.startDate || new Date().toISOString(),
        endDate: data.endDate || new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString(),
        promoterId: promoterId
    };

    const payload = JSON.stringify(opportunity);

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const response = http.post(`${baseUrl}/api/opportunities`, payload, params);

    check(response, {
        'opportunity created': (r) => r.status === 201 || r.status === 200,
    });

    return response;
}

export function createApplication(baseUrl, volunteerId, opportunityId) {
    const payload = JSON.stringify({
        volunteerId: volunteerId,
        opportunityId: opportunityId,
        message: 'I am interested in this opportunity'
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const response = http.post(`${baseUrl}/api/applications`, payload, params);

    check(response, {
        'application created': (r) => r.status === 201 || r.status === 200,
    });

    return response;
}

export function createVolunteerProfile(baseUrl, volunteerId, data = {}) {
    const profile = {
        volunteerId: volunteerId,
        bio: data.bio || 'Passionate volunteer looking to make a difference',
        skills: data.skills || ['Communication', 'Leadership', 'Teamwork'],
        interests: data.interests || ['Education', 'Environment'],
        availability: data.availability || 'WEEKENDS',
        location: data.location || 'Lisbon, Portugal'
    };

    const payload = JSON.stringify(profile);

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const response = http.post(`${baseUrl}/api/volunteers/profile`, payload, params);

    check(response, {
        'profile created': (r) => r.status === 201 || r.status === 200,
    });

    return response;
}

export function createBenefit(baseUrl, partnerId, data = {}) {
    const benefit = {
        title: data.title || `Benefit ${randomString(6)}`,
        description: data.description || 'Test benefit for load testing',
        pointsCost: data.pointsCost || randomInt(50, 500),
        category: data.category || randomChoice(['DISCOUNT', 'VOUCHER', 'EXPERIENCE']),
        provider: data.provider || 'Test Provider',
        availability: data.availability || true,
        partnerId: partnerId
    };

    const payload = JSON.stringify(benefit);

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const response = http.post(`${baseUrl}/api/benefits/partner`, payload, params);

    check(response, {
        'benefit created': (r) => r.status === 201 || r.status === 200,
    });

    return response;
}
