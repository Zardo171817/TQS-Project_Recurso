import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Counter } from 'k6/metrics';
import { BASE_URL, OPTIONS_LOAD } from '../config.js';
import {
    randomEmail,
    randomString,
    registerUser,
    loginUser,
    createOpportunity,
    createApplication,
    createVolunteerProfile
} from '../utils/helpers.js';

// Custom metrics
const applicationCreationErrors = new Rate('application_creation_errors');
const applicationsCreated = new Counter('applications_created');

export const options = OPTIONS_LOAD;

export function setup() {
    console.log(`Starting applications load test against ${BASE_URL}`);

    // Create a test promoter
    const promoterEmail = `promoter_${randomString(10)}@test.com`;
    const promoterData = {
        email: promoterEmail,
        password: 'Test@1234',
        name: 'Test Promoter',
        userType: 'PROMOTER'
    };

    registerUser(BASE_URL, promoterData);
    const promoter = loginUser(BASE_URL, promoterEmail, 'Test@1234');

    if (!promoter) {
        console.error('Failed to create test promoter');
        return null;
    }

    // Create test opportunities
    const opportunities = [];
    for (let i = 0; i < 3; i++) {
        const response = createOpportunity(BASE_URL, promoter.id, {
            numberOfVacancies: 50 // High number to allow many applications
        });

        if (response.status === 201 || response.status === 200) {
            opportunities.push(response.json());
        }
    }

    console.log(`Setup complete. Created ${opportunities.length} test opportunities`);

    return {
        baseUrl: BASE_URL,
        promoterId: promoter.id,
        opportunities: opportunities
    };
}

export default function (data) {
    if (!data || !data.opportunities || data.opportunities.length === 0) {
        console.error('Setup failed or no opportunities available, skipping test iteration');
        return;
    }

    const baseUrl = data.baseUrl;

    // Create a new volunteer for each iteration
    const volunteerEmail = randomEmail();
    const volunteerData = {
        email: volunteerEmail,
        password: 'Test@1234',
        name: `Volunteer ${randomString(6)}`,
        userType: 'VOLUNTEER'
    };

    registerUser(baseUrl, volunteerData);
    const volunteer = loginUser(baseUrl, volunteerEmail, 'Test@1234');

    if (!volunteer) {
        console.error('Failed to create volunteer');
        return;
    }

    sleep(1);

    // Create volunteer profile
    createVolunteerProfile(baseUrl, volunteer.id);

    sleep(1);

    // Test 1: Create application for a random opportunity
    const randomOpportunity = data.opportunities[Math.floor(Math.random() * data.opportunities.length)];
    const createAppResponse = createApplication(baseUrl, volunteer.id, randomOpportunity.id);

    const createSuccess = createAppResponse.status === 201 || createAppResponse.status === 200;
    applicationCreationErrors.add(!createSuccess);

    let applicationId = null;
    if (createSuccess) {
        applicationsCreated.add(1);
        const application = createAppResponse.json();
        applicationId = application.id;
    }

    sleep(1);

    // Test 2: Get applications by volunteer
    const volunteerAppsResponse = http.get(`${baseUrl}/api/applications/volunteer/${volunteer.id}`);
    check(volunteerAppsResponse, {
        'get volunteer applications successful': (r) => r.status === 200,
        'applications array returned': (r) => Array.isArray(r.json()),
        'at least one application': (r) => r.json().length > 0,
    });

    sleep(1);

    // Test 3: Get applications by opportunity
    const opportunityAppsResponse = http.get(`${baseUrl}/api/applications/opportunity/${randomOpportunity.id}`);
    check(opportunityAppsResponse, {
        'get opportunity applications successful': (r) => r.status === 200,
        'applications for opportunity returned': (r) => Array.isArray(r.json()),
    });

    sleep(1);

    // Test 4: Get applications by promoter
    const promoterAppsResponse = http.get(`${baseUrl}/api/applications/promoter/${data.promoterId}`);
    check(promoterAppsResponse, {
        'get promoter applications successful': (r) => r.status === 200,
        'promoter applications returned': (r) => Array.isArray(r.json()),
    });

    sleep(1);

    // Test 5: Update application status (if application was created successfully)
    if (applicationId) {
        const updatePayload = JSON.stringify({
            status: 'ACCEPTED'
        });

        const params = {
            headers: {
                'Content-Type': 'application/json',
            },
        };

        const updateResponse = http.patch(
            `${baseUrl}/api/applications/${applicationId}/status`,
            updatePayload,
            params
        );

        check(updateResponse, {
            'update application status successful': (r) => r.status === 200,
        });
    }

    sleep(2);
}

export function teardown(data) {
    console.log('Applications load test completed');
}
