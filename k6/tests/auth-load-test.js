import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';
import { BASE_URL, OPTIONS_LOAD } from '../config.js';
import { randomEmail, randomString, registerUser, loginUser } from '../utils/helpers.js';

// Custom metrics
const registrationErrors = new Rate('registration_errors');
const loginErrors = new Rate('login_errors');

export const options = OPTIONS_LOAD;

export function setup() {
    console.log(`Starting auth load test against ${BASE_URL}`);
    return { baseUrl: BASE_URL };
}

export default function (data) {
    const baseUrl = data.baseUrl;

    // Test 1: User Registration
    const newEmail = randomEmail();
    const newUser = {
        email: newEmail,
        password: 'Test@1234',
        name: `User ${randomString(6)}`,
        userType: 'VOLUNTEER'
    };

    const registerResponse = registerUser(baseUrl, newUser);
    registrationErrors.add(registerResponse.status !== 201 && registerResponse.status !== 200);

    sleep(1);

    // Test 2: User Login
    const loginResponse = loginUser(baseUrl, newEmail, 'Test@1234');
    loginErrors.add(!loginResponse);

    sleep(1);

    // Test 3: Check if email exists
    const checkEmailResponse = http.get(`${baseUrl}/api/auth/check-email/${newEmail}`);
    check(checkEmailResponse, {
        'email check successful': (r) => r.status === 200,
        'email exists': (r) => r.json('exists') === true,
    });

    sleep(1);

    // Test 4: Get user by email (if login was successful)
    if (loginResponse) {
        const getUserResponse = http.get(`${baseUrl}/api/auth/user/email/${newEmail}`);
        check(getUserResponse, {
            'get user successful': (r) => r.status === 200,
            'user email matches': (r) => r.json('email') === newEmail,
        });
    }

    sleep(2);
}

export function teardown(data) {
    console.log('Auth load test completed');
}
