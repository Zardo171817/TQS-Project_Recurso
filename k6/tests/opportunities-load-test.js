import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Counter } from 'k6/metrics';
import { BASE_URL, OPTIONS_LOAD } from '../config.js';
import { randomEmail, randomString, registerUser, loginUser, createOpportunity } from '../utils/helpers.js';

// Custom metrics
const opportunityCreationErrors = new Rate('opportunity_creation_errors');
const opportunityListErrors = new Rate('opportunity_list_errors');
const opportunityFilterErrors = new Rate('opportunity_filter_errors');
const opportunitiesCreated = new Counter('opportunities_created');

export const options = OPTIONS_LOAD;

export function setup() {
    console.log(`Starting opportunities load test against ${BASE_URL}`);

    // Create a test promoter for creating opportunities
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

    // Create some initial opportunities for testing
    const opportunityIds = [];
    for (let i = 0; i < 5; i++) {
        const response = createOpportunity(BASE_URL, promoter.id, {
            category: ['EDUCATION', 'HEALTH', 'ENVIRONMENT', 'SOCIAL', 'CULTURE'][i % 5],
            status: 'OPEN'
        });

        if (response.status === 201 || response.status === 200) {
            const opportunity = response.json();
            opportunityIds.push(opportunity.id);
        }
    }

    console.log(`Setup complete. Created ${opportunityIds.length} test opportunities`);

    return {
        baseUrl: BASE_URL,
        promoterId: promoter.id,
        opportunityIds: opportunityIds
    };
}

export default function (data) {
    if (!data) {
        console.error('Setup failed, skipping test iteration');
        return;
    }

    const baseUrl = data.baseUrl;
    const promoterId = data.promoterId;

    // Test 1: List all opportunities
    const listResponse = http.get(`${baseUrl}/api/opportunities`);
    const listSuccess = check(listResponse, {
        'list opportunities successful': (r) => r.status === 200,
        'opportunities array returned': (r) => Array.isArray(r.json()),
    });
    opportunityListErrors.add(!listSuccess);

    sleep(1);

    // Test 2: Filter opportunities by category
    const categories = ['EDUCATION', 'HEALTH', 'ENVIRONMENT', 'SOCIAL', 'CULTURE'];
    const randomCategory = categories[Math.floor(Math.random() * categories.length)];

    const filterResponse = http.get(`${baseUrl}/api/opportunities/filter?category=${randomCategory}`);
    const filterSuccess = check(filterResponse, {
        'filter by category successful': (r) => r.status === 200,
        'filtered results returned': (r) => Array.isArray(r.json()),
    });
    opportunityFilterErrors.add(!filterSuccess);

    sleep(1);

    // Test 3: Get opportunities by status
    const statusResponse = http.get(`${baseUrl}/api/opportunities/status/OPEN`);
    check(statusResponse, {
        'get by status successful': (r) => r.status === 200,
        'status results returned': (r) => Array.isArray(r.json()),
    });

    sleep(1);

    // Test 4: Get opportunities by promoter
    const promoterOppResponse = http.get(`${baseUrl}/api/opportunities/promoter/${promoterId}`);
    check(promoterOppResponse, {
        'get promoter opportunities successful': (r) => r.status === 200,
        'promoter opportunities returned': (r) => Array.isArray(r.json()),
    });

    sleep(1);

    // Test 5: Create new opportunity (20% of users)
    if (Math.random() < 0.2) {
        const createResponse = createOpportunity(baseUrl, promoterId);
        const createSuccess = createResponse.status === 201 || createResponse.status === 200;
        opportunityCreationErrors.add(!createSuccess);

        if (createSuccess) {
            opportunitiesCreated.add(1);
        }
    }

    sleep(2);

    // Test 6: Get specific opportunity details (if opportunities exist)
    if (data.opportunityIds && data.opportunityIds.length > 0) {
        const randomOppId = data.opportunityIds[Math.floor(Math.random() * data.opportunityIds.length)];
        const detailsResponse = http.get(`${baseUrl}/api/opportunities/${randomOppId}`);
        check(detailsResponse, {
            'get opportunity details successful': (r) => r.status === 200,
            'opportunity has required fields': (r) => {
                const opp = r.json();
                return opp.title && opp.description && opp.category;
            },
        });
    }

    sleep(1);
}

export function teardown(data) {
    console.log('Opportunities load test completed');
}
